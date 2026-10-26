package com.example.common.domain.Entity.CloudDiskSystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网盘导出任务（cloud_export_task，异步打包 zip）。
 */
@Data
@TableName("cloud_export_task")
public class CloudExportTask {

    /** 主键（雪花 ID） */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属仓库（cloud_repository.id） */
    private Long repoId;

    /** 导出根节点 ID，为空表示导出整个仓库 */
    private Long nodeId;

    /** 导出类型：1 文件夹 / 2 整个仓库 */
    private Integer exportType;

    /** 状态：0 排队 / 1 打包中 / 2 成功 / 3 失败 */
    private Integer status;

    /** zip 对象 key（成功后写入） */
    private String storageKey;

    /** 文件总数（用于进度） */
    private Integer totalFiles;

    /** 已打包文件数 */
    private Integer processedFiles;

    /** 失败原因 */
    private String errorMsg;

    /** 发起用户 ID */
    private Long ownerId;

    /** zip 临时有效期 */
    private LocalDateTime expireTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
