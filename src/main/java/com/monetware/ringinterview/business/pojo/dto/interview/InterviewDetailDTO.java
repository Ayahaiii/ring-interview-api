package com.monetware.ringinterview.business.pojo.dto.interview;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringinterview.business.pojo.dto.sample.ManagerDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Linked
 * @date 2020/2/25 17:32
 */
@Data
public class InterviewDetailDTO {

    private Integer id;

    private String interviewName;

    private ManagerDTO manager;

    private List<ManagerDTO> assistantList;

    private List<ManagerDTO> sampleList;

    private Integer status;
    /**
     * 访谈计划
     */
    private String planText;

    /**
     * 备注
     */
    private String description;

    /**
     * 地址
     */
    private String address;

    /**
     * 访谈创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 计划访谈时长
     */
    private String planDuration;


    /**
     * 计划开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planStartTime;

    /**
     * 计划结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planEndTime;

    /**
     * 开始访谈时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;

    /**
     * 访谈结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 实际访谈耗时
     */
    private String interviewDuration;

    /**
     * 整理开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date docBeginTime;

    /**
     * 整理完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date docEndTime;

    /**
     * 整理资料耗时
     */
    private String docDuration;

    /**
     * 提交审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;

    /**
     * 审核完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditEndTime;

    /**
     * 审核耗时
     */
    private String auditDuration;

    private String auditNote;
}
