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
    void executeSql(@Param("sql") String sql);

}
