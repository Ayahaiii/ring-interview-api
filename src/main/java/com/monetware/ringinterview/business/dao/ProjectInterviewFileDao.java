package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.interview.FileCountDTO;
import com.monetware.ringinterview.business.pojo.dto.interview.ProjectInterviewFileDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewFile;
import com.monetware.ringinterview.business.pojo.vo.interview.ProjectInterviewFileAppVO;
import com.monetware.ringinterview.business.pojo.vo.interview.ProjectInterviewFileVO;
import com.monetware.ringinterview.business.pojo.vo.project.ProjectFileVO;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:10
 */
@Mapper
@Repository
public interface ProjectInterviewFileDao extends MyMapper<BaseProjectInterviewFile> {

    /**
     * 获取访谈录音(附件)列表
     * @param interviewFileVO
     * @return
     */
    List<ProjectInterviewFileDTO> getInterviewFileList(ProjectInterviewFileVO interviewFileVO);

    /**
     * 获取访谈录音(附件)列表
     * @param interviewFileAppVO
     * @return
     */
    List<ProjectInterviewFileDTO> getInterviewFileListApp(ProjectInterviewFileAppVO interviewFileAppVO);

    /**
     * 获取录音/附件数量
     * @param interviewId
     * @return
     */
    FileCountDTO getFileCount(@Param("interviewId") Integer interviewId);

    /**
     * 获取录音/附件信息
     * @param fileVO
     * @return
     */
    ProjectInterviewFileDTO getFileMessage(ProjectFileVO fileVO);
}
