package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.interview.SpeakerListDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewSpeaker;
import com.monetware.ringinterview.business.pojo.vo.interview.SpeakerDeleteVO;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:11
 */
@Mapper
@Repository
public interface ProjectInterviewSpeakerDao extends MyMapper<BaseProjectInterviewSpeaker> {

    /**
     * 删除/批量删除讲话者
     * @param deleteVO
     * @return
     */
    Integer deleteSpeaker(SpeakerDeleteVO deleteVO);


    /**
     * 讲话者列表
     *
     * @param interviewId
     * @param projectId
     * @return
     */
    List<SpeakerListDTO> getSpeakList(@Param("interviewId") Integer interviewId, @Param("projectId") Integer projectId);

    /**
     * 根据讲话者名字得到id
     * @param name
     * @param interviewId
     * @return
     */
    Integer checkSpeak(@Param("name") String name,@Param("interviewId")Integer interviewId);
}
