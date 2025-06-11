package com.example.tint.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeForm {

    private Long id;

    @NotEmpty(message = "회원아이디는 필수입니다.")
    private String loginId;

    @NotEmpty(message = "회원이름은 필수입니다")
    private String name;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;

    @NotEmpty(message = "position는 필수입니다.")
    private String position;

    @NotEmpty(message = "phone는 필수입니다.")
    private String phone;
}
