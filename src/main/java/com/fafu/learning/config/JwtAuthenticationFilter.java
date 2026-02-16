package com.fafu.learning.config;

import com.fafu.learning.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // 白名单配置逻辑：放行登录接口
        if (shouldSkipAuthentication(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 拦截所有/api/**路径的请求（除了白名单）
        if (requestURI.startsWith("/api/")) {
            try {
                // 从请求头中获取JWT token
                String authorizationHeader = request.getHeader("Authorization");
                
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String token = authorizationHeader.substring(7);
                    
                    // 验证token有效性
                    if (jwtUtil.validateToken(token)) {
                        // 从token中提取用户名
                        String username = jwtUtil.getUsernameFromToken(token);
                        
                        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            // 加载用户详情（包含角色信息）
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            
                            // 创建认证对象，包含用户角色权限
                            UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            
                            // 设置安全上下文
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } else {
                        // Token无效，返回401未授权
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"code\":401,\"msg\":\"Token无效或已过期\"}");
                        return;
                    }
                } else {
                    // 缺少Authorization头，返回401未授权
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"code\":401,\"msg\":\"缺少Authorization头\"}");
                    return;
                }
            } catch (Exception e) {
                logger.error("JWT认证处理异常: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":401,\"msg\":\"认证失败\"}");
                return;
            }
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
    
    /**
     * 判断是否应该跳过JWT认证
     * 白名单配置逻辑：
     * 1. 放行登录接口 /api/auth/login
     * 2. 可以扩展其他不需要认证的接口
     * 
     * @param requestURI 请求URI
     * @return boolean true表示跳过认证，false表示需要认证
     */
    private boolean shouldSkipAuthentication(String requestURI) {
        // 白名单配置：这些接口不需要JWT认证
        return requestURI.equals("/api/auth/login");
    }
}