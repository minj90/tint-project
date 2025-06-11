package com.example.tint.service;

import com.example.tint.domain.Employee;
import com.example.tint.domain.PositionName;
import com.example.tint.dto.EmployeeForm;
import com.example.tint.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)  //읽기전용
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    //회원가입
    @Transactional
    public Long save(Employee employee){
        employeeRepository.save(employee);
        return employee.getId();
    }
    //중복회원검증
    public Employee validateDuplicateMember(String loginId) {
        Employee employee = employeeRepository.findByLoginId(loginId).orElse(null);
        //회원이 없으면 null을 반환
        if(employee == null){
            return null;
        }

        return employee;
    }

    //전체회원조회
    public List<Employee> employeeList(){
        return employeeRepository.findAll();
    }

    //단건조회
    public Optional<Employee> findOneEmployee(Long employeeId){
        return employeeRepository.findById(employeeId);

    }

    //loginId로 찾기
    public Optional<Employee> findIoginidEmployee(String loginId) {
        return employeeRepository.findByLoginId(loginId);
    }

    //회원삭제, 테스트에서 삭제가 이루어지지 않음, 삭제가 즉시 반영되지 않음, 이미로드되어 있어서 캐시에서만 지워지고 실제 DB에서는 쿼리가 실행되지 않음
    @Transactional
    public void deleteEmployee(Long employeeId){
        this.employeeRepository.deleteById(employeeId);
    }
}
