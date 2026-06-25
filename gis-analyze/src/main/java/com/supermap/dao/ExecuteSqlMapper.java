package com.supermap.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author gzw
 */
@Mapper
public interface ExecuteSqlMapper {

    @Update("""
            ${sql}
            """)
    void executeOverlay(@Param("sql") String sql);

}
