package hgc.flowsyncapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc / Swagger 配置 —— 添加 JWT Bearer 鉴权按钮
 */
@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI flowSyncOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FlowSync API")
                        .description("FlowSync 小组任务协同管理系统")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .name("Bearer")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("在此输入 JWT Token（登录接口返回的 token 值）")));
    }
}
