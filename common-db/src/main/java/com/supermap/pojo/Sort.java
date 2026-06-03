package com.supermap.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(title = "排序")
@Data
public class Sort {

    @Schema(title = "排序字段")
    private String field;

    @Schema(title = "排序规则 (0: asc; 1: desc)")
    private Integer order;

}