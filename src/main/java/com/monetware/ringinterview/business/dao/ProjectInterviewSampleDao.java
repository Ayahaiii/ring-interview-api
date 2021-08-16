package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewSample;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Linked
 * @date 2020/3/2 21:05
 */
@Mapper
@Repository
public interface ProjectInterviewSampleDao extends MyMapper<BaseProjectInterviewSample> {


}
