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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("process")
public class ProcessController {

    private final ProcessService processService;
    private final JobOrderService jobOrderService;
    private final ProcessDataService processDataService;


    //공정시작
    @PostMapping("/new/{jobOrderId}")
    public String createProcess(@PathVariable("jobOrderId") Long jobOrderId){

        JobOrder jobOrder = jobOrderService.findOneOrder(jobOrderId).orElseThrow(() -> new IllegalArgumentException("해당 작업오더를 찾을 수 없습니다"));

        jobOrder.setJobOrderState(JobOrderState.IN_PROGRESS);
        jobOrderService.updateOrderState(jobOrder.getId(), jobOrder.getJobOrderState());  //상태변경 저장

        //공정생성 및 저장
        processService.processSave(jobOrder);

        return String.format("redirect:/process/list/%s", jobOrderId);

    }

    //공정보기 페이지
    @GetMapping("/list/{jobOrderId}")
    public String processList(@PathVariable("jobOrderId") Long jobOrderId, Model model){


        ProcessDataDto processData = processDataService.getProcessData(jobOrderId);

        model.addAttribute("processes",processData.getProcesses());
        model.addAttribute("jobOrderId", jobOrderId);

        //차트를 위한 추가
        model.addAttribute("countByStep",processData.getCountByStep());
        model.addAttribute("ngCountByStep", processData.getNgCountByStep());  //공정별 에러
        model.addAttribute("okCountByStep", processData.getOkCountByStep()); //공정별 ok
        model.addAttribute("productCount",processData.getProductCount()); //완제품수
        model.addAttribute("totalCount", processData.getTotalCount()); //주문수
        
        return "process/processList";
    }



}
