package com.supermap.file.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.file.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件管理表
 *
 * @author gzw
 */
@Mapper
public interface FileDao extends BaseMapper<FileEntity> {

}
