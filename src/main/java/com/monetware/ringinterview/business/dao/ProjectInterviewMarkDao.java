package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.stat.CodeViewDTO;
import com.monetware.ringinterview.business.pojo.dto.stat.InterviewMarkDTO;
import com.monetware.ringinterview.business.pojo.dto.stat.SampleCodeMarkDTO;
import com.monetware.ringinterview.business.pojo.dto.stat.StatDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewMark;
import com.monetware.ringinterview.business.pojo.vo.stat.*;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:28
 */
@Mapper
@Repository
public interface ProjectInterviewMarkDao extends MyMapper<BaseProjectInterviewMark> {

    List<StatDTO> getProjectStatList(StatVO statVO);

    List<StatDTO> getProjectStatAppList(StatAppVO statAppVO);

    List<CodeViewDTO> getCodeViewData(CodeViewVO codeViewVO);

    List<InterviewMarkDTO> getInterviewMarkList(InterviewMarkSearchVO searchVO);

    List<SampleCodeMarkDTO> getSampleCodeMarkList(InterviewMarkFindVO markFindVO);
}
