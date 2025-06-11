package com.example.tint.service;


import com.example.tint.domain.Employee;
import com.example.tint.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final EmployeeRepository employeeRepository;

    //null을 반환하면 로그인 실패
    public Employee login(String loginId, String password){
//        회원이 없으면 null을 반환하게 수정.
        Employee employee = employeeRepository.findByLoginId(loginId).orElse(null);

        //회원이 없으면 null을 반환
        if(employee == null){
            return null;
        }

        if(employee.getPassword().equals(password)){
            return employee;
        }
        return null;
    }
}
