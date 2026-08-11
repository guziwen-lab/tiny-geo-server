package com.supermap.task.modules.task.dao;

import com.supermap.task.modules.task.entity.TaskStepEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务执行记录表
 * 
 * @author gzw
 */
@Mapper
public interface TaskStepDao extends BaseMapper<TaskStepEntity> {
	
}
