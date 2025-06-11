package com.example.tint.repository;

import com.example.tint.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    //loginId, 사용자아이디로 찾기
    Optional<Employee> findByLoginId(String loginId);
}
