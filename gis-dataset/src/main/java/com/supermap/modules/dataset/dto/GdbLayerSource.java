package com.supermap.modules.dataset.dto;

/**
 * @author gzw
 */
public record GdbLayerSource(String path, String layerName, String encoding) {

    public GdbLayerSource(String path, String layerName) {
        this(path, layerName, null);
    }

}
