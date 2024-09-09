package kr.okku.server.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.clientconfig.Http2ClientFeignConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportAutoConfiguration({FeignAutoConfiguration.class, Http2ClientFeignConfiguration.class})
public class FeignClientConfig {
}