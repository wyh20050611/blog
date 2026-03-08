# demo-blog

## 项目介绍

demo-blog 是一个基于 Spring Boot 和 Vue 3 开发的个人技术博客系统，包含后端 API 和前端页面。

## 技术栈

- **后端**：Spring Boot、MyBatis、Redis、JWT
- **前端**：Vue 3、Vite、Pinia、Axios
- **数据库**：MySQL

## 项目结构

```
demo-blog/
├── blog-common/        # 公共模块
├── blog-pojo/          # 实体类模块
├── blog-server/        # 后端服务模块
└── pom.xml             # 项目依赖配置

vue-blog/
├── public/             # 静态资源
├── src/                # 源代码
│   ├── api/            # API 调用
│   ├── components/     # 组件
│   ├── router/         # 路由
│   ├── stores/         # 状态管理
│   ├── utils/          # 工具类
│   └── views/          # 页面
└── package.json        # 前端依赖配置
```

## 后端配置

由于 `.gitignore` 文件屏蔽了配置文件，下载项目后需要在 `d:\Project\blog\demo-blog\blog-server\src\main\resources` 目录下添加以下两个配置文件：

### 1. application.yml

```yaml
server:
  port: 8080

spring:
  profiles:
    active: dev
  main:
    allow-circular-references: true
  datasource:
    druid:
      driver-class-name: ${blog.datasource.driver-class-name}
      url: jdbc:mysql://${blog.datasource.host}:${blog.datasource.port}/${blog.datasource.database}?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
      username: ${blog.datasource.username}
      password: ${blog.datasource.password}
  servlet:
    multipart:
      enabled: true
      max-file-size: 20MB
      max-request-size: 20MB
  redis:
    # host: redis
    host: localhost
    port: 6379
    database: 1

mybatis:
  #mapper配置文件
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.example.rearend.entity
  configuration:
    #开启驼峰命名
    map-underscore-to-camel-case: true

logging:
  level:
    com:
      example.rearend:
        mapper: debug
        service: info
        controller: info

sky:
  jwt:
    # 设置jwt签名加密时使用的秘钥
    admin-secret-key: itcast
    # 设置jwt过期时间
    admin-ttl: 86400000
    # 设置前端传递过来的令牌名称
    token: token
  alioss:
    endpoint: ${blog.alioss.endpoint}
    access-key-id: ${blog.alioss.access-key-id}
    access-key-secret: ${blog.alioss.access-key-secret}
    bucket-name: ${blog.alioss.bucket-name}
```

### 2. application-dev.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: xxx
    password: xxx
  redis:
    host: localhost
    port: 6379
    password: xxx

jwt:
  secret-key: xxx
  expire-time: 7200

alioss:
  endpoint: xxx
  access-key-id: xxx
  access-key-secret: xxx
  bucket-name: xxx
```

> 注意：请将配置文件中的 `xxx` 替换为实际的配置值。

## 前端配置

### 项目安装

```bash
# 进入前端目录
cd vue-blog

# 安装依赖
npm install
```

浏览器会自动打开，若是要进入文章编写页面，则访问 `localhost:5173/writing`。

## 数据库配置

1. 创建名为 `blog` 的数据库
2. 执行 `sql/blog.sql` 文件中的 SQL 语句，创建数据表

## 运行项目

### 1. 启动后端服务

- 运行 `blog-server` 模块中的 `BlogServerApplication.java` 类
- 后端服务默认运行在 `http://localhost:8080`

### 2. 启动前端服务

```bash
# 启动开发服务器
npm run copy:vditor
npm run dev
```
- 前端服务默认运行在 `http://localhost:5173`

## 项目展示

### 首页

![首页](https://w--blog.oss-cn-guangzhou.aliyuncs.com/a7dd42fe-6787-4c53-91c6-8a8f41e71846.png)


### 文章详情页

![文章详情页](https://w--blog.oss-cn-guangzhou.aliyuncs.com/3e1275af-998f-4462-b137-7c197a305bb1.png)

### 文章编写页

![文章编写页](https://w--blog.oss-cn-guangzhou.aliyuncs.com/c1a79f22-3bcf-43e4-941a-43c4aed4f2bd.png)

## 注意事项

1. 确保 MySQL 和 Redis 服务已启动
2. 配置文件中的敏感信息（如数据库密码、JWT 密钥等）请根据实际情况修改
3. 前端开发服务器默认运行在 `localhost:5173`，后端服务默认运行在 `localhost:8080`
4. 文章编写页面地址：`localhost:5173/writing`

