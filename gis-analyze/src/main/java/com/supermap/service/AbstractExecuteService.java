package com.supermap.service;

import com.supermap.security.SqlInjectionCheck;

/**
 * @author gzw
 */
public abstract class AbstractExecuteService {

    public String execute(String current, String next) {
        SqlInjectionCheck.checkTableName(current, next);
        return executeInternal(current, next);
    }

    /**
     * 执行
     *
     * @param current 当前数据集
     * @param next    下一个数据集
     * @return 执行结果表名
     */
    protected abstract String executeInternal(String current, String next);

}
