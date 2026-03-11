# Docker 部署教程（前后端一体）

本教程使用 Docker Compose 一键启动前后端（含 SQLite 数据卷）。

## 1. 前置条件

- 已安装 Docker 与 Docker Compose
- 服务器端口开放：
  - 9919（后端 API）
  - 8081（前端管理台）

## 2. 目录结构要求

在项目根目录下执行：

```
/ai-wechat-bot
  /backend
  /frontend
  docker-compose.yml
  config.json
  /data
  /logs
```

`data/` 和 `logs/` 用于 SQLite 数据与日志持久化。

## 3. 一键启动

在项目根目录执行：

```bash
docker compose up -d --build
```

首次启动会构建镜像，耗时取决于网络速度。

## 4. 访问地址

- 管理台：`http://<服务器IP>:8081/`
- 健康检查：`http://<服务器IP>:9919/api/health`

默认账号密码：

- 用户名：`admin`
- 密码：`123456`

## 5. 修改默认账号

编辑 `docker-compose.yml` 中的环境变量：

```
ADMIN_AUTH_USERNAME=你的账号
ADMIN_AUTH_PASSWORD=你的密码
```

修改后执行：

```bash
docker compose up -d --build
```

## 6. 停止与重启

```bash
docker compose down
```

```bash
docker compose up -d --build
```

## 7. 常见问题

1. **前端无法请求后端**
   - 这里使用 Nginx 反向代理 `/api` 到后端容器，前后端同域，不会产生 CORS。

2. **数据库无法创建**
   - 确保 `data/` 目录存在并可写。

3. **需要绑定公网地址**
   - 默认已映射 `8081` 与 `9919`，确保安全组/防火墙放行。
