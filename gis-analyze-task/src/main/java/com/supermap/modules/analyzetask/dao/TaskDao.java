package com.supermap.modules.analyzetask.dao;

import com.supermap.modules.analyzetask.entity.TaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 任务表
 * 
 * @author gzw
 */
@Mapper
public interface TaskDao extends BaseMapper<TaskEntity> {

    @Select("""
            select * from gis_task where id = #{taskId} and status in ('NOT_PROCESSED','FAILED')
            """)
    TaskEntity getStartableById(@Param("taskId") Long taskId);

}
