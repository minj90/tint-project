package com.example.tint.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="employee_id")
    private Long id;

    private String loginId;

    @Setter
    private String name;

    @Setter
    private String password;

    @Setter
    @Enumerated(EnumType.STRING)
    private PositionName position;

    @Setter
    private String phone;

    //Employee가 여러 개의 Material을 가질 수 있는 OneToMany 관계
    @OneToMany(mappedBy = "employee")
    private List<Material> materials = new ArrayList<>();

    //직원은 여러개의 작업오더를 넣을 수 있다.
    @OneToMany(mappedBy = "employee")
    private List<JobOrder> jobOrders = new ArrayList<>();

}
