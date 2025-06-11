package com.example.tint.dto;


import com.example.tint.domain.CorrespondentName;
import com.example.tint.domain.MaterialName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialForm {

    private Long id;

    @NotNull(message = "재료이름은 필수입니다.")
    private MaterialName materialName;

    @NotNull(message = "수량은 필수입니다.")
    private Integer materialQuantity;

    @NotNull(message = "거래처는 필수입니다.")
    private CorrespondentName correspondentName;


    //@NotEmpty는 문자열(String), 컬렉션(Collection), 배열(Array)에서만 사용
    //@MaterialName은 Enum이므로 **@NotEmpty 대신 @NotNull**을 사용

    //int는 원시 타입이라 null이 될 수 없기 때문에 @NotNull을 사용할 수 없어.
    //즉, @NotNull(message = "수량은 필수입니다.")는 원시 타입 int에서는 효과가 없음.

}
