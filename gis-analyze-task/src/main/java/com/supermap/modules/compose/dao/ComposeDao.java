package com.supermap.modules.compose.dao;

import com.supermap.modules.compose.entity.ComposeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 组合任务表
 * 
 * @author gzw
 */
@Mapper
public interface ComposeDao extends BaseMapper<ComposeEntity> {
	
}
