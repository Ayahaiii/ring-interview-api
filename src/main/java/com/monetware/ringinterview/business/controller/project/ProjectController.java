package com.monetware.ringinterview.business.controller.project;

import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.dto.interview.InterviewLocationDTO;
import com.monetware.ringinterview.business.pojo.dto.monitor.ProjectMonitoringReportDTO;
import com.monetware.ringinterview.business.pojo.dto.monitor.ProjectMonitoringSampleDTO;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectHeadDTO;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectInfoDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectConfig;
import com.monetware.ringinterview.business.pojo.vo.monitor.ProgressMonitoringVO;
import com.monetware.ringinterview.business.pojo.vo.project.*;
import com.monetware.ringinterview.business.service.project.MonitorService;
import com.monetware.ringinterview.business.service.project.ProjectService;
import com.monetware.ringinterview.system.authorize.MonetwareAuthorize;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * @author Simo
 * @date 2020-02-12
 */
@Api("项目管理")
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MonitorService monitorService;

    // ******************** 项目相关 ********************

    @PostMapping("create")
    @ApiOperation(value = "创建项目")
    public ResultData createProject(@RequestBody ProjectVO projectVO) {
        projectService.createProject(projectVO);
        return new ResultData<>("创建成功");
    }

    @PostMapping("delete/{projectId}")
    @ApiOperation(value = "删除项目")
    public ResultData<Integer> deleteProject(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("删除成功", projectService.deleteProject(projectId));
    }

    @PostMapping("update")
    @ApiOperation(value = "修改项目")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_EDIT)
    public ResultData<Integer> updateProject(@RequestBody ProjectVO projectVO) {
        return new ResultData<>("修改成功", projectService.updateProject(projectVO));
    }

    @PostMapping("update/status")
    @ApiOperation(value = "修改项目状态")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_CONFIG_ADMIN)
    public ResultData<Integer> updateProjectStatus(@RequestBody ProjectStatusVO projectVO) {
        return new ResultData<>("状态变更成功", projectService.updateProjectStatus(projectVO));
    }

    @PostMapping("get/{projectId}")
    @ApiOperation(value = "获取项目基本信息")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<ProjectInfoDTO> getProjectInfo(@PathVariable("projectId") Integer projectId){
        return new ResultData<>("获取成功", projectService.getProjectInfo(projectId));
    }

    @PostMapping("get/head/{projectId}")
    @ApiOperation(value = "获取项目基本信息")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<ProjectHeadDTO> getProjectNameAndRole(@PathVariable("projectId") Integer projectId){
        return new ResultData<>("获取成功", projectService.getProjectNameAndRole(projectId));
    }

    @PostMapping("get/config/{projectId}")
    @ApiOperation(value = "获取项目配置信息")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<BaseProjectConfig> getProjectConfig(@PathVariable("projectId") Integer projectId){
        return new ResultData<>("获取成功", projectService.getProjectConfig(projectId));
    }

    @PostMapping("get/monitor/{projectId}")
    @ApiOperation(value = "获取项目图表信息")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<ProjectMonitoringReportDTO> getProjectMonitoringReport(@PathVariable("projectId") Integer projectId){
        return new ResultData<>("获取成功", monitorService.getProjectMonitoringReport(projectId));
    }

    @PostMapping("get/monitor/itw")
    @ApiOperation(value = "获取项目访谈进度信息")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<LinkedHashMap<String, Object>> getProgressMonitoring(@RequestBody ProgressMonitoringVO monitoringVO){
        return new ResultData<>("获取成功", monitorService.getProgressMonitoring(monitoringVO));
    }

    @PostMapping("get/locations/{projectId}")
    @ApiOperation("获取访谈项目经纬度")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_VIEW)
    public ResultData<List<InterviewLocationDTO>> getInterviewLocations(@PathVariable("projectId")Integer projectId){
        return new ResultData(0,"获取成功",monitorService.getInterviewLocations(projectId));
    }

    @PostMapping("get/monitor/sample/{projectId}")
    @ApiOperation("获取分派样本各项状态")
    //@MonetwareAuthorize(role=)
    public ResultData<ProjectMonitoringSampleDTO> getProjectMonitoringSample(@PathVariable("projectId") Integer projectId){
        return new ResultData<>("获取成功",monitorService.getProjectMonitoringSample(projectId));
    }

    @PostMapping("update/config")
    @ApiOperation(value = "修改项目配置")
    @MonetwareAuthorize(role = AuthorizedConstants.RP_PROJECT_CONFIG_ADMIN)
    public ResultData<Integer> updateProjectConfig(@RequestBody ProjectConfigVO projectConfigVO) {
        return new ResultData<>("修改成功", projectService.updateProjectConfig(projectConfigVO));
    }

    @PostMapping("list/page")
    @ApiOperation(value = "分页获取项目列表")
    public ResultData<PageList> getProjectList(@RequestBody ProjectListVO projectListVO){
        return new ResultData("获取成功", projectService.getProjectList(projectListVO));
    }

    @PostMapping("list/page/app")
    @ApiOperation(value = "获取项目列表")
    public ResultData<List> getProjectListApp(@RequestBody ProjectListAppVO projectListAppVO){
        return new ResultData("获取成功", projectService.getProjectListApp(projectListAppVO));
    }

    @PostMapping("apply")
    @ApiOperation(value = "申请加入项目")
    public ResultData<Integer> insertApplyTeam(@RequestBody ProjectApplyVO projectApplyVO) {
        return new ResultData<>("申请成功", projectService.insertApplyTeam(projectApplyVO));
    }

    @PostMapping("permission/{projectId}")
    @ApiOperation(value = "获取项目权限")
    public ResultData<List<String>> getProjectPermission(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("获取成功", projectService.getProjectPermission(projectId));
    }

    @PostMapping("update/code/info")
    @ApiOperation(value = "修改邀请码信息")
    @MonetwareAuthorize(role = AuthorizedConstants.RT_MEMBER_INVITECODE_ADMIN)
    public ResultData<Integer> updateInvitedCodeInfo(@RequestBody ProjectInvitedVO projectApplyVO) {
        return new ResultData<>("修改成功", projectService.updateInvitedCodeInfo(projectApplyVO));
    }

    @PostMapping("update/code/{projectId}")
    @ApiOperation(value = "修改邀请码")
    @MonetwareAuthorize(role = AuthorizedConstants.RT_MEMBER_INVITECODE_ADMIN)
    public ResultData<Integer> updateInvitedCode(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>("修改成功", projectService.updateInvitedCode(projectId));
    }

    @PostMapping("get/visit/{projectId}")
    @ApiOperation(value="样例项目判断")
    public ResultData<Integer> getVisit(@PathVariable("projectId") Integer projectId){
        return new ResultData("查询成功",projectService.getVisit(projectId));
    }
    // ******************** 回收站 ********************

}
