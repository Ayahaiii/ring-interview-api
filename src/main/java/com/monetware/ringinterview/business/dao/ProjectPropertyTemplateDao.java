package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.po.BaseProjectPropertyTemplate;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Linked
 * @date 2020/2/17 18:18
 */
@Mapper
@Repository
public interface ProjectPropertyTemplateDao extends MyMapper<BaseProjectPropertyTemplate> {

}
