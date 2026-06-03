#!/usr/bin/env bash

set -euo pipefail

REPO_URL="https://gitee.com/eat-vermicelli/game.git"
BRANCH="master"
APP_DIR="/app/game"

if ! command -v docker >/dev/null 2>&1; then
  echo "docker 未安装，请先安装 docker 与 docker compose"
  exit 1
fi

if [[ "${REPO_URL}" == *"your-account/your-repo.git" ]]; then
  echo "请先设置正确的 REPO_URL（可通过环境变量传入）"
  exit 1
fi

if [ ! -d "${APP_DIR}/.git" ]; then
  echo "首次部署，克隆仓库到 ${APP_DIR}"
  mkdir -p "${APP_DIR}"
  git clone -b "${BRANCH}" "${REPO_URL}" "${APP_DIR}"
fi

cd "${APP_DIR}"
echo "拉取最新代码: ${BRANCH}"
git fetch --all --prune
git checkout "${BRANCH}"
git reset --hard "origin/${BRANCH}"

if [ ! -f ".env" ]; then
  echo ".env 不存在，自动从 .env.example 复制，请先补全数据库账号密码"
  cp .env.example .env
  echo "请编辑 ${APP_DIR}/.env 后再次执行本脚本"
  exit 0
fi

echo "重建并启动容器"
docker compose down
docker compose up -d --build

echo "清理未使用镜像与构建缓存"
docker image prune -f

echo "部署完成"
echo "用户端访问地址：http://119.29.221.211:5432"
echo "管理端访问地址：http://119.29.221.211:5432/admin"
