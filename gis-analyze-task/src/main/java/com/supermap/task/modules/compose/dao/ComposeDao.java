package com.supermap.task.modules.compose.dao;

import com.supermap.task.modules.compose.entity.ComposeEntity;
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
