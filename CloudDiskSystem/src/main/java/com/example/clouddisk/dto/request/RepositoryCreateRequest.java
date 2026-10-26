package com.example.clouddisk.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建仓库请求（FR-01）。
 */
@Data
@NoArgsConstructor
public class RepositoryCreateRequest {

    /** 仓库名称 */
    private String name;

    /** 仓库描述 */
    private String description;
}
