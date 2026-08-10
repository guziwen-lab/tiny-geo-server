package com.supermap.admin.modules.sys.dao;

import com.supermap.admin.modules.sys.entity.AdministrativeDivisionCodeWithGeomEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 行政区划代码表
 * 
 * @author gzw
 */
@Mapper
public interface AdministrativeDivisionCodeWithGeomDao extends BaseMapper<AdministrativeDivisionCodeWithGeomEntity> {

    void setGeom(@Param("code") String code, @Param("geoJSON") String geoJSON);

}
