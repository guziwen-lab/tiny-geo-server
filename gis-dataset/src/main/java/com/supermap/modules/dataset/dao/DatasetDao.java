package com.supermap.modules.dataset.dao;

import com.supermap.enums.UploadStatus;
import com.supermap.modules.dataset.entity.DatasetEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 数据集表
 * 
 * @author gzw
 */
@Mapper
public interface DatasetDao extends BaseMapper<DatasetEntity> {

    @Update("""
            update gis_dataset set status = #{uploadStatus} where id = #{id}
            and status = 'SUCCESS'
            """)
    int updateStatusBySuccess(@Param("id") Long id, @Param("uploadStatus") UploadStatus uploadStatus);

}
