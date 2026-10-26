package com.example.common.domain.Entity.CloudDiskSystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网盘文件/文件夹节点（cloud_node，目录树，node_type 区分）。
 */
@Data
@TableName("cloud_node")
public class CloudNode {

    /** 主键（雪花 ID） */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属仓库（cloud_repository.id） */
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

    /** 内容哈希，文件指向 cloud_blob.hash，文件夹为空 */
    private String blobHash;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
