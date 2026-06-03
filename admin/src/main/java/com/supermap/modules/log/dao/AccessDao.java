package com.supermap.modules.log.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.log.entity.AccessEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 全局日志表
 * 
 * @author gzw
 */
@Mapper
public interface AccessDao extends BaseMapper<AccessEntity> {
	
}
