package com.example.clouddisk.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新仓库请求（重命名 / 改描述，FR-03）。
 */
@Data
@NoArgsConstructor
public class RepositoryUpdateRequest {

    /** 仓库名称 */
    private String name;

    /** 仓库描述 */
    private String description;
}
