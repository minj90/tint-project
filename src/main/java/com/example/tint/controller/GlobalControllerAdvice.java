package com.example.tint.controller;


import com.example.tint.domain.Employee;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final HttpSession session;

    @ModelAttribute
    public void addLoginMemberToModel(Model model){
        Employee loginMember = (Employee) session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("loginMember", loginMember);
    }

}
