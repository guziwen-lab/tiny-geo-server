package com.supermap.modules.platform.service;

import com.supermap.modules.platform.dto.UploadGeoJsonDTO;
import com.supermap.modules.platform.dto.UploadWktDTO;

/**
 * @author gzw
 */
public interface UploadService {

    void uploadGeoJson(UploadGeoJsonDTO dto);

    void uploadWkt(UploadWktDTO dto);

}
