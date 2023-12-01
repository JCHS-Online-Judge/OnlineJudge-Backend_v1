## Online Judge

## 채점 이미지 설정

채점 이미지를 설정하기 위해 Dockerfile을 수정합니다. Dockerfile은 채점 환경을 구성하는 설정 파일입니다. 아래 부분을 편집합니다.

```dockerfile
ENV MONGODB_HOST host.docker.internal
ENV MONGODB_PORT 27017
ENV MONGODB_USERNAME (USERNAME)
ENV MONGODB_PASSWORD (PASSWORD)
ENV MONGODB_AUTH_DATABASE admin
ENV MONGODB_DATABASE OnlineJudge
ENV MONGODB_JUDGE_COLLECTION judgeHistory
```

## 채점 이미지 생성

온라인 저지 시스템에서 사용할 채점 이미지를 생성하는 과정입니다. Docker를 활용하여 이미지를 빌드하고, 채점 환경을 구성합니다. 아래는 Gradle을 사용하여 Dockerfile을 빌드하는 명령어입니다.

```bash
./gradlew :JudgeContainer:buildImage
```

## 웹서버 설정

웹 서버를 구성하기 위해 application.yml 파일을 수정합니다.

```yaml
server:
  port: 80

spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      username: (USERNAME)
      password: (PASSWORD)
      authentication-database: admin
      database: OnlineJudge

app:
  jwt:
    secret: (JWT 비밀키 - 256바이트 이상 문자열)
    expirationDay: 30
  judge:
    containerName: oj-judge-container
    threadPoolSize: 4
```

## 웹서버 실행

웹 서버를 실행하기 위해 Gradle을 사용합니다.

```bash
./gradlew :WebServer:bootRun
```

## 웹서버 빌드

웹 서버를 빌드하기 위해 Gradle을 사용합니다.

```bash
./gradlew :WebServer:build
```
