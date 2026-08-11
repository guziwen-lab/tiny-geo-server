package com.supermap.analyze.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 只读 SQL 查询
 */
@Mapper
public interface QuerySqlMapper {

    @Select("${sql}")
    List<Map<String, Object>> query(@Param("sql") String sql);

}
