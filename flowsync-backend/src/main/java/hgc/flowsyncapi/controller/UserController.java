package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.dto.PasswordUpdateRequest;
import hgc.flowsyncapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户列表查询与密码修改")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取全部用户列表")
    @GetMapping("/list")
    public ApiResponse<?> list() {
        return ApiResponse.ok(userService.listUsers());
    }

    @Operation(summary = "修改密码")
    @PostMapping("/update-password")
    public ApiResponse<?> updatePassword(
            @RequestParam Long currentUserId,
            @RequestBody PasswordUpdateRequest request) {

        try {
            userService.updatePassword(
                    currentUserId,
                    request.getOldPassword(),
                    request.getNewPassword()
            );

            return ApiResponse.ok("密码修改成功");
        } catch (RuntimeException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
