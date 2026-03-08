/*
 Navicat Premium Dump SQL (极简版，仅保留 3 篇短文及其关联数据)
 Source Server         : blog
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : 8.135.237.0:3306
 Source Schema         : blog
 Date: 2026-03-08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for label
-- ----------------------------
DROP TABLE IF EXISTS `label`;
CREATE TABLE `label`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `writing_id` int NOT NULL COMMENT '文章id集合',
  `label_name` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名字',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of label (只关联 writing_id=40,43,45)
-- ----------------------------
INSERT INTO `label` VALUES (68, 40, 'docker');
INSERT INTO `label` VALUES (69, 40, '服务器');
INSERT INTO `label` VALUES (74, 43, 'springboot');
INSERT INTO `label` VALUES (76, 45, 'nginx');
INSERT INTO `label` VALUES (77, 45, '服务器');

-- ----------------------------
-- Table structure for star
-- ----------------------------
DROP TABLE IF EXISTS `star`;
CREATE TABLE `star`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '用户id',
  `writing_id` int NOT NULL COMMENT '文章id',
  `status` int NOT NULL DEFAULT 2 COMMENT '点赞状态( 1为已点赞，2为未点赞）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of star (只关联 writing_id=40,43,45)
-- ----------------------------
INSERT INTO `star` VALUES (17, '0', 40, 1, '2024-09-08 20:07:09');
INSERT INTO `star` VALUES (18, '0', 40, 1, '2024-09-12 18:13:45');
INSERT INTO `star` VALUES (23, '0', 43, 1, '2025-03-24 14:56:28');
INSERT INTO `star` VALUES (24, '0', 43, 2, '2025-04-07 22:06:36');
INSERT INTO `star` VALUES (26, '0', 45, 1, '2025-05-17 11:01:48');
INSERT INTO `star` VALUES (37, 'device_nndn85tu3', 45, 1, '2025-11-12 18:56:16');
INSERT INTO `star` VALUES (38, 'device_nndn85tu3', 43, 1, '2025-11-12 22:08:53');

-- ----------------------------
-- Table structure for writing
-- ----------------------------
DROP TABLE IF EXISTS `writing`;
CREATE TABLE `writing`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `content` varchar(10000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章内容',
  `star` int NULL DEFAULT 0 COMMENT '点赞量',
  `create_time` datetime NOT NULL COMMENT '文章发布时间',
  `update_time` datetime NOT NULL COMMENT '文章修改时间',
  `label_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `summary` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of writing (仅保留 id=40,43,45)
-- ----------------------------
INSERT INTO `writing` VALUES (40, '服务器docker容器和镜像重新构建', '# 服务器docker容器和镜像重新构建\n```\n按顺序执行下面代码\n\n1、docker ps 找到容器id\n\n2、docker rmi -f [容器id]  //强制删除容器和镜像\n\n3、docker ps\n   docker images\n查询是否还存在java的镜像和容器\n\n4、重新上传完java的相关文件后，重新执行\n  docker build -t [容器名] .\n\n5、一般来说就是重新部署好了，但是如果报错，就根据报错给你的id重新执行\n  docker rmi -f [报错给你的id]\n原因：镜像或者容器没删干净\n```\n      \n', 6, '2024-09-08 19:59:08', '2025-12-27 20:18:10', '[docker, 服务器]', '提供服务器上Docker容器和镜像重新构建的完整操作流程，包括删除旧容器、构建新镜像以及常见问题的解决方法。', 10);
INSERT INTO `writing` VALUES (43, 'springboot中依赖存在但显示无法找到', '# springboot中依赖存在但显示无法找到\n- 昨天有个同学问了我个问题，说自己的项目里依赖明明已经存在，但是就是无法生效，我一开始忘记了怎么解决，后面想了以下，就想起来我也遇到这样的问题，我就想着把它记录下来，以后如果忘记了也可以很快找到解决方法。\n## 解决方法\n1. 点击idea右侧里的maven，然后找到你的主项目，先点击clean，之后点击package。\n2. 找到文件里的“使缓存失效”\n    ![](https://w--blog.oss-cn-guangzhou.aliyuncs.com/b34d45a1-a1ea-406f-b928-351259347461.png)\n3. 可以全部勾选\n     ![](https://w--blog.oss-cn-guangzhou.aliyuncs.com/92af69d1-5725-4465-b8ef-3a07ff4d97aa.png)\n4. 点击失效并重启', 13, '2025-03-22 10:57:37', '2026-03-08 21:37:44', '[springboot]', '解决SpringBoot项目中依赖存在但无法生效的问题，提供Maven清理打包和IDEA缓存失效重启的完整解决方案。', 4);
INSERT INTO `writing` VALUES (45, '母亲节快乐网页使用nginx配置', '# 母亲节快乐网页使用nginx配置\n## 昨天想着今天不是母亲节，就想着做个东西给我的妈妈，毕竟是学计算机的\n## 1、先写好前端的代码，也没有多复杂的，就不展示了\n## 2、然后是上传代码到服务器上，在nginx文件下新建文件夹happy，在happy放入前端代码\n## 3、接下来修改nginx.conf的配置,在原来的nginx.conf文件里添加了下面的代码，server与原来就有的server平级，想看原来的nginx.conf文件访问下面的网址\n```\nhttp://8.135.237.0/lookWriting?id=39\n```\n```\nserver {\n        listen       端口号;\n        server_name 8.135.237.0;\n\n        location / {\n            root   /usr/share/nginx/happy;\n            index  happy.html happy.htm;\n            # 解决history路由模式刷新404\n            try_files $uri $uri/ /happy.html;\n        }\n\n        error_page   500 502 503 504  /50x.html;\n        location = /50x.html {\n            root /usr/share/nginx/happy;\n        }\n    }\n```\n## 4、删除原有的镜像和容器，重新执行nginx命令，我在这一步的时候卡住了一下，因为我的命令写错了，我忘记去添加一个主机端口号去映射容器端口号，正确的命令应该是下面的这一条：\n```\ndocker run -d --name nginx --network blog -v /usr/local/nginx/html:/usr/share/nginx/html -v /usr/local/nginx/happy:/usr/share/nginx/happy -v /usr/local/nginx/nginx.conf:/etc/nginx/nginx.conf -v  /usr/local/nginx/https:/usr/share/nginx/https -p 主机端口号:容器端口号 -p 443:443 -p 主机端口号:容器端口号 nginx:latest\n```\n## 5、最后就部署好了，访问8.135.237.0:1000 就可以看到我祝妈妈母亲节快乐的网页了，祝所有的母亲母亲节快乐。\n', 16, '2025-05-11 20:00:56', '2026-02-11 03:36:59', '[nginx, 服务器]', '记录为母亲节制作祝福网页并使用nginx配置多站点部署的过程，包括配置文件修改、端口映射和容器重启等步骤。', 6);

SET FOREIGN_KEY_CHECKS = 1;