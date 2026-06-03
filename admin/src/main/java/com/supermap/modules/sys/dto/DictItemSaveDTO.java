package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典项表
 *
 * @author gzw
 */
@Schema(title = "字典项表")
@Data
public class DictItemSaveDTO {

    @NotNull(groups = Update.class)
    @Schema(title = "主键")
    private Long dictItemId;

    @NotNull(groups = {Add.class})
    @Schema(title = "字典id")
    private Long dictId;

    @Schema(title = "上级字典项id")
    private Long parentId;

    @NotNull(groups = {Add.class})
    @Schema(title = "字典项名称")
    private String name;

    @Schema(title = "字典项编码")
    private String code;

    @NotNull(groups = {Add.class})
    @Schema(title = "排序")
    private Integer sort;

}
