package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典表
 *
 * @author gzw
 */
@Schema(title = "字典表")
@Data
public class DictSaveDTO {

    @NotNull(groups = Update.class)
    @Schema(title = "主键")
    private Long dictId;

    @NotBlank(groups = Add.class)
    @Schema(title = "字典名称")
    private String name;

    @NotBlank(groups = Add.class)
    @Schema(title = "字典描述")
    private String description;

}
