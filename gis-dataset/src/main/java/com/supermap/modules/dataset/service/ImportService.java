package com.supermap.modules.dataset.service;

import com.supermap.modules.dataset.dto.UploadGeoJsonDTO;
import com.supermap.modules.dataset.dto.UploadWktDTO;

import java.util.List;

/**
 * @author gzw
 */
public interface ImportService {

    Long importShp(String path);

    List<Long> importGdb(String path, String layerName);

    void uploadGeoJson(UploadGeoJsonDTO dto);

    void uploadWkt(UploadWktDTO dto);

}
