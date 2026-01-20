# 멀티 스테이지 빌드
FROM eclipse-temurin:17-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 래퍼와 빌드 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

# 실행 권한 부여 및 빌드
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon


# 런타임 스테이지
FROM eclipse-temurin:17-jre

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]