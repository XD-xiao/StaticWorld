package com.example.common.domain.Entity.CloudDiskSystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件类型黑名单配置（cloud_filetype_rule，管理员维护）。
 */
@Data
@TableName("cloud_filetype_rule")
public class CloudFiletypeRule {

    /** 主键（雪花 ID） */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 规则类型：1 扩展名 / 2 MIME 类型 */
    private Integer ruleType;

    /** 规则值（如 .exe / application/x-msdownload） */
    private String ruleValue;

    /** 是否启用：1 启用 / 0 停用 */
    private Integer enabled;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
