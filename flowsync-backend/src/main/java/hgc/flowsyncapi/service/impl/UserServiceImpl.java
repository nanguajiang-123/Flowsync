package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.mapper.UserMapper;
import hgc.flowsyncapi.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listUsers() {
        List<User> users = userMapper.selectList(null);

        // 返回用户列表时，不暴露密码哈希
        users.forEach(user -> user.setPassword(null));

        return users;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);

        if (user != null) {
            user.setPassword(null);
        }

        return user;
    }

    @Override
    public User register(
            String username,
            String password,
            String confirmPassword,
            String realName,
            String role,
            String phone,
            String email) {

        if (password == null || confirmPassword == null) {
            throw new RuntimeException("密码不能为空");
        }

        if (!password.equals(confirmPassword)) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        if (password.length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }

        Set<String> allowedRoles =
                new HashSet<>(Arrays.asList("负责人", "成员"));

        if (!allowedRoles.contains(role)) {
            throw new RuntimeException("角色只能为：负责人、成员");
        }

        LambdaQueryWrapper<User> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(User::getUsername, username);

        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);

        // 将明文密码转换为 BCrypt 哈希后再存入数据库
        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setRealName(realName);
        user.setRole(role);
        user.setPhone(phone);
        user.setEmail(email);

        userMapper.insert(user);

        // 返回前删除密码字段
        user.setPassword(null);

        return user;
    }

    @Override
    public void updatePassword(
            Long userId,
            String oldPassword,
            String newPassword) {

        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (oldPassword == null || newPassword == null) {
            throw new RuntimeException("密码不能为空");
        }

        // 校验用户输入的旧密码与数据库哈希是否匹配
        if (!passwordEncoder.matches(
                oldPassword,
                user.getPassword())) {

            throw new RuntimeException("旧密码错误");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }

        if (passwordEncoder.matches(
                newPassword,
                user.getPassword())) {

            throw new RuntimeException("新密码不能与旧密码相同");
        }

        // 对新密码重新生成 BCrypt 哈希
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userMapper.updateById(user);
    }
}