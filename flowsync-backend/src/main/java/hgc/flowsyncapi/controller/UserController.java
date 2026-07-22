package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.dto.PasswordUpdateRequest;
import hgc.flowsyncapi.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 获取全部用户列表（不含密码） */
    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.ok(userService.listUsers());
    }

    /** 修改密码 */
    @PostMapping("/update-password")
    public ApiResponse<?> updatePassword(@RequestBody PasswordUpdateRequest request) {
        try {
            userService.updatePassword(request.getUserId(), request.getOldPassword(), request.getNewPassword());
            return ApiResponse.ok("密码修改成功");
        } catch (RuntimeException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
