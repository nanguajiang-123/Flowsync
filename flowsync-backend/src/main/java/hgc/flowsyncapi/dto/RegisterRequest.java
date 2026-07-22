package hgc.flowsyncapi.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String realName;
    private String role;
    private String phone;
    private String email;
}
