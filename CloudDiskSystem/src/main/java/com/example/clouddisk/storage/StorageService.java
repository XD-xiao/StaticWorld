package com.example.clouddisk.storage;

import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.Part;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

/**
 * RustFS 对象存储接口（私有桶，下载 / 预览走预签名 URL）。
 */
public interface StorageService {

    /** 简单上传整个对象（小文件 / 文本覆盖 / office 覆盖）。 */
    void putObject(String key, InputStream in, long size, String contentType);

    /** 初始化 S3 Multipart Upload，返回 uploadId。 */
    String initMultipartUpload(String key, String contentType);

    /** 上传单个分片，返回 ETag。 */
    String uploadPart(String key, String uploadId, int partNumber, InputStream in, long size);

    /** 合并分片（parts 为 listParts 返回的 CompletedPart）。 */
    void completeMultipartUpload(String key, String uploadId, List<CompletedPart> parts);

    /** 取消分片上传。 */
    void abortMultipartUpload(String key, String uploadId);

    /** 查询已上传分片（断点续传）。 */
    List<Part> listParts(String key, String uploadId);

    /** 预签名下载 URL（fileName 为下载文件名，与 key 解耦，中文名按 RFC 5987 编码）。 */
    String presignDownload(String key, String fileName, Duration expiry);

    /** 预签名预览 URL（inline + Content-Type）。 */
    String presignPreview(String key, String contentType, Duration expiry);

    /** 删除单个对象。 */
    void deleteObject(String key);

    /** 批量删除对象（1000/批）。 */
    void deleteObjects(List<String> keys);

    /** 流式读取对象内容（大文件 / 导出打包，调用方负责关闭流）。 */
    InputStream getObject(String key);

    /** 读取对象字节（文本预览，超限抛 400）。 */
    byte[] getObjectBytes(String key, long maxSize);
}
