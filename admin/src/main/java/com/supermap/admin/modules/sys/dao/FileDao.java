package com.supermap.admin.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.admin.modules.sys.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件管理表
 *
 * @author gzw
 */
@Mapper
public interface FileDao extends BaseMapper<FileEntity> {

}
