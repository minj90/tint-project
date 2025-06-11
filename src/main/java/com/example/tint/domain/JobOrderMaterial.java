package com.example.tint.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JobOrderMaterial {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private JobOrder jobOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private Material material;

    //주문수량 - Materail를 기준으로 가져온 수량
    private int deductedQuantity;
}
