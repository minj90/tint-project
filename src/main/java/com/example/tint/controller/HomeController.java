package com.example.tint.controller;

import com.example.tint.domain.JobOrder;
import com.example.tint.domain.JobOrderState;
import com.example.tint.dto.OrderInput;
import com.example.tint.dto.PredictRequest;
import com.example.tint.dto.PredictResult;
import com.example.tint.dto.ProcessDataDto;
import com.example.tint.repository.JobOrderRepository;
import com.example.tint.service.JobOrderService;
import com.example.tint.service.PredictService;
import com.example.tint.service.ProcessDataService;
import com.example.tint.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final ProcessDataService processDataService;
    private final JobOrderRepository jobOrderRepository;
    private final PredictService predictService;


    @GetMapping("/")
    public String homeController(Model model){
        //예측하는 부분
        PredictRequest request = new PredictRequest();

        //반드시 orders초기화
        ArrayList<OrderInput> list = new ArrayList<>();
        list.add(new OrderInput());
        list.add(new OrderInput());
        list.add(new OrderInput());

        log.info("list{}", list);

        request.setOrders(list);
        model.addAttribute("request", request);

        //잡오더 부분
        prepareModel(model);


        return "home";
    }

    @PostMapping("/")
    public String predict(@Valid @ModelAttribute("request") PredictRequest request, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            prepareModel(model);
            return "home";
        }

        PredictResult result = predictService.getPrediction(request);
        log.info("예측결과는? {} ", request.getOrders());
        model.addAttribute("prediction", result.getPrediction());
        model.addAttribute("predictedDate", result.getPredictedDate());


        //잡오더 부분
        prepareModel(model);
        return "home";
    }

    private void prepareModel(Model model) {
        List<JobOrder> jobOrders = jobOrderRepository.findByJobOrderState(JobOrderState.IN_PROGRESS);
        Long jobOrderId = !jobOrders.isEmpty()
                ? jobOrders.get(0).getId()
                : jobOrderRepository.findTopByOrderByIdDesc().map(JobOrder::getId).orElse(null);

        ProcessDataDto processData = processDataService.getProcessData(jobOrderId);

        log.info("현재 작업중인 id {}", jobOrderId  );


        model.addAttribute("processes",processData.getProcesses());
        model.addAttribute("jobOrderId", jobOrderId);

        //차트를 위한 추가
        model.addAttribute("countByStep",processData.getCountByStep());
        model.addAttribute("ngCountByStep", processData.getNgCountByStep());  //공정별 에러
        model.addAttribute("okCountByStep", processData.getOkCountByStep()); //공정별 ok
        model.addAttribute("productCount",processData.getProductCount()); //완제품수
        model.addAttribute("totalCount", processData.getTotalCount()); //주문수


        //날짜별 완제품수 계산
        LocalDate today = LocalDate.now();
        ProcessDataDto dto = processDataService.getProcessDataByDate(today);

        model.addAttribute("date", today);
        model.addAttribute("productCountDate", dto.getProductCount());
    }

}
