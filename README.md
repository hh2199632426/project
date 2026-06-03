# 赛车答题PK游戏

基于 Vue 3 + Java（Spring Boot）+ MySQL 的双人同屏PK赛车答题游戏。

## 项目结构

- `frontend/`: Vue 3前端项目
- `backend-java/`: Java后端服务（Spring Boot + MyBatis-Plus + MySQL 题库）
- `backend/`: 旧版 Node.js 后端（已不作为主后端）

## 快速开始

### 1. 初始化数据库

执行 `backend-java/sql/schema.sql` 创建库表并插入初始题目：

```bash
mysql -u root -p < backend-java/sql/schema.sql
```

### 2. 配置后端连接参数

后端连接参数已直接写在 `backend-java/src/main/resources/application.yml` 中，如需调整数据库地址、账号、密码，直接修改该文件即可。

### 3. 启动后端服务

```bash
cd backend-java
mvn spring-boot:run
```
后端服务将运行在 `http://localhost:3000`

### 4. 启动前端项目

```bash
cd frontend
npm install
npm run dev
```
前端项目将运行在 `http://localhost:5173`

## 功能说明

- **双人同屏PK**: 左右分屏，两人同时答题竞速。
- **赛车答题联动**: 答对加速，答错减速。
- **动态效果**: 赛车移动，树木后移模拟速度感。
- **数据库题目**: 题目从MySQL数据库读取，接口为 `/api/questions`。

## 技术栈

- **Frontend**: Vue 3, Pinia, Axios, Vite
- **Backend**: Java 21, Spring Boot 3, MyBatis-Plus, MySQL
