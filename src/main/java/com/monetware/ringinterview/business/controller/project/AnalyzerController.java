package com.monetware.ringinterview.business.controller.project;

import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.dto.stat.AnalystParagraphDTO;
import com.monetware.ringinterview.business.pojo.dto.stat.AnalystUserDTO;
import com.monetware.ringinterview.business.pojo.dto.stat.SampleCodeMarkDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewCode;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewMark;
import com.monetware.ringinterview.business.pojo.vo.stat.*;
import com.monetware.ringinterview.business.service.project.AnalyzerService;
import com.monetware.ringinterview.system.authorize.MonetwareAuthorize;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Api("质性分析管理")
@RestController
@RequestMapping("/analyze")
public class AnalyzerController {

    @Autowired
    private AnalyzerService analyzerService;

    @PostMapping("get/analyst/{projectId}")
    @ApiOperation(value = "获取分析员列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<List<AnalystUserDTO>> getAnalystUserList(@PathVariable("projectId") Integer projectId){
        return new ResultData<>("获取成功", analyzerService.getAnalystUserList(projectId));
    }

    @PostMapping("list/page")
    @ApiOperation(value = "分页分析首页列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<PageList> getProjectStatList(@RequestBody StatVO statVO){
        return new ResultData("获取成功", analyzerService.getProjectStatList(statVO));
    }

    @PostMapping("list/page/app")
    @ApiOperation(value = "分析首页列表")
    //@MonetwareAuthorize
    public ResultData<List> getProjectStatAppList(@RequestBody StatAppVO statAppVO){
        return new ResultData<>("获取成功", analyzerService.getProjectStatAppList(statAppVO));
    }

    @PostMapping("code/save")
    @ApiOperation(value = "保存编码")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Integer> saveProjectInterviewCode(@RequestBody InterviewCodeVO interviewCodeVO) {
        return new ResultData<>("保存成功", analyzerService.saveProjectInterviewCode(interviewCodeVO));
    }

    @PostMapping("code/list/page")
    @ApiOperation(value = "分页编码列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<PageList> getProjectInterviewCodeListPage(@RequestBody InterviewCodeSearchVO codeSearchVO){
        return new ResultData("获取成功", analyzerService.getProjectInterviewCodeListPage(codeSearchVO));
    }

    @PostMapping("code/find")
    @ApiOperation(value = "查找编码")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<List<BaseProjectInterviewCode>> getProjectInterviewCodeList(@RequestBody InterviewCodeFindVO codeFindVO) {
        return new ResultData<>("查找成功", analyzerService.getProjectInterviewCodeList(codeFindVO));
    }

    @PostMapping("code/delete")
    @ApiOperation(value = "删除编码")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Integer> deleteProjectInterviewCode(@RequestBody InterviewCodeDelVO codeDelVO) {
        return new ResultData<>("删除成功", analyzerService.deleteProjectInterviewCode(codeDelVO));
    }

    @PostMapping("code/cloud")
    @ApiOperation(value = "词云图")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Map<String, Map<String, Integer>>> getProjectInterviewCodeViewData(@RequestBody InterviewCodeSearchVO codeSearchVO) {
        return new ResultData<>("获取成功", analyzerService.getProjectInterviewCodeViewData(codeSearchVO));
    }

    @PostMapping("code/text/list")
    @ApiOperation(value = "样本文本列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<PageList> getProjectInterviewMarkBySampleId(@RequestBody AnalystParagraphVO paragraphVO) {
        return new ResultData<>("查找成功", analyzerService.getProjectInterviewParagraphBySampleId(paragraphVO));
    }

    @PostMapping("code/mark/list")
    @ApiOperation(value = "样本文本标注列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<List<SampleCodeMarkDTO>> getProjectInterviewMarkBySampleId(@RequestBody InterviewMarkFindVO markFindVO) {
        return new ResultData<>("查找成功", analyzerService.getProjectInterviewMarkBySampleId(markFindVO));
    }

    @PostMapping("mark/list/page")
    @ApiOperation(value = "分页标注列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<PageList> getInterviewMarkList(@RequestBody InterviewMarkSearchVO searchVO){
        return new ResultData("获取成功", analyzerService.getInterviewMarkList(searchVO));
    }

    @PostMapping("mark/save")
    @ApiOperation(value = "保存标注")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Integer> saveProjectInterviewMark(@RequestBody InterviewMarkVO markVO) {
        return new ResultData<>("保存成功", analyzerService.saveProjectInterviewMark(markVO));
    }

    @PostMapping("mark/update")
    @ApiOperation(value = "修改标注")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Integer> updateInterviewMark(@RequestBody InterviewMarkUpdateVO updateVO) {
        return new ResultData<>("修改成功", analyzerService.updateInterviewMark(updateVO));
    }

    @PostMapping("mark/delete")
    @ApiOperation(value = "删除标注")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Integer> deleteInterviewMark(@RequestBody InterviewMarkDelVO delVO) {
        return new ResultData<>("删除成功", analyzerService.deleteInterviewMark(delVO));
    }

    @PostMapping("text/word")
    @ApiOperation(value = "词频统计")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Map<String, Integer>> runWordStatistics(@RequestBody WordStatisticsVO statisticsVO) {
        return new ResultData<>("查找成功", analyzerService.runWordStatistics(statisticsVO));
    }

    @PostMapping("code/view")
    @ApiOperation(value = "编码图谱")
    @MonetwareAuthorize(role = AuthorizedConstants.RA_STAT_LIST)
    public ResultData<Map<String, Object>> getCodeViewData(@RequestBody CodeViewVO codeViewVO) {
        return new ResultData<>("查询成功", analyzerService.getCodeViewData(codeViewVO));
    }

}
