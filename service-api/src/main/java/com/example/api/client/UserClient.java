package com.example.api.client;

import com.example.common.domain.PermissionPoint;
import com.example.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户中心（user-service）远程调用客户端。
 *
 * <p>供其他业务服务（如云盘）复用用户中心的鉴权与权限能力：
 * 查询某用户的权限点标识、以及把本服务扫描到的接口权限点同步到用户中心。</p>
 */
@FeignClient(name = "user-service", path = "/api/userservice")
public interface UserClient {

    /**
     * 查询指定用户聚合后的全部接口权限点标识（perm_key）。
     *
     * @param userId 用户 ID
     */
    @GetMapping("/internal/authorities")
    Result<List<String>> getAuthorities(@RequestParam("userId") Long userId);

    /**
     * 同步接口权限点到用户中心，按路径前缀隔离各服务的权限点。
     *
     * @param pathPrefix 本服务接口路径前缀（如 /api/clouddisksystem/），用于清理失效权限点时隔离范围
     * @param points     本次扫描到的权限点集合
     */
    @PostMapping("/internal/permissions/sync")
    Result<Void> syncApiPermissions(@RequestParam("pathPrefix") String pathPrefix,
                                    @RequestBody List<PermissionPoint> points);
}
