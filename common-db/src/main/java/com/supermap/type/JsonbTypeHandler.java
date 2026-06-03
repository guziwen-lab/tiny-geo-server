package com.supermap.type;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 支持 jsonb 类型，自动序列化和反序列化
 *
 * @author gzw
 */
@MappedTypes({Object.class})
public class JsonbTypeHandler extends JacksonTypeHandler {

    public static final String JSONB_TYPE = "jsonb";

    public JsonbTypeHandler(Class<?> type) {
        super(type);
    }

    public JsonbTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (ps != null) {
            PGobject jsonObject = new PGobject();
            jsonObject.setType(JSONB_TYPE);
            jsonObject.setValue(toJson(parameter));
            ps.setObject(i, jsonObject);
        }
    }

}