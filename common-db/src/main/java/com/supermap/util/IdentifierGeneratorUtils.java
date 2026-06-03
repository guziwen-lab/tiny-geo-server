package com.supermap.util;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@Component
public class IdentifierGeneratorUtils {

    private static IdentifierGenerator identifierGenerator;

    private static SqlSessionFactory sqlSessionFactory;

    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        IdentifierGeneratorUtils.sqlSessionFactory = sqlSessionFactory;
    }

    public static Long nextId() {
        if (identifierGenerator == null) {
            Configuration configuration = sqlSessionFactory.getConfiguration();
            identifierGenerator = GlobalConfigUtils.getGlobalConfig(configuration).getIdentifierGenerator();
        }
        return identifierGenerator.nextId(null).longValue();
    }

}
