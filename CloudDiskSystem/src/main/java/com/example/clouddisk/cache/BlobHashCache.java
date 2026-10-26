package com.example.clouddisk.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 秒传哈希集合缓存（只访问 Redis，不写业务逻辑）。
 *
 * <p>Set key：{@code clouddisk:blob:hash}，value 为 SHA-256 十六进制字符串，
 * 是 {@code cloud_blob} 表的「存在性缓存」，无 TTL（随库维护）。</p>
 */
@Component
@RequiredArgsConstructor
public class BlobHashCache {

    private static final String KEY = "clouddisk:blob:hash";

    private final StringRedisTemplate redis;

    /**
     * 判断内容哈希是否已存在（命中即可秒传）。
     *
     * @param hash 内容哈希（SHA-256）
     * @return {@code true} 表示缓存命中，{@code false} 表示未命中或入参为 {@code null}
     */
    public boolean exists(String hash) {
        if (hash == null) {
            return false;
        }
        return Boolean.TRUE.equals(redis.opsForSet().isMember(KEY, hash));
    }

    /**
     * 新增单个内容哈希（新内容对象写入 cloud_blob 后调用）。
     */
    public void add(String hash) {
        if (hash != null) {
            redis.opsForSet().add(KEY, hash);
        }
    }

    /**
     * 移除单个内容哈希（内容对象引用归零删除后调用）。
     */
    public void remove(String hash) {
        if (hash != null) {
            redis.opsForSet().remove(KEY, hash);
        }
    }

    /**
     * 批量新增内容哈希。
     */
    public void addAll(Collection<String> hashes) {
        if (hashes != null && !hashes.isEmpty()) {
            redis.opsForSet().add(KEY, hashes.toArray(new String[0]));
        }
    }

    /**
     * 批量移除内容哈希。
     */
    public void removeAll(Collection<String> hashes) {
        if (hashes != null && !hashes.isEmpty()) {
            redis.opsForSet().remove(KEY, hashes.toArray(new Object[0]));
        }
    }
}
