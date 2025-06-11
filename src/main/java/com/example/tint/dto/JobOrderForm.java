package com.example.tint.dto;

import com.example.tint.domain.ProductName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobOrderForm {

    private Long id;

    @NotNull(message = "제품명은 필수입니다.")
    private ProductName productName;

    @NotNull(message = "수량은 필수입니다.")
    private Integer orderQuantity;
}
