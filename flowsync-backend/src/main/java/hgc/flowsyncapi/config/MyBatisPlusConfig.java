package hgc.flowsyncapi.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("hgc.flowsyncapi.mapper")
public class MyBatisPlusConfig {
}
