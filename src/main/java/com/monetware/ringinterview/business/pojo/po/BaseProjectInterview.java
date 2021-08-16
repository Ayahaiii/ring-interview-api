package com.monetware.ringinterview.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Linked
 * @date 2020/2/17 17:31
 */
@Data
@Table(name = "rs_project_interview")
public class BaseProjectInterview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    /**
     * 负责人
     */
    private Integer userId;

    /**
     * 访谈标题
     */
    private String name;

    /**
     * 访谈计划
     */
    private String planText;

    /**
     * 备注
     */
    private String description;

    /**
     * 访谈地址
     */
    private String address;

    private Date planStartTime;

    private Date planEndTime;

    /**
     * 访谈时长(秒)
     */
    private Long planDuration;

    /**
     * 信息量
     */
    private Long fileSize;

    /**
     * 访谈总时长
     */
    private Long interviewTimeLen;

    private Date beginTime;

    private Date endTime;

    /**
     * 访谈经纬度
     */
    private String location;

    /**
     * 资料整理开始时间
     */
    private Date docBeginTime;

    /**
     * 资料整理结束时间
     */
    private Date docEndTime;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核完成时间
     */
    private Date auditEndTime;

    /**
     * 审核备注
     */
    private String auditNote;

    /**
     * 审核人
     */
    private Integer auditUser;

    /**
     * 审核状态 1:审核通过 2:退回到整理阶段 3:退回到访谈阶段 4:拒访
     */
    private Integer valid;

    private Integer status;

    private Integer createUser;

    private Date createTime;

    private Integer isDelete;

    private Date deleteTime;


}
