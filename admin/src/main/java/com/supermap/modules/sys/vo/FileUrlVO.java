package com.supermap.modules.sys.vo;

import com.supermap.modules.sys.entity.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileUrlVO extends FileEntity {

    private String url;

}