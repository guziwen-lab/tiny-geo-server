package com.supermap.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.locationtech.jts.geom.MultiPolygon;

@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(value = MultiPolygon.class)
public class MultiPolygonTypeHandler extends AbstractGeometryTypeHandler<MultiPolygon> {
}
