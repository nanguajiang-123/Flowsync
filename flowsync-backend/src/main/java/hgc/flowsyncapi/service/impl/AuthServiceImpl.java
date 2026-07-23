package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.mapper.UserMapper;
import hgc.flowsyncapi.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<String, Object> login(
            String username,
            String password) {

        LambdaQueryWrapper<User> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.eq(User::getUsername, username);

        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 明文密码与数据库中的 BCrypt 哈希进行匹配
        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException("密码错误");
        }

        // 返回给前端前清除密码字段
        user.setPassword(null);

        // 暂时保留原来的简单 token，JWT 后续再实现
        String token = UUID.randomUUID()
                .toString()
                .replace("-", "");

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);

        return result;
    }
}