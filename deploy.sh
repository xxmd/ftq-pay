#!/bin/bash

# 检查是否安装了 Git
if ! command -v git &> /dev/null; then
    echo "❌ Git 未安装，请先安装 Git。"
    exit 1
fi

# 检查是否安装 JDK（java 命令）
if ! command -v java &> /dev/null; then
    echo "❌ JDK 未安装，请先安装 JDK。"
    exit 1
fi

# 检查是否安装了 Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven 未安装，请先安装 Maven。"
    exit 1
fi

# 执行 Git 拉取代码
echo "✅ Git 已安装，正在拉取最新代码..."
git pull

# 执行 Maven 构建
echo "🚧 开始构建项目..."
mvn clean package -DskipTests

# 获取 Maven 项目名称和版本
ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# 构建docker镜像
docker build -t ${ARTIFACT_ID}:${VERSION} .

# 停止并删除已存在的容器（如果存在）
if docker ps -a --format '{{.Names}}' | grep -Eq "^${ARTIFACT_ID}$"; then
  echo "停止并删除已有容器：$ARTIFACT_ID"
  docker stop "$ARTIFACT_ID"
  docker rm "$ARTIFACT_ID"
fi

# 启动新容器
echo "启动容器：${ARTIFACT_ID}:${VERSION}"
docker run -d --name "$ARTIFACT_ID" -p 4000:8080 "${ARTIFACT_ID}:${VERSION}"
