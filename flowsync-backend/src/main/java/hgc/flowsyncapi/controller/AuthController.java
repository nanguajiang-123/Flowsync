package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.dto.LoginRequest;
import hgc.flowsyncapi.dto.RegisterRequest;
import hgc.flowsyncapi.service.AuthService;
import hgc.flowsyncapi.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /** 用户注册 */
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest request) {
        try {
            return ApiResponse.ok("注册成功", userService.register(
                    request.getUsername(),
                    request.getPassword(),
                    request.getConfirmPassword(),
                    request.getRealName(),
                    request.getRole(),
                    request.getPhone(),
                    request.getEmail()));
        } catch (RuntimeException e) {
            return ApiResponse.fail(e.getMessage());
        }
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
