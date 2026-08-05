package com.supermap.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@ConfigurationProperties(prefix = "dataset.import")
@Component
@Setter
@Getter
public class DatasetProperties {

    private String pgConnect;

    private String schema = "public";

    private String pkColumnName = "id";

}
