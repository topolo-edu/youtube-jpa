package io.goorm.youtube.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()    // 모든 경로 허용
                )
                .csrf(csrf -> csrf.disable())             // CSRF 보호 비활성화
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())  // X-Frame-Options 비활성화
                )
                .formLogin(form -> form.disable())          // 폼 로그인 비활성화
                .httpBasic(basic -> basic.disable());       // HTTP Basic 인증 비활성화

        return http.build();
    }

}
