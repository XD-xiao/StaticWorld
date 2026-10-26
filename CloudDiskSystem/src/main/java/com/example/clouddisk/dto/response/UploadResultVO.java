package com.example.clouddisk.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传结果视图对象（简单上传 / 秒传）。
 */
@Data
@NoArgsConstructor
public class UploadResultVO {

    /** 文件节点 ID */
    private Long nodeId;

    /** 文件名 */
    private String name;

    /** 文件大小（字节） */
    private Long size;

    /** 内容类型 */
    private String mimeType;

    /** 是否秒传（命中已有内容对象，未重新上传） */
    private Boolean instantUpload;

    /** 内容哈希（SHA-256） */
    private String blobHash;
}
