package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.stat.InterviewCodeSearchDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewCode;
import com.monetware.ringinterview.business.pojo.vo.stat.InterviewCodeSearchVO;
import com.monetware.ringinterview.business.pojo.vo.stat.InterviewCodeVO;
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
public interface ProjectInterviewCodeDao extends MyMapper<BaseProjectInterviewCode> {

    List<InterviewCodeSearchDTO> getInterviewCodeList(InterviewCodeSearchVO codeSearchVO);

    List<InterviewCodeSearchDTO> getAllInterviewCodeList(InterviewCodeVO codeVO);

}
