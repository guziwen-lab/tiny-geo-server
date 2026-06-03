package com.supermap.modules.analyze.dao;

import com.supermap.modules.analyze.entity.DatasetEntity;
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
