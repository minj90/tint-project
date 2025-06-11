package com.example.tint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PredictResult {

    private double prediction;
    private LocalDate predictedDate;


}
