package com.supermap.idgenerator;

import org.springframework.stereotype.Component;

/**
 * 数据集表名生成器
 *
 * @author gzw
 */
@Component
public class TaskNameGenerator extends AbstractSnGenerator {

    public TaskNameGenerator() {
        super("TASK:NAME:");
    }

    public String getTaskName() {
        return "task_" + super.generate();
    }

}
