package com.example.tint.domain;

import com.example.tint.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "materialQuantity")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="material_id")
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    private MaterialName materialName;

    @Setter
    private int materialQuantity;

    //지연로딩 : 데이터를 조회할 때 필요한 시점에 연관된 데이터를 불러온다.
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id")
    private Employee employee;

    @Setter
    @Enumerated(EnumType.STRING)
    private CorrespondentName correspondentName;  //거래처, 코러스판던트

    @Setter
    private LocalDateTime materialDate;  //입고날짜


    //비즈니스 로직 추가
    //재고증가
    public void addStock(MaterialName materialName, int quantity){
        if(this.materialName.equals(materialName)){
            this.materialQuantity += quantity;
        }
    }

    //재고 감소
    public void removeStock(MaterialName materialName){
        if(this.materialName == materialName){
            int restStock = this.materialQuantity -1;
            if(restStock < 0 ) {
                throw new NotEnoughStockException("need more stock");
            }
            this.materialQuantity = restStock;
        }
    }

}
