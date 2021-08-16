package com.monetware.ringinterview.business.pojo.vo.interview;

import lombok.Data;

/**
 * @author Linked
 * @date 2020/2/25 17:29
 */
@Data
public class InterviewDetailVO {

    private Integer projectId;

    private Integer interviewId;

    /**
     * 1:审核通过 2:退回到整理阶段 3:退回到访谈阶段 4:拒访
     */
    private Integer valid;

    private String auditNote;

    private Integer status;

    private String location;
}
