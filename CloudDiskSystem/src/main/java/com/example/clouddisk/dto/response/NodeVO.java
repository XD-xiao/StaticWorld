package com.example.clouddisk.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件/文件夹节点视图对象（cloud_node，目录浏览用）。
 */
@Data
@NoArgsConstructor
public class NodeVO {

    /** 节点 ID */
    private Long id;

    /** 所属仓库 ID */
    private Long repoId;

    /** 父节点 ID，0 表示仓库根目录 */
    private Long parentId;

    /** 文件或文件夹名（不含路径） */
    private String name;

    /** 节点类型：1 文件夹 / 2 文件 */
    private Integer nodeType;

    /** 文件大小（字节），文件夹为 0 */
    private Long size;

    /** 内容类型（预览用） */
    private String mimeType;

    /** 是否可预览（文本 / 图片等可内联预览类型） */
    private Boolean previewable;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
