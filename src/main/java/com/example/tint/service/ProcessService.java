package com.example.tint.service;

import com.example.tint.domain.*;
import com.example.tint.dto.ProcessDataDto;
import com.example.tint.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;
    private final JobOrderService jobOrderService;

    //공정저장
    public void processSave(JobOrder jobOrder){

        // 날짜 포맷 지정
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 현재 날짜를 YYYYMMDD 형식으로 변환
        String currentDate = LocalDateTime.now().format(dateTimeFormatter);

        //주문수량
        int orderQuantity = jobOrder.getOrderQuantity();

        //제품이름
        ProductName productName = jobOrder.getProductName();

        //jobOrderId
        Long jobOrderId = jobOrder.getId();

        // 주문 수량만큼 1공정만 생성
        for(int i=1; i<= orderQuantity; i++) {
            String sequence = String.format("%03d", i);  //순번 001, 002
            //공정번호를 무조건 1로, 첫단계
            String processStep = String.format("%02d", 1);

            //lotNumber만들기 제품이름_오더아이디_순서_공정순서_생성일
            String lotNumber = productName.name() +"_"+jobOrderId + "_" + sequence + "_" + processStep +"_"+ currentDate;


            //PinkProcess 객체 생성 및 저장
            PinkProcess pinkProcess = PinkProcess.builder()
                    .lotNumber(lotNumber)
                    .step(1)
                    .status(Status.WAITING)
                    .processDate(LocalDateTime.now())
                    .errorCode("-")
                    .jobOrder(jobOrder)
                    .build();

            processRepository.save(pinkProcess);

       }
    }

    //전체공정조회
    public List<PinkProcess> pinkProcessAll(){
        return processRepository.findAll();
    }

    //단건조회
    public Optional<PinkProcess> findOnePinkProcess(Long processId){
        return processRepository.findById(processId);
    }

    //jobOrderId로 조회
    public List<PinkProcess> findAllJobOrder(Long jobOrderId){
        return processRepository.findAllByJobOrder_Id(jobOrderId);
    }

    //날짜를 기준으로 주문조회
    public List<PinkProcess> findAllByDate(LocalDate date){
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return processRepository.findAllByProcessDateBetween(start, end);
    }

}
