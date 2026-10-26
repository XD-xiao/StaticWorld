package com.example.clouddisk.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 复制节点请求（FR-10）。
 */
@Data
@NoArgsConstructor
public class NodeCopyRequest {

    /** 目标父节点 ID，0 表示仓库根目录 */
    private Long targetParentId;
}
