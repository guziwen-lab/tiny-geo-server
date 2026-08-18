package com.supermap.file.dto;

import com.supermap.file.entity.FileEntity;
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
