package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

@Data
public class ProjectInterviewFileAppVO extends BaseVO {

    private Integer interviewId;


    /**
     * 状态:
     * 1.录音 2.附件
     */
    private Integer type;
}
