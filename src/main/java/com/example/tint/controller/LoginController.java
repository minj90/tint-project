package com.example.tint.controller;

import com.example.tint.domain.Employee;
import com.example.tint.dto.LoginForm;
import com.example.tint.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    //로그인 폼 보여주는로직
    @GetMapping("/login")
    public String loginform(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login/loginForm";
    }

    //로그인 되는 로직
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) {

        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Employee loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());


        if(loginMember == null) {
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }

        //로그인 성공시 세션이 있으면 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();

        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

    //로그아웃 기능
    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        //세션을 삭제한다.
        HttpSession session = request.getSession(false);
        //true는 세션이 없으면 만들어 버린다., 일단 가지고 오는데 없으면 null

        if(session != null) {
            session.invalidate();   //세션을 제거한다.
        }

        return "redirect:/";
    }

    //서블릿을 통해 HttpSession을 생성하면 다음과 같은 쿠키를 생성한다. 쿠기 이름이 JSESSIONID이고 값은 추정 불가능한 랜덤값이다.


}
