package com.example.tint.dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class PredictRequest {

    @Valid  //리스트 내부 요소까지 유효성 검사 적용
    private List<OrderInput> orders;
}
