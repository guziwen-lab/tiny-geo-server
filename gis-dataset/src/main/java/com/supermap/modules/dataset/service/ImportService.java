package com.supermap.modules.dataset.service;

import java.util.List;

/**
 * @author gzw
 */
public interface ImportService {

    Long importShp(String path);

    List<Long> importGdb(String path, String layerName);

}
