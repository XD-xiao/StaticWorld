package com.example.clouddisk.permission;

import com.example.api.client.UserClient;
import com.example.common.domain.PermissionPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 云盘服务权限点自动同步组件。
 *
 * <p>应用启动完成后扫描本服务所有已注册的 HandlerMethod，读取方法（或类）上的
 * {@link PreAuthorize} 注解，提取其中的 {@code hasAuthority('...')} /
 * {@code hasAnyAuthority('...')} 权限标识，并同步到用户中心的 {@code sys_permission} 表
 * （通过 {@link UserClient} 远程调用，而非直接写本地库）。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionSyncRunner implements ApplicationRunner {

    /** 本服务接口路径前缀，用于清理失效权限点时隔离范围。 */
    private static final String PATH_PREFIX = "/api/clouddisksystem/";

    /** 匹配 hasAuthority('xxx') */
    private static final Pattern HAS_AUTHORITY = Pattern.compile("hasAuthority\\('([^']+)'\\)");

    /** 匹配 hasAnyAuthority('a','b') */
    private static final Pattern HAS_ANY_AUTHORITY = Pattern.compile("hasAnyAuthority\\(([^)]*)\\)");

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final UserClient userClient;

    @Override
    public void run(ApplicationArguments args) {
        syncPermissions();
    }

    /**
     * 执行权限点扫描与同步。
     */
    public void syncPermissions() {
        List<PermissionPoint> points = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            Set<String> keys = extractPermissionKeys(entry.getValue());
            if (keys.isEmpty()) {
                continue;
            }
            String name = resolvePermissionName(entry.getValue());
            String path = buildPath(entry.getKey());
            for (String key : keys) {
                // 名称仅对单一权限标识有意义；多个权限标识无法一一对应，回退为 null
                points.add(new PermissionPoint(key, keys.size() == 1 ? name : null, path));
            }
        }

        try {
            userClient.syncApiPermissions(PATH_PREFIX, points);
            log.info("云盘权限点同步完成，数量：{}", points.size());
        } catch (Exception e) {
            // 用户中心未就绪或不可用时不应阻塞本服务启动，仅记录告警
            log.warn("云盘权限点同步失败：{}", e.getMessage());
        }
    }

    /**
     * 从 HandlerMethod 的方法或所属类上提取 {@link PreAuthorize} 中的权限标识。
     */
    private Set<String> extractPermissionKeys(HandlerMethod handlerMethod) {
        PreAuthorize preAuthorize = AnnotatedElementUtils.findMergedAnnotation(
                handlerMethod.getMethod(), PreAuthorize.class);
        if (preAuthorize == null) {
            preAuthorize = AnnotatedElementUtils.findMergedAnnotation(
                    handlerMethod.getBeanType(), PreAuthorize.class);
        }
        if (preAuthorize == null) {
            return Collections.emptySet();
        }
        return parseAuthorities(preAuthorize.value());
    }

    /**
     * 提取方法或类上的 {@link PermissionName}，未配置返回 {@code null}。
     */
    private String resolvePermissionName(HandlerMethod handlerMethod) {
        PermissionName permissionName = AnnotatedElementUtils.findMergedAnnotation(
                handlerMethod.getMethod(), PermissionName.class);
        if (permissionName == null) {
            permissionName = AnnotatedElementUtils.findMergedAnnotation(
                    handlerMethod.getBeanType(), PermissionName.class);
        }
        return permissionName == null ? null : permissionName.value();
    }

    /**
     * 解析 SpEL 表达式中的权限标识。
     */
    private Set<String> parseAuthorities(String expression) {
        Set<String> keys = new LinkedHashSet<>();

        Matcher authorityMatcher = HAS_AUTHORITY.matcher(expression);
        while (authorityMatcher.find()) {
            keys.add(authorityMatcher.group(1).trim());
        }

        Matcher anyAuthorityMatcher = HAS_ANY_AUTHORITY.matcher(expression);
        while (anyAuthorityMatcher.find()) {
            for (String part : anyAuthorityMatcher.group(1).split(",")) {
                String key = part.trim();
                if (key.length() >= 2 && key.startsWith("'") && key.endsWith("'")) {
                    key = key.substring(1, key.length() - 1);
                }
                if (!key.isBlank()) {
                    keys.add(key);
                }
            }
        }

        return keys;
    }

    /**
     * 组合 HTTP 方法与接口路径，如 {@code POST /api/clouddisksystem/file/upload}。
     */
    private String buildPath(RequestMappingInfo info) {
        Set<String> patterns = resolvePatterns(info);
        Set<RequestMethod> methods = info.getMethodsCondition().getMethods();

        String methodPart = methods.isEmpty()
                ? "ANY"
                : methods.stream().map(Enum::name).sorted().collect(Collectors.joining(","));
        String pathPart = patterns.stream().sorted().collect(Collectors.joining(","));

        return methodPart + " " + pathPart;
    }

    private Set<String> resolvePatterns(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() != null) {
            return info.getPathPatternsCondition().getPatternValues();
        }
        if (info.getPatternsCondition() != null) {
            return info.getPatternsCondition().getPatterns();
        }
        return Collections.emptySet();
    }
}
