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
* 安全框架：Apache Shiro 2.0
* 持久层框架：MyBatis 3.5
* 日志管理：Logback
* 页面交互：Vue3.x
* 图层叠加分析: PostGIS
* 导入导出GDB/Shp: GDAL 3.x

## 软件需求

* JDK17+
* Maven3.6+
* PostgreSQL 12+ (PostGIS)
* redis
* GDAL 3.x

## 本地部署

1. 通过git下载源码
2. 创建数据库
3. psql导入db/tiny_geo_server.sql文件，初始化数据
4. 修改application-dev.yml文件，修改数据库和redis以及gdal的数据库的账号和密码
5. swagger文档路径：http://localhost:8081/doc.html
6. 账号密码：admin/123456

## 使用说明

### postgis导入导出

pg_dump -h localhost -p 5432 -U postgres -n public tiny_geo_server > /Users/guziwen/Downloads/tiny_geo_server.sql

psql -h 192.168.222.238 -p 5866 -U highgo tiny_geo_server < /Users/guziwen/Downloads/tiny_geo_server.sql

### 安装python3.3

略

#### 安装gdal

pip install gdal-3.12.2-cp312-cp312-win_amd64.whl

### 版本信息

ogr2ogr --version
GDAL 3.11.4 "Eganville", released 2025/09/04

ogrinfo --version
GDAL 3.11.4 "Eganville", released 2025/09/04

### 导入gdb

ogr2ogr -f PostgreSQL -overwrite PG:host=localhost port=5432 dbname=tiny_geo_server user=postgres password=123456
/Users/guziwen/Java/temp/tiny-geo-server/test.gdb -nln ds_20260727000001 sys_administrative_division_code_with_geom -lco
GEOMETRY_NAME=geom -lco SPATIAL_INDEX=NONE -lco SCHEMA=dataset

#### 追加导入gdb

ogr2ogr -f PostgreSQL -append -addfields PG:host=localhost port=5432 dbname=tiny_geo_server user=postgres
password=123456 /Users/guziwen/Java/temp/tiny-geo-server/test.gdb -nln dataset.ds_20260703000002
sys_administrative_division_code_with_geom -nlt PROMOTE_TO_MULTI

### 读取gdb图层列表

ogrinfo -so /Users/guziwen/Downloads/ds_20260703000003.gdb

```text
INFO: Open of `/Users/guziwen/Downloads/ds_20260703000003.gdb'
      using driver`OpenFileGDB' successful.
Layer: dataset_ds_20260703000003 (Point)
Layer: dataset_ds_20260703000002 (Multi Polygon)
Layer: dataset_ds_20260703000004 (Multi Line String)
Layer: dataset_analyze_2072897114854473730 (Multi Polygon)
```

### 读取gdb图层信息

ogrinfo -so /Users/guziwen/Java/temp/tiny-geo-server/test.gdb sys_administrative_division_code_with_geom

```text
INFO: Open of `/Users/guziwen/Java/temp/tiny-geo-server/test.gdb'
      using driver`OpenFileGDB' successful.

Layer name: sys_administrative_division_code_with_geom
Geometry: Multi Polygon
Feature Count: 3277
Extent: (73.502355, 3.823583) - (135.095670, 53.563624)
Layer SRS WKT:
GEOGCRS["WGS 84",
ENSEMBLE["World Geodetic System 1984 ensemble",
MEMBER["World Geodetic System 1984 (Transit)"],
MEMBER["World Geodetic System 1984 (G730)"],
MEMBER["World Geodetic System 1984 (G873)"],
MEMBER["World Geodetic System 1984 (G1150)"],
MEMBER["World Geodetic System 1984 (G1674)"],
MEMBER["World Geodetic System 1984 (G1762)"],
MEMBER["World Geodetic System 1984 (G2139)"],
MEMBER["World Geodetic System 1984 (G2296)"],
ELLIPSOID["WGS 84",6378137,298.257223563,
LENGTHUNIT["metre",1]],
ENSEMBLEACCURACY[2.0]],
PRIMEM["Greenwich",0,
ANGLEUNIT["degree",0.0174532925199433]],
CS[ellipsoidal,2],
AXIS["geodetic latitude (Lat)",north,
ORDER[1],
ANGLEUNIT["degree",0.0174532925199433]],
AXIS["geodetic longitude (Lon)",east,
ORDER[2],
ANGLEUNIT["degree",0.0174532925199433]],
USAGE[
SCOPE["Horizontal component of 3D system."],
AREA["World."],
BBOX[-90,-180,90,180]],
ID["EPSG",4326]]
Data axis to CRS axis mapping: 2,1
FID Column = OBJECTID
Geometry Column = SHAPE
id: Real (0.0)
code: String (0.0)
pcode: String (0.0)
name: String (0.0)
level: Integer (0.0)
path: String (0.0)
create_time: DateTime
update_time: DateTime
```
