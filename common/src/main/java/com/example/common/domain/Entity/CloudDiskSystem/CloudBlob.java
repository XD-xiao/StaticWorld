package com.example.common.domain.Entity.CloudDiskSystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网盘内容对象（cloud_blob，内容寻址，秒传/去重）。
 */
@Data
@TableName("cloud_blob")
public class CloudBlob {

    /** 内容哈希（SHA-256，主键） */
    @TableId(value = "hash", type = IdType.INPUT)
    private String hash;

    /** RustFS 对象 key（blob/{hash}） */
    private String storageKey;

    /** 内容字节数 */
    private Long size;

    /** 内容类型 */
    private String mimeType;

    /** 被引用次数（文件节点数） */
    private Integer refCount;

    /** 首次写入时间 */
    private LocalDateTime createTime;
}
