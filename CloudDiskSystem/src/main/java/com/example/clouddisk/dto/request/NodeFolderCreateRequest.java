package com.example.clouddisk.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新建文件夹请求（FR-07）。
 */
@Data
@NoArgsConstructor
public class NodeFolderCreateRequest {

    /** 所属仓库 ID */
    private Long repoId;

    /** 父节点 ID，0 表示仓库根目录 */
    private Long parentId;

    /** 文件夹名称 */
    private String name;
}
