package com.supermap.modules.dataset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gzw
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class GdbLayerSource {

    private String path;
    private String layerName;
    private String encoding;

    public GdbLayerSource(String path, String layerName) {
        this(path, layerName, null);
    }

}
