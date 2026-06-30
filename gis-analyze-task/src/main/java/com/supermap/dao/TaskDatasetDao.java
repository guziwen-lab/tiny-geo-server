package com.supermap.dao;

import com.supermap.entity.TaskDatasetEntity;
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
