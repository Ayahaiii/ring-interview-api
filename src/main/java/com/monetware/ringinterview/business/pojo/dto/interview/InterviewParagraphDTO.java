package com.monetware.ringinterview.business.pojo.dto.interview;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewParagraph;
import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/2/27 16:52
 */
@Data
public class InterviewParagraphDTO extends BaseProjectInterviewParagraph {

    private Integer status;

    private String speakName;

    private String fileName;

    private String createName;

    private Integer type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

}
