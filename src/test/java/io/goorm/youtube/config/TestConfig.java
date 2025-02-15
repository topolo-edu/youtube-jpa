package io.goorm.youtube.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.goorm.youtube.commom.util.FileUploadUtil;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public FileUploadUtil fileUploadUtil() {
        FileUploadUtil fileUploadUtil = new FileUploadUtil();
        fileUploadUtil.setUploadDir("src/test/resources/uploads");
        return fileUploadUtil;
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
