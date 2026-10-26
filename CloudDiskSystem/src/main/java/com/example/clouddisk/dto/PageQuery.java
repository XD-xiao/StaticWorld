package com.example.clouddisk.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用分页查询入参。
 *
 * <p>page 从 1 开始，size 为每页条数；{@link #getOffset()} 供分页 SQL 的 OFFSET 使用。</p>
 */
@Data
@NoArgsConstructor
public class PageQuery {

    /** 页码，从 1 开始，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 20 */
    private Integer size = 20;

    /**
     * 计算分页偏移量 {@code (page - 1) * size}。
     *
     * <p>page 或 size 为空或非法（小于 1）时回退到默认值，避免产生负偏移。</p>
     *
     * @return 分页偏移量，从 0 开始
     */
    public int getOffset() {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 20 : size;
        return (safePage - 1) * safeSize;
    }
}
