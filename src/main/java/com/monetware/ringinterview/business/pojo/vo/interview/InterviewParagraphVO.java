package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewParagraph;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/2/26 21:27
 */
@Data
public class InterviewParagraphVO extends BaseProjectInterviewParagraph {

    private Integer projectId;

    private Integer status;

}
