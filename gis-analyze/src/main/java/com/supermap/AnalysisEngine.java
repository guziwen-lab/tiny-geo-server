package com.supermap;

import com.supermap.common.util.CollectionUtils;
import com.supermap.enums.AnalysisType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class AnalysisEngine {

    private final Map<AnalysisType, AnalysisTask<?>> taskMap;

    public AnalysisEngine(List<AnalysisTask<?>> tasks) {
        this.taskMap = CollectionUtils.toMap(tasks, AnalysisTask::getType, Function.identity());
    }

    @SuppressWarnings("unchecked")
    public <T extends AnalysisParam> AnalysisResult execute(AnalysisType type, AnalysisContext<T> context) {
        AnalysisTask<T> task = (AnalysisTask<T>) getTask(type);
        return task.execute(context);
    }

    public AnalysisTask<?> getTask(AnalysisType type) {
        AnalysisTask<?> task = taskMap.get(type);
        if (task == null) {
            throw new IllegalArgumentException("Unsupported analysis type: " + type);
        }
        return task;
    }

}