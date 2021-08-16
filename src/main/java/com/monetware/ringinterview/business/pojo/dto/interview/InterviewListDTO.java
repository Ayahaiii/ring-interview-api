package com.monetware.ringinterview.business.pojo.dto.interview;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/2/24 17:15
 */
@Data
public class InterviewListDTO{

    private Integer id;

    private String name;

    private String sampleName;

    private Integer managerId;

    private String managerName;

    private String assistantName;

    /**
     * 访谈时长
     */
    private String interviewTimeLen;

    /**
     * 信息量
     */
    private String fileSize;

    /**
     * 计划开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planStartTime;

    private String address;

    private String planText;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planEndTime;

    /**
     * 开始访谈时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    /**
     * 整理完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date docEndTime;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;

    /**
     * 状态
     */
    private Integer status;

    private FileCountDTO fileCountDTO;

}
