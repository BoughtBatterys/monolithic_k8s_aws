# dockerhub에서 베이스 이미지를 가져올거임 ㅇㅇ 아래 처럼 이름을 적어주면 알아서 찾음
# a jdk image pull
FROM eclipse-temurin:17-jdk-alpine

# jar
ARG JAR_FILE=build/libs/*.jar

# build/libs에 있는 .jar파일을 루트에 backend.jar라는 이름으로 복사
COPY ${JAR_FILE} ./backend.jar

# run : java -jar 경로/.jar파일명
ENTRYPOINT [ "java", "-jar", "./backend.jar" ]