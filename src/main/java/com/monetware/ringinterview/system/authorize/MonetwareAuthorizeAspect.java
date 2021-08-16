package com.monetware.ringinterview.system.authorize;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.dto.sample.SamplePropertyDTO;
import com.monetware.ringinterview.business.service.project.ProjectService;
import com.monetware.ringinterview.business.service.project.SampleService;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.ringinterview.system.util.redis.RedisUtil;
import com.monetware.ringinterview.system.util.spring.SpringBeanUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Simo
 * @date 2020-02-20
 */
@Aspect
@Component
public class MonetwareAuthorizeAspect {


    @Pointcut("@annotation(monetwareAuthorize)")
    public void doAuthorize(MonetwareAuthorize monetwareAuthorize) {
    }


    @Around("doAuthorize(monetwareAuthorize)")
    public Object deBefore(ProceedingJoinPoint pjp, MonetwareAuthorize monetwareAuthorize) throws Throwable {

        // 获取当前项目ID
        String projectId;
        Object[] args = pjp.getArgs();
        Object obj = args[0];
        if (obj instanceof Integer) {
            // 获取restful上ID
            projectId = obj.toString();
        } else {
            // 获取json数据中ID
            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject json = JSONObject.parseObject(objectMapper.writeValueAsString(args[0]));
            projectId = json.getString("projectId");
            if (projectId == null)  projectId = json.getString("id");
        }
        RedisUtil redisUtil = SpringBeanUtil.getBean(RedisUtil.class);
        // 用于获取接口路径
        String roleKey = "RI_ROLE_" + projectId + "_" + ThreadLocalManager.getUserId();
        int roleId = AuthorizedConstants.ROLE_INTERVIEWER;
        if (redisUtil.hasKey(roleKey)) {
            roleId = Integer.parseInt(redisUtil.get(roleKey).toString());
        }
        if (roleId == AuthorizedConstants.ROLE_INTERVIEWER) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String url = request.getServletPath();
            if (url.contains("sample/save")
                    || url.contains("sample/batch/import")) {
                SampleService sampleService = SpringBeanUtil.getBean(SampleService.class);
                SamplePropertyDTO propertyDTO = sampleService.getSampleProperty(Integer.parseInt(projectId));
                if (propertyDTO.getSampleEdit() && propertyDTO.getAllowAddSample() == 1) {
                    // 访问员并且允许新增样本 放行以上接口
                    return pjp.proceed();
                }
            }
        }

        // 没有传递项目ID视为非法攻击
        if (projectId == null) throw new ServiceException(ErrorCode.ROLE_WITHOUT);

        // 验证权限
        String authKey = "RI_AUTH_" + projectId + "_" + ThreadLocalManager.getUserId();
        List<String> permissions;
        if (redisUtil.hasKey(authKey)) {
            permissions = (List<String>) redisUtil.get(authKey);
        } else {
            ProjectService projectService = SpringBeanUtil.getBean(ProjectService.class);
            permissions = projectService.getProjectPermission(Integer.parseInt(projectId));
            redisUtil.set(authKey, permissions);
        }

        // 没有权限 即未分配权限
        if (permissions == null || permissions.size() == 0) throw new ServiceException(ErrorCode.ROLE_WITHOUT);

        // 判断是否拥有所有权限
        if (permissions.contains(AuthorizedConstants.R_ALL)) {
            return pjp.proceed();
        }

        // 判断是否有当前访问接口权限
        String role = monetwareAuthorize.role();
        if (permissions.contains(role))  {
            return pjp.proceed();
        } else {
            throw new ServiceException(ErrorCode.ROLE_WITHOUT);
        }

    }

}
