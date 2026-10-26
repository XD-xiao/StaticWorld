package com.example.common.domain.Entity.CloudDiskSystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网盘仓库（cloud_repository，文件根容器）。
 */
@Data
@TableName("cloud_repository")
public class CloudRepository {

    /** 主键（雪花 ID） */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 属主用户 ID（sys_user.id） */
    private Long ownerId;

    /** 仓库名称 */
    private String name;

    /** 仓库描述 */
    private String description;

    /** 可见状态：0 私密 / 1 公开 */
    private Integer visibility;

    /** 公开组 ID（预留，公开时指定可访问组） */
    private Long publicGroupId;

    /** 仓库容量配额（字节）：NULL 跟随全局默认，0 不限，>0 单仓覆盖 */
    private Long quotaSize;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
