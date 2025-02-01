# 21 버전의 Java JDK를 사용하기 위한 베이스 이미지
FROM eclipse-temurin:21-jdk-alpine

# 빌드된 jar 파일을 이미지 내에 app.jar로 추가
ADD ./build/libs/youtube-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너가 시작될 때 Java 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]