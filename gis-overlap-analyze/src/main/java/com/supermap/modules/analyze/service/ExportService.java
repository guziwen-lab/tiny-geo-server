package com.supermap.modules.analyze.service;

/**
 * @author gzw
 */
public interface ExportService {

    String exportShp(String path);

    String exportGdb(String path, String layerName);

}
