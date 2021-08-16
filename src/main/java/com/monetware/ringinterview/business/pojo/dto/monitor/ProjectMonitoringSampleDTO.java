package com.monetware.ringinterview.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @description: 获取分派样本各项状态数据接口
 * @author: twitch
 * @param:
 * @Date: 2021/1/7
 */
@Data
public class ProjectMonitoringSampleDTO {

    /**
     * 未开始样本
     */
    private Integer numOfAssignSample;

    /**
     * 进行中样本
     */
    private Integer numOfRunningSample;

    /**
     * 已完成样本
     */
    private Integer numOfFinishSample;

}
