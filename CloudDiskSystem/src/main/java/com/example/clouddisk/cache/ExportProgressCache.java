package com.example.clouddisk.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 导出任务进度缓存（只访问 Redis，不写业务逻辑）。
 *
 * <p>String key：{@code clouddisk:export:{taskId}}，value 为进度 JSON，
 * TTL 由调用方传入（任务完成后 30 分钟，与 zip 临时有效期一致）。数据库 cloud_export_task 仍为真相源。</p>
 */
@Component
@RequiredArgsConstructor
public class ExportProgressCache {

    private static final String KEY_PREFIX = "clouddisk:export:";

    private final StringRedisTemplate redis;

    /**
     * 写入导出进度 JSON。
     *
     * @param taskId     导出任务 ID
     * @param json       进度 JSON（status / totalFiles / processedFiles / storageKey / errorMsg）
     * @param ttlSeconds 缓存有效期（秒）
     */
    public void put(Long taskId, String json, long ttlSeconds) {
        if (taskId == null || json == null) {
            return;
        }
        redis.opsForValue().set(key(taskId), json, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 读取导出进度 JSON；未命中返回 {@code null}。
     */
    public String get(Long taskId) {
        if (taskId == null) {
            return null;
        }
        return redis.opsForValue().get(key(taskId));
    }

    /**
     * 删除导出进度缓存。
     */
    public void delete(Long taskId) {
        if (taskId != null) {
            redis.delete(key(taskId));
        }
    }

    private String key(Long taskId) {
        return KEY_PREFIX + taskId;
    }
}
