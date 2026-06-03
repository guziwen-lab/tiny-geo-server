package com.supermap.modules.sys.dto;

import com.supermap.modules.sys.entity.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileDownloadDTO extends FileEntity {

    private byte[] file;

}
