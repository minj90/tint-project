package com.example.tint.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JobOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="jobOrder_id")
    private Long id;

    //주문자- 로그인회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id")
    private Employee employee;

    //제품명
    @Enumerated(EnumType.STRING)
    private ProductName productName;

    private int orderQuantity;

    private LocalDateTime orderDate;

    //하나의 오더는 여러 프로세스를 가진다.
    @OneToMany(mappedBy = "jobOrder", cascade = CascadeType.ALL)
    private List<PinkProcess> pinkProcesses = new ArrayList<>();

    @Setter
    @Enumerated(EnumType.STRING)
    private JobOrderState jobOrderState;

    @Builder.Default
    @OneToMany(mappedBy = "jobOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobOrderMaterial> jobOrderMaterials = new ArrayList<>();


    //양방향 연관관계 설정
    public void addJobOrderMaterial(JobOrderMaterial jm) {
        jobOrderMaterials.add(jm);    // 1. 자식 리스트에 추가
        jm.setJobOrder(this);   // 2. 자식의 부모 설정
    }
}
