package com.supermap.modules.platform.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 数据库空间计算代理层（Spatial Gateway）
 *
 * @author gzw
 */
@Mapper
public interface GeometryDao {

    @Select("""
        SELECT ST_AsBinary(
            ST_Transform(
                ST_SetSRID(ST_GeomFromWKB(#{wkb}), #{sourceSrid}),
                #{targetSrid}
            )
        )
    """)
    byte[] transform(@Param("wkb") byte[] wkb, @Param("sourceSrid") int sourceSrid, @Param("targetSrid") int targetSrid);

}
