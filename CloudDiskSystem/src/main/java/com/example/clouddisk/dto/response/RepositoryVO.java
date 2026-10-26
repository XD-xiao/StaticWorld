package com.example.clouddisk.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 仓库信息视图对象（cloud_repository）。
 */
@Data
@NoArgsConstructor
public class RepositoryVO {

    /** 仓库 ID */
    private Long id;

    /** 仓库名称 */
    private String name;

    /** 仓库描述 */
    private String description;

    /** 可见状态：0 私密 / 1 公开 */
    private Integer visibility;

    /** 仓库容量配额（字节）：NULL 跟随全局默认，0 不限，>0 单仓覆盖 */
    private Long quotaSize;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
