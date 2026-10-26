package com.example.common.domain;

/**
 * 接口权限点同步项，供其他服务把扫描到的接口权限点同步到用户中心（sys_permission）。
 *
 * @param permKey  权限标识（如 clouddisk:file:upload）
 * @param permName 权限名称，可为 {@code null}（同步方回退为 permKey）
 * @param path     接口路径（如 POST /api/clouddisksystem/file/upload）
 */
public record PermissionPoint(String permKey, String permName, String path) {
}
