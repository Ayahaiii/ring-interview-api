package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.interview.InterviewParagraphDTO;
import com.monetware.ringinterview.business.pojo.dto.stat.AnalystParagraphAutoDTO;
import com.monetware.ringinterview.business.pojo.dto.stat.AnalystParagraphDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewParagraph;
import com.monetware.ringinterview.business.pojo.vo.interview.ParagraphListAppVO;
import com.monetware.ringinterview.business.pojo.vo.interview.ParagraphListVO;
import com.monetware.ringinterview.business.pojo.vo.stat.AnalystParagraphVO;
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
public interface ProjectInterviewParagraphDao extends MyMapper<BaseProjectInterviewParagraph> {

    /**
     * 访谈文本列表
     * @param paragraphListVO
     * @return
     */
    List<InterviewParagraphDTO> getParagraphList(ParagraphListVO paragraphListVO);

    /**
     * App访谈文本列表
     * @param paragraphListAppVO
     * @return
     */
    List<InterviewParagraphDTO> getParagraphListApp(ParagraphListAppVO paragraphListAppVO);

    List<String> getParagraphsBySampleIds(@Param("projectId") Integer projectId, @Param("sampleIds") List<Integer> sampleIds);

    List<AnalystParagraphDTO> getParagraphsBySampleId(AnalystParagraphVO paragraphVO);

    List<AnalystParagraphAutoDTO> getParagraphsByProjectId(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    Integer insertBatchParagraph(@Param("paragraphs")List<BaseProjectInterviewParagraph> paragraphs);
}
