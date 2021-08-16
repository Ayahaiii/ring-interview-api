package com.monetware.ringinterview.business.controller.project;

import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.dto.sample.ProjectSampleDTO;
import com.monetware.ringinterview.business.pojo.dto.sample.SampleListDTO;
import com.monetware.ringinterview.business.pojo.dto.sample.SamplePropertyDTO;
import com.monetware.ringinterview.business.pojo.dto.sample.TeamMemberDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectProperty;
import com.monetware.ringinterview.business.pojo.po.BaseProjectPropertyTemplate;
import com.monetware.ringinterview.business.pojo.po.BaseProjectSample;
import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import com.monetware.ringinterview.business.pojo.vo.sample.*;
import com.monetware.ringinterview.business.service.project.SampleService;
import com.monetware.ringinterview.system.authorize.MonetwareAuthorize;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.PageParam;
import com.monetware.ringinterview.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Api("访谈对象管理")
@RestController
@RequestMapping("/sample")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @PostMapping("get/town")
    @ApiOperation(value = "获取镇列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<List<String>> getTownNameList(@RequestBody TownVO townVO) {
        return new ResultData<>("获取成功", sampleService.getTownNameList(townVO));
    }

    @PostMapping("save")
    @ApiOperation(value = "添加或编辑研究对象")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<Integer> saveProjectSample(@RequestBody ProjectSampleVO projectSampleVO) {
        return new ResultData(0, "保存成功", sampleService.saveProjectSample(projectSampleVO));
    }

    @PostMapping("list")
    @ApiOperation(value = "研究对象列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<PageList> projectSampleList(@RequestBody SampleListVO sampleListVO) {
        return new ResultData<>(0, "查询成功", sampleService.getSampleList(sampleListVO));
    }

    @PostMapping("batch/import")
    @ApiOperation(value = "导入样本")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<List<BaseProjectSample>> insertSampleByImport(@RequestBody SampleImportVO importVO) {
        return new ResultData<>("导入成功", sampleService.insertSampleByImport(importVO));
    }

    @PostMapping("batch/update")
    @ApiOperation(value = "更新样本")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_ADD)
    public ResultData<List<BaseProjectSample>> updateSampleByImport(@RequestBody SampleImportVO importVO) {
        return new ResultData<>("导入成功", sampleService.updateSampleByImport(importVO));
    }

    @PostMapping("batch/export")
    @ApiOperation(value = "导出样本")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_EXPORT)
    public ResultData<Integer> exportTeamUser(@RequestBody SampleExportVO exportVO) throws Exception {
        return new ResultData<>("导出成功", sampleService.exportSample(exportVO));
    }

    @PostMapping("export/list")
    @ApiOperation(value = "导出文件列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_EXPORT)
    public ResultData<PageList> getSampleDownList(@RequestBody PageParam pageParam) {
        return new ResultData<>(0, "查询成功", sampleService.getSampleDownList(pageParam));
    }

    @GetMapping("download")
    @ApiOperation(value = "下载样本文件")
    public void downloadSample(Integer id, HttpServletResponse response) throws Exception {
        sampleService.downloadSample(id, response);
    }

    @PostMapping("file/delete")
    @ApiOperation(value = "删除文件")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_EXPORT)
    public ResultData<Integer> deleteSampleFile(@RequestBody SampleExportDelVO delVO) {
        return new ResultData<>("删除成功", sampleService.deleteSampleFile(delVO));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除和批量删除研究对象")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_DELETE)
    public ResultData<Integer> deleteProjectSample(@RequestBody DeleteSampleVO deleteSampleVO) {
        return new ResultData<>(0, "删除成功", sampleService.deleteProjectSample(deleteSampleVO));
    }

    @PostMapping("/detail")
    @ApiOperation(value = "研究对象详情")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_VIEW)
    public ResultData<ProjectSampleDTO> getComments(@RequestBody ProjectSampleDetailVO detailVO) {
        return new ResultData<>(0, "查询成功", sampleService.getSampleDetail(detailVO));
    }

    @PostMapping("member")
    @ApiOperation(value = "查询所有团队成员")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_ASSIGN_ADMIN)
    public ResultData<List<TeamMemberDTO>> getTeamMember(@RequestBody TeamMemberSearchVO teamMemberVO) {
        return new ResultData<>(0, "查询成功", sampleService.getTeamMember(teamMemberVO));
    }

    @PostMapping("assign")
    @ApiOperation(value = "分派团队成员")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_ASSIGN_ADMIN)
    public ResultData<Integer> assignTeam(@RequestBody AssignTeamVO assignTeamVO) {
        return new ResultData<>(0, "分派成功", sampleService.insertAssign(assignTeamVO));
    }

    @PostMapping("batch/assign")
    @ApiOperation(value = "批量分派")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_ASSIGN_ADMIN)
    public ResultData<Map> insertAssignByImport(@RequestBody BatchAssignByImportVO importVO) {
        return new ResultData<>("导入成功", sampleService.insertAssignByImport(importVO));
    }

    @PostMapping("/property/project")
    @ApiOperation(value = "添加/修改项目属性")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<Integer>saveProjectProperty(@RequestBody ProjectPropertyVO propertyVO) {
        return new ResultData<>(0, "添加成功", sampleService.saveProjectProperty(propertyVO));
    }

    @PostMapping("/property/get/{projectId}")
    @ApiOperation(value = "获取项目属性")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<BaseProjectProperty> getProjectSampleProperty(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "获取成功", sampleService.getProjectSampleProperty(projectId));
    }

    @PostMapping("/property/check/{projectId}")
    @ApiOperation(value = "获取使用属性")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_LIST)
    public ResultData<SamplePropertyDTO> getSampleProperty(@PathVariable("projectId") Integer projectId) {
        return new ResultData<>(0, "保存成功", sampleService.getSampleProperty(projectId));
    }

    @PostMapping("/property/template/save")
    @ApiOperation(value = "保存项目属性模板")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<Integer> savePropertyTemplate(@RequestBody PropertyTemplateVO templateVO) {
        return new ResultData<>(0, "保存成功", sampleService.savePropertyTemplate(templateVO));
    }

    @PostMapping("/property/template/list")
    @ApiOperation(value = "查询项目属性模板")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<List<BaseProjectPropertyTemplate>> getPropertyTemplateList(@RequestBody BaseVO baseVO) {
        return new ResultData<>(0, "查询成功", sampleService.getPropertyTemplate());
    }

    @PostMapping("/property/save")
    @ApiOperation(value = "项目属性别名")
    @MonetwareAuthorize(role = AuthorizedConstants.RS_SAMPLE_PROPERTY_ADMIN)
    public ResultData<Integer> saveProjectPropertyAlisa(@RequestBody SamplePropertySaveVO samplePropertySaveVO) {
        return new ResultData<>(0, "修改成功", sampleService.saveProjectPropertyAlisa(samplePropertySaveVO));
    }

    @PostMapping("/list/app")
    @ApiOperation(value="被分配样本列表")
    public ResultData<List<SampleListDTO>> getSampleListApp(@RequestBody SampleListAppVO sampleListAppVO){
        return new ResultData<>(0, "查询成功", sampleService.getSampleListApp(sampleListAppVO));
    }



}
