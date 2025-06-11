package com.example.tint.controller;

import com.example.tint.domain.JobOrder;
import com.example.tint.dto.ProcessDataDto;
import com.example.tint.service.JobOrderService;
import com.example.tint.service.ProcessDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class productController {

    private final JobOrderService jobOrderService;
    private final ProcessDataService processDataService;
    private final ObjectMapper objectMapper;  //Json변환

    //공정보기 페이지
    @GetMapping("/product/list")
    public String processList(Model model) throws JsonProcessingException {

        List<JobOrder> jobOrders = jobOrderService.findJobOrders();
        model.addAttribute("jobOrders", jobOrders);

        // 각 JobOrder에 대한 공정 데이터 계산
        Map<Long, Long> ngCountByJobOrder = new HashMap<>();
        Map<Long, Long> productCountByJobOrder = new HashMap<>();
        //차트에 사용할 주문수

        for(JobOrder jobOrder : jobOrders){
             ProcessDataDto processData = processDataService.getProcessData(jobOrder.getId());
            ngCountByJobOrder.put(jobOrder.getId(), processData.getNgCountByStep().values().stream().mapToLong(Long::longValue).sum());
            productCountByJobOrder.put(jobOrder.getId(),processData.getProductCount());
        }

        model.addAttribute("ngCountByJobOrder", ngCountByJobOrder);
        model.addAttribute("productCountByJobOrder", productCountByJobOrder);

        // 차트 데이터 JSON 변환 (주문수는 jobOrders에서 바로 가져옴)
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", jobOrders.stream().map(j -> j.getProductName().toString()).toArray());
        chartData.put("orderQuantity", jobOrders.stream().map(JobOrder::getOrderQuantity).toArray());
        chartData.put("errorCount", jobOrders.stream().map(j -> ngCountByJobOrder.getOrDefault(j.getId(), 0L)).toArray());
        chartData.put("productCount", jobOrders.stream().map(j -> productCountByJobOrder.getOrDefault(j.getId(), 0L)).toArray());
        chartData.put("productId", jobOrders.stream().map(j -> j.getId().longValue()).toList());

        log.info("차트데이터: {}", chartData);
        log.info("차트데이터 jSon으로 변환: {}", objectMapper.writeValueAsString(chartData));
        model.addAttribute("chartData", objectMapper.writeValueAsString(chartData));


        return "product/productList";
    }
}
