package com.supermap.modules.analyze.dao;

import com.supermap.modules.analyze.entity.TaskStepEntity;
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
