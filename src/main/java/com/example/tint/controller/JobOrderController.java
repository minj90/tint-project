package com.example.tint.controller;

import com.example.tint.domain.*;
import com.example.tint.dto.JobOrderForm;
import com.example.tint.service.JobOrderService;
import com.example.tint.service.MaterialService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/order")
public class JobOrderController {

    private final JobOrderService jobOrderService;
    private final MaterialService materialService;

    //제품생산주문페이지 보여주기, 로그인회원만 접근
    @GetMapping("/new")
    public String createJobOrderform(Model model, HttpSession session){
        //재료재고확인
        Map<MaterialName, Long> materialQuantities = materialService.getMaterialQuantities();
        log.info("총 재료 {}", materialQuantities);

        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if(loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("jobOrderForm", new JobOrderForm());
        return "jobOrder/createJobOrderForm";
    }

    //주문저장
    @PostMapping("/new")
    public String createJobOrder(@Valid JobOrderForm jobOrderForm, BindingResult bindingResult,
                                 @ModelAttribute("loginMember") Employee loginMember){
        //productName에 따른 수량 가져오기
        Map<MaterialName, Long> requiredMaterialStock = materialService.getRequiredMaterialStock(jobOrderForm.getProductName());
        log.info("productName에 따른 수량 {}", requiredMaterialStock);

        log.info("원재료-넘어오는 값 {}", jobOrderForm.getProductName().name() );

        if(bindingResult.hasErrors()){
            return "jobOrder/createJobOrderForm";
        }

        //재고가 충분한지 확인, 재고가 충분하지 않으면 글로벌에러 발생
        if(!materialService.isEnoughMaterial(jobOrderForm.getProductName(), jobOrderForm.getOrderQuantity())) {
            bindingResult.reject("notEnoughMaterial", "재고가 부족합니다.");
            return "jobOrder/createJobOrderForm";
        }

        JobOrder jobOrder = JobOrder.builder()
                .employee(loginMember)
                .productName(jobOrderForm.getProductName())
                .orderQuantity(jobOrderForm.getOrderQuantity())
                .orderDate(LocalDateTime.now())
                .jobOrderState(JobOrderState.PENDING)
                .build();

        jobOrderService.save(jobOrder);
        return "redirect:/order/list";
    }

    //주문리스트
    @GetMapping("/list")
    public String orderList(Model model){
        List<JobOrder> jobOrders = jobOrderService.findJobOrders();
        model.addAttribute("jobOrders", jobOrders);
        return "jobOrder/jobOrderList";
    }

    //삭제하기
    @GetMapping("/delete/{jobOrderId}")
    public String deleteJobOrder(@PathVariable("jobOrderId") Long jobOrderId){
        jobOrderService.findOneOrder(jobOrderId).orElseThrow(()-> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));

        jobOrderService.deleteJobOrder(jobOrderId);
        return "redirect:/order/list";
    }


}
