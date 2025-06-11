package com.example.tint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateForm {

    private Long id;

    private String loginId;

    private String name;

//    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotEmpty(message = "position는 필수입니다.")
    private String position;

    @NotEmpty(message = "phone는 필수입니다.")
    private String phone;
}
