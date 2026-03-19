package com.campus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer; // 记得导入这个
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 允许 iframe 加载 (关键修改！！！)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // 允许同源 iframe
                )

                // 3. 配置路径权限
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/", "/login", "/register", "/user/login", "/user/register").permitAll()
                        .requestMatchers("/captcha/generate", "/item/search", "/item/detail/**").permitAll()
                        // 放行后台页面和接口 (开发阶段建议先全部放行，上线再收紧)
                        .requestMatchers("/admin/**", "/mainAdministrator.html").permitAll()
                        .anyRequest().permitAll()
                )

                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"));

        return http.build();
    }
}
