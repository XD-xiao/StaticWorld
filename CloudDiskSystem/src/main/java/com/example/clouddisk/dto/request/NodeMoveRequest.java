package com.example.clouddisk.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 移动节点请求（FR-09）。
 */
@Data
@NoArgsConstructor
public class NodeMoveRequest {

    /** 目标父节点 ID，0 表示仓库根目录 */
    private Long targetParentId;
}
