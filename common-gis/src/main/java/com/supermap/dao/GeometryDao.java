package com.supermap.dao;

import com.supermap.type.Column;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 数据库空间计算代理层（Spatial Gateway）
 *
 * @author gzw
 */
@Mapper
public interface GeometryDao {

    void transformTable(@Param("schema") String schema,
                        @Param("columns") List<String> columns,
                        @Param("postgisGeometryType") String postgisGeometryType,
                        @Param("sourceTable") String sourceTable,
                        @Param("targetSrid") int targetSrid,
                        @Param("resultTableName") String resultTableName);

    @Select("""
            SELECT ST_SRID(geom)
            FROM ${table}
            WHERE geom IS NOT NULL
            LIMIT 1
            """)
    Integer getSrid(@Param("table") String table);

    @Select("""
            SELECT ST_GeometryType(geom)
            FROM ${table}
            WHERE geom IS NOT NULL
            LIMIT 1
            """)
    String getGeometryType(@Param("table") String table);

    @Update("""
            ANALYZE ${table}
            """)
    void analyzeTable(@Param("table") String table);

    @Select("""
            select count(1) from ${table}
            """)
    long getFeatureCount(@Param("table") String table);

    @Update("""
            CREATE INDEX IF NOT EXISTS  ${table}_geom_gix
            ON ${schema}.${table} USING gist (geom)
            """)
    void createGistIndex(@Param("schema") String schema, @Param("table") String table);

    @Delete("""
            DROP TABLE IF EXISTS ${table}
            """)
    void dropTableIfExists(@Param("table") String table);

    @Update("""
            ALTER TABLE ${schema}.${table} ALTER COLUMN ${pkCol} SET NOT NULL
            """)
    void alterIdNotNull(@Param("schema") String schema, @Param("table") String table, @Param("pkCol") String pkCol);

    @Update("""
            ALTER TABLE ${schema}.${table} ADD PRIMARY KEY (${pkCol})
            """)
    void addPrimaryKey(@Param("schema") String schema, @Param("table") String table, @Param("pkCol") String pkCol);

    void normalizeGeometry(@Param("schema") String schema,
                           @Param("table") String table,
                           @Param("columns") List<String> columns,
                           @Param("tempTableName") String tempTableName,
                           @Param("dimension") int dimension,
                           @Param("postgisGeometryType") String postgisGeometryType,
                           @Param("srid") Integer srid);

    @Update("""
            ALTER TABLE ${current} RENAME TO ${resultTableName}
            """)
    void renameTable(String current, String resultTableName);

    @Select("""
            SELECT c.column_name,
                   c.data_type
            FROM information_schema.columns c
            WHERE c.table_name = #{table}
              AND c.column_name <> 'geom'
              AND c.column_name <> #{pkCol}
              AND c.table_schema = #{schema}
            ORDER BY c.ordinal_position
            """)
    List<Column> listAttrColumns(@Param("schema") String schema,
                                 @Param("table") String table,
                                 @Param("pkCol") String pkCol);

    @Select("""
            SELECT COUNT(*)
            FROM ${schema}.${tableName}
            WHERE geom IS NOT NULL
              AND (
                NOT ST_IsValid(geom)
                    OR st_geometrytype(geom) <> #{geoType})
            """)
    int countNeedNormalize(@Param("schema") String schema,
                           @Param("tableName") String tableName,
                           @Param("geoType") String geoType);

    void copyTable(@Param("tableName") String tableName,
                   @Param("newTableName") String newTableName,
                   @Param("schema") String schema,
                   @Param("columns") List<String> columns,
                   @Param("originSrid") Integer originSrid,
                   @Param("postgisGeometryType") String postgisGeometryType,
                   @Param("targetSrid") Integer targetSrid);

}
