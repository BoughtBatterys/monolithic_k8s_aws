# # dockerhub에서 베이스 이미지를 가져올거임 ㅇㅇ 아래 처럼 이름을 적어주면 알아서 찾음
# # a jdk image pull
# FROM eclipse-temurin:17-jdk-alpine

# # jar
# ARG JAR_FILE=build/libs/*.jar

# # build/libs에 있는 .jar파일을 루트에 backend.jar라는 이름으로 복사
# COPY ${JAR_FILE} ./backend.jar

# # run : java -jar 경로/.jar파일명
# ENTRYPOINT [ "java", "-jar", "./backend.jar" ]


# 실무적으로 아래와 같이 build와 run으로 나눠서 작성하는 것을 권장한다. 
# build stage (JDK + gradle) -> jar 파일 생성
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies

COPY src src
RUN ./gradlew bootJar

# rutime stage (jre) -> jar
# jre는 jdk보다 가볍지만 jar파일을 실행하는데 필요한 환경은 제공한다.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]