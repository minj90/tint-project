package com.example.tint;

import com.example.tint.domain.JobOrder;
import com.example.tint.domain.JobOrderState;
import com.example.tint.domain.PinkProcess;
import com.example.tint.domain.Status;
import com.example.tint.dto.PinkProcessDto;
import com.example.tint.repository.ProcessRepository;
import com.example.tint.service.JobOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessScheduler {
    private final ProcessRepository processRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JobOrderService jobOrderService;

    @Scheduled(fixedRate = 5000)
    public void processOneByOne() {
        log.info("스케줄러가 호출되나?");
        // Status가 WAITING인 PinkProcess 중에서 jobOrder.id가 가장 작은(즉, 먼저 등록된) 하나를 가져온다
        Optional<PinkProcess> orderByIdAsc = processRepository.findFirstByStatusOrderByJobOrderIdAsc(Status.WAITING);

        if (orderByIdAsc.isEmpty()) return;

        PinkProcess current = orderByIdAsc.get();
        log.info("가져온 공정step1 {}, {}", current.getStep(), current.getJobOrder().getId());
        // 상태 업데이트
        boolean isOk = new Random().nextInt(10) + 1 <= 9;
        current.setStatus(isOk ? Status.OK : Status.NG);
        current.setProcessDate(LocalDateTime.now());
        current.setErrorCode(isOk ? "_" : "10" + current.getStep());
        processRepository.save(current);

        //step2생성
        boolean isOk2 = new Random().nextInt(10) + 1 <= 9;
        log.info("가져온 공정step2 {}, isOk2 {}", current.getStep()+1, isOk2);
        //PINKTINT_1_001_01_20250522 => _01_ => _02_
        PinkProcess step2 = PinkProcess.builder()
                .lotNumber(current.getLotNumber().replace(
                        "_" + String.format("%02d", current.getStep()) + "_",
                        "_" + String.format("%02d", current.getStep() + 1) + "_"))
                .step(current.getStep() + 1)
                .status( isOk2 ? Status.OK : Status.NG)
                .processDate(LocalDateTime.now())
                .errorCode(isOk2 ? "_" : "10" + (current.getStep()+1))
                .jobOrder(current.getJobOrder())
                .build();
        processRepository.save(step2);

        //step3생성
        boolean isOk3 = new Random().nextInt(10) + 1 <= 9;
        log.info("가져온 공정step2 {}, isOk3 {}", current.getStep()+2, isOk3 );
        PinkProcess step3 = PinkProcess.builder()
                .lotNumber(current.getLotNumber().replace("_" + String.format("%02d", current.getStep()) + "_",
                        "_" + String.format("%02d", current.getStep() + 2) + "_"))
                .step(current.getStep() + 2)
                .status( isOk3 ? Status.OK : Status.NG)
                .processDate(LocalDateTime.now())
                .errorCode(isOk3 ? "_" : "10" + (current.getStep()+2))
                .jobOrder(current.getJobOrder())
                .build();
        processRepository.save(step3);

//        jobOrderService.updateOrderState(current.getJobOrder().getId(), JobOrderState.COMPLETED);

        // 모든 제품의 공정(step 3)이 끝났는지 확인
        Long jobOrderId = current.getJobOrder().getId();
        int totalQuantity = current.getJobOrder().getOrderQuantity();

// step == 3인 공정 수 조회 (OK + NG 포함)
        long finishedCount = processRepository.countByJobOrderIdAndStep(jobOrderId, 3);

        if (finishedCount >= totalQuantity) {
            jobOrderService.updateOrderState(jobOrderId, JobOrderState.COMPLETED);
        }


        //웹소켓 전송
        PinkProcessDto dto = new PinkProcessDto(
                current.getLotNumber(),
                current.getStep(),
                current.getStatus().name(),
                current.getProcessDate() != null ? current.getProcessDate().toString() : null,
                current.getErrorCode(),
                current.getJobOrder().getProductName().name()
        );
        log.info("Sending to WebSocket:{}}", current.getLotNumber());

        //전송
        messagingTemplate.convertAndSend("/topic/process", dto);

        // step2 전송
        PinkProcessDto dto2 = new PinkProcessDto(
                step2.getLotNumber(),
                step2.getStep(),
                step2.getStatus().name(),
                step2.getProcessDate() != null ? current.getProcessDate().toString() : null,
                step2.getErrorCode(),
                step2.getJobOrder().getProductName().name()
        );
        messagingTemplate.convertAndSend("/topic/process", dto2);

        // step3 전송
        PinkProcessDto dto3 = new PinkProcessDto(
                step3.getLotNumber(),
                step3.getStep(),
                step3.getStatus().name(),
                step3.getProcessDate() != null ? current.getProcessDate().toString() : null,
                step3.getErrorCode(),
                step3.getJobOrder().getProductName().name()
        );
        messagingTemplate.convertAndSend("/topic/process", dto3);

    }
}

