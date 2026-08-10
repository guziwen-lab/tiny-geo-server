package com.supermap.dataset.modules.dataset.service;

import com.supermap.dataset.modules.dataset.dto.UploadGeoJsonDTO;
import com.supermap.dataset.modules.dataset.dto.UploadWktDTO;

import java.util.List;

/**
 * @author gzw
 */
public interface ImportService {

    Long importShp(String path);

    Long importShp(String path, Long datasetId);

    List<Long> importGdb(String path, String layerName);

    Long importGdb(String path, String layerName, Long datasetId);

    void uploadGeoJson(UploadGeoJsonDTO dto);

    void uploadWkt(UploadWktDTO dto);

    Long importGdbBatch(List<String> paths, String layerName, Integer srid, String tableName);

    Long importShpBatch(List<String> paths,
                        String layerName,
                        Integer srid,
                        String tableName);

}
