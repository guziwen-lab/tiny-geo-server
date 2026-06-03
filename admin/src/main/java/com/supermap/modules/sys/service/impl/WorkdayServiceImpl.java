package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.util.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.sys.dao.WorkdayDao;
import com.supermap.modules.sys.entity.WorkdayEntity;
import com.supermap.modules.sys.service.WorkdayService;
import com.supermap.modules.sys.dto.WorkdayDTO;
import com.supermap.modules.sys.dto.WorkdaySaveDTO;

@Service("workdayService")
public class WorkdayServiceImpl extends ServiceImpl<WorkdayDao, WorkdayEntity> implements WorkdayService {

    @Override
    public Page<WorkdayEntity> queryPage(WorkdayDTO dto) {
        LambdaQueryWrapper<WorkdayEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(WorkdaySaveDTO dto) {
        WorkdayEntity workdayEntity = new WorkdayEntity();
        BeanUtils.copyProperties(dto, workdayEntity);
        save(workdayEntity);
        return workdayEntity.getId();
    }

    @Override
    public void updateDTOById(WorkdaySaveDTO dto) {
        WorkdayEntity workdayEntity = new WorkdayEntity();
        BeanUtils.copyProperties(dto, workdayEntity);
        updateById(workdayEntity);
    }

}