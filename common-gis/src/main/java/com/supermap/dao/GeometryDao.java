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
                CREATE TABLE ${tempTableName} AS
                SELECT *,
                       ST_Transform(geom, #{targetSrid}) AS geom
                FROM ${sourceTable}
            """)
    void transformTable(@Param("sourceTable") String sourceTable, @Param("targetSrid") int targetSrid, @Param("tempTableName") String tempTableName);

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
            SELECT reltuples::bigint
                          FROM pg_class
                          WHERE relname=#{table}
            """)
    long getFeatureCount(@Param("table") String table);

    @Update("""
            CREATE INDEX IF NOT EXISTS idx_gist_${table}
            ON ${table} USING gist (geom)
            """)
    void createGistIndex(@Param("table") String table);

    @Delete("""
            DROP TABLE IF EXISTS ${table}
            """)
    void dropTable(@Param("table") String table);

    @Update("""
            UPDATE ${table}
            SET geom = ST_Multi(ST_CollectionExtract(ST_MakeValid(geom), 3))
            WHERE geom IS NOT NULL and NOT ST_IsValid(geom)
            """)
    long fixGeometryTypeByMultipolygon(@Param("table") String table);

    @Update("""
            UPDATE ${table}
            SET geom = ST_Multi(ST_CollectionExtract(ST_MakeValid(geom), 2))
            WHERE geom IS NOT NULL and NOT ST_IsValid(geom)
            """)
    long fixGeometryTypeByMultiLineString(@Param("table") String table);

    @Update("""
            UPDATE ${table}
            SET geom = ST_Multi(ST_CollectionExtract(ST_MakeValid(geom), 1))
            WHERE geom IS NOT NULL and NOT ST_IsValid(geom)
            """)
    long fixGeometryTypeByPoint(@Param("table") String table);

    @Update("""
            ALTER TABLE ${current} RENAME TO ${resultTableName}
            """)
    void renameTable(String current, String resultTableName);

    @Select("""
            SELECT column_name,
                   data_type
            FROM information_schema.columns
            WHERE table_name = #{table}
              AND column_name <> 'geom'
            ORDER BY ordinal_position
            """)
    List<Column> listColumns(@Param("table") String table);

}
