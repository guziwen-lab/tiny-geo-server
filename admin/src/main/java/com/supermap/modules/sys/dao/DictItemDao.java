package com.supermap.modules.sys.dao;

import com.supermap.modules.sys.entity.DictItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典项表
 * 
 * @author gzw
 */
@Mapper
public interface DictItemDao extends BaseMapper<DictItemEntity> {

    DictItemEntity getByCodeAndDictName(@Param("code") String code, @Param("dictName") String dictName);

    List<DictItemEntity> getByDictName(@Param("dictName") String dictName);

}
