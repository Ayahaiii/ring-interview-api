package com.monetware.ringinterview.business.controller.project;

import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.dto.interview.*;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterview;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewFile;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewParagraph;
import com.monetware.ringinterview.business.pojo.vo.interview.*;
import com.monetware.ringinterview.business.pojo.vo.interview.InterviewDetailVO;
import com.monetware.ringinterview.business.pojo.vo.interview.SaveInterviewVO;
import com.monetware.ringinterview.business.pojo.vo.interview.ProjectInterviewFileVO;
import com.monetware.ringinterview.business.pojo.vo.project.ProjectFileVO;
import com.monetware.ringinterview.business.pojo.vo.sample.SampleAssignedVO;
import com.monetware.ringinterview.business.service.project.InterviewService;
import com.monetware.ringinterview.system.authorize.MonetwareAuthorize;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.PageParam;
import com.monetware.ringinterview.system.base.ResultData;
import com.monetware.ringinterview.system.util.file.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Api("访谈管理")
@RestController
@RequestMapping("/interview")
public class InterviewController {


    @Autowired
    private InterviewService interviewService;

    @PostMapping("/file")
    @ApiOperation("获取附件列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_FILE_VIEW)
    public ResultData<PageList> getInterviewFileList(@RequestBody ProjectInterviewFileVO interviewFileVO) {
        return new ResultData<>(0, "获取成功", interviewService.getInterviewFileList(interviewFileVO));
    }

    @PostMapping("/file/app")
    @ApiOperation("app获取录音列表")
    //@MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_VOICE_VIEW)
    public ResultData<List<ProjectInterviewFileDTO>> getInterviewFileListApp(@RequestBody ProjectInterviewFileAppVO interviewFileAppVO) {
        return new ResultData<>(0, "获取成功", interviewService.getInterviewFileListApp(interviewFileAppVO));
    }

    @PostMapping("/voice")
    @ApiOperation("获取录音列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_VOICE_VIEW)
    public ResultData<PageList> getInterviewVoiceList(@RequestBody ProjectInterviewFileVO interviewFileVO) {
        return new ResultData<>(0, "获取成功", interviewService.getInterviewFileList(interviewFileVO));
    }

    @PostMapping("/voice/app")
    @ApiOperation("app获取录音列表")
    //@MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_VOICE_VIEW)
    public ResultData<List<ProjectInterviewFileDTO>> getInterviewVoiceListApp(@RequestBody ProjectInterviewFileAppVO interviewFileAppVO) {
        return new ResultData<>(0, "获取成功", interviewService.getInterviewFileListApp(interviewFileAppVO));
    }

    @PostMapping("/voice/singleFile")
    @ApiOperation("获取单条录音/附件的信息")
    public ResultData<ProjectInterviewFileDTO> getFile(@RequestBody ProjectFileVO fileVO){
        return new ResultData(0,"获取成功",interviewService.getFile(fileVO));
    }

    @GetMapping("/file/download")
    @ApiOperation("下载录音/附件")
    public void downloadFile(Integer fileId, HttpServletResponse response) throws Exception {
        interviewService.downloadFile(fileId, response);
    }

    @PostMapping("/delete/file")
    @ApiOperation("删除附件")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_FILE_ADMIN)
    public ResultData<Integer> deleteInterviewFile(@RequestBody FileDeleteVO deleteVO) {
        return new ResultData(0, "删除成功", interviewService.deleteInterviewFile(deleteVO));
    }

    @PostMapping("/delete/voice")
    @ApiOperation("删除录音")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_VOICE_ADMIN)
    public ResultData<Integer> deleteInterviewVoice(@RequestBody FileDeleteVO deleteVO) {
        return new ResultData(0, "删除成功", interviewService.deleteInterviewFile(deleteVO));
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存/编辑访谈")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_ADD)
    public ResultData<Integer> saveInterview(@RequestBody SaveInterviewVO saveInterviewVO) {
        return new ResultData<>(0, "保存成功", interviewService.saveInterview(saveInterviewVO));
    }

    @PostMapping("/sample/assigned")
    @ApiOperation(value = "已分派样本")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_ADD)
    public ResultData<List<SampleAssignedDTO>> getSampleAssign(@RequestBody SampleAssignedVO assignedVO) {
        return new ResultData<>(0, "查询成功", interviewService.getSampleAssigned(assignedVO));
    }

    @PostMapping("/list")
    @ApiOperation(value = "访谈列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_LIST)
    public ResultData<PageList> interviewList(@RequestBody InterviewVO interviewVO) {
        return new ResultData<>(0, "查询成功", interviewService.getInterviewList(interviewVO));
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除访谈")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_DELETE)
    public ResultData<Integer> deleteInterview(@RequestBody InterviewDeleteVO deleteVO) {
        return new ResultData<>(0, "删除成功", interviewService.deleteInterview(deleteVO));
    }


    @PostMapping("/detail")
    @ApiOperation(value = "访谈详情")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_VIEW)
    public ResultData<InterviewDetailDTO> getInterviewDetail(@RequestBody InterviewDetailVO detailVO) {
        return new ResultData<>(0, "查询成功", interviewService.getInterviewDetail(detailVO));
    }

    @PostMapping("/detail/status")
    @ApiOperation(value = "访谈计划进度更新")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_ADD)
    public ResultData<Integer> updateInterviewStatus(@RequestBody InterviewDetailVO detailVO) {
        return new ResultData<>(0, "更新成功", interviewService.updateInterviewStatus(detailVO));
    }

    @PostMapping("/audit")
    @ApiOperation(value = "访谈审核")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_AUDIT)
    public ResultData<Integer> updateInterviewAudit(@RequestBody InterviewDetailVO detailVO) {
        return new ResultData<>(0, "审核通过", interviewService.updateInterviewAudit(detailVO));
    }

    @PostMapping("/sample/owner")
    @ApiOperation(value = "查询样本归属")
    public ResultData<List<SampleOwnerDTO>> getSampleOwner(@RequestBody SampleOwnerVO sampleOwnerVO) {
        return new ResultData<>(0, "查询成功", interviewService.getSampleOwner(sampleOwnerVO));
    }

    @PostMapping("/save/speaker")
    @ApiOperation(value = "保存/编辑讲话者")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_ADD)
    public ResultData<Integer> saveSpeak(@RequestBody InterviewSpeakerVO speakVO) {
        return new ResultData<>(0, "保存成功", interviewService.saveSpeaker(speakVO));
    }

    @PostMapping("/list/speaker")
    @ApiOperation(value = "讲话者列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_LIST)
    public ResultData<List<SpeakerListDTO>> getSpeakList(@RequestBody InterviewSpeakerVO speakerVO) {
        return new ResultData<>(0, "查询成功", interviewService.getSpeakList(speakerVO.getInterviewId()));
    }

    @PostMapping("/detail/speaker")
    @ApiOperation(value = "讲话者详情")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_VIEW)
    public ResultData<SpeakDTO> getSpeakDetail(@RequestBody InterviewSpeakerVO speakerVO) {
        return new ResultData<>(0, "查询成功", interviewService.getSpeakDetail(speakerVO.getId()));

    }

    @PostMapping("/delete/speaker")
    @ApiOperation(value = "删除/批量删除讲话者")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_DELETE)
    public ResultData<Integer> deleteSpeaker(@RequestBody SpeakerDeleteVO deleteVO) {
        return new ResultData<>(0, "删除成功", interviewService.deleteSpeaker(deleteVO));
    }

    @PostMapping("/save/paragraph")
    @ApiOperation(value = "保存/编辑访谈文本")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_ADD)
    public ResultData<Integer> saveParagraph(@RequestBody InterviewParagraphVO paragraphVO) {
        return new ResultData<>(0, "保存成功", interviewService.saveParagraph(paragraphVO));
    }

    @PostMapping("/list/paragraph")
    @ApiOperation(value = "访谈文本列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_VIEW)
    public ResultData<PageList> getParagraphList(@RequestBody ParagraphListVO paragraphListVO) {
        return new ResultData<>(0, "保存成功", interviewService.getParagraphList(paragraphListVO));
    }

    @PostMapping("/list/paragraph/app")
    @ApiOperation(value = "app访谈文本列表")
    //@MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_VIEW)
    public ResultData<List<InterviewParagraphDTO>> getParagraphListApp(@RequestBody ParagraphListAppVO paragraphListAppVO) {
        return new ResultData<>(0, "保存成功", interviewService.getParagraphListApp(paragraphListAppVO));
    }

    @PostMapping("/detail/paragraph")
    @ApiOperation(value = "文本详情")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_VIEW)
    public ResultData<BaseProjectInterviewParagraph> getParagraphDetail(@RequestBody ParagraphVO paragraphVO) {
        return new ResultData<>(0, "查询成功", interviewService.getParagraphDetail(paragraphVO.getParagraphId()));
    }

    @PostMapping("/delete/paragraph")
    @ApiOperation(value = "删除文本")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_DELETE)
    public ResultData<Integer> deleteParagraph(@RequestBody ParagraphVO paragraphVO) {
        return new ResultData<>(0, "删除成功", interviewService.deleteParagraph(paragraphVO));
    }

    @PostMapping("/audit/paragraph")
    @ApiOperation(value = "审核文本")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_AUDIT)
    public ResultData<Integer> paragraphAudit(@RequestBody ParagraphAuditVO auditVO) {
        return new ResultData<>(0, "更新成功", interviewService.updateParagraphAudit(auditVO));
    }

    @PostMapping("/upload/file/{projectId}")
    @ApiOperation(value = "上传文件")
    // @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_FILE_ADMIN)
    public ResultData<Integer> uploadFiles(@RequestBody MultipartFile[] file, Integer interviewId, @PathVariable("projectId") Integer projectId, Integer fileType) {
        return new ResultData<>(0, "上传成功", interviewService.uploadFiles(file, interviewId, projectId, fileType));
    }

    @PostMapping("/update/filename")
    @ApiOperation(value = "文件重命名")
    public ResultData<Integer> updateInterviewFileName(@RequestBody FileUpdateVO updateVO) {
        return new ResultData<>(0, "更新成功", interviewService.updateFileName(updateVO));
    }

    @PostMapping("batch/export")
    @ApiOperation(value = "导出访谈")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_EXPORT)
    public ResultData<Integer> exportInterview(@RequestBody InterviewExportVO exportVO) throws Exception {
        return new ResultData<>("导出成功", interviewService.exportInterview(exportVO));
    }

    @GetMapping("download")
    @ApiOperation(value = "下载导出访谈")
    public void downloadInterview(Integer id, HttpServletResponse response) throws Exception {
        interviewService.downloadInterview(id, response);
    }

    @GetMapping("itwt/temp/download")
    @ApiOperation(value = "下载对话文本模板")
    public void downloadTempItwt(HttpServletResponse response) throws Exception {
        interviewService.downloadTempItwt(response);
    }

    @GetMapping("itwt/download")
    @ApiOperation(value = "下载对话文本")
    public void downloadItwt(Integer id, HttpServletResponse response) throws Exception {
        interviewService.downloadItwt(id, response);
    }

    @PostMapping("down/delete")
    @ApiOperation(value = "删除访谈导出文件")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_EXPORT)
    public ResultData<Integer> deleteInterviewDown(@RequestBody InterviewDownDelVO delVO) {
        return new ResultData<>(0, "删除成功", interviewService.deleteInterviewDown(delVO.getId()));
    }

    @PostMapping("export/list")
    @ApiOperation(value = "导出访谈文件列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_LIST)
    public ResultData<PageList> getInterviewDownList(@RequestBody PageParam pageParam) {
        return new ResultData<>(0, "查询成功", interviewService.getInterviewDownList(pageParam));
    }

    @PostMapping("batch/import")
    @ApiOperation(value = "批量导入对话文本")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_ADD)
    public ResultData<Map<String, List<BatchImportVO>>> insertParagraphByImport(@RequestBody ParagraphImportVO importVO) {
        return new ResultData<>(0, "导入成功", interviewService.insertParagraphByImport(importVO));
    }

    @PostMapping("export/itwt")
    @ApiOperation(value = "导出对话文本")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_EXPORT)
    public ResultData<Integer> exportParagraph(@RequestBody ParagraphExportVO exportVO) throws Exception {
        return new ResultData<>(0, "导出成功", interviewService.exportItwt(exportVO));
    }

    @PostMapping("export/itwt/list")
    @ApiOperation(value = "导出对话文本列表")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_VIEW)
    public ResultData<PageList> getItwtDownList(@RequestBody ExportHistoryVO historyVO) {
        return new ResultData<>(0, "查询成功", interviewService.getItwtDownList(historyVO));
    }

    @PostMapping("down/itwt/delete")
    @ApiOperation(value = "删除对话文本导出文件")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_DELETE)
    public ResultData<Integer> deleteItwtDown(@RequestBody ItwtDownDelVO itwtDownDelVO) {
        return new ResultData<>(0, "删除成功", interviewService.deleteItwtDown(itwtDownDelVO.getHistoryId()));
    }

    @PostMapping("voice/data")
    @ApiOperation(value = "智能语音转换")
    // @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_ADD)
    public ResultData<List> convertData(@RequestBody ParagraphConvertVO convertVO) {
        return new ResultData<>(0, "处理成功", interviewService.convertData(convertVO));
    }

    @PostMapping("batch/text")
    @ApiOperation(value = "智能语音导入")
    @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_ADD)
    public ResultData<Integer> insertBatchText(@RequestBody ParagraphImportVO importVO) {
        return new ResultData<>(0, "导入成功", interviewService.insertBatchText(importVO));
    }

    @PostMapping("batch/text/word")
    @ApiOperation(value = "word格式导入")
    // @MonetwareAuthorize(role = AuthorizedConstants.RI_INTERVIEW_TEXT_ADD)
    public ResultData<Map> batchImportItwtByWord(@RequestBody MultipartFile file, Integer interviewId, Integer projectId, Integer fileId) {
        return new ResultData<>(0, "导入成功", interviewService.batchImportItwtByWord(file, interviewId, projectId, fileId));
    }

    @PostMapping("batch/paragraphAI")
    @ApiOperation(value = "智能语音导入2")
    public ResultData<Integer> insertBatchParagraphAI(@RequestBody ParagraphBatchImportListVO param) {
        return new ResultData<>(0, "导入成功", interviewService.insertBatchParagraphAI(param));
    }
}
