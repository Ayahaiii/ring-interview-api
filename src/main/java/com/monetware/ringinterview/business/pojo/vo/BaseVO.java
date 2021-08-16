package com.monetware.ringinterview.business.pojo.vo;

import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.system.util.redis.RedisUtil;
import com.monetware.ringinterview.system.util.spring.SpringBeanUtil;
import com.monetware.threadlocal.ThreadLocalManager;

/**
 * @author Simo
 * @date 2020-03-03
 */
public class BaseVO {

    private Integer projectId;

    private Integer checkRole;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getCheckRole() {
        RedisUtil redisUtil = SpringBeanUtil.getBean(RedisUtil.class);
        String roleKey = "RI_ROLE_" + this.projectId + "_" + ThreadLocalManager.getUserId();
        if (redisUtil.hasKey(roleKey)) {
            return Integer.parseInt(redisUtil.get(roleKey).toString());
        }
        return AuthorizedConstants.ROLE_INTERVIEWER;
    }

    public void setCheckRole(Integer checkRole) {
        this.checkRole = checkRole;
    }
}
