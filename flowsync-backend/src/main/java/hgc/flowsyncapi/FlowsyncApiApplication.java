package hgc.flowsyncapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FlowSync 启动类
 * <p>
 * 启动时会自动加载项目根目录下的 {@code .env} 文件，
 * 将其中的键值对注入到系统属性，供 Spring 的 {@code ${...}} 占位符读取。
 * {@code .env} 文件不纳入版本控制（已在 .gitignore 中排除）。
 */
@SpringBootApplication
public class FlowsyncApiApplication {

    public static void main(String[] args) {
        // ===== 在 Spring 启动之前加载 .env 文件 =====
        // .systemProperties() 会把 .env 中的每一项设为 Java 系统属性
        // .ignoreIfMissing() 表示如果 .env 不存在也不报错（生产环境通常通过真正的环境变量注入）
        Dotenv.configure()
                .systemProperties()
                .ignoreIfMissing()
                .load();

        SpringApplication.run(FlowsyncApiApplication.class, args);
    }
}
