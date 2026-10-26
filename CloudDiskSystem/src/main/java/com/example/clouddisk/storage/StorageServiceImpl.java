package com.example.clouddisk.storage;

import com.example.clouddisk.config.RustFsProperties;
import com.example.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * {@link StorageService} 实现，基于 RustFS（S3 兼容，私有桶）。
 */
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    /** S3 批量删除单次上限。 */
    private static final int BATCH_DELETE_SIZE = 1000;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final RustFsProperties props;

    @Override
    public void putObject(String key, InputStream in, long size, String contentType) {
        PutObjectRequest.Builder builder = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key);
        if (contentType != null) {
            builder.contentType(contentType);
        }
        s3Client.putObject(builder.build(), RequestBody.fromInputStream(in, size));
    }

    @Override
    public String initMultipartUpload(String key, String contentType) {
        CreateMultipartUploadRequest.Builder builder = CreateMultipartUploadRequest.builder()
                .bucket(props.getBucket())
                .key(key);
        if (contentType != null) {
            builder.contentType(contentType);
        }
        return s3Client.createMultipartUpload(builder.build()).uploadId();
    }

    @Override
    public String uploadPart(String key, String uploadId, int partNumber, InputStream in, long size) {
        UploadPartRequest req = UploadPartRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();
        return s3Client.uploadPart(req, RequestBody.fromInputStream(in, size)).eTag();
    }

    @Override
    public void completeMultipartUpload(String key, String uploadId, List<CompletedPart> parts) {
        CompleteMultipartUploadRequest req = CompleteMultipartUploadRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(parts).build())
                .build();
        s3Client.completeMultipartUpload(req);
    }

    @Override
    public void abortMultipartUpload(String key, String uploadId) {
        s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .uploadId(uploadId)
                .build());
    }

    @Override
    public List<Part> listParts(String key, String uploadId) {
        ListPartsRequest req = ListPartsRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .uploadId(uploadId)
                .build();
        return s3Client.listParts(req).parts();
    }

    @Override
    public String presignDownload(String key, String fileName, Duration expiry) {
        // RFC 5987：filename*=UTF-8''percent-encoded，中文名不乱码
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .responseContentDisposition("attachment; filename*=UTF-8''" + encoded)
                .build();
        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(expiry)
                .getObjectRequest(getReq)
                .build();
        return s3Presigner.presignGetObject(presignReq).url().toString();
    }

    @Override
    public String presignPreview(String key, String contentType, Duration expiry) {
        GetObjectRequest.Builder builder = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .responseContentDisposition("inline");
        if (contentType != null) {
            builder.responseContentType(contentType);
        }
        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(expiry)
                .getObjectRequest(builder.build())
                .build();
        return s3Presigner.presignGetObject(presignReq).url().toString();
    }

    @Override
    public void deleteObject(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build());
    }

    @Override
    public void deleteObjects(List<String> keys) {
        for (int i = 0; i < keys.size(); i += BATCH_DELETE_SIZE) {
            List<ObjectIdentifier> ids = keys.subList(i, Math.min(i + BATCH_DELETE_SIZE, keys.size()))
                    .stream()
                    .map(k -> ObjectIdentifier.builder().key(k).build())
                    .toList();
            s3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(props.getBucket())
                    .delete(Delete.builder().objects(ids).quiet(true).build())
                    .build());
        }
    }

    @Override
    public InputStream getObject(String key) {
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build());
    }

    @Override
    public byte[] getObjectBytes(String key, long maxSize) {
        HeadObjectResponse head = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build());
        if (head.contentLength() > maxSize) {
            throw new BusinessException(400, "内容过大，请下载后编辑");
        }
        ResponseBytes<GetObjectResponse> bytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder().bucket(props.getBucket()).key(key).build());
        return bytes.asByteArray();
    }
}
