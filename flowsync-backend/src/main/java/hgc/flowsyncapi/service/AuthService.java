package hgc.flowsyncapi.service;

import java.util.Map;

public interface AuthService {

    /**
     * 用户登录 —— 明文比对密码
     * @return 包含用户信息（密码置空）和 token 的 Map
     */
    Map<String, Object> login(String username, String password);
}
