package com.supermap.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.sys.entity.AdministrativeDivisionCodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 行政区划代码表
 * 
 * @author gzw
 */
@Mapper
public interface AdministrativeDivisionCodeDao extends BaseMapper<AdministrativeDivisionCodeEntity> {

    AdministrativeDivisionCodeEntity getByProvinceAndCityAndCounty(@Param("province") String province,
                                                                   @Param("city") String city,
                                                                   @Param("county") String county);

    List<String> getCodeByCode(@Param("code") String code);

    List<AdministrativeDivisionCodeEntity> getAllSubordinates(@Param("code") String code);

}
