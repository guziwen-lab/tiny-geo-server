package com.supermap.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 将WKB转换为JTS对应的Geometry几何类型，或反向转换
 */
public class AbstractGeometryTypeHandler<T extends Geometry> extends BaseTypeHandler<Geometry> {

    private static final WKBReader WKB_READER = new WKBReader();
    private static final WKBWriter WKB_WRITER = new WKBWriter(2, true); // 支持SRID（EWKB）

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Geometry parameter, JdbcType jdbcType) throws SQLException {
        byte[] bytes = WKB_WRITER.write(parameter);
        ps.setBytes(i, bytes);
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toGeometry(rs.getBytes(columnName));
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toGeometry(rs.getBytes(columnIndex));
    }

    @Override
    public Geometry getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toGeometry(cs.getBytes(columnIndex));
    }

    private Geometry toGeometry(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return WKB_READER.read(bytes);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse WKB to Geometry", e);
        }
    }

}
