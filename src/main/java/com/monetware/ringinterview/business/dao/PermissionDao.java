package com.monetware.ringinterview.business.dao;


import com.monetware.ringinterview.business.pojo.po.BasePermission;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * @author Simo
 * @date 2019-02-27
 */
@Mapper
@Repository
public interface PermissionDao extends MyMapper<BasePermission> {


}
