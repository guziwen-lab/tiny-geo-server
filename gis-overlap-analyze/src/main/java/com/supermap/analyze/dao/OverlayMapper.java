package com.supermap.analyze.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author gzw
 */
@Mapper
public interface OverlayMapper {

    @Update("""
            CREATE TABLE ${result} AS
            SELECT to_jsonb(a) - 'geom' as ${current}_attr,
                   to_jsonb(b) - 'geom' as ${next}_attr,
                   ST_Intersection(a.geom, b.geom) as geom
            FROM ${current} a
                     JOIN ${next} b ON ST_Intersects(a.geom, b.geom)
            """)
    void executeOverlay(@Param("current") String current, @Param("next") String next, @Param("result") String result);

}
