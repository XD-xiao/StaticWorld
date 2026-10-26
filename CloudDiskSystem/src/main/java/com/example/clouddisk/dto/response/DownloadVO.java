package com.example.clouddisk.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 下载视图对象（预签名下载 URL）。
 */
@Data
@NoArgsConstructor
public class DownloadVO {

    /** 预签名下载 URL */
    private String url;

    /** 下载文件名 */
    private String fileName;
}
