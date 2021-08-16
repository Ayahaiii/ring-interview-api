package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/28 16:38
 */
@Data
public class InterviewVO extends PageParam {

    private List<Integer> interviewId;

    private Integer sampleId;

    private String sampleCondition;

    private String keyword;

    private Integer status;

    private Integer userId;
}
