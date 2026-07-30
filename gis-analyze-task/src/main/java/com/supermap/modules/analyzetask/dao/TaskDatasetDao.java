package com.supermap.modules.analyzetask.dao;

import com.supermap.modules.analyzetask.entity.TaskDatasetEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.dataset.entity.DatasetEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 图层引用表
 * 
 * @author gzw
 */
@Mapper
public interface TaskDatasetDao extends BaseMapper<TaskDatasetEntity> {

    @Select("""
            select d.* from gis_task_dataset td left join gis_dataset d on td.dataset_id = d.id
                       where td.task_id = #{taskId}
            order by td.sort
            """)
    List<DatasetEntity> getDatasetEntityByTaskId(@Param("taskId") Long taskId);

}
