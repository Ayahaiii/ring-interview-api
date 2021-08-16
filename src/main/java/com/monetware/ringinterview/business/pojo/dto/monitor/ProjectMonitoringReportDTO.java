package com.monetware.ringinterview.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @ClassName ProjectMonitoringReport
 * @Author: zhangd
 * @Description: 获取项目监控报表数据
 * @Date: Created in 17:00 2020/2/24
 */
@Data
public class ProjectMonitoringReportDTO {

    /**
     * 团队数量
     */
    private Integer numOfTeam;

    /**
     * 研究对象数
     */
    private Integer numOfSample;

    /**
     * 访谈数量
     */
    private Integer numOfInterview;

    /**
     * 访谈总时长
     */
    private Long interviewTimeLen;


    private String timeStr;

    /**
     * 信息总量
     */
    private Long fileSize;

    private String fileSizeStr;


}
