package hgc.flowsyncapi.service;

import hgc.flowsyncapi.entity.User;

import java.util.List;

public interface UserService {

    /** 获取全部用户（不含密码） */
    List<User> listUsers();

    /** 根据 ID 查询用户 */
    User getUserById(Long id);

    /** 修改密码 */
    void updatePassword(Long userId, String oldPassword, String newPassword);
}
