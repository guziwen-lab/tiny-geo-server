package com.supermap;

import com.supermap.enums.AnalysisType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalysisEngine {

    private final Map<AnalysisType, AnalysisTask<?>> taskMap;

    public AnalysisEngine(List<AnalysisTask<?>> tasks) {
        this.taskMap = tasks.stream()
                .collect(Collectors.toMap(AnalysisTask::getType, Function.identity()));
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