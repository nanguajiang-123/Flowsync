package hgc.flowsyncapi.config;

import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PasswordMigrationRunner
        implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        List<User> users =
                userMapper.selectList(null);

        for (User user : users) {

            String storedPassword =
                    user.getPassword();

            /*
             * 只有仍为明文的密码才迁移。
             * 已经是 BCrypt 哈希的密码不能重复加密。
             */
            if (storedPassword != null
                    && !isBcryptHash(storedPassword)) {

                String encodedPassword =
                        passwordEncoder.encode(
                                storedPassword
                        );

                user.setPassword(encodedPassword);

                userMapper.updateById(user);
            }
        }
    }

    private boolean isBcryptHash(String password) {

        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}