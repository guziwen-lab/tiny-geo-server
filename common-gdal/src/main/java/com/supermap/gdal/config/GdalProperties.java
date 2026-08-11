package com.supermap.gdal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 命令路径
 * ogr-info: /usr/bin/ogrinfo
 * ogr2ogr: /usr/bin/ogr2ogr
 *
 * @author gzw
 */
@ConfigurationProperties(prefix = "gdal")
@Getter
@Setter
@Component
public class GdalProperties {

    private String ogrInfo = "ogrinfo";

    private String ogr2Ogr = "ogr2ogr";

    private String pgConnect;

    private String pgPassword;

    private String schema = "public";

    private String pkColumnName = "id";

    public String getPgConnect() {
        return pgConnect + " " + pgPassword;
    }

}
