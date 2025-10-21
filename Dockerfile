FROM openjdk:17-jdk-slim

# OpenTelemetry Java 에이전트 다운로드
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

# JAR 파일 복사 및 실행
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} product-0.0.1.jar

# OpenTelemetry 에이전트 설정(JVM 시작 시 agent 자동 주입)
ENV JAVA_TOOL_OPTIONS="-javaagent:/otel/opentelemetry-javaagent.jar"

# OTEL 환경 변수
# 실제 값은 Compose나 K8s에서 override할 예정
ENV OTEL_SERVICE_NAME=product-service
ENV OTEL_EXPORTER_OTLP_PROTOCOL=grpc
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://tempo:4317"
ENV OTEL_RESOURCE_ATTRIBUTES="env=dev;version=1.0.0"

# Tempo로 trace만 전송될 수 있게
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_TRACES_EXPORTER=otlp
# 모든 요청 기록
ENV OTEL_TRACES_SAMPLER=always_on



ENTRYPOINT ["java","-javaagent:/otel/opentelemetry-javaagent.jar", "-jar","/product-0.0.1.jar"]
