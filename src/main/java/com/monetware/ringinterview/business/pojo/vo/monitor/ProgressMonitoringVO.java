package com.monetware.ringinterview.business.pojo.vo.monitor;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName ProgressMonitoringVO
 * @Author: zhangd
 * @Description: 获取进度监控数据请求参数
 * @Date: Created in 18:37 2020/2/24
 */
@Data
public class ProgressMonitoringVO extends BaseVO {

    private Integer userId;

    /**
     * 开始时间（格式：2020-01-01）
     */
    private Date startTime;

    /**
     * 结束时间（格式：2020-01-02）
     */
    private Date endTime;
}
