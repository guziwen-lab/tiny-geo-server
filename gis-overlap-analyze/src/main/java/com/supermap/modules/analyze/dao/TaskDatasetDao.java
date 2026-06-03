package com.supermap.modules.analyze.dao;

import com.supermap.modules.analyze.entity.TaskDatasetEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图层引用表
 * 
 * @author gzw
 */
@Mapper
public interface TaskDatasetDao extends BaseMapper<TaskDatasetEntity> {
	
}
