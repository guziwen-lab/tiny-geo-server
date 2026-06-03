package com.supermap.modules.sys.dto;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 行政区划代码表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "行政区划代码表")
@Data
public class AdministrativeDivisionCodeDTO extends PageParam {

    /**
     * 主键
     */
    @Schema(title = "主键")
    private Long id;
    /**
     * 行政区划代码
     */
    @Schema(title = "行政区划代码")
    private String code;
    /**
     * 上级行政区划代码
     */
    @Schema(title = "上级行政区划代码")
    private String pcode;
    /**
     * 行政区划名称
     */
    @Schema(title = "行政区划名称")
    private String name;
    /**
     * 中东西部
     */
    @Schema(title = "中东西部")
    private String position;
    /**
     * 行政区划级别 (0: 国家; 1: 省; 2: 市; 3: 区县)
     */
    @Schema(title = "行政区划级别 (0: 国家; 1: 省; 2: 市; 3: 区县)")
    private Integer level;
    /**
     * 年份
     */
    @Schema(title = "年份")
    private Integer year;
    /**
     * 季度
     */
    @Schema(title = "季度")
    private Integer quarter;
    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @Schema(title = "更新时间")
    private Date updateTime;

}
