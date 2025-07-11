# 使用官方 OpenJDK 镜像
FROM openjdk:11-jre-slim

# 添加时区环境变量，亚洲，上海
ENV TimeZone=Asia/Shanghai
# 使用软连接，并且将时区配置覆盖/etc/timezone
RUN ln -snf /usr/share/zoneinfo/$TimeZone /etc/localtime && echo $TimeZone > /etc/timezone

# 设置工作目录
WORKDIR /app

# 复制构建好的 JAR 文件到容器中
COPY target/ftq-pay-1.0.0.jar app.jar

# 设置环境变量（默认为开发环境）
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露端口
EXPOSE 8080

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]