package com.monetware.ringinterview.business.pojo.vo.stat;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
public class InterviewMarkVO {

    private Integer id;

    private Integer projectId;

    private Integer interviewId;

    private Integer paragraphId;

    private Integer sampleId;

    private Integer codeId;

    private Integer begin;

    private Integer len;

    private String note;

}
