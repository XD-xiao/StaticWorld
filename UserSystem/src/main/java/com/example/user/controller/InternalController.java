package com.example.user.controller;

import com.example.common.domain.PermissionPoint;
import com.example.common.domain.Result;
import com.example.user.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户中心对外（服务间）内部接口。
 *
 * <p>供其他业务服务复用用户中心的鉴权与权限能力，例如云盘服务通过 Feign 调用
 * {@code /internal/authorities} 获取用户权限点、通过 {@code /internal/permissions/sync}
 * 把自身扫描到的接口权限点同步到用户中心。</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userservice/internal")
public class InternalController {

    private final PermissionService permissionService;

    /**
     * 查询指定用户聚合后的全部接口权限点标识（perm_key）。
     */
    @GetMapping("/authorities")
    public Result<List<String>> getAuthorities(@RequestParam Long userId) {
        return Result.success("获取用户权限成功", permissionService.getPermKeysByUserId(userId));
    }

    /**
     * 同步接口权限点到用户中心（按路径前缀隔离各服务）。
     */
    @PostMapping("/permissions/sync")
    public Result<Void> syncApiPermissions(@RequestParam String pathPrefix,
                                           @RequestBody List<PermissionPoint> points) {
        permissionService.syncApiPermissions(pathPrefix, points);
        return Result.success("权限同步成功", null);
    }
}
