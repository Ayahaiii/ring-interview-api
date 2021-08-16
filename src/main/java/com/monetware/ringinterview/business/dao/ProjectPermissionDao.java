package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.po.BaseProjectPermission;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:03
 */
@Mapper
@Repository
public interface ProjectPermissionDao extends MyMapper<BaseProjectPermission> {

    List<String> getProjectPermissionForUser(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

}
