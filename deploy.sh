#!/bin/bash
set -euxo pipefail

NGINX_CONF="/etc/nginx/nginx.conf"

# 检查依赖函数
check_command() {
    if ! command -v "$1" &> /dev/null; then
        echo "❌ $1 未安装，请先安装 $1。"
        exit 1
    else
        echo "✅ $1 已安装"
    fi
}

echo "=== 依赖检查 ==="
check_command git
check_command nginx
check_command java
check_command mvn
check_command docker

echo "=== 拉取最新代码 ==="
git pull

echo "=== 构建 Maven 项目 ==="
mvn clean package -DskipTests

echo "=== 获取项目 artifactId 和 version ==="
ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout | tr -d '\r\n')
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout | tr -d '\r\n')

echo "项目名称：$ARTIFACT_ID"
echo "版本号：$VERSION"

echo "=== 构建 Docker 镜像 ==="
docker build -t "${ARTIFACT_ID}:${VERSION}" .

# 停止并删除已有容器
if docker ps -a --format '{{.Names}}' | grep -Eq "^${ARTIFACT_ID}$"; then
    echo "停止并删除已有容器：$ARTIFACT_ID"
    docker stop "$ARTIFACT_ID"
    docker rm "$ARTIFACT_ID"
fi

echo "=== 启动新容器 ==="
container_id=$(docker run -d --name "$ARTIFACT_ID" -P "${ARTIFACT_ID}:${VERSION}")
if [ -z "$container_id" ]; then
    echo "❌ Docker 容器启动失败"
    exit 1
fi

echo "等待 Docker 端口映射生效..."
HOST_PORT=""
for i in {1..5}; do
    HOST_PORT=$(docker port "$ARTIFACT_ID" 8080/tcp | head -n1 | sed 's/.*://')
    if [ -n "$HOST_PORT" ]; then
        break
    fi
    sleep 1
done

if [ -z "$HOST_PORT" ]; then
    echo "❌ 获取 Docker 宿主机端口失败"
    exit 1
fi

echo "Docker 宿主机端口：$HOST_PORT"

# 生成动态 nginx 配置
LOCATION_BLOCK=$(cat <<EOF
location /$ARTIFACT_ID/ {
    proxy_pass http://localhost:$HOST_PORT/$ARTIFACT_ID/;

    proxy_set_header Host \$host;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;

    # 可选：处理重定向（如果后端有 Location 头跳转）
    proxy_redirect default;
}
EOF
)


echo "=== 更新 nginx 配置 ==="
if grep -q "location /$ARTIFACT_ID/" "$NGINX_CONF"; then
    echo "location /$ARTIFACT_ID/ 已存在，跳过添加"
else
    echo "location /$ARTIFACT_ID/ 不存在，添加配置..."

    # 备份 nginx 配置
    cp "$NGINX_CONF" "${NGINX_CONF}.bak.$(date +%F_%T)"

    # 插入 location 到第一个 server 块内
    awk -v block="$LOCATION_BLOCK" '
      # 记录是否刚匹配到 location /
      {
        print
        if ($0 ~ /^[[:space:]]*location[[:space:]]+\/[[:space:]]*\{[[:space:]]*$/) {
          getline next_line
          print next_line
          # 紧跟着 location / {} 之后插入 block
          print block
          next
        }
      }
    ' "$NGINX_CONF" > "${NGINX_CONF}.tmp" && mv "${NGINX_CONF}.tmp" "$NGINX_CONF"

    echo "已添加 location 配置"
fi

echo "=== 重载 nginx 配置 ==="
# 如果需要 sudo 请改为 sudo nginx -s reload
if nginx -s reload; then
    echo "✅ nginx 重载成功"
else
    echo "❌ nginx 重载失败，请手动检查"
fi

echo "=== 部署完成 ==="
