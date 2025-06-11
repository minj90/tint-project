package com.example.tint.controller;

import com.example.tint.domain.Employee;
import com.example.tint.domain.PositionName;
import com.example.tint.dto.EmployeeForm;
import com.example.tint.dto.EmployeeUpdateForm;
import com.example.tint.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    //가입페이지 보여주기
    @GetMapping("/new")
    public String createEmployee(Model model){
        model.addAttribute("employeeForm",new EmployeeForm());
        return "employee/createEmployeeForm";
    }

    //가입하기
    @PostMapping("/new")
    public String create(@Valid EmployeeForm employeeForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("error: {}", bindingResult.getAllErrors());
            return "employee/createEmployeeForm";
        }

        Employee findEmployee = employeeService.validateDuplicateMember(employeeForm.getLoginId());

        if(findEmployee != null) {
            bindingResult.reject("DuplicateMember","아이디가 존재합니다.");
            return "employee/createEmployeeForm";
        }

        Employee employee = Employee.builder()
                .loginId(employeeForm.getLoginId())
                .name(employeeForm.getName())
                .password(employeeForm.getPassword())
                .position(PositionName.valueOf(employeeForm.getPosition()))
                .phone(employeeForm.getPhone())
                .build();

        employeeService.save(employee);
        return "home";
    }

    //조회
    @GetMapping("/list")
    public String list(Model model){
        List<Employee> employees = employeeService.employeeList();
        model.addAttribute("employees", employees);
        return "employee/employeeList";

    }

    //수정페이지-보여주기
    @GetMapping("/{employeeId}/edit")
    public String updateEmployeeForm(@PathVariable("employeeId") Long employeeId, Model model) {
        Employee employee = employeeService.findOneEmployee(employeeId).orElseThrow(() -> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));
        EmployeeForm employeeForm = EmployeeForm.builder()
                .id(employee.getId())
                .loginId(employee.getLoginId())
                .name(employee.getName())
                .password(employee.getPassword())
                .position(String.valueOf(employee.getPosition()))
                .phone(employee.getPhone())
                .build();

        model.addAttribute("employeeForm", employeeForm);
        return "employee/updateEmployeeForm";
    }

    //수정-저장
    @PostMapping("/{employeeId}/edit")
    public String updateEmployeeForm(@Valid @ModelAttribute("employeeForm") EmployeeUpdateForm employeeForm, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            Employee employee = employeeService.findOneEmployee(employeeForm.getId()).orElseThrow(() -> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));

            employeeForm.setId(employee.getId());
            employeeForm.setLoginId(employee.getLoginId());
            employeeForm.setName(employee.getName());

            model.addAttribute("employeeForm", employeeForm);
            return "employee/updateEmployeeForm";
        }

        Employee employee = employeeService.findOneEmployee(employeeForm.getId()).orElseThrow(() -> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));

        //비밀번호가 입력되지 않았다면 기존 비밀번호 유지
        String newPassword = employeeForm.getPassword();
        if(newPassword == null || newPassword.isBlank()){
            newPassword =  employee.getPassword(); //기존 비밀번호 유지
        }

        Employee updateEmployee = employee.toBuilder()
                .password(newPassword)
                .position(PositionName.valueOf(employeeForm.getPosition()))
                .phone(employeeForm.getPhone())
                .build();

        employeeService.save(updateEmployee);
        return "redirect:/employee/list";
    }

    //삭제하기
    @GetMapping("/delete/{employeeId}")
    public String deleteEmployee(@PathVariable("employeeId") Long employeeId){

        log.info("여기왔어? {}", employeeId);

        Employee employee = employeeService.findOneEmployee(employeeId).orElseThrow(() -> new IllegalArgumentException("해당 직원을 찾을 수 없습니다."));

        log.info("삭제아이디 {}", employee.getId());

        employeeService.deleteEmployee(employee.getId());
        return "redirect:/employee/list";
    }
}
