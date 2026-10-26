package com.example.clouddisk.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 分片上传会话缓存（只访问 Redis，不写业务逻辑）。
 *
 * <p>会话 Hash key：{@code clouddisk:upload:{userId}:{uploadId}}，字段：</p>
 * <ul>
 *     <li>{@code fileName}：文件名；</li>
 *     <li>{@code repoId}：目标仓库 ID；</li>
 *     <li>{@code parentId}：目标父目录 ID（0 表示仓库根）；</li>
 *     <li>{@code contentType}：内容类型；</li>
 *     <li>{@code size}：文件总大小（字节）；</li>
 *     <li>{@code totalParts}：总分片数；</li>
 *     <li>{@code contentHash}：内容哈希（SHA-256，秒传用，可选）；</li>
 *     <li>{@code status}：{@code uploading}（上传中）/ {@code ready}（分片齐，待合并）/ {@code completed}（已合并）；</li>
 *     <li>{@code createTime}：会话创建时间。</li>
 * </ul>
 *
 * <p>分片列表 Hash key：{@code clouddisk:upload:{userId}:{uploadId}:parts}，
 * field = partNumber、value = etag。会话与分片 TTL 均为 24 小时，覆盖跨天续传。</p>
 */
@Component
@RequiredArgsConstructor
public class UploadSessionCache {

    private static final String KEY_PREFIX = "clouddisk:upload:";
    private static final long TTL_SECONDS = 24 * 60 * 60;

    private final StringRedisTemplate redis;

    /**
     * 初始化分片上传会话，写入会话字段并设置 24 小时 TTL。
     *
     * @param userId   发起上传的用户 ID
     * @param uploadId 分片上传会话 ID
     * @param fields   会话字段（key-value，见类注释字段说明）
     */
    public void initSession(Long userId, String uploadId, Map<String, String> fields) {
        if (userId == null || uploadId == null || fields == null || fields.isEmpty()) {
            return;
        }
        String key = sessionKey(userId, uploadId);
        redis.opsForHash().putAll(key, fields);
        redis.expire(key, TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 读取会话字段；会话不存在时返回 {@code null}。
     *
     * @return 会话字段 Map，未命中返回 {@code null}
     */
    public Map<String, String> getSession(Long userId, String uploadId) {
        return readHash(sessionKey(userId, uploadId));
    }

    /**
     * 更新会话中单个字段（如 status、contentHash 等）。
     */
    public void updateField(Long userId, String uploadId, String field, String value) {
        if (userId == null || uploadId == null || field == null || value == null) {
            return;
        }
        String key = sessionKey(userId, uploadId);
        redis.opsForHash().put(key, field, value);
        redis.expire(key, TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 设置会话状态（{@code uploading} / {@code ready} / {@code completed}）。
     */
    public void setStatus(Long userId, String uploadId, String status) {
        updateField(userId, uploadId, "status", status);
    }

    /**
     * 记录一个分片（partNumber → etag），并刷新 parts 与会话 TTL。
     *
     * @param partNumber 分片编号
     * @param etag       该分片上传后返回的 ETag
     */
    public void addPart(Long userId, String uploadId, int partNumber, String etag) {
        if (userId == null || uploadId == null || etag == null) {
            return;
        }
        String partsKey = partsKey(userId, uploadId);
        redis.opsForHash().put(partsKey, String.valueOf(partNumber), etag);
        redis.expire(partsKey, TTL_SECONDS, TimeUnit.SECONDS);
        // 会话随分片写入同步续期，避免长时间上传期间会话过期
        redis.expire(sessionKey(userId, uploadId), TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 读取全部分片（partNumber → etag）；无分片时返回 {@code null}。
     *
     * @return 分片 Map，未命中返回 {@code null}
     */
    public Map<String, String> getParts(Long userId, String uploadId) {
        return readHash(partsKey(userId, uploadId));
    }

    /**
     * 删除会话及分片缓存（合并成功或放弃上传后调用）。
     */
    public void deleteSession(Long userId, String uploadId) {
        if (userId == null || uploadId == null) {
            return;
        }
        redis.delete(List.of(sessionKey(userId, uploadId), partsKey(userId, uploadId)));
    }

    private String sessionKey(Long userId, String uploadId) {
        return KEY_PREFIX + userId + ":" + uploadId;
    }

    private String partsKey(Long userId, String uploadId) {
        return KEY_PREFIX + userId + ":" + uploadId + ":parts";
    }

    private Map<String, String> readHash(String key) {
        Map<Object, Object> entries = redis.opsForHash().entries(key);
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        Map<String, String> result = new HashMap<>(entries.size());
        entries.forEach((k, v) -> result.put(String.valueOf(k), String.valueOf(v)));
        return result;
    }
}
