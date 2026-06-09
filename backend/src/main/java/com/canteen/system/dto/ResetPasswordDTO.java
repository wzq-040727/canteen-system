package com.canteen.system.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ResetPasswordDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "安全答案不能为空")
    private String securityAnswer;
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String newPassword;
}
