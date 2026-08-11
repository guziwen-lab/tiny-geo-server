# tiny-geo-server

## 项目说明

* 处理图层叠加，支持各种图层叠加类型，详情见com.supermap.analyze.enums.AnalysisType。
* 导入导出 GDB/Shp 利用 GDAL，服务器需要先安装GDAL。但是如果数据库里已有数据集，用不到导入导出则无需安装。
* 提供了代码生成器（基于人人开源），只需编写30%左右代码，其余的代码交给系统自动生成，可快速完成开发任务。
* 用户管理、角色管理、部门管理、菜单管理、字典管理、系统日志等系统常见功能。

## 项目结构

```plain text
tiny-geo-server/
├── pom.xml                         # Maven 父工程：统一依赖、插件与模块聚合
├── README.md
├── docs/                           # 项目文档
├── .mvn/                           # Maven Wrapper
│
├── common-core/                    # 通用基础能力
│   └── src/main/java/com/supermap/core/
│       ├── common/                 # 通用响应、枚举、工具类
│       ├── config/                 # 基础配置
│       ├── exception/              # 全局异常处理
│       └── validator/              # 参数校验
│
├── common-db/                      # 数据库基础设施
│   └── src/main/java/com/supermap/db/
│       ├── config/                 # MyBatis-Plus、数据库配置
│       ├── handler/                # 类型处理器
│       ├── util/                   # 数据库工具
│       └── xss/                    # SQL/XSS 防护相关能力
│
├── common-gis/                     # GIS 通用能力
│   └── src/main/java/com/supermap/gis/
│       ├── config/                 # GIS 配置
│       ├── enums/                  # 几何类型、数据集类型等
│       ├── service/                # 几何、空间表处理服务
│       ├── type/                   # 图层、字段、几何处理类型
│       └── util/                   # 空间表名、坐标与几何工具
│
├── common-command/                 # 外部命令执行能力
│   └── src/main/java/com/supermap/command/
│       ├── config/                 # 命令线程池、超时配置
│       └── CommandExecutor.java    # 进程执行与输出采集
│
├── common-gdal/                    # GDAL/OGR 封装
│   └── src/main/java/com/supermap/gdal/
│       ├── config/                 # GDAL 配置
│       ├── info/                   # 图层元数据
│       └── GdalTool.java           # ogrinfo、ogr2ogr 等调用封装
│
├── common-id-generator/            # ID、任务名、表名生成器
│   └── src/main/java/com/supermap/idgenerator/
│
├── common-office/                  # Excel、Word 等办公文件能力
│   └── src/main/java/com/supermap/office/
│
├── common-generator/               # 数据库代码生成工具
│   └── src/main/java/com/supermap/generator/
│
├── admin/                          # 通用后台管理模块
│   └── src/main/java/com/supermap/admin/
│       ├── config/                 # 安全、线程池等配置
│       ├── modules/
│       │   ├── security/           # 登录、验证码、认证
│       │   ├── sys/                # 用户、角色、权限、字典、部门
│       │   └── log/                # 登录与访问日志
│       └── shiro/                  # Shiro Realm、鉴权
│
├── gis-dataset/                    # 空间数据集管理
│   └── src/main/java/com/supermap/dataset/
│       ├── executor/               # 导入、导出异步线程池
│       └── modules/dataset/
│           ├── controller/         # SHP、GDB、GeoJSON、WKT 导入接口
│           ├── dto/                # 导入/上传参数
│           ├── entity/             # 数据集、要素、导入导出任务
│           ├── service/            # 导入导出、状态更新、数据集服务
│           └── dao/                # 数据访问层
│
├── gis-analyze/                    # GIS 原子分析引擎
│   └── src/main/java/com/supermap/analyze/
│       ├── task/                   # AnalysisTask 抽象及具体分析任务
│       │   ├── impl/               # 相交拆分、过滤、裁剪、擦除等
│       │   └── param/              # 各分析任务参数
│       ├── service/                # SQL 执行与空间处理服务
│       │   └── impl/
│       ├── helper/                 # 字段去重、SQL 标识符校验
│       ├── resolver/               # 几何类型、SRID 推导
│       ├── dao/                    # 动态 SQL 执行 Mapper
│       ├── AnalysisEngine.java     # 按分析类型路由任务
│       ├── AnalysisContext.java    # 一次分析的上下文
│       └── AnalysisResult.java     # 分析结果
│
├── gis-analyze-task/               # 分析任务与业务组合编排
│   └── src/main/java/com/supermap/task/
│       ├── modules/task/           # 分析任务创建、启动、状态、步骤
│       ├── modules/compose/        # 多步骤组合任务及执行状态
│       ├── modules/business/       # 固定业务流程编排
│       └── support/
│           ├── analysis/           # 分析上下文构建、同步/异步执行器
│           └── compose/            # 组合任务异步执行器
│
└── gis-platform/                   # GIS 平台启动模块
    └── src/main/java/com/supermap/platform/
        └── GisPlatformApplication.java
```

## 模块依赖关系

```plain text
gis-analyze-task
  ├── gis-analyze ── common-gis
  └── gis-dataset ── common-gdal ── common-command
       ├── admin ── common-db ── common-core
       └── common-gis ── common-db

gis-platform
  └── gis-dataset
```

## 技术选型：

* 核心框架：Spring Boot 3.x
* 安全框架：Apache Shiro 2.1
* 持久层框架：MyBatis 3.5
* 日志管理：Logback
* 页面交互：Vue3.x

## 软件需求

* JDK17+
* Maven3.6+
* PostgreSQL 12+ (postgis)
* redis

## 本地部署

1. 通过git下载源码
2. 创建数据库
3. 执行db/postgis.sql文件，初始化数据
4. 修改application-dev.yml文件，修改数据库账号和密码
5. swagger文档路径：http://localhost:8081/doc.html
6. 账号密码：admin/123456
