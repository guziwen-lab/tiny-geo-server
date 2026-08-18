package com.supermap.file.vo;

import com.supermap.file.entity.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileUrlVO extends FileEntity {

    private String url;

}