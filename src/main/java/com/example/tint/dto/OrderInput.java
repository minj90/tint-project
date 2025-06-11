package com.example.tint.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.tint.domain.MaterialName;

@Data
public class OrderInput {

    @NotEmpty(message = "날짜는 비워있을 수 없습니다.")
    private String date;

    @NotNull(message = "수량은 비워있을 수 없습니다")
    private Integer qty;

    @NotNull(message = "원재료는 비워있을 수 없습니다.")
    private MaterialName materialName;


    //@NotEmpty는 문자열(String), 컬렉션(Collection), 배열(Array)에서만 사용
    //@MaterialName은 Enum이므로 **@NotEmpty 대신 @NotNull**을 사용
}
