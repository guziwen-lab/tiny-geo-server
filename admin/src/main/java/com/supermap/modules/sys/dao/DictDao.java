package com.supermap.modules.sys.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.dto.SearchDTO;
import com.supermap.modules.sys.entity.DictEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 字典表
 * 
 * @author gzw
 */
@Mapper
public interface DictDao extends BaseMapper<DictEntity> {

    Page<DictEntity> page(Page<Object> page, @Param("dto") SearchDTO dto);

}
