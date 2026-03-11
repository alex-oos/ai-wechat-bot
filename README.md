# ai-wechat-bot (frontend + backend)

本仓库已整理为前后端分离结构：

- `backend/`：Spring Boot 后端服务（提供管理端登录、微信二维码登录 API）
- `frontend/`：Vue3 + Vite 管理台（AstrBot 风格 UI）

## 启动后端

```bash
cd backend
mvn -DskipTests clean package
java -jar target/ai-wechat-bot.jar
```

健康检查：

- `http://localhost:9919/api/health`

## 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问：

- `http://localhost:5173/`

说明：

- 前端已配置 Vite 代理：`/api -> http://localhost:9919`

## Docker 部署（前后端一体）

使用 Docker Compose 一键启动前后端：

```bash
docker compose up -d --build
```

默认访问：

- 管理台：`http://localhost:8081/`
- 后端 API：`http://localhost:9919/`

完整教程见：`docs/DEPLOY_DOCKER.md`
