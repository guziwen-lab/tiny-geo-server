# tiny-geo-server

## postgis导入导出

pg_dump -h localhost -p 5432 -U postgres -n public tiny_geo_server > /Users/guziwen/Downloads/tiny_geo_server.sql

psql -h 192.168.222.238 -p 5866 -U highgo tiny_geo_server < /Users/guziwen/Downloads/tiny_geo_server.sql

## 安装python3.3

略

### 安装gdal

pip install gdal-3.12.2-cp312-cp312-win_amd64.whl

## 版本信息

ogr2ogr --version
GDAL 3.11.4 "Eganville", released 2025/09/04

ogrinfo --version
GDAL 3.11.4 "Eganville", released 2025/09/04

## 导入gdb

ogr2ogr -f PostgreSQL -overwrite PG:host=localhost port=5432 dbname=tiny_geo_server user=postgres password=123456 /Users/guziwen/Java/temp/tiny-geo-server/test.gdb -nln ds_20260727000001 sys_administrative_division_code_with_geom -lco GEOMETRY_NAME=geom -lco SPATIAL_INDEX=NONE -lco SCHEMA=dataset

### 追加导入gdb

ogr2ogr -f PostgreSQL -append -addfields PG:host=localhost port=5432 dbname=tiny_geo_server user=postgres password=123456 /Users/guziwen/Java/temp/tiny-geo-server/test.gdb -nln dataset.ds_20260703000002 sys_administrative_division_code_with_geom -nlt PROMOTE_TO_MULTI

## 读取gdb图层列表

ogrinfo -so /Users/guziwen/Downloads/ds_20260703000003.gdb

INFO: Open of `/Users/guziwen/Downloads/ds_20260703000003.gdb'
      using driver`OpenFileGDB' successful.
Layer: dataset_ds_20260703000003 (Point)
Layer: dataset_ds_20260703000002 (Multi Polygon)
Layer: dataset_ds_20260703000004 (Multi Line String)
Layer: dataset_analyze_2072897114854473730 (Multi Polygon)

## 读取gdb图层信息

ogrinfo -so /Users/guziwen/Java/temp/tiny-geo-server/test.gdb sys_administrative_division_code_with_geom

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
