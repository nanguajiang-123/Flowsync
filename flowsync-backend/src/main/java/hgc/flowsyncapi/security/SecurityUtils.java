package hgc.flowsyncapi.security;

/**
 * 持有当前请求的用户信息的 ThreadLocal
 * <p>
 * 由 JwtInterceptor 在请求进入时设置，请求结束后清除。
 */
public class SecurityUtils {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();
    private static final ThreadLocal<String> currentRole = new ThreadLocal<>();

    public static void set(Long userId, String username, String role) {
        currentUserId.set(userId);
        currentUsername.set(username);
        currentRole.set(role);
    }

    public static void clear() {
        currentUserId.remove();
        currentUsername.remove();
        currentRole.remove();
    }

    /** 获取当前登录用户ID */
    public static Long getCurrentUserId() {
        Long id = currentUserId.get();
        if (id == null) {
            throw new RuntimeException("未登录或 token 已过期");
        }
        return id;
    }

    /** 获取当前登录用户名 */
    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    /** 获取当前登录用户角色 */
    public static String getCurrentRole() {
        return currentRole.get();
    }

    // ==================== 权限校验 ====================

    /** 校验当前用户是否是"负责人"角色 */
    public static void requireLeaderRole() {
        if (!"负责人".equals(getCurrentRole())) {
            throw new RuntimeException("权限不足：仅项目负责人可操作");
        }
    }

    /** 校验当前用户是否是目标 owner（用于项目编辑/删除） */
    public static void requireOwner(Long ownerId) {
        if (!getCurrentUserId().equals(ownerId)) {
            throw new RuntimeException("权限不足：您不是该项目的负责人");
        }
    }

    /** 校验当前用户是否是目标创建者（用于任务编辑/删除） */
    public static void requireCreator(Long creatorId) {
        if (!getCurrentUserId().equals(creatorId)) {
            throw new RuntimeException("权限不足：您不是该任务的创建者");
        }
    }
}
