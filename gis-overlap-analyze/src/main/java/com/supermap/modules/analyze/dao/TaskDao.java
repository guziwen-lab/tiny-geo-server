package com.supermap.modules.analyze.dao;

import com.supermap.modules.analyze.entity.TaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务表
 * 
 * @author gzw
 */
@Mapper
public interface TaskDao extends BaseMapper<TaskEntity> {
	
}
