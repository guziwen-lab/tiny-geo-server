package com.supermap.modules.analyze.service;

import java.util.List;

/**
 * @author gzw
 */
public interface UploadService {

    Long importShp(String path);

    List<Long> importGdb(String path, String layerName);

}
