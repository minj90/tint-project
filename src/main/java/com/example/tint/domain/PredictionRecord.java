package com.example.tint.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class PredictionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private double prediction;

    @Setter
    private LocalDateTime predictedAt;

    @Lob // 긴 JSON 저장을 위해
    @Setter
    private String inputData;

    @Setter
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<PredictionOrderInput> orderInputs;

    //@Lob
    //@Lob = Large Object의 줄임말
    //DB에 텍스트나 바이너리 대용량 필드를 저장하고 싶을 때 사용
    // JSON 문자열, 로그 백업 등 길고 유동적인 문자열을 사용하고 싶을 때
}
