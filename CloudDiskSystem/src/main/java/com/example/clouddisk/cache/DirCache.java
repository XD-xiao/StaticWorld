package com.example.clouddisk.cache;

import com.example.common.utils.RefreshTokenRedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 目录列表缓存（只访问 Redis，不写业务逻辑）。
 *
 * <p>String key：{@code clouddisk:dir:{repoId}:{parentId}:{page}:{size}}，value 为目录列表 JSON，
 * 默认 TTL 60 秒，也可由调用方通过 {@link #put} 的 ttlSeconds 覆盖。</p>
 */
@Component
@RequiredArgsConstructor
public class DirCache {

    private static final String KEY_PREFIX = "clouddisk:dir:";
    private static final long DEFAULT_TTL_SECONDS = 60;

    private final StringRedisTemplate redis;

    /**
     * 读取目录列表 JSON；未命中返回 {@code null}。
     */
    public String get(Long repoId, Long parentId, int page, int size) {
        return redis.opsForValue().get(key(repoId, parentId, page, size));
    }

    /**
     * 写入目录列表 JSON。
     *
     * @param json       目录列表 JSON（子节点数组 + 分页信息）
     * @param ttlSeconds 缓存有效期（秒）
     */
    public void put(Long repoId, Long parentId, int page, int size, String json, long ttlSeconds) {
        if (repoId == null || json == null) {
            return;
        }
        redis.opsForValue().set(key(repoId, parentId, page, size), json, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 写入目录列表 JSON，使用默认 TTL（60 秒）。
     */
    public void put(Long repoId, Long parentId, int page, int size, String json) {
        put(repoId, parentId, page, size, json, DEFAULT_TTL_SECONDS);
    }

    /**
     * 失效指定目录的全部分页缓存（该目录下发生增 / 删 / 改 / 重命名时调用）。
     */
    public void evict(Long repoId, Long parentId) {
        if (repoId != null && parentId != null) {
            RefreshTokenRedisUtil.deleteByPattern(redis, dirPrefix(repoId, parentId) + "*");
        }
    }

    private String key(Long repoId, Long parentId, int page, int size) {
        return dirPrefix(repoId, parentId) + page + ":" + size;
    }

    private String dirPrefix(Long repoId, Long parentId) {
        return KEY_PREFIX + repoId + ":" + parentId + ":";
    }
}
