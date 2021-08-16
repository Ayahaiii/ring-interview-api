package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.interview.InterviewDetailDTO;
import com.monetware.ringinterview.business.pojo.dto.interview.InterviewListDTO;
import com.monetware.ringinterview.business.pojo.dto.interview.SampleOwnerDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterview;
import com.monetware.ringinterview.business.pojo.vo.interview.InterviewDeleteVO;
import com.monetware.ringinterview.business.pojo.vo.interview.InterviewDetailVO;
import com.monetware.ringinterview.business.pojo.vo.interview.InterviewVO;
import com.monetware.ringinterview.business.pojo.vo.interview.SampleOwnerVO;
import com.monetware.ringinterview.business.pojo.vo.monitor.ProgressMonitoringVO;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Linked
 * @date 2020/2/17 18:10
 */
@Mapper
@Repository
public interface ProjectInterviewDao extends MyMapper<BaseProjectInterview> {

    /**
     * 访谈列表
     *
     * @param interviewVO
     * @return
     */
    List<InterviewListDTO> getInterviewList(InterviewVO interviewVO);

    /**
     * 归属样本查询
     *
     * @param sampleOwnerVO
     * @return
     */
    List<SampleOwnerDTO> getSampleOwner(SampleOwnerVO sampleOwnerVO);

    /**
     * 访谈详情
     *
     * @param detailVO
     * @return
     */
    InterviewDetailDTO getInterviewDetail(InterviewDetailVO detailVO);

    /**
     * 删除/批量删除访谈
     * @param deleteVO
     * @return
     */
    Integer deleteInterview(InterviewDeleteVO deleteVO);

    Integer updateInterviewAdd(BaseProjectInterview interview);

    Integer updateinterviewDel(BaseProjectInterview interview);

    List<Map<String, Object>> selectInterviewDayCount(ProgressMonitoringVO monitoringVO);

    Map<String, Object> getInterviewStatData(@Param("projectId") Integer projectId);
}
