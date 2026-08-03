package com.supermap.modules.compose.dao;

import com.supermap.modules.compose.entity.ComposeTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 组合任务任务关联表
 * 
 * @author gzw
 */
@Mapper
public interface ComposeTaskDao extends BaseMapper<ComposeTaskEntity> {
	
}
