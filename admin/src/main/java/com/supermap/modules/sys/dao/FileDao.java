package com.supermap.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.sys.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件管理表
 *
 * @author gzw
 */
@Mapper
public interface FileDao extends BaseMapper<FileEntity> {

}
