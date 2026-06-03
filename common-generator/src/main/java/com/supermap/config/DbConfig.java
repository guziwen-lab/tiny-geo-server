/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.supermap.config;

import com.supermap.dao.*;
import com.supermap.utils.RenException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * 数据库配置
 *
 * @author gzw
 */
@Configuration
public class DbConfig {
    @Value("${renren.database: postgresql}")
    private String database;
    @Resource
    private MySQLGeneratorDao mySQLGeneratorDao;
    @Resource
    private OracleGeneratorDao oracleGeneratorDao;
    @Resource
    private SQLServerGeneratorDao sqlServerGeneratorDao;
    @Resource
    private PostgreSQLGeneratorDao postgreSQLGeneratorDao;
    @Resource
    private DmGeneratorDao dmGeneratorDao;

    @Bean
    @Primary
    public GeneratorDao getGeneratorDao() {
        if ("mysql".equalsIgnoreCase(database)) {
            return mySQLGeneratorDao;
        } else if ("oracle".equalsIgnoreCase(database)) {
            return oracleGeneratorDao;
        } else if ("sqlserver".equalsIgnoreCase(database)) {
            return sqlServerGeneratorDao;
        } else if ("postgresql".equalsIgnoreCase(database)) {
            return postgreSQLGeneratorDao;
        } else if("dm".equalsIgnoreCase(database)){
            return dmGeneratorDao;
        }else {
            throw new RenException("不支持当前数据库：" + database);
        }
    }
}
