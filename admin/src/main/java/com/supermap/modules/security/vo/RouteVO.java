package com.supermap.modules.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
@Data
public class RouteVO {

    private Long permissionId;

    private Long parentId;

    private String path;

    private String redirect;

    private String name;

    private String title;

    private Boolean hidden;

    private String icon;

    @Schema(title = "打开方式 (0: 内部打开; 1: 外部打开)")
    private Integer openStyle;

    private List<RouteVO> children = new ArrayList<>();

}
