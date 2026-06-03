package com.supermap.pojo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;

/**
 * @author gzw
 */
@Schema(title = "分页参数")
@Setter
public class PageParam {

    @Schema(title = "当前页")
    private Long current = 1L;

    @Schema(title = "每页条数")
    private Long size = 10L;

    public Long getCurrent() {
        if (current <= 0L)
            return 1L;
        return current;
    }

    public Long getSize() {
        if (size <= 0L)
            return 1L;
        return size;
    }

    public <T> Page<T> page() {
        return new Page<>(current, size);
    }

    public PageParam getPageParam() {
        return this;
    }

}
