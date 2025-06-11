package com.example.tint;

import com.example.tint.domain.*;
import com.example.tint.service.EmployeeService;
import com.example.tint.service.MaterialService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

//@Component
@AllArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final EmployeeService employeeService;
    private final MaterialService materialService;

    @Override
    public void run(String... args) throws Exception {

        //초기직원생성
        Employee employee = Employee.builder()
                .loginId("test")
                .name("관리자")
                .password("1111")
                .position(PositionName.MANAGER)
                .phone("010-1111-1111")
                .build();

        //기초데이터삽입시 직원 정보 필요
        employeeService.save(employee);
        //기초데이터 종류 리스트
        List<MaterialName> materialNames = List.of(
                MaterialName.BASEDYE,
                MaterialName.INCENSE,
                MaterialName.GREEN,
                MaterialName.RED,
                MaterialName.PINK
        );

        //기초데이터 삽입,
        List<Material> materials = materialNames.stream().map(materialName -> Material.builder()
                .materialName(materialName)
                .materialQuantity(100)
                .employee(employee)
                .correspondentName(CorrespondentName.ABC)
                .materialDate(LocalDateTime.now())
                .build()).toList();


        //람다표현식
//        materials.forEach(material -> materialService.saveMaterial(material));
        //위 람다표현식을 메서드참조로 수정하면
        materials.forEach(materialService::saveMaterial);


    }
}
