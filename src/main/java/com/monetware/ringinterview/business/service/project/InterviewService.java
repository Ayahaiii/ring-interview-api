package com.monetware.ringinterview.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringinterview.business.dao.*;
import com.monetware.ringinterview.business.pojo.constant.InterviewConstants;
import com.monetware.ringinterview.business.pojo.constant.ProjectConstants;
import com.monetware.ringinterview.business.pojo.constant.SampleConstants;
import com.monetware.ringinterview.business.pojo.dto.interview.*;
import com.monetware.ringinterview.business.pojo.dto.sample.ManagerDTO;
import com.monetware.ringinterview.business.pojo.po.*;
import com.monetware.ringinterview.business.pojo.vo.interview.*;
import com.monetware.ringinterview.business.pojo.vo.interview.InterviewDetailVO;
import com.monetware.ringinterview.business.pojo.vo.interview.SaveInterviewVO;
import com.monetware.ringinterview.business.pojo.vo.interview.ProjectInterviewFileVO;
import com.monetware.ringinterview.business.pojo.vo.project.ProjectFileVO;
import com.monetware.ringinterview.business.pojo.vo.sample.SampleAssignedVO;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.PageParam;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.ringinterview.system.util.date.DateUtil;
import com.monetware.ringinterview.system.util.file.*;
import com.monetware.ringinterview.system.util.lfasr.LfasrUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Slf4j
@Service
public class InterviewService {

    @Value("${upload.dir}")
    private String filePath;

    @Autowired
    private ProjectInterviewDao interviewDao;

    @Autowired
    private ProjectInterviewFileDao fileDao;

    @Autowired
    private ProjectInterviewSpeakerDao speakerDao;

    @Autowired
    private ProjectInterviewParagraphDao paragraphDao;

    @Autowired
    private ProjectInterviewSampleDao interviewSampleDao;

    @Autowired
    private ProjectExportDao exportDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectSampleDao sampleDao;

    @Autowired
    private ProjectDao projectDao;


    /**
     * 获取附件列表
     *
     * @param interviewFileVO
     * @return
     */
    public PageList<Page> getInterviewFileList(ProjectInterviewFileVO interviewFileVO) {
        Page page = PageHelper.startPage(interviewFileVO.getPageNum(), interviewFileVO.getPageSize());
        List<ProjectInterviewFileDTO> fileList = fileDao.getInterviewFileList(interviewFileVO);
        for (ProjectInterviewFileDTO fileDTO : fileList) {
            fileDTO.setFileSize(FileUtil.byteFormat(Long.valueOf(fileDTO.getFileSize()), true));
            fileDTO.setBackGround("/file/background.png");
        }
        return new PageList<>(page);
    }

    /**
     * App获取附件列表，不分页
     *
     * @param interviewFileAppVO
     * @return
     */
    public List<ProjectInterviewFileDTO> getInterviewFileListApp(ProjectInterviewFileAppVO interviewFileAppVO) {
        List<ProjectInterviewFileDTO> fileList = fileDao.getInterviewFileListApp(interviewFileAppVO);
        for (ProjectInterviewFileDTO fileDTO : fileList) {
            fileDTO.setFileSize(FileUtil.byteFormat(Long.valueOf(fileDTO.getFileSize()), true));
            fileDTO.setBackGround("/file/background.png");
        }
        return fileList;
    }

    /**
     * 获取录音/附件
     *
     * @param fileVO
     * @return
     */
    public ProjectInterviewFileDTO getFile(ProjectFileVO fileVO){
        ProjectInterviewFileDTO fileMessage=fileDao.getFileMessage(fileVO);
        fileMessage.setFileSize(FileUtil.byteFormat(Long.valueOf(fileMessage.getFileSize()), true));
        fileMessage.setBackGround("/file/background.png");
        return fileMessage;
    }

    // ======================End==================================

    /**
     * 删除录音/附件文件
     *
     * @param deleteVO
     * @return
     */
    public Integer deleteInterviewFile(FileDeleteVO deleteVO) {
        for (Integer fileId : deleteVO.getFileIds()) {
            BaseProjectInterviewFile interviewFile = fileDao.selectByPrimaryKey(fileId);
            String[] split = interviewFile.getFilePath().split("/");
            String fileName = split[split.length - 1];
            String path;
            if (interviewFile.getType() == 1) {
                path = filePath + "/file/voice/" + deleteVO.getProjectId() + "/";
            } else {
                path = filePath + "/file/attachment/" + deleteVO.getProjectId() + "/";
            }
            boolean flag = FileUtil.deleteFile(path, fileName);
            if (flag) {
                fileDao.deleteByPrimaryKey(fileId);
                // 回写信息量总数
                BaseProject p = new BaseProject();
                p.setFileSize(interviewFile.getFileSize());
                p.setUpdateUser(ThreadLocalManager.getUserId());
                p.setUpdateTime(new Date());
                p.setId(deleteVO.getProjectId());
                projectDao.updateProjectDel(p);

                // 回写访谈信息量
                BaseProjectInterview interview = new BaseProjectInterview();
                interview.setId(interviewFile.getInterviewId());
                interview.setFileSize(interviewFile.getFileSize());
                interviewDao.updateinterviewDel(interview);

                //删除对话文本
                Example example = new Example(BaseProjectInterviewParagraph.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("fileId", fileId);
                paragraphDao.deleteByExample(example);
            } else {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "文件删除失败");
            }
        }
        return deleteVO.getFileIds().size();
    }

    /**
     * 已分派样本
     *
     * @param assignedVO
     * @return
     */
    public List<SampleAssignedDTO> getSampleAssigned(SampleAssignedVO assignedVO) {
        return sampleDao.getSampleAssigned(assignedVO);
    }

    /**
     * 新建/编辑访谈(优化)
     *
     * @param saveInterviewVO
     * @return
     */
    public Integer saveInterview(SaveInterviewVO saveInterviewVO) {
        Date now = new Date();
        List<Integer> sampleIds = new ArrayList<>();
        BaseProjectInterview projectInterview = new BaseProjectInterview();
        BeanUtils.copyProperties(saveInterviewVO, projectInterview);
        if (saveInterviewVO.getId() == null) {
            projectInterview.setFileSize(ProjectConstants.INIT_VALUE.longValue());
            projectInterview.setInterviewTimeLen(ProjectConstants.INIT_VALUE.longValue());
            projectInterview.setUserId(saveInterviewVO.getManager().getId());
            projectInterview.setPlanDuration(DateUtil.getDateDuration(saveInterviewVO.getPlanStartTime(), saveInterviewVO.getPlanEndTime()));
            projectInterview.setCreateUser(ThreadLocalManager.getUserId());
            projectInterview.setStatus(1);
            projectInterview.setCreateTime(now);
            int row = interviewDao.insertSelective(projectInterview);
            // 项目访谈样本表
            for (int i = 0; i < saveInterviewVO.getSample().size(); i++) {
                BaseProjectInterviewSample interviewSample = new BaseProjectInterviewSample();
                interviewSample.setProjectId(saveInterviewVO.getProjectId());
                interviewSample.setInterviewId(projectInterview.getId());
                interviewSample.setSampleId(saveInterviewVO.getSample().get(i).getId());
                interviewSampleDao.insertSelective(interviewSample);
            }
            // 添加负责人->讲话者
            if (saveInterviewVO.getManager() != null) {
                BaseProjectInterviewSpeaker speak = new BaseProjectInterviewSpeaker();
                speak.setInterviewId(projectInterview.getId());
                speak.setName(saveInterviewVO.getManager().getName());
                speak.setUserId(saveInterviewVO.getManager().getId());
                speak.setType(1);
                speak.setCreateUser(ThreadLocalManager.getUserId());
                speak.setCreateTime(now);
                speakerDao.insertSelective(speak);
            }
            // 添加协助者->讲话者
            if (saveInterviewVO.getAssistant() != null) {
                for (ManagerDTO managerDTO : saveInterviewVO.getAssistant()) {
                    BaseProjectInterviewSpeaker speak = new BaseProjectInterviewSpeaker();
                    speak.setInterviewId(projectInterview.getId());
                    speak.setName(managerDTO.getName());
                    speak.setUserId(managerDTO.getId());
                    speak.setType(2);
                    speak.setCreateUser(ThreadLocalManager.getUserId());
                    speak.setCreateTime(now);
                    speakerDao.insertSelective(speak);
                }
            }
            // 添加受访人->讲话者
            if (saveInterviewVO.getSample() != null) {
                for (ManagerDTO managerDTO : saveInterviewVO.getSample()) {
                    BaseProjectInterviewSpeaker speak = new BaseProjectInterviewSpeaker();
                    speak.setInterviewId(projectInterview.getId());
                    speak.setName(managerDTO.getName());
                    speak.setType(3);
                    speak.setSampleOwner(managerDTO.getId());
                    speak.setSampleId(managerDTO.getId());
                    speak.setCreateUser(ThreadLocalManager.getUserId());
                    speak.setCreateTime(now);
                    sampleIds.add(managerDTO.getId());
                    speakerDao.insertSelective(speak);
                }
            }
            if (row > 0) {
                // 回写访谈总数
                BaseProject project = new BaseProject();
                project.setNumOfInterview(row);
                project.setUpdateUser(ThreadLocalManager.getUserId());
                project.setUpdateTime(now);
                project.setId(saveInterviewVO.getProjectId());
                projectDao.updateProjectAdd(project);
            }
            sampleDao.updateSampleStatus(saveInterviewVO.getProjectId(), sampleIds, 2);
            return projectInterview.getId();
        } else {
            BaseProjectInterview interview = interviewDao.selectByPrimaryKey(saveInterviewVO.getId());
            JSONArray sample = JSON.parseArray(JSON.toJSONString(saveInterviewVO.getSample()));
            JSONArray oldSample = JSON.parseArray(JSON.toJSONString(saveInterviewVO.getOldSample()));
            JSONArray assistant = JSON.parseArray(JSON.toJSONString(saveInterviewVO.getAssistant()));
            JSONArray oldAssistant = JSON.parseArray(JSON.toJSONString(saveInterviewVO.getOldAssistant()));
            List<Integer> delSampleIds = new ArrayList<>();
            for (int i = 0; i < sample.size(); i++) {
                // 新增样本
                if (!oldSample.contains(sample.getJSONObject(i))) {
                    // 访谈样本表
                    BaseProjectInterviewSample interviewSample = new BaseProjectInterviewSample();
                    interviewSample.setProjectId(saveInterviewVO.getProjectId());
                    interviewSample.setInterviewId(interview.getId());
                    interviewSample.setSampleId((Integer) sample.getJSONObject(i).get("id"));
                    interviewSampleDao.insertSelective(interviewSample);
                    // 讲话者表
                    BaseProjectInterviewSpeaker speak = new BaseProjectInterviewSpeaker();
                    speak.setInterviewId(interview.getId());
                    speak.setName((String) sample.getJSONObject(i).get("name"));
                    speak.setType(3);
                    speak.setSampleOwner((Integer) sample.getJSONObject(i).get("id"));
                    speak.setSampleId((Integer) sample.getJSONObject(i).get("id"));
                    speak.setCreateUser(ThreadLocalManager.getUserId());
                    speak.setCreateTime(now);
                    sampleIds.add((Integer) sample.getJSONObject(i).get("id"));
                    speakerDao.insertSelective(speak);
                }
            }
            // 修改样本状态
            if (sampleIds.size() > 0) {
                sampleDao.updateSampleStatus(saveInterviewVO.getProjectId(), sampleIds, 2);
            }
            // 删除样本
            for (int i = 0; i < oldSample.size(); i++) {
                if (!sample.contains(oldSample.getJSONObject(i))) {
                    // 访谈样本表
                    Example example = new Example(BaseProjectInterviewSample.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("projectId", saveInterviewVO.getProjectId());
                    criteria.andEqualTo("interviewId", interview.getId());
                    criteria.andEqualTo("sampleId", oldSample.getJSONObject(i).get("id"));
                    interviewSampleDao.deleteByExample(example);
                    // 讲话者表
                    Example example1 = new Example(BaseProjectInterviewSpeaker.class);
                    Example.Criteria criteria1 = example1.createCriteria();
                    criteria1.andEqualTo("sampleId", oldSample.getJSONObject(i).get("id"));
                    criteria1.andEqualTo("interviewId", interview.getId());
                    speakerDao.deleteByExample(example1);
                    delSampleIds.add((Integer) oldSample.getJSONObject(i).get("id"));
                }
            }
            // 修改样本状态
            if (delSampleIds.size() > 0) {
                sampleDao.updateSampleStatus(saveInterviewVO.getProjectId(), delSampleIds, 1);
            }
            // 新增协作者
            for (int i = 0; i < assistant.size(); i++) {
                if (!oldAssistant.contains(assistant.getJSONObject(i))) {
                    // 讲话者表
                    BaseProjectInterviewSpeaker speak = new BaseProjectInterviewSpeaker();
                    speak.setInterviewId(interview.getId());
                    speak.setName((String) assistant.getJSONObject(i).get("name"));
                    speak.setType(2);
                    speak.setUserId((Integer) assistant.getJSONObject(i).get("id"));
                    speak.setCreateUser(ThreadLocalManager.getUserId());
                    speak.setCreateTime(now);
                    speakerDao.insertSelective(speak);
                }
            }
            //删除协作者
            for (int i = 0; i < oldAssistant.size(); i++) {
                if (!assistant.contains(oldAssistant.getJSONObject(i))) {
                    // 讲话者表
                    Example example = new Example(BaseProjectInterviewSpeaker.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("interviewId", interview.getId());
                    criteria.andEqualTo("name", oldAssistant.getJSONObject(i).get("name"));
                    criteria.andEqualTo("type", 2);
                    criteria.andEqualTo("userId", oldAssistant.getJSONObject(i).get("id"));
                    speakerDao.deleteByExample(example);
                }
            }
            return interviewDao.updateByPrimaryKeySelective(projectInterview);
        }
    }

    /**
     * 访谈列表
     *
     * @param searchVO
     * @return
     */
    public PageList<Page> getInterviewList(InterviewVO searchVO) {
        searchVO.setUserId(ThreadLocalManager.getUserId());
        Page page = PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        interviewDao.getInterviewList(searchVO);
        return new PageList<>(page);
    }

    /**
     * 删除/批量删除访谈
     *
     * @param deleteVO
     * @return
     */
    public Integer deleteInterview(InterviewDeleteVO deleteVO) {
        List<Integer> sampleIds = new ArrayList<>();
        List<Integer> fileIds = new ArrayList<>();
        for (Integer interviewId : deleteVO.getInterviewIds()) {
            // 删除讲话者
            Example example1 = new Example(BaseProjectInterviewSpeaker.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("interviewId", interviewId);
            speakerDao.deleteByExample(example1);

            // 删除访谈对象表
            Example example2 = new Example(BaseProjectInterviewSample.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("interviewId", interviewId);
            List<BaseProjectInterviewSample> interviewSamples = interviewSampleDao.selectByExample(example2);
            for (BaseProjectInterviewSample interviewSample : interviewSamples) {
                sampleIds.add(interviewSample.getSampleId());
            }
            interviewSampleDao.deleteByExample(example2);

            // 删除文本
            Example example3 = new Example(BaseProjectInterviewParagraph.class);
            Example.Criteria criteria3 = example3.createCriteria();
            criteria3.andEqualTo("interviewId", interviewId);
            paragraphDao.deleteByExample(example3);

            // 删除访谈文件
            Example example4 = new Example(BaseProjectInterviewFile.class);
            Example.Criteria criteria4 = example4.createCriteria();
            criteria4.andEqualTo("interviewId", interviewId);
            List<BaseProjectInterviewFile> interviewFiles = fileDao.selectByExample(example4);
            for (BaseProjectInterviewFile file : interviewFiles) {
                fileIds.add(file.getId());
            }

            //删除对话文本导出文件
            Example example5 = new Example(BaseProjectExportHistory.class);
            Example.Criteria criteria5 = example5.createCriteria();
            criteria5.andEqualTo("type", "ITWT_" + interviewId);
            List<BaseProjectExportHistory> historys = exportDao.selectByExample(example5);
            for (BaseProjectExportHistory history : historys) {
                deleteItwtDown(history.getId());
            }
            //回写访谈总时长
            BaseProjectInterview interview = interviewDao.selectByPrimaryKey(interviewId);
            BaseProject project = new BaseProject();
            project.setId(deleteVO.getProjectId());
            project.setInterviewTimeLen(interview.getInterviewTimeLen());
            project.setUpdateTime(new Date());
            project.setUpdateUser(ThreadLocalManager.getUserId());
            projectDao.updateProjectDel(project);
        }
        // 修改样本状态(已分派)
        sampleDao.updateSampleStatus(deleteVO.getProjectId(), sampleIds, SampleConstants.STATUS_ASSIGN);

        // 删除访谈文件
        FileDeleteVO fileDeleteVO = new FileDeleteVO();
        fileDeleteVO.setFileIds(fileIds);
        fileDeleteVO.setProjectId(deleteVO.getProjectId());
        deleteInterviewFile(fileDeleteVO);

        int row = interviewDao.deleteInterview(deleteVO);
        if (row > 0) {
            // 回写访谈总数
            BaseProject project = new BaseProject();
            project.setNumOfInterview(row);
            project.setUpdateUser(ThreadLocalManager.getUserId());
            project.setUpdateTime(new Date());
            project.setId(deleteVO.getProjectId());
            projectDao.updateProjectDel(project);
        }
        return row;
    }

    /**
     * 访谈详情
     *
     * @param detailVO
     * @return
     */
    public InterviewDetailDTO getInterviewDetail(InterviewDetailVO detailVO) {
        InterviewDetailDTO detailDTO = interviewDao.getInterviewDetail(detailVO);
        detailDTO.setPlanDuration(DateUtil.secondToHourMinuteSecondChineseStrByLong(DateUtil.getDateDuration(detailDTO.getPlanStartTime(), detailDTO.getPlanEndTime())));
        if (detailDTO.getBeginTime() != null && detailDTO.getEndTime() != null) {
            detailDTO.setInterviewDuration(DateUtil.secondToHourMinuteSecondChineseStrByLong(DateUtil.getDateDuration(detailDTO.getBeginTime(), detailDTO.getEndTime())));
        }
        if (detailDTO.getDocEndTime() != null && detailDTO.getDocBeginTime() != null) {
            detailDTO.setDocDuration(DateUtil.secondToHourMinuteSecondChineseStrByLong(DateUtil.getDateDuration(detailDTO.getDocBeginTime(), detailDTO.getDocEndTime())));
        }
        if (detailDTO.getAuditTime() != null && detailDTO.getAuditEndTime() != null) {
            detailDTO.setAuditDuration(DateUtil.secondToHourMinuteSecondChineseStrByLong(DateUtil.getDateDuration(detailDTO.getAuditTime(), detailDTO.getAuditEndTime())));
        }
        return detailDTO;
    }

    /**
     * 更新访谈计划
     *
     * @param detailVO
     * @return status  2:开始访谈 3:结束访谈 4;提交审核 5:审核通过
     */
    public Integer updateInterviewStatus(InterviewDetailVO detailVO) {
        BaseProject baseProject = projectDao.selectByPrimaryKey(detailVO.getProjectId());
        if (baseProject.getStatus() != 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目未启动");
        }
        Date now = new Date();
        BaseProjectInterview projectInterview = interviewDao.selectByPrimaryKey(detailVO.getInterviewId());
        projectInterview.setStatus(detailVO.getStatus());
        switch (detailVO.getStatus()) {
            case 2:
                // 开始访谈
                projectInterview.setBeginTime(now);
                break;
            case 3:
                // 结束访谈
                projectInterview.setEndTime(now);
                projectInterview.setInterviewTimeLen(DateUtil.getDateDuration(projectInterview.getBeginTime(), now));
                projectInterview.setDocBeginTime(now);
                // 回写项目总时长
                BaseProject project = new BaseProject();
                project.setInterviewTimeLen(DateUtil.getDateDuration(projectInterview.getBeginTime(), now));
                project.setUpdateUser(ThreadLocalManager.getUserId());
                project.setUpdateTime(now);
                project.setId(detailVO.getProjectId());
                projectDao.updateProjectAdd(project);
                // 回写访谈总时长
                BaseProjectInterview interview = new BaseProjectInterview();
                interview.setId(detailVO.getInterviewId());
                interview.setInterviewTimeLen(DateUtil.getDateDuration(projectInterview.getBeginTime(), now));
                interviewDao.updateInterviewAdd(interview);
                // app记录位置
                if (!StringUtils.isEmpty(detailVO.getLocation())) {
                    projectInterview.setLocation(detailVO.getLocation());
                }
                break;
            case 4:
                // 提交审核
                projectInterview.setDocEndTime(now);
                projectInterview.setAuditTime(now);
                break;
            default:
        }
        return interviewDao.updateByPrimaryKeySelective(projectInterview);
    }

    /**
     * 访谈审核
     *
     * @param detailVO
     * @return
     */
    public Integer updateInterviewAudit(InterviewDetailVO detailVO) {
        Date now = new Date();
        // 更新样本状态
        List<Integer> sampleIds = new ArrayList<>();
        Example example = new Example(BaseProjectInterviewSample.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("interviewId", detailVO.getInterviewId());
        List<BaseProjectInterviewSample> interviewSamples = interviewSampleDao.selectByExample(example);
        for (BaseProjectInterviewSample interviewSample : interviewSamples) {
            sampleIds.add(interviewSample.getSampleId());
        }
        // 更新访谈状态
        BaseProjectInterview projectInterview = interviewDao.selectByPrimaryKey(detailVO.getInterviewId());
        projectInterview.setAuditNote(detailVO.getAuditNote());
        projectInterview.setAuditUser(ThreadLocalManager.getUserId());
        projectInterview.setAuditEndTime(now);
        switch (detailVO.getValid()) {
            case 1:
                // 访谈通过
                projectInterview.setValid(1);
                projectInterview.setStatus(InterviewConstants.INTERVIEW_FINISH);
                sampleDao.updateSampleStatus(detailVO.getProjectId(), sampleIds, 3);
                break;
            case 2:
                // 退回到整理阶段
                projectInterview.setValid(2);
                projectInterview.setStatus(InterviewConstants.INTERVIEW_ARRANGE);
                break;
            case 3:
                // 退回到访谈阶段
                // TODO 删除录音文本相关?
                projectInterview.setValid(3);
                projectInterview.setStatus(InterviewConstants.INTERVIEW_EXE);
                break;
            case 4:
                // 拒访
                projectInterview.setValid(4);
                projectInterview.setStatus(InterviewConstants.INTERVIEW_FINISH);
                sampleDao.updateSampleStatus(detailVO.getProjectId(), sampleIds, 3);
                break;
            default:
        }
        return interviewDao.updateByPrimaryKeySelective(projectInterview);
    }

    /**
     * 归属样本
     *
     * @param sampleOwnerVO
     * @return
     */
    public List<SampleOwnerDTO> getSampleOwner(SampleOwnerVO sampleOwnerVO) {
        return interviewDao.getSampleOwner(sampleOwnerVO);
    }

    /**
     * 保存/编辑讲话者
     *
     * @param speakVO
     * @return
     */
    public Integer saveSpeaker(InterviewSpeakerVO speakVO) {
        BaseProjectInterviewSpeaker speak = new BaseProjectInterviewSpeaker();
        BeanUtils.copyProperties(speakVO, speak);
        if (speakVO.getId() == null) {
            speak.setCreateTime(new Date());
            speak.setCreateUser(ThreadLocalManager.getUserId());
            return speakerDao.insertSelective(speak);
        } else {
            return speakerDao.updateByPrimaryKeySelective(speak);
        }
    }

    /**
     * 讲话者列表
     *
     * @param interviewId
     * @return
     */
    public List<SpeakerListDTO> getSpeakList(Integer interviewId) {
        BaseProjectInterview interview = interviewDao.selectByPrimaryKey(interviewId);
        return speakerDao.getSpeakList(interviewId, interview.getProjectId());
    }

    /**
     * 删除/批量删除讲话者
     *
     * @param deleteVO
     * @return
     */
    public Integer deleteSpeaker(SpeakerDeleteVO deleteVO) {
        return speakerDao.deleteSpeaker(deleteVO);
    }

    /**
     * 讲话者详情
     *
     * @param speakId
     * @return
     */
    public SpeakDTO getSpeakDetail(Integer speakId) {
        SpeakDTO speakDTO = new SpeakDTO();
        BaseProjectInterviewSpeaker speaker = speakerDao.selectByPrimaryKey(speakId);
        BeanUtils.copyProperties(speaker, speakDTO);
        return speakDTO;
    }

    /**
     * 保存/编辑访谈文本
     *
     * @param paragraphVO
     * @return
     */
    public Integer saveParagraph(InterviewParagraphVO paragraphVO) {
        if (paragraphVO.getStatus() >= InterviewConstants.INTERVIEW_AUDIT) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "已提交审核,无法编辑文本");
        }
        BaseProjectInterviewParagraph paragraph = new BaseProjectInterviewParagraph();
        BeanUtils.copyProperties(paragraphVO, paragraph);
        if (paragraphVO.getId() == null) {
            paragraph.setCreateTime(new Date());
            paragraph.setCreateUser(ThreadLocalManager.getUserId());
            return paragraphDao.insertSelective(paragraph);
        } else {
            // BaseProjectInterview interview = interviewDao.selectByPrimaryKey(paragraphVO.getInterviewId());
            return paragraphDao.updateByPrimaryKeySelective(paragraph);
        }
    }

    /**
     * 访谈文本列表
     *
     * @param paragraphListVO
     * @return
     */
    public PageList<Page> getParagraphList(ParagraphListVO paragraphListVO) {
        Page page = PageHelper.startPage(paragraphListVO.getPageNum(), paragraphListVO.getPageSize());
        paragraphDao.getParagraphList(paragraphListVO);
        return new PageList<>(page);
    }

    /**
     * 访谈文本列表
     *
     * @param paragraphListAppVO
     * @return
     */
    public List<InterviewParagraphDTO> getParagraphListApp(ParagraphListAppVO paragraphListAppVO) {
        return paragraphDao.getParagraphListApp(paragraphListAppVO);
    }

    /**
     * 文本详情
     *
     * @param paragraphId
     * @return
     */
    public BaseProjectInterviewParagraph getParagraphDetail(Integer paragraphId) {
        return paragraphDao.selectByPrimaryKey(paragraphId);
    }

    /**
     * 删除文本
     *
     * @param paragraphVO
     * @return
     */
    public Integer deleteParagraph(ParagraphVO paragraphVO) {
        BaseProjectInterview interview = interviewDao.selectByPrimaryKey(paragraphVO.getInterviewId());
        if (interview.getStatus() >= 4) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "审核已提交,无法删除文本");
        }
        if (paragraphVO.getFileId() != null) {
            Example example = new Example(BaseProjectInterviewParagraph.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("fileId", paragraphVO.getFileId());
            return paragraphDao.deleteByExample(example);
        }
        return paragraphDao.deleteByPrimaryKey(paragraphVO.getParagraphId());
    }

    /**
     * 文本审核
     *
     * @param auditVO
     * @return
     */
    public Integer updateParagraphAudit(ParagraphAuditVO auditVO) {
        Date now = new Date();
        Example example = new Example(BaseProjectInterviewParagraph.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("fileId", auditVO.getFileId());
        List<BaseProjectInterviewParagraph> paragraphs = paragraphDao.selectByExample(example);
        BaseProjectInterview interview = interviewDao.selectByPrimaryKey(auditVO.getInterviewId());
        for (BaseProjectInterviewParagraph paragraph : paragraphs) {
            paragraph.setAuditUser(ThreadLocalManager.getUserId());
            paragraph.setAuditTime(now);
            paragraph.setValid(auditVO.getValid());
            paragraph.setAuditNote(auditVO.getAuditNote());
            paragraphDao.updateByPrimaryKeySelective(paragraph);
        }
        // 修改访谈状态
        switch (auditVO.getValid()) {
            case 1:
                break;
            case 2:
                interview.setValid(3);
                break;
            case 3:
                interview.setValid(1);
                break;
            default:
        }
        return interviewDao.updateByPrimaryKeySelective(interview);
    }


    /**
     * 文件上传
     *
     * @param files
     * @param interviewId
     */
    public Integer uploadFiles(MultipartFile[] files, Integer interviewId, Integer projectId, Integer fileType) {
        String pre;
        BaseProjectInterview interview = interviewDao.selectByPrimaryKey(interviewId);
        if (interview.getStatus().equals(InterviewConstants.INTERVIEW_FINISH)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "访谈审核完成,无法上传录音和附件");
        }
        if (fileType == 1) {
            pre = "/file/voice/" + projectId + "/";
        } else {
            pre = "/file/attachment/" + projectId + "/";
        }
        String path = filePath + pre;
        List<Map<String, Object>> resMap = FileUtil.uploadFiles(files, path);
        for (int i = 0; i < resMap.size(); i++) {
            BaseProjectInterviewFile interviewFile = new BaseProjectInterviewFile();
            interviewFile.setInterviewId(interviewId);
            interviewFile.setType(fileType);
            interviewFile.setFilePath(pre + resMap.get(i).get("fileName"));
            interviewFile.setName((String) resMap.get(i).get("originName"));
            interviewFile.setFileSize((Long) resMap.get(i).get("fileSize"));
            // interviewFile.setDuration(); 文件时长
            interviewFile.setCreateTime(new Date());
            interviewFile.setCreateUser(ThreadLocalManager.getUserId());
            interviewFile.setUploadTime(new Date());
            int row = fileDao.insertSelective(interviewFile);
            if (row > 0) {
                // 回写访谈信息量
                BaseProjectInterview projectInterview = new BaseProjectInterview();
                projectInterview.setId(interviewId);
                projectInterview.setFileSize((Long) resMap.get(i).get("fileSize"));
                interviewDao.updateInterviewAdd(projectInterview);
                // 回写项目信息量
                BaseProject project = new BaseProject();
                project.setFileSize((Long) resMap.get(i).get("fileSize"));
                project.setUpdateUser(ThreadLocalManager.getUserId());
                project.setUpdateTime(new Date());
                project.setId(projectId);
                projectDao.updateProjectAdd(project);
            }

        }
        return resMap.size();
    }


    /**
     * 文件重命名
     *
     * @param updateVO
     * @return
     */
    public Integer updateFileName(FileUpdateVO updateVO) {
        BaseProjectInterviewFile file = fileDao.selectByPrimaryKey(updateVO.getFileId());
        file.setName(updateVO.getName());
        return fileDao.updateByPrimaryKeySelective(file);
    }

    /**
     * 导出访谈
     *
     * @param exportVO
     * @return
     * @throws Exception
     */
    public int exportInterview(InterviewExportVO exportVO) throws Exception {
        List<InterviewListDTO> data = new ArrayList<>();
        if ("ALL".equals(exportVO.getOpt())) {
            InterviewVO searchVO = new InterviewVO();
            searchVO.setUserId(ThreadLocalManager.getUserId());
            searchVO.setCheckRole(projectService.getProjectRole(exportVO.getProjectId()));
            searchVO.setProjectId(exportVO.getProjectId());
            data = interviewDao.getInterviewList(searchVO);
            for (InterviewListDTO interviewDTO : data) {
                interviewDTO.setFileSize(FileUtil.byteFormat(Long.valueOf(interviewDTO.getFileSize()), true));
                interviewDTO.setInterviewTimeLen(DateUtil.secondToHourMinuteSecondEnStrByLong(Long.valueOf(interviewDTO.getInterviewTimeLen())));
            }
        } else if ("SEARCH".equals(exportVO.getOpt())) {
            exportVO.getInterviewVO().setUserId(ThreadLocalManager.getUserId());
            exportVO.getInterviewVO().setCheckRole(projectService.getProjectRole(exportVO.getProjectId()));
            data = interviewDao.getInterviewList(exportVO.getInterviewVO());
            for (InterviewListDTO interviewDTO : data) {
                interviewDTO.setFileSize(FileUtil.byteFormat(Long.valueOf(interviewDTO.getFileSize()), true));
                interviewDTO.setInterviewTimeLen(DateUtil.secondToHourMinuteSecondEnStrByLong(Long.valueOf(interviewDTO.getInterviewTimeLen())));
            }
        } else if ("CHECK".equals(exportVO.getOpt())) {
            InterviewVO checkVO = new InterviewVO();
            checkVO.setInterviewId(exportVO.getInterviewId());
            checkVO.setProjectId(exportVO.getProjectId());
            data = interviewDao.getInterviewList(checkVO);
            for (InterviewListDTO interviewDTO : data) {
                interviewDTO.setFileSize(FileUtil.byteFormat(Long.valueOf(interviewDTO.getFileSize()), true));
                interviewDTO.setInterviewTimeLen(DateUtil.secondToHourMinuteSecondEnStrByLong(Long.valueOf(interviewDTO.getInterviewTimeLen())));
            }
        }
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        JSONArray array = JSON.parseArray(JSON.toJSONString(data, filter, SerializerFeature.WriteDateUseDateFormat));
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            obj.put("assistantName", obj.get("assistantName").toString().replace(",", ";"));
        }
        String pre = "/export/interview/" + exportVO.getProjectId() + "/";
        String path = filePath + pre;
        Map<String, Object> res = new HashMap<>();
        if ("EXCEL".equals(exportVO.getFileType())) {
            res = ExcelUtil.createExcelFile("ITW", exportVO.getProperties(), array, path);
        } else if ("CSV".equals(exportVO.getFileType())) {
            res = CsvUtil.createCsvFile("ITW", exportVO.getProperties(), array, path);
        } else if ("TXT".equals(exportVO.getFileType())) {
            res = TxtUtil.createTextFile("ITW", exportVO.getProperties(), array, path);
        }
        BaseProjectExportHistory exportHistory = new BaseProjectExportHistory();
        exportHistory.setName(res.get("fileName").toString());
        exportHistory.setFileSize(Long.parseLong(res.get("fileSize").toString()));
        exportHistory.setFilePath(pre + res.get("fileName").toString());
        exportHistory.setType("ITW");
        exportHistory.setFileType(exportVO.getFileType());
        exportHistory.setProjectId(exportVO.getProjectId());
        exportHistory.setDescription(exportVO.getDescription());
        exportHistory.setCreateUser(ThreadLocalManager.getUserId());
        exportHistory.setCreateTime(new Date());
        return exportDao.insert(exportHistory);
    }

    // JSON转化过程中将null值过滤为空字符串
    private ValueFilter filter = new ValueFilter() {
        @Override
        public Object process(Object obj, String s, Object v) {
            if (v == null)
                return "";
            return v;
        }
    };

    /**
     * 下载访谈导出
     *
     * @param id
     * @param response
     * @throws Exception
     */
    public void downloadInterview(Integer id, HttpServletResponse response) throws Exception {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/interview/" + exportHistory.getProjectId() + "/" + exportHistory.getName();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * 下载对话文本模板
     *
     * @param response
     * @throws Exception
     */
    public void downloadTempItwt(HttpServletResponse response) throws Exception {
        String path = filePath + "/导入对话文本模板.doc";
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * 下载对话文本导出
     *
     * @param id
     * @param response
     * @throws Exception
     */
    public void downloadItwt(Integer id, HttpServletResponse response) throws Exception {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/itwt/" + exportHistory.getProjectId() + "/" + exportHistory.getName();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * 下载录音或附件
     *
     * @param fileId
     * @param response
     * @throws Exception
     */
    public void downloadFile(Integer fileId, HttpServletResponse response) throws Exception {
        BaseProjectInterviewFile file = fileDao.selectByPrimaryKey(fileId);
        String path = filePath + file.getFilePath();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * 删除访谈导出文件
     *
     * @param id
     * @return
     */
    public Integer deleteInterviewDown(Integer id) {
        BaseProjectExportHistory history = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/interview/" + history.getProjectId() + "/";
        boolean flag = FileUtil.deleteFile(path, history.getName());
        if (flag) {
            return exportDao.deleteByPrimaryKey(id);
        } else {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "文件删除失败");
        }
    }

    /**
     * 获取访谈下载记录
     *
     * @param pageParam
     * @return
     */
    public PageList<Page> getInterviewDownList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        Example example = new Example(BaseProjectExportHistory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", pageParam.getProjectId());
        criteria.andEqualTo("type", "ITW");
        example.orderBy("createTime").desc();
        exportDao.selectByExample(example);
        return new PageList<>(page);
    }

    /**
     * 导入对话文本
     *
     * @param importVO
     * @return
     */
    public Map<String, List<BatchImportVO>> insertParagraphByImport(ParagraphImportVO importVO) {
        HashMap<String, List<BatchImportVO>> map = new HashMap<>();
        List<BatchImportVO> errorList = new ArrayList<>();
        List<BatchImportVO> correctList = new ArrayList<>();
        List<BaseProjectInterviewParagraph> paragraphList = new ArrayList<>();
        for (BatchImportVO batchImportVO : importVO.getBatchImport()) {
            if (StringUtils.isEmpty(batchImportVO.getParagraph()) && StringUtils.isEmpty(batchImportVO.getName())) {
                errorList.add(batchImportVO);
                continue;
            }
            // 判断讲话者是否存在
            if (speakerDao.checkSpeak(batchImportVO.getName(), importVO.getInterviewId()) == null) {
                errorList.add(batchImportVO);
                continue;
            }
            // 添加文本
            BaseProjectInterviewParagraph paragraph = new BaseProjectInterviewParagraph();
            paragraph.setName(batchImportVO.getName());
            paragraph.setSpeakId(speakerDao.checkSpeak(batchImportVO.getName(), importVO.getInterviewId()));
            paragraph.setParagraph(batchImportVO.getParagraph());
            paragraph.setCreateUser(ThreadLocalManager.getUserId());
            paragraph.setCreateTime(new Date());
            paragraph.setInterviewId(importVO.getInterviewId());
            paragraph.setFileId(importVO.getFileId());
            correctList.add(batchImportVO);
            paragraphList.add(paragraph);
            //paragraphDao.insert(paragraph);
        }
        if (paragraphList.size() > 0) {
            paragraphDao.insertBatchParagraph(paragraphList);
        }
        map.put("error", errorList);
        map.put("correct", correctList);
        return map;
    }


    /**
     * word导入对话文本
     *
     * @param file
     * @param interviewId
     * @param projectId
     * @param fileId
     * @return
     */
    public Map<String, List<BatchImportVO>> batchImportItwtByWord(MultipartFile file, Integer interviewId, Integer projectId, Integer fileId) {
        ParagraphImportVO paragraphImportVO = new ParagraphImportVO();
        List<BatchImportVO> importList = readWord(file);
        paragraphImportVO.setBatchImport(importList);
        paragraphImportVO.setFileId(fileId);
        paragraphImportVO.setInterviewId(interviewId);
        return insertParagraphByImport(paragraphImportVO);
    }

    /**
     * 读取Word文件流
     *
     * @param file
     * @return
     */
    public static List<BatchImportVO> readWord(MultipartFile file) {
        List<BatchImportVO> batchImport = new ArrayList<>();
        InputStream is = null;
        String separate = ":|：|\\s+";
        try {
            if (file.getOriginalFilename().trim().endsWith("doc")) {
                is = file.getInputStream();
                WordExtractor ex = new WordExtractor(is);
                // 截取段落
                String[] strArr = ex.getParagraphText();
                // 中英文冒号做分割
                if (strArr.length > 0) {
                    for (int i = 0; i < strArr.length; i++) {
                        if ("\r\n".equals(strArr[i]) || StringUtils.isEmpty(strArr[i]) || !(strArr[i].contains(":") || strArr[i].contains("："))) {
                            continue;
                        }
                        int eIndex = strArr[i].indexOf("：") > 0 ? strArr[i].indexOf("：") : 0;
                        int cIndex = strArr[i].indexOf(":") > 0 ? strArr[i].indexOf(":") : 0;
                        int index;
                        if (eIndex != 0 && cIndex != 0) {
                            index = Math.min(eIndex, cIndex);
                        }else {
                            index = Math.max(eIndex, cIndex);
                        }
                        if (index > 0) {
                            BatchImportVO importVO = new BatchImportVO();
                            importVO.setName(strArr[i].substring(0, index));
                            importVO.setParagraph(strArr[i].substring(index + 1));
                            batchImport.add(importVO);
                        }
                    }
                }
                // ex.close();
            } else if (file.getOriginalFilename().trim().endsWith("docx")) {
                is = file.getInputStream();
                XWPFDocument doc = new XWPFDocument(is);
                List<XWPFParagraph> paragraphList = doc.getParagraphs();
                for (XWPFParagraph paragraph : paragraphList) {
                    String text = paragraph.getParagraphText();
                    if ("\r\n".equals(text) || StringUtils.isEmpty(text) || !(text.contains(":") || text.contains("："))) {
                        continue;
                    }
                    int eIndex = text.indexOf("：") > 0 ? text.indexOf("：") : 0;
                    int cIndex = text.indexOf(":") > 0 ? text.indexOf(":") : 0;
                    int index;
                    if (eIndex != 0 && cIndex != 0) {
                        index = Math.min(eIndex, cIndex);
                    }else {
                        index = Math.max(eIndex, cIndex);
                    }
                    if (index > 0) {
                        BatchImportVO importVO = new BatchImportVO();
                        importVO.setName(text.substring(0, index));
                        importVO.setParagraph(text.substring(index + 1));
                        batchImport.add(importVO);
                    }
                }
            } else {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "文件格式错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return batchImport;
    }

    /**
     * 对话文本导出
     *
     * @param exportVO
     * @return
     * @throws Exception
     */
    public int exportItwt(ParagraphExportVO exportVO) throws Exception {
        List<File> fileList = new ArrayList<>();
        Example example = new Example(BaseProjectInterviewFile.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("interviewId", exportVO.getListVO().getInterviewId());
        criteria.andEqualTo("type", 1);
        List<BaseProjectInterviewFile> audioList = fileDao.selectByExample(example);
        String pre = "/export/itwt/" + exportVO.getProjectId() + "/";
        String path = filePath + pre;
        for (BaseProjectInterviewFile audio : audioList) {
            exportVO.getListVO().setFileId(audio.getId());
            List<InterviewParagraphDTO> data = paragraphDao.getParagraphList(exportVO.getListVO());
            if (data.size() == 0) {
                continue;
            }
            JSONArray array = JSON.parseArray(JSON.toJSONString(data));
            Map<String, Object> res = new HashMap<>();
            if ("EXCEL".equals(exportVO.getFileType())) {
                res = ExcelUtil.createExcelFile("ITWT", exportVO.getProperties(), array, path);
            } else if ("CSV".equals(exportVO.getFileType())) {
                res = CsvUtil.createCsvFile("ITWT", exportVO.getProperties(), array, path);
            } else if ("TXT".equals(exportVO.getFileType())) {
                res = TxtUtil.createTextFile("ITWT", exportVO.getProperties(), array, path);
            }
            fileList.add(new File(path + res.get("fileName").toString()));
        }
        String zipName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".zip";
        String zipPath = path + zipName;
        try {
            FileOutputStream fos1 = new FileOutputStream(new File(zipPath));
            ZipUtil.toZip(fileList, fos1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long size = new File(zipPath).length();
        // 删除文件
        for (int i = 0; i < fileList.size(); i++) {
            File tempFile = fileList.get(i);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        BaseProjectExportHistory exportHistory = new BaseProjectExportHistory();
        exportHistory.setName(zipName);
        exportHistory.setFileSize(size);
        exportHistory.setFilePath(pre + zipName);
        exportHistory.setType("ITWT_" + exportVO.getListVO().getInterviewId());
        exportHistory.setFileType(exportVO.getFileType());
        exportHistory.setProjectId(exportVO.getProjectId());
        exportHistory.setDescription(exportVO.getDescription());
        exportHistory.setCreateUser(ThreadLocalManager.getUserId());
        exportHistory.setCreateTime(new Date());
        return exportDao.insert(exportHistory);
    }

    /**
     * 获取对话文本下载记录
     *
     * @param historyVO
     * @return
     */
    public PageList<Page> getItwtDownList(ExportHistoryVO historyVO) {
        Page page = PageHelper.startPage(historyVO.getPageNum(), historyVO.getPageSize());
        Example example = new Example(BaseProjectExportHistory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", historyVO.getProjectId());
        criteria.andEqualTo("type", "ITWT_" + historyVO.getInterviewId());
        example.orderBy("createTime").desc();
        exportDao.selectByExample(example);
        return new PageList<>(page);
    }

    /**
     * 删除导出对话文本
     *
     * @param id
     * @return
     */
    public Integer deleteItwtDown(Integer id) {
        BaseProjectExportHistory history = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/itwt/" + history.getProjectId() + "/";
        boolean flag = FileUtil.deleteFile(path, history.getName());
        if (flag) {
            return exportDao.deleteByPrimaryKey(id);
        } else {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "文件删除失败");
        }
    }

    /**
     * 智能语音转换
     *
     * @param convertVO
     * @return
     */
    public List convertData(ParagraphConvertVO convertVO) {
        List<BatchImportVO> list = new ArrayList<>();
        BaseProjectInterviewFile file = fileDao.selectByPrimaryKey(convertVO.getFileId());
        JSONArray result = LfasrUtil.getLfasrUtil().convertDataToJSON(filePath + file.getFilePath(), convertVO.getNumber());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        for (int i = 0; i < result.size(); i++) {
            BatchImportVO importVO = new BatchImportVO();
            JSONObject obj = result.getJSONObject(i);
            System.out.println(obj.toJSONString());
            importVO.setName("讲话者" + obj.get("speaker"));
            importVO.setRealName("讲话者" + obj.get("speaker"));
            importVO.setParagraph(obj.getString("onebest"));
            importVO.setBeginTime(sdf.format(Long.valueOf(obj.getString("bg")) - TimeZone.getDefault().getRawOffset()));
            importVO.setEndTime(sdf.format(Long.valueOf(obj.getString("ed")) - TimeZone.getDefault().getRawOffset()));
            importVO.setDuration((Long.valueOf(obj.getString("ed")) - Long.valueOf(obj.getString("bg"))));
            list.add(importVO);
        }
        return list;
    }

    public Integer insertBatchText(ParagraphImportVO importVO) {
        for (BatchImportVO batchImportVO : importVO.getBatchImport()) {
            BaseProjectInterviewParagraph paragraph = new BaseProjectInterviewParagraph();
            paragraph.setFileId(importVO.getFileId());
            paragraph.setInterviewId(importVO.getInterviewId());
            paragraph.setCreateTime(new Date());
            paragraph.setCreateUser(ThreadLocalManager.getUserId());
            paragraph.setParagraph(batchImportVO.getParagraph());
            paragraph.setSpeakId(batchImportVO.getId());
            paragraph.setBeginTime(batchImportVO.getBeginTime());
            paragraph.setEndTime(batchImportVO.getEndTime());
            paragraph.setDuration(batchImportVO.getDuration());
            paragraphDao.insert(paragraph);
        }
        return importVO.getBatchImport().size();
    }

    // =========================== budi Begin ===========================

    /**
     * AI语音转换批量导入对话文本
     * @param param
     * @return
     */
    public Integer insertBatchParagraphAI(ParagraphBatchImportListVO param) {
        for (ParagraphBatchImportVO importVO : param.getList()) {
            BaseProjectInterviewParagraph paragraph = new BaseProjectInterviewParagraph();
            paragraph.setName(importVO.getName());
            paragraph.setInterviewId(importVO.getInterviewId());
            paragraph.setCreateTime(new Date());
            paragraph.setCreateUser(ThreadLocalManager.getUserId());
            paragraph.setSpeakId(importVO.getSpeakerId());
            paragraph.setFileId(importVO.getFileId());
            paragraph.setBeginTime(importVO.getBeginTime());
            paragraph.setEndTime(importVO.getEndTime());
            paragraph.setParagraph(importVO.getParagraph());
            paragraph.setDuration(importVO.getDuration());
            paragraphDao.insert(paragraph);
        }
        return param.getList().size();
    }

    // =========================== budi End ===========================

}
