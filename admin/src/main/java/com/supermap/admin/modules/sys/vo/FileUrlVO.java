package com.supermap.admin.modules.sys.vo;

import com.supermap.admin.modules.sys.entity.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileUrlVO extends FileEntity {

    private String url;

}