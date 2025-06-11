package com.example.tint.controller;

import com.example.tint.domain.JobOrder;
import com.example.tint.domain.JobOrderState;
import com.example.tint.domain.PinkProcess;
import com.example.tint.domain.Status;
import com.example.tint.dto.ProcessDataDto;
import com.example.tint.service.JobOrderService;
import com.example.tint.service.ProcessDataService;
import com.example.tint.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("process")
public class ProcessController_data이동전 {

    private final ProcessService processService;
    private final JobOrderService jobOrderService;


    //공정시작
    @PostMapping("/new/{jobOrderId}")
    public String createProcess(@PathVariable("jobOrderId") Long jobOrderId){

        JobOrder jobOrder = jobOrderService.findOneOrder(jobOrderId).orElseThrow(() -> new IllegalArgumentException("해당 작업오더를 찾을 수 없습니다"));

        jobOrder.setJobOrderState(JobOrderState.IN_PROGRESS);
        jobOrderService.save(jobOrder);  //상태변경 저장

        //공정생성 및 저장
        processService.processSave(jobOrder);

        return String.format("redirect:/process/list/%s", jobOrderId);

    }

    //공정보기 페이지
    @GetMapping("/list/{jobOrderId}")
    public String processList(@PathVariable("jobOrderId") Long jobOrderId, Model model){

        List<PinkProcess> processes = processService.findAllJobOrder(jobOrderId);

        //차트를 위한 추가
        //공정별 통계, Map<Integer, Long> 형태로 저장됨
        Map<Integer, Long> countByStep = processes.stream()
                .collect(Collectors.groupingBy(PinkProcess::getStep, Collectors.counting()));
        log.info("공정별 개수 {}", countByStep);

        //공정별 에러
        Map<Integer, Long> ngCountByStep = processes.stream()
                .filter(p-> Status.NG.equals(p.getStatus()))
                .collect(Collectors.groupingBy(PinkProcess::getStep, Collectors.counting()));
        log.info("공정별 에러개수 {}", ngCountByStep);

        //공졍별 OK
        Map<Integer, Long> okCountByStep = processes.stream()
                .filter(p-> Status.OK.equals(p.getStatus()))
                .collect(Collectors.groupingBy(PinkProcess::getStep, Collectors.counting()));
        log.info("공정별 에러개수 {}", okCountByStep);

        //lotNumber별 모든 공정이 "OK"인지를 확인하여 공정 개수 계산 - 로트번호별 상태값을 가져온다
        //lotNumber 별로 모든 공정 상태를 Set<String>으로 저장
        Map<String, Set<Status>> lotStatusMap =
                processes.stream()
                        .collect(Collectors.groupingBy(p-> lotNumberGroup(p.getLotNumber()) ,
                                Collectors.mapping(PinkProcess::getStatus, Collectors.toSet())));

        log.info("lot별 ? {}", lotStatusMap);

        //모든 상태가 "OK"인 LotNumber만 선택하여 개수 계산
        //Set<String>의 크기가 1이고, 그 값이 "OK"라면 해당 lotNumber는 완제품
        long productCount = lotStatusMap.values().stream()
                .filter(statuses -> statuses.size() == 1 && statuses.contains(Status.OK)).count();

        log.info("lot별 모두 ok인 : 완제품 개수 {}", productCount);

        //공정 전체 개수, 주문수
        int totalCount = (processes.size() / 3);  //주문수
        log.info("주문수 {} ", totalCount);


        model.addAttribute("processes",processes);
        model.addAttribute("jobOrderId", jobOrderId);
        //차트를 위한 추가
        model.addAttribute("countByStep",countByStep);
        model.addAttribute("ngCountByStep", ngCountByStep);  //공정별 에러
        model.addAttribute("okCountByStep", okCountByStep); //공정별 ok
        model.addAttribute("productCount",productCount); //완제품수
        model.addAttribute("totalCount", totalCount); //주문수
        
        return "process/processList";
    }

    //로트번호에서 공통 부분만 추출하는 함수, PINKTINT_1_001_01_20250308
    private String lotNumberGroup(String lotNumber) {
        String[] parts = lotNumber.split("_");
        if(parts.length >= 3) {
            return parts[0] + "_" + parts[1] + "_" + parts[2];
        }
        return lotNumber;
    };

}
