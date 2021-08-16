package com.monetware.ringinterview.business.controller.project;

import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.dto.interview.InterviewLocationDTO;
import com.monetware.ringinterview.business.pojo.dto.monitor.PercentDataDTO;
import com.monetware.ringinterview.business.pojo.vo.monitor.GanteDataVO;
import com.monetware.ringinterview.business.pojo.vo.monitor.ProgressMonitoringVO;
import com.monetware.ringinterview.business.service.project.MonitorService;
import com.monetware.ringinterview.system.authorize.MonetwareAuthorize;
import com.monetware.ringinterview.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Api("监控报表管理")
@RestController
@RequestMapping("/monitor")
public class MonitorController {


    @Autowired
    private MonitorService monitorService;

    @PostMapping("/getMonitoring/{projectId}")
    @ApiOperation("获取项目监控报表数据")
    @MonetwareAuthorize(role = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData getProjectMonitoringReport(@PathVariable("projectId")Integer projectId){
        return new ResultData(0,"获取成功",monitorService.getProjectMonitoringReport(projectId));
    }

    @PostMapping("/getProgress/monitoring")
    @ApiOperation("获取进度监控数据")
    @MonetwareAuthorize(role = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData getProgressMonitoring(@RequestBody ProgressMonitoringVO progressMonitoringVO){
        return new ResultData(0,"获取成功",monitorService.getProgressMonitoring(progressMonitoringVO));
    }

    @PostMapping("/getInterview/locations/{projectId}")
    @ApiOperation("获取访谈项目经纬度")
    @MonetwareAuthorize(role = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<List<InterviewLocationDTO>> getInterviewLocations(@PathVariable("projectId")Integer projectId){
        return new ResultData(0,"获取成功",monitorService.getInterviewLocations(projectId));
    }

    @PostMapping("/percent/{projectId}")
    @ApiOperation("获取指标仪表盘数据")
    @MonetwareAuthorize(role = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData<PercentDataDTO> getPercentData(@PathVariable("projectId")Integer projectId){
        return new ResultData(0,"获取成功",monitorService.getPercentData(projectId));
    }

    @PostMapping("/gante")
    @ApiOperation("获取甘特图数据列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RM_MONITOR_LIST)
    public ResultData getProgressMonitoringList(@RequestBody GanteDataVO ganteDataVO){
        return new ResultData(0,"获取成功",monitorService.getProgressMonitoringList(ganteDataVO));
    }

}
