package com.supermap.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.locationtech.jts.geom.Polygon;

@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(value = Polygon.class)
public class PolygonTypeHandler extends AbstractGeometryTypeHandler<Polygon> {

}
