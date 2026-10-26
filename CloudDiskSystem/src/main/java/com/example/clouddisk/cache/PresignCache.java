package com.example.clouddisk.cache;

import com.example.common.utils.RefreshTokenRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 预签名 URL 缓存（只访问 Redis，不写业务逻辑）。
 *
 * <p>String key：{@code clouddisk:presign:{nodeId}:{type}}，{@code type} ∈ {@code preview} / {@code download}；
 * TTL 由调用方传入（应略小于预签名有效期，避免返回失效 URL）。</p>
 */
@Component
@RequiredArgsConstructor
public class PresignCache {

    private static final String KEY_PREFIX = "clouddisk:presign:";

    private final StringRedisTemplate redis;

    /**
     * 读取预签名 URL；未命中返回 {@code null}。
     */
    public String get(Long nodeId, String type) {
        if (nodeId == null || type == null) {
            return null;
        }
        return redis.opsForValue().get(key(nodeId, type));
    }

    /**
     * 写入预签名 URL。
     *
     * @param ttlSeconds 缓存有效期（秒）
     */
    public void put(Long nodeId, String type, String url, long ttlSeconds) {
        if (nodeId == null || type == null || url == null) {
            return;
        }
        redis.opsForValue().set(key(nodeId, type), url, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 失效指定节点的全部预签名 URL（文件内容变更 / 删除时调用）。
     */
    public void evict(Long nodeId) {
        if (nodeId != null) {
            RefreshTokenRedisUtil.deleteByPattern(redis, key(nodeId, "*"));
        }
    }

    private String key(Long nodeId, String type) {
        return KEY_PREFIX + nodeId + ":" + type;
    }
}
