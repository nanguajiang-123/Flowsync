package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.dto.LoginRequest;
import hgc.flowsyncapi.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 用户登录 */
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest request) {
        try {
            return ApiResponse.ok("登录成功", authService.login(request.getUsername(), request.getPassword()));
        } catch (RuntimeException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
