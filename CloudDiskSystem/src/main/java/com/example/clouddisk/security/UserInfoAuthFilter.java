package com.example.clouddisk.security;

import com.example.api.client.UserClient;
import com.example.common.auth.UserIdentity;
import com.example.common.auth.UserIdentityCodec;
import com.example.common.domain.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 云盘服务用户信息认证过滤器。
 *
 * <p>从网关透传的统一身份头解析出 {@link UserIdentity}（头名与解析逻辑统一由
 * {@link UserIdentityCodec} 定义），再通过用户中心（Feign）查询该用户的权限点，
 * 最终构造带权限集合的 {@code Authentication} 放入安全上下文，供下游的
 * {@code @PreAuthorize} 授权校验使用。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserInfoAuthFilter extends OncePerRequestFilter {

    private final UserClient userClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 网关已校验 token，只要存在账号标识即视为已认证；
        // 即使没有权限，也保留一个空权限的 Authentication，让 @PreAuthorize 走 403 而不是 401。
        UserIdentity identity = UserIdentityCodec.read(request);
        if (identity.isPresent()) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(identity.userId(), null, loadAuthorities(identity));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 通过用户中心查询用户聚合后的权限点（perm_key）作为授权标识。
     *
     * <p>用户中心不可用或返回异常时按无权限处理（403），避免以 500 暴露内部错误。</p>
     */
    private List<GrantedAuthority> loadAuthorities(UserIdentity identity) {
        try {
            Result<List<String>> result = userClient.getAuthorities(identity.userId());
            if (result == null || result.getCode() == null || result.getCode() != 200 || result.getData() == null) {
                return List.of();
            }
            return result.getData().stream()
                    .filter(permKey -> permKey != null && !permKey.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .map(authority -> (GrantedAuthority) authority)
                    .toList();
        } catch (Exception e) {
            log.warn("查询用户权限失败，userId={}", identity.userId(), e);
            return List.of();
        }
    }
}
