package com.supermap.dao;

import com.supermap.entity.DatasetEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集表
 * 
 * @author gzw
 */
@Mapper
public interface DatasetDao extends BaseMapper<DatasetEntity> {
	
}
