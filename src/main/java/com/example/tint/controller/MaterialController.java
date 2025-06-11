package com.example.tint.controller;

import com.example.tint.domain.Employee;
import com.example.tint.domain.Material;
import com.example.tint.dto.MaterialForm;
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

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/material")
public class MaterialController {

    private final MaterialService materialService;

    //재고입력 폼, 입력하기 전 화면
    @GetMapping("/new")
    public String createMaterial(Model model, HttpSession session){

        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if(loginMember == null) {
            return "redirect:/login";
        }

        model.addAttribute("materialForm", new MaterialForm());
        return "material/createMaterialForm";
    }

    //재고저장
    @PostMapping("/new")
    public String create(@Valid MaterialForm materialForm,
                         BindingResult bindingResult,
                         @ModelAttribute("loginMember") Employee loginMember){

        if(bindingResult.hasErrors()){
            return "material/createMaterialForm";
        }

        Material material = Material.builder()
                .materialName(materialForm.getMaterialName())
                .materialQuantity(materialForm.getMaterialQuantity())
                .employee(loginMember)  //로그인한 직원 정보 저장
                .correspondentName(materialForm.getCorrespondentName())
                .materialDate(LocalDateTime.now())
                .build();

        materialService.saveMaterial(material);
        return "redirect:/material/list";  // 재고 조회 리스트 페이지로 이동
    }

    //재고조회하기
    @GetMapping("/list")
    public String list(Model model){
        List<Material> materials = materialService.findMaterialList();
        model.addAttribute("materials", materials);
        return "material/materialList";
    }

    //재고수정하기 - 수정하기 위한 form보여주기
    @GetMapping("/{materialId}/edit")
    public String updateMaterialForm(@PathVariable("materialId") Long materialId, Model model){

        Material material = materialService.findOne(materialId).orElseThrow(()->new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));

        Material materialForm = Material.builder()
                .id(material.getId())
                .materialName(material.getMaterialName())
                .materialQuantity(material.getMaterialQuantity())
                .employee(material.getEmployee())
                .materialDate(material.getMaterialDate())
                .correspondentName(material.getCorrespondentName())
                .build();

        model.addAttribute("materialForm", materialForm);
        return "material/updateMaterialForm";
    }

    //수정한 재고 리포지터리에 저장
    @PostMapping("/{materialId}/edit")
    public String updateMaterial(@Valid @ModelAttribute("materialForm") MaterialForm materialForm, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            Material material = materialService.findOne(materialForm.getId()).orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다"));

            materialForm.setId(material.getId());
            materialForm.setMaterialName(material.getMaterialName());
            materialForm.setMaterialQuantity(material.getMaterialQuantity());
            materialForm.setCorrespondentName(material.getCorrespondentName());

            model.addAttribute("materialForm", materialForm);
            return "material/updateMaterialForm";
        }

        Material material = materialService.findOne(materialForm.getId()).orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다"));

        Material updateMaterial = material.toBuilder()
                .materialName(materialForm.getMaterialName())
                .materialQuantity(materialForm.getMaterialQuantity())
                .correspondentName(materialForm.getCorrespondentName())
                .build();

        materialService.saveMaterial(updateMaterial);
        return "redirect:/material/list";  //리스트로 이동
    }

    //@ModelAttribute는 사용자가 요청시 전달하는 값을 오브젝트 형태로 매핑해주는 어노테이션입니다.

    //삭제하기
    @GetMapping("/delete/{materialId}")
    public String deleteMaterial(@PathVariable("materialId") Long materialId){
        Material material = materialService.findOne(materialId).orElseThrow(() -> new IllegalArgumentException("해당 제품을 찾을 수 없습니다."));

        materialService.deleteMaterial(material.getId());
        return "redirect:/material/list";
    }

}
