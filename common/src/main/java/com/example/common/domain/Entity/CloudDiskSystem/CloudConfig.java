package com.example.common.domain.Entity.CloudDiskSystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网盘模块通用配置（cloud_config，配额 / 阈值 / 上限等）。
 */
@Data
@TableName("cloud_config")
public class CloudConfig {

    /** 配置键（主键） */
    @TableId(value = "config_key", type = IdType.INPUT)
    private String configKey;

    /** 配置值（字符串存储，读取时转类型） */
    private String configValue;

    /** 配置说明 */
    private String description;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
