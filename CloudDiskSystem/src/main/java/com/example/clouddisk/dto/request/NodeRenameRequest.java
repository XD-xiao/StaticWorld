package com.example.clouddisk.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 重命名节点请求（FR-08）。
 */
@Data
@NoArgsConstructor
public class NodeRenameRequest {

    /** 新名称 */
    private String name;
}
