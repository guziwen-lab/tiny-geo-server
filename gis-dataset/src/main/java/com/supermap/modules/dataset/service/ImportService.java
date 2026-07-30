package com.supermap.modules.dataset.service;

import com.supermap.modules.dataset.dto.UploadGeoJsonDTO;
import com.supermap.modules.dataset.dto.UploadWktDTO;
import com.supermap.modules.dataset.dto.BatchImportGdbDTO;

import java.util.List;

/**
 * @author gzw
 */
public interface ImportService {

    Long importShp(String path);

    Long importShp(String path, Long datasetId);

    List<Long> importGdb(String path, String layerName);

    Long importGdb(String path, String layerName, Long datasetId);

    /**
     * 批量导入 GDB，并以实际 SRID 为最小坐标分组单位创建数据集。
     */
    List<Long> importGdbBatchByGrouping(List<BatchImportGdbDTO> dtoList);

    void uploadGeoJson(UploadGeoJsonDTO dto);

    void uploadWkt(UploadWktDTO dto);

    Long importGdbBatch(List<String> paths, String layerName, Integer srid);

    Long importShpBatch(List<String> paths, String layerName, Integer srid, String encoding);

}
