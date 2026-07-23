package hgc.flowsyncapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类 —— 生成、解析、校验 Token
 */
@Component
public class JwtUtils {

    private final SecretKey key;
    private final long expiration;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration}") long expiration) {
        this.key = deriveKey(secret);
        this.expiration = expiration;
    }

    /**
     * 从配置的明文字符串派生 HMAC 密钥（确保 ≥256 位）
     */
    private static SecretKey deriveKey(String secret) {
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        // 如果原始密钥不足 32 字节（256 位），用 SHA-256 哈希派生
        if (raw.length < 32) {
            try {
                raw = MessageDigest.getInstance("SHA-256").digest(raw);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 不可用", e);
            }
        }
        return Keys.hmacShaKeyFor(raw);
    }

    /**
     * 生成 JWT Token
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    /**
     * 解析 Token，返回 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 校验 Token 是否合法
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
