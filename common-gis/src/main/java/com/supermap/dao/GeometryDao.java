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

    @Update("""
                CREATE TABLE ${schema}.${tempTableName} AS
                SELECT *,
                       ST_Transform(geom, #{targetSrid}) AS geom
                FROM ${schema}.${sourceTable}
            """)
    void transformTable(@Param("schema") String schema,
                        @Param("sourceTable") String sourceTable,
                        @Param("targetSrid") int targetSrid,
                        @Param("tempTableName") String tempTableName);

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
            ALTER TABLE ${schema}.${table} ALTER COLUMN serial_id SET NOT NULL
            """)
    void alterIdNotNull(@Param("schema") String schema, @Param("table") String table);

    @Update("""
            ALTER TABLE ${schema}.${table} ADD PRIMARY KEY (serial_id)
            """)
    void addPrimaryKey(@Param("schema") String schema, @Param("table") String table);

    void normalizeGeometry(@Param("schema") String schema,
                           @Param("table") String table,
                           @Param("columns") List<String> columns,
                           @Param("tempTableName") String tempTableName,
                           @Param("dimension") int dimension);

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
              AND c.column_name <> 'serial_id'
              AND c.table_schema = #{schema}
              /*AND NOT EXISTS (
                  SELECT 1
                  FROM information_schema.key_column_usage kcu
                  JOIN information_schema.table_constraints tc
                    ON kcu.constraint_name = tc.constraint_name
                   AND kcu.table_schema = tc.table_schema
                  WHERE tc.constraint_type = 'PRIMARY KEY'
                    AND tc.table_name = c.table_name
                    AND tc.table_schema = c.table_schema
                    AND kcu.column_name = c.column_name
              )*/
            ORDER BY c.ordinal_position
            """)
    List<Column> listAttrColumns(@Param("schema") String schema, @Param("table") String table);

    @Select("""
            SELECT COUNT(*)
            FROM ${schema}.${tableName}
            WHERE geom IS NOT NULL
              AND (
                NOT ST_IsValid(geom)
                    OR GeometryType(geom) <> #{geoType})
            """)
    int countNeedNormalize(@Param("schema") String schema,
                           @Param("tableName") String tableName,
                           @Param("geoType") String geoType);

}
