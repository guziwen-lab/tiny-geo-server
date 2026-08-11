package com.supermap.task.modules.business.service.impl;

import com.supermap.analyze.AnalysisContext;
import com.supermap.task.modules.analyzetask.dto.ComposeTaskDTO;
import com.supermap.task.modules.analyzetask.entity.TaskEntity;
import com.supermap.task.modules.analyzetask.service.TaskService;
import com.supermap.task.modules.business.constant.BusinessConstants;
import com.supermap.task.modules.business.enums.Caliber;
import com.supermap.task.modules.business.service.OtherAgriculturalLandService;
import com.supermap.task.modules.compose.dto.ComposeSaveDTO;
import com.supermap.task.modules.compose.entity.ComposeEntity;
import com.supermap.task.modules.compose.service.ComposeService;
import com.supermap.task.modules.compose.vo.ComposeVO;
import com.supermap.task.support.compose.AsyncComposeExecutor;
import com.supermap.task.support.analysis.SyncAnalysisExecutor;
import com.supermap.analyze.task.param.FilterParam;
import com.supermap.analyze.task.param.IntersectSplitParam;
import com.supermap.analyze.task.param.IntersectSplitParam.SplitField;
import com.supermap.analyze.enums.AnalysisType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 其他农用地分析服务实现
 * <p>
 * 工作流程：
 * <ol>
 *   <li>相交+面积拆分：ZT ∩ DLTB，拆分A表jcmj字段，B表tbmj/kcmj/tbdlmj字段</li>
 *   <li>过滤提取：根据口径过滤（非同口径用dlbm，同口径用dlbmtkj），
 *       同时过滤ysdm和tbdlmj_split面积阈值</li>
 *   <li>保存结果为数据集</li>
 * </ol>
 *
 * @author gzw
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtherAgriculturalLandServiceImpl implements OtherAgriculturalLandService {

    private final ComposeService composeService;
    private final TaskService taskService;
    private final SyncAnalysisExecutor syncAnalysisExecutor;
    private final AsyncComposeExecutor asyncComposeExecutor;

    @Override
    public ComposeEntity analyze(Long ztDatasetId, Long dltbDatasetId, Caliber caliber) {
        ComposeSaveDTO dto = new ComposeSaveDTO();
        dto.setName("其他农用地分析");
        ComposeEntity composeEntity = composeService.createCompose(dto);

        asyncComposeExecutor.executeAsync(composeEntity, () -> {
            TaskEntity taskEntity = step1IntersectSplit(ztDatasetId, dltbDatasetId, composeEntity);
            return step2AttrFilter(taskEntity.getResultDatasetId(), caliber, composeEntity);
        });

        return composeEntity;
    }

    private TaskEntity step2AttrFilter(Long datasetId, Caliber caliber, ComposeEntity composeEntity) {
        String whereClause = buildWhereClause(caliber);
        FilterParam filterParam = new FilterParam(whereClause);

        ComposeTaskDTO<FilterParam> composeTaskDTO = new ComposeTaskDTO<>();
        composeTaskDTO.setTaskDescription("其他农用地变化图斑第二步");
        composeTaskDTO.setAnalysisType(AnalysisType.FILTER);
        composeTaskDTO.setTaskParam(filterParam);
        composeTaskDTO.addDataset(List.of(datasetId));
        composeTaskDTO.setComposeEntity(composeEntity);
        ComposeVO<FilterParam> composeVO = composeService.createTask(composeTaskDTO, 2);

        // 执行分析任务
        TaskEntity taskEntity = composeVO.getTaskEntity();
        AnalysisContext<FilterParam> context = composeVO.getAnalysisContext();
        syncAnalysisExecutor.execute(taskEntity, AnalysisType.FILTER, context);

        return taskService.getById(taskEntity.getId());
    }

    private TaskEntity step1IntersectSplit(Long ztDatasetId, Long dltbDatasetId, ComposeEntity composeEntity) {
        // 构建分析任务参数
        IntersectSplitParam splitParam = new IntersectSplitParam(
                List.of(SplitField.withDefaultResult("jcmj")),
                List.of(SplitField.withDefaultResult("tbmj"), SplitField.withDefaultResult("kcmj"),
                        SplitField.withDefaultResult("tbdlmj")),
                "ZT_RATIO",
                "DLTB_RATIO"
        );

        // 创建任务
        ComposeTaskDTO<IntersectSplitParam> composeTaskDTO = new ComposeTaskDTO<>();
        composeTaskDTO.setTaskDescription("其他农用地变化图斑第一步");
        composeTaskDTO.setAnalysisType(AnalysisType.INTERSECT_SPLIT);
        composeTaskDTO.setTaskParam(splitParam);
        composeTaskDTO.addDataset(List.of(ztDatasetId, dltbDatasetId));
        composeTaskDTO.setComposeEntity(composeEntity);
        ComposeVO<IntersectSplitParam> composeVO = composeService.createTask(composeTaskDTO, 1);

        // 执行分析任务
        TaskEntity taskEntity = composeVO.getTaskEntity();
        AnalysisContext<IntersectSplitParam> context = composeVO.getAnalysisContext();
        syncAnalysisExecutor.execute(taskEntity, AnalysisType.INTERSECT_SPLIT, context);

        return taskService.getById(taskEntity.getId());
    }

    /**
     * 构建过滤WHERE子句
     * <p>
     * 非同口径：dlbm IN (...) AND ysdm IN (...) AND tbdlmj_split > 阈值
     * 同口径：dlbmtkj IN (...) AND ysdm IN (...) AND tbdlmj_split > 阈值
     */
    private String buildWhereClause(Caliber caliber) {
        String dlbmField = caliber == Caliber.TONG_KOU_JING ? "dlbmtkj" : "dlbm";

        String dlbmValues = BusinessConstants.QTYD_DLBM.stream()
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(","));
        String ysdmValues = BusinessConstants.QTYD_YSDM.stream()
                .map(s -> "'" + s + "'")
                .collect(Collectors.joining(","));

        return "%s IN (%s) AND ysdm IN (%s) AND tbdlmj_split > %s".formatted(
                dlbmField, dlbmValues, ysdmValues, BusinessConstants.AREA_THRESHOLD);
    }

}
