package com.example.tint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PinkProcessDto {

    private String lotNumber;
    private int step;
    private String status;
    private String processDate;
    private String errorCode;
    private String productName;

}


