package com.monetware.ringinterview.system.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monetware.ringinterview.system.util.redis.RedisUtil;
import com.monetware.ringinterview.system.util.spring.SpringBeanUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import com.monetware.threadlocal.TokenContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simo
 * @date 2019-08-16
 */
public class TokenFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object p = authentication.getPrincipal();
            Object t = ((Map<String, Object>) p).get("user_name");
            ObjectMapper mapper = new ObjectMapper();
            TokenContext tokenContext = mapper.convertValue(t, TokenContext.class);
            // 缓存用户信息
            tokenContext.setToken(servletRequest.getAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE).toString());
            RedisUtil redisUtil = SpringBeanUtil.getBean(RedisUtil.class);
            String securityToken = tokenContext.getToken();
            Map map = new HashMap();
            map.put("data", null);
            if (securityToken == null) {
                map.put("code", "10001");
                map.put("message", "登陆信息已失效,请重新登陆");
                doResult(response, map);
                return;
            }
            String accessKey = "monetware:user:token:" + securityToken;
            if (tokenContext.getType().equals(2)) {
                accessKey = "monetware:manage:token:" + securityToken;
            }
            if (!redisUtil.hasKey(accessKey)) {
                map.put("code", "10002");
                map.put("message", "登陆信息已失效,请重新登陆");
                doResult(response, map);
                return;
            }
            String loginStatus = (String) redisUtil.get(accessKey);
            if ("OTHER_LOGIN".equals(loginStatus)) {
                map.put("code", "10003");
                map.put("message", "当前账号已在其他地方登录");
                doResult(response, map);
                redisUtil.remove(accessKey);
                return;
            }
            ThreadLocalManager.setTokenContext(tokenContext);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private void doResult(HttpServletResponse response, Map<String, Object> map) throws ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), map);
        } catch (Exception e) {
            throw new ServletException();
        }
    }
}
