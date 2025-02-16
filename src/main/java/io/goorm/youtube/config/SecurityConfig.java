package io.goorm.youtube.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.goorm.youtube.security.RestAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 기존 API 허용 설정들 유지
                        .requestMatchers(HttpMethod.GET, "/api").permitAll()
                        .requestMatchers("/api/join", "/api/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 정적 리소스 접근 허용 확장
                        .requestMatchers("/assets/**", "/upload/**").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/favicon.ico", "/error").permitAll()

                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("memberId")
                        .passwordParameter("memberPw")
                        // 로그인 성공 핸들러 추가
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            Map<String, Object> result = new HashMap<>();
                            result.put("success", true);
                            result.put("message", "로그인 성공");
                            result.put("memberId", authentication.getName());

                            new ObjectMapper().writeValue(response.getWriter(), result);
                        })
                        // 로그인 실패 핸들러 추가
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            Map<String, Object> result = new HashMap<>();
                            result.put("success", false);
                            result.put("message", "로그인 실패: " + exception.getMessage());

                            new ObjectMapper().writeValue(response.getWriter(), result);
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            Map<String, Object> result = new HashMap<>();
                            result.put("success", true);
                            result.put("message", "로그아웃 성공");

                            new ObjectMapper().writeValue(response.getWriter(), result);
                        })
                        .deleteCookies("JSESSIONID") // 세션 쿠키 삭제
                        .invalidateHttpSession(true) // HTTP 세션 무효화
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}