# Multi-stage build for optimization
FROM gradle:8.5-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 설정 파일들 먼저 복사 (캐시 최적화)
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/

# 소스 코드 복사
COPY src/ src/

# 애플리케이션 빌드 (테스트 제외)
RUN gradle clean build -x test --no-daemon

# Runtime stage
FROM amazoncorretto:17-alpine

# 애플리케이션 실행을 위한 디렉토리 생성
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행을 위한 사용자 생성 (보안)
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# 포트 노출
EXPOSE 8080

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]