package com.monetware.ringinterview.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringinterview.business.dao.*;
import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.constant.SampleConstants;
import com.monetware.ringinterview.business.pojo.dto.interview.FileCountDTO;
import com.monetware.ringinterview.business.pojo.dto.interview.InterviewListDTO;
import com.monetware.ringinterview.business.pojo.dto.sample.*;
import com.monetware.ringinterview.business.pojo.po.*;
import com.monetware.ringinterview.business.pojo.vo.interview.InterviewVO;
import com.monetware.ringinterview.business.pojo.vo.sample.*;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.PageParam;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.ringinterview.system.util.file.*;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Slf4j
@Service
public class SampleService {

    @Autowired
    private InfoProvinceCityDao cityDao;

    @Autowired
    private ProjectSampleDao sampleDao;

    @Autowired
    private SampleAssignmentDao assignmentDao;

    @Autowired
    private ProjectTeamUserDao teamUserDao;

    @Autowired
    private ProjectPropertyDao propertyDao;

    @Autowired
    private ProjectPropertyTemplateDao templateDao;

    @Autowired
    private ProjectExportDao exportDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectInterviewDao interviewDao;

    @Autowired
    private ProjectInterviewFileDao fileDao;

    @Value("${upload.dir}")
    private String filePath;

    /**
     * 获取五级行政
     *
     * @param townVO
     * @return
     */
    public List<String> getTownNameList(TownVO townVO) {
        if (townVO.getCityName().equals("北京市")
                || townVO.getCityName().equals("上海市")
                || townVO.getCityName().equals("天津市")
                || townVO.getCityName().equals("重庆市")) {
            townVO.setCityName("市辖区");
        }
        return cityDao.getTownNameList(townVO);
    }

    /**
     * 添加或编辑研究对象
     *
     * @param projectSampleVO
     * @return
     */
    public Integer saveProjectSample(ProjectSampleVO projectSampleVO) {
        Date now = new Date();
        projectSampleVO.setLastModifyUser(ThreadLocalManager.getUserId());
        projectSampleVO.setLastModifyTime(now);
        //判断code是否重复
        if (sampleDao.checkSampleExistByCode(projectSampleVO.getProjectId(), projectSampleVO.getCode(), projectSampleVO.getId()) > 0) {
            throw new ServiceException(ErrorCode.CODE_HAVE_BEEN_USED, "样本编号已存在");
        }
        if (projectSampleVO.getId() == null) {
            projectSampleVO.setCreateUser(ThreadLocalManager.getUserId());
            projectSampleVO.setCreateTime(now);
            projectSampleVO.setStatus(SampleConstants.STATUS_INIT);
            int row = sampleDao.saveProjectSample(projectSampleVO);
            if (row > 0) {
                // 回写样本总数
                BaseProject p = new BaseProject();
                p.setNumOfSample(row);
                p.setUpdateUser(ThreadLocalManager.getUserId());
                p.setUpdateTime(now);
                p.setId(projectSampleVO.getProjectId());
                projectDao.updateProjectAdd(p);
            }
            // 访问员添加样本默认自己为负责人
            if (projectSampleVO.getCheckRole() == AuthorizedConstants.ROLE_INTERVIEWER) {
                List<Integer> sampleIds = new ArrayList<>();
                sampleIds.add(projectSampleVO.getId());
                List<BaseSampleAssignment> assignmentList = new ArrayList<>();
                BaseSampleAssignment assignment = new BaseSampleAssignment();
                assignment.setType(1);
                assignment.setCreateUser(ThreadLocalManager.getUserId());
                assignment.setCreateTime(new Date());
                assignment.setSampleId(projectSampleVO.getId());
                assignmentList.add(assignment);
                // 样本状态调整为以分派
                sampleDao.updateSampleStatus(projectSampleVO.getProjectId(), sampleIds, SampleConstants.STATUS_ASSIGN);
                assignmentDao.insertAssign(projectSampleVO.getProjectId(), assignmentList);
            }
            return row;
        } else {
            return sampleDao.updateProjectSample(projectSampleVO);
        }
    }

    /**
     * 查询所有研究对象
     *
     * @param sampleListVO
     * @return
     */
    public PageList<Page> getSampleList(SampleListVO sampleListVO) {
        sampleListVO.setUserId(ThreadLocalManager.getUserId());
        Page page = PageHelper.startPage(sampleListVO.getPageNum(), sampleListVO.getPageSize());
        sampleDao.getSampleList(sampleListVO);
        return new PageList<>(page);
    }

    /**
     * App查询所有研究对象
     *
     * @param sampleListAppVO
     * @return
     */
    public List<SampleListDTO> getSampleListApp(SampleListAppVO sampleListAppVO) {
        sampleListAppVO.setUserId(ThreadLocalManager.getUserId());
        List<SampleListDTO> list = sampleDao.getSampleListApp(sampleListAppVO);
        return list;
    }

    /**
     * 删除研究对象
     *
     * @param deleteSampleVO
     * @return
     */
    public Integer deleteProjectSample(DeleteSampleVO deleteSampleVO) {
        // TODO 假删除，若需要真删除 需要删除 样本相关的所有东西
        int row = sampleDao.deleteSample(deleteSampleVO);
        if (row > 0) {
            // 回写样本总数
            BaseProject p = new BaseProject();
            p.setNumOfSample(row);
            p.setUpdateUser(ThreadLocalManager.getUserId());
            p.setUpdateTime(new Date());
            p.setId(deleteSampleVO.getProjectId());
            projectDao.updateProjectDel(p);

            // 删除访谈分派表
            assignmentDao.deleteAssignmentBySampleId(deleteSampleVO.getProjectId(), deleteSampleVO.getSampleIds());
        }
        return row;
    }

    /**
     * 研究对象详情
     *
     * @param detailVO
     * @return
     */
    public ProjectSampleDTO getSampleDetail(ProjectSampleDetailVO detailVO) {
        InterviewVO interviewVO = new InterviewVO();
        interviewVO.setProjectId(detailVO.getProjectId());
        interviewVO.setSampleId(detailVO.getId());
        interviewVO.setUserId(ThreadLocalManager.getUserId());
        interviewVO.setCheckRole(detailVO.getCheckRole());
        List<InterviewListDTO> interviewList = interviewDao.getInterviewList(interviewVO);
        for (InterviewListDTO interview : interviewList) {
            FileCountDTO fileCountDTO = fileDao.getFileCount(interview.getId());
            interview.setFileCountDTO(fileCountDTO);
        }
        ProjectSampleDTO projectSampleDTO = sampleDao.getSampleDetail(detailVO.getProjectId(), detailVO.getId());
        projectSampleDTO.setInterviewList(interviewList);
        return projectSampleDTO;
    }

    /**
     * 检查code是否重复
     *
     * @param codeCheckVO
     * @return
     */
    public boolean checkSampleCodeExist(SampleCodeCheckVO codeCheckVO) {
        return sampleDao.checkSampleExistByCode(codeCheckVO.getProjectId(), codeCheckVO.getCode(), null) > 0;
    }

    /**
     * 批量上传
     *
     * @param importVO
     * @return
     */
    public List<BaseProjectSample> insertSampleByImport(SampleImportVO importVO) {
        Date now = new Date();
        List<BaseProjectSample> res = new ArrayList<>();
        List<BaseProjectSample> insertList = new ArrayList<>();
        for (BaseProjectSample sample : importVO.getSampleList()) {
            // 判断样本名称, 编号是否为空，编号是否唯一
            if (StringUtils.isEmpty(sample.getName())
                    || StringUtils.isEmpty(sample.getCode())
                    || sampleDao.checkSampleExistByCode(importVO.getProjectId(), sample.getCode(), null) > 0
                    || validata(sample)) {
                res.add(sample);
                continue;
            }
            sample.setLastModifyTime(now);
            sample.setLastModifyUser(ThreadLocalManager.getUserId());
            sample.setCreateUser(ThreadLocalManager.getUserId());
            sample.setCreateTime(now);
            sample.setIsDelete(SampleConstants.INIT_VALUE);
            sample.setStatus(SampleConstants.STATUS_INIT);
            insertList.add(sample);
        }
        if (insertList.size() > 0) {
            sampleDao.insertSampleList(importVO.getProjectId(), insertList);
            // 回写样本总数
            BaseProject p = new BaseProject();
            p.setNumOfSample(insertList.size());
            p.setUpdateUser(ThreadLocalManager.getUserId());
            p.setUpdateTime(now);
            p.setId(importVO.getProjectId());
            projectDao.updateProjectAdd(p);
            // 访问员添加样本自动分派
            List<Integer> sampleIds = new ArrayList<>();
            if (importVO.getCheckRole() == AuthorizedConstants.ROLE_INTERVIEWER) {
                List<BaseSampleAssignment> assignmentList = new ArrayList<>();
                for (BaseProjectSample sample : insertList) {
                    BaseSampleAssignment assignment = new BaseSampleAssignment();
                    assignment.setType(1);
                    assignment.setCreateUser(ThreadLocalManager.getUserId());
                    assignment.setCreateTime(new Date());
                    assignment.setSampleId(sample.getId());
                    assignmentList.add(assignment);
                    sampleIds.add(sample.getId());
                }
                // 样本状态调整为以分派
                sampleDao.updateSampleStatus(importVO.getProjectId(), sampleIds, SampleConstants.STATUS_ASSIGN);
                assignmentDao.insertAssign(importVO.getProjectId(), assignmentList);
            }
        }
        return res;
    }

    /**
     * 校验参数
     * @param sample
     * @return
     */
    private boolean validata(BaseProjectSample sample) {
        if (!StringUtils.isEmpty(sample.getName()) && sample.getName().length() > 100)return true;
        if (!StringUtils.isEmpty(sample.getCode()) && sample.getCode().length() > 100)return true;
        if (!StringUtils.isEmpty(sample.getGender()) && sample.getGender().length() > 10)return true;
        if (!StringUtils.isEmpty(sample.getBirth()) && sample.getBirth().length() > 100)return true;
        if (!StringUtils.isEmpty(sample.getOrganization()) && sample.getOrganization().length() > 200)return true;
        if (!StringUtils.isEmpty(sample.getProfession()) && sample.getProfession().length() > 200) return true;
        if (!StringUtils.isEmpty(sample.getPosition()) && sample.getPosition().length() > 255) return true;
        if (!StringUtils.isEmpty(sample.getNationality()) && sample.getNationality().length() > 10) return true;
        if (!StringUtils.isEmpty(sample.getLanguage()) && sample.getLanguage().length() > 100) return true;
        if (!StringUtils.isEmpty(sample.getPlaceOfBirth()) && sample.getPlaceOfBirth().length() > 200) return true;
        if (!StringUtils.isEmpty(sample.getDialects()) && sample.getDialects().length() > 200) return true;
        if (!StringUtils.isEmpty(sample.getDescription()) && sample.getDescription().length() > 200) return true;
        if (!StringUtils.isEmpty(sample.getEmail()) && sample.getEmail().length() > 100) return true;
        if (!StringUtils.isEmpty(sample.getMobile()) && sample.getMobile().length() > 20) return true;
        if (!StringUtils.isEmpty(sample.getPhone()) && sample.getPhone().length() > 20) return true;
        if (!StringUtils.isEmpty(sample.getWeixin()) && sample.getWeixin().length() > 100) return true;
        if (!StringUtils.isEmpty(sample.getQq()) && sample.getQq().length() > 20) return true;
        if (!StringUtils.isEmpty(sample.getWeibo()) && sample.getWeibo().length() > 100) return true;
        if (!StringUtils.isEmpty(sample.getProvince()) && sample.getProvince().length() > 20) return true;
        if (!StringUtils.isEmpty(sample.getCity()) && sample.getCity().length() > 20) return true;
        if (!StringUtils.isEmpty(sample.getDistrict()) && sample.getDistrict().length() > 20) return true;
        if (!StringUtils.isEmpty(sample.getTown()) && sample.getTown().length() > 20) return true;
        if (!StringUtils.isEmpty(sample.getAddress()) && sample.getAddress().length() > 200) return true;
        return false;
    }

    /**
     * 批量更新
     *
     * @param importVO
     * @return
     */
    public List<BaseProjectSample> updateSampleByImport(SampleImportVO importVO) {
        Date now = new Date();
        List<BaseProjectSample> res = new ArrayList<>();
        List<BaseProjectSample> updateList = new ArrayList<>();
        ProjectSampleDTO checkSample;
        for (BaseProjectSample sample : importVO.getSampleList()) {
            // 判断样本ID, 名称是否存在，编号是否唯一
            if (sample.getId() == null) {
                res.add(sample);
                continue;
            }
            checkSample = sampleDao.getSampleDetail(importVO.getProjectId(), sample.getId());
            if (!checkSample.getName().equals(sample.getName()) || !checkSample.getCode().equals(sample.getCode())) {
                res.add(sample);
                continue;
            }
            sample.setLastModifyTime(now);
            sample.setLastModifyUser(ThreadLocalManager.getUserId());
            sample.setIsDelete(SampleConstants.INIT_VALUE);
            updateList.add(sample);
        }
        if (updateList.size() > 0) {
            int row = sampleDao.updateSampleList(importVO.getProjectId(), updateList);
        }
        return res;
    }

    /**
     * 导出
     *
     * @param exportVO
     * @return
     * @throws Exception
     */
    public int exportSample(SampleExportVO exportVO) throws Exception {
        List<SampleListDTO> data = new ArrayList<>();
        if ("ALL".equals(exportVO.getOpt())) {
            SampleListVO searchVO = new SampleListVO();
            searchVO.setUserId(ThreadLocalManager.getUserId());
            searchVO.setCheckRole(exportVO.getCheckRole());
            searchVO.setProjectId(exportVO.getProjectId());
            data = sampleDao.getSampleList(searchVO);
        } else if ("SEARCH".equals(exportVO.getOpt())) {
            exportVO.getSearchVO().setUserId(ThreadLocalManager.getUserId());
            exportVO.getSearchVO().setCheckRole(exportVO.getCheckRole());
            data = sampleDao.getSampleList(exportVO.getSearchVO());
        } else {
            data = sampleDao.getSampleListByIds(exportVO.getProjectId(), exportVO.getSampleIds());
        }
        JSONArray array = JSON.parseArray(JSON.toJSONString(data));
        String pre = "/export/sample/" + exportVO.getProjectId() + "/";
        String path = filePath + pre;
        Map<String, Object> res = new HashMap<>();
        if ("EXCEL".equals(exportVO.getFileType())) {
            res = ExcelUtil.createExcelFile("SAMPLE", exportVO.getProperties(), array, path);
        } else if ("CSV".equals(exportVO.getFileType())) {
            res = CsvUtil.createCsvFile("SAMPLE", exportVO.getProperties(), array, path);
        } else if ("TXT".equals(exportVO.getFileType())) {
            res = TxtUtil.createTextFile("SAMPLE", exportVO.getProperties(), array, path);
        }
        BaseProjectExportHistory exportHistory = new BaseProjectExportHistory();
        exportHistory.setName(res.get("fileName").toString());
        exportHistory.setFileSize(Long.parseLong(res.get("fileSize").toString()));
        exportHistory.setFilePath(pre + res.get("fileName").toString());
        exportHistory.setType("SAMPLE");
        exportHistory.setFileType(exportVO.getFileType());
        exportHistory.setProjectId(exportVO.getProjectId());
        exportHistory.setDescription(exportVO.getDescription());
        exportHistory.setCreateUser(ThreadLocalManager.getUserId());
        exportHistory.setCreateTime(new Date());
        int row = exportDao.insert(exportHistory);
        if (row > 0) {
            // 回写信息量总数
            BaseProject p = new BaseProject();
            p.setFileSize(Long.parseLong(res.get("fileSize").toString()));
            p.setUpdateUser(ThreadLocalManager.getUserId());
            p.setUpdateTime(new Date());
            p.setId(exportVO.getProjectId());
            projectDao.updateProjectAdd(p);
        }
        return row;
    }

    /**
     * 获取下载历史记录表
     *
     * @param pageParam
     * @return
     */
    public PageList<Page> getSampleDownList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        Example example = new Example(BaseProjectExportHistory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", pageParam.getProjectId());
        criteria.andEqualTo("type", "SAMPLE");
        example.orderBy("createTime").desc();
        exportDao.selectByExample(example);
        return new PageList<>(page);
    }

    /**
     * 下载样本文件
     *
     * @param id
     * @param response
     * @throws Exception
     */
    public void downloadSample(Integer id, HttpServletResponse response) throws Exception {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/sample/" + exportHistory.getProjectId() + "/" + exportHistory.getName();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * 删除文件
     *
     * @param delVO
     * @return
     */
    public int deleteSampleFile(SampleExportDelVO delVO) {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(delVO.getId());
        String path = filePath + "/export/sample/" + exportHistory.getProjectId() + "/";
        // 回写信息量总数
        BaseProject p = new BaseProject();
        p.setFileSize(new File(path + exportHistory.getName()).length());
        p.setUpdateUser(ThreadLocalManager.getUserId());
        p.setUpdateTime(new Date());
        p.setId(delVO.getProjectId());
        projectDao.updateProjectDel(p);
        boolean flag = FileUtil.deleteFile(path, exportHistory.getName());
        if (flag) {
            return exportDao.deleteByPrimaryKey(delVO.getId());
        } else {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "文件删除失败");
        }

    }


    /**
     * 分派团队成员
     *
     * @param assignTeamVO
     * @return
     */
    public Integer insertAssign(AssignTeamVO assignTeamVO) {
        if ("ALL".equals(assignTeamVO.getOpt())) {
            List<Integer> sampleIds = sampleDao.getSampleIdsAssigned(assignTeamVO.getProjectId());
            assignTeamVO.setSampleIds(sampleIds);
        }
        List<BaseSampleAssignment> assignmentList = new ArrayList<>();
        // 首先插入相同负责人的研究对象
        for (int i = 0; i < assignTeamVO.getSampleIds().size(); i++) {
            if (sampleDao.getStatusById(assignTeamVO.getProjectId(), assignTeamVO.getSampleIds().get(i)) != 0) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "所选样本已被分派");
            }
            BaseSampleAssignment sampleAssignment = new BaseSampleAssignment();
            sampleAssignment.setSampleId(assignTeamVO.getSampleIds().get(i));
            sampleAssignment.setCreateUser(ThreadLocalManager.getUserId());
            sampleAssignment.setType(SampleConstants.ASSIGN_TYPE_HEAD);
            sampleAssignment.setCreateTime(new Date());
            sampleAssignment.setTeamUserId(assignTeamVO.getManagerId());
            assignmentList.add(sampleAssignment);
            // 再插入相同协助者的研究对象
            for (int j = 0; j < assignTeamVO.getAssistantId().size(); j++) {
                BaseSampleAssignment assignment = new BaseSampleAssignment();
                assignment.setSampleId(assignTeamVO.getSampleIds().get(i));
                assignment.setCreateUser(ThreadLocalManager.getUserId());
                assignment.setType(SampleConstants.ASSIGN_TYPE_AID);
                assignment.setCreateTime(new Date());
                assignment.setTeamUserId(assignTeamVO.getAssistantId().get(j));
                assignmentList.add(assignment);
            }
        }
        // 样本状态调整为以分派
        sampleDao.updateSampleStatus(assignTeamVO.getProjectId(), assignTeamVO.getSampleIds(), SampleConstants.STATUS_ASSIGN);
        return assignmentDao.insertAssign(assignTeamVO.getProjectId(), assignmentList);
    }

    /**
     * 批量分派
     *
     * @param importVO
     * @return
     */
    public Map insertAssignByImport(BatchAssignByImportVO importVO) {
        List<BaseSampleAssignment> assignmentList = new ArrayList<>();
        List<Integer> sampleIds = new ArrayList<>();
        List<BatchAssignVO> errorList = new ArrayList<>();
        List<BatchAssignVO> correctList = new ArrayList<>();
        Map<String, List> assignMap = new HashMap<>();
        boolean flag = true;
        for (BatchAssignVO assignVO : importVO.getAssignList()) {
            // 负责人,样本是否存在
            BaseProjectSample sample = sampleDao.getSampleByCode(importVO.getProjectId(), assignVO.getCode());
            if (StringUtils.isEmpty(assignVO.getAssistantName())
                    || StringUtils.isEmpty(assignVO.getManagerName())
                    || StringUtils.isEmpty(assignVO.getCode())
                    || teamUserDao.getTeamUserIdByName(importVO.getProjectId(), assignVO.getManagerName(), 5) == null
                    || sample == null
                    || sample.getStatus() != 0) {
                errorList.add(assignVO);
                continue;
            }
            // 协作者在团队中是否存在
            String[] assistantNameArr = assignVO.getAssistantName().split(",");
            for (String assistantName : assistantNameArr) {
                if (teamUserDao.getTeamUserIdByName(importVO.getProjectId(), assistantName, 3) == null) {
                    errorList.add(assignVO);
                    flag = false;
                } else {
                    // 添加协助者
                    BaseSampleAssignment sampleAssignment = new BaseSampleAssignment();
                    sampleAssignment.setType(2);
                    sampleAssignment.setCreateTime(new Date());
                    sampleAssignment.setCreateUser(ThreadLocalManager.getUserId());
                    sampleAssignment.setSampleId(sample.getId());
                    sampleAssignment.setTeamUserId(teamUserDao.getTeamUserIdByName(importVO.getProjectId(), assistantName, 3));
                    flag = true;
                    assignmentList.add(sampleAssignment);
                }
            }
            // 添加负责人
            if (flag) {
                BaseSampleAssignment assignment = new BaseSampleAssignment();
                assignment.setSampleId(sample.getId());
                assignment.setCreateUser(ThreadLocalManager.getUserId());
                assignment.setTeamUserId(teamUserDao.getTeamUserIdByName(importVO.getProjectId(), assignVO.getManagerName(), 5));
                assignment.setCreateTime(new Date());
                assignment.setType(1);
                assignmentList.add(assignment);
                sampleIds.add(sample.getId());
                correctList.add(assignVO);
            }
        }
        assignMap.put("error", errorList);
        assignMap.put("correct", correctList);
        if (correctList.size() > 0) {
            // 样本状态调整为以分派
            sampleDao.updateSampleStatus(importVO.getProjectId(), sampleIds, SampleConstants.STATUS_ASSIGN);
            assignmentDao.insertAssign(importVO.getProjectId(), assignmentList);
        }
        return assignMap;
    }

    /**
     * 查询所有团队成员
     *
     * @param teamMemberVO
     * @return
     */
    public List<TeamMemberDTO> getTeamMember(TeamMemberSearchVO teamMemberVO) {
        return teamUserDao.getTeamMemberList(teamMemberVO);
    }

    /**
     * 添加/修改项目属性
     *
     * @param propertyVO
     * @return
     */
    public Integer saveProjectProperty(ProjectPropertyVO propertyVO) {
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        BeanUtils.copyProperties(propertyVO, projectProperty);
        if (propertyVO.getId() == null) {
            projectProperty.setCreateUser(ThreadLocalManager.getUserId());
            projectProperty.setCreateTime(new Date());
            propertyDao.insert(projectProperty);
        } else {
            projectProperty.setCreateUser(ThreadLocalManager.getUserId());
            projectProperty.setCreateTime(new Date());
            propertyDao.updateByPrimaryKeySelective(projectProperty);
        }
        return projectProperty.getId();
    }

    /**
     * 获取项目属性列表
     *
     * @param projectId
     * @return
     */
    public BaseProjectProperty getProjectSampleProperty(Integer projectId) {
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        projectProperty.setProjectId(projectId);
        return propertyDao.selectOne(projectProperty);
    }

    /**
     * 获取样本使用属性
     *
     * @param projectId
     * @return
     */
    public SamplePropertyDTO getSampleProperty(Integer projectId) {
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        projectProperty.setProjectId(projectId);
        projectProperty = propertyDao.selectOne(projectProperty);
        SamplePropertyDTO propertyDTO = new SamplePropertyDTO();
        if (projectProperty != null) {
            propertyDTO.setListProperty(projectProperty.getListProperty());
            propertyDTO.setUseProperty(projectProperty.getUseProperty());
        }
        if (projectService.getProjectRole(projectId) == AuthorizedConstants.ROLE_INTERVIEWER) {
            propertyDTO.setSampleEdit(true);
            BaseProjectConfig config = projectService.getProjectConfig(projectId);
            propertyDTO.setAllowAddSample(config.getAllowAddSample());
        } else {
            propertyDTO.setSampleEdit(false);
        }
        return propertyDTO;
    }

    /**
     * 保存项目属性模板
     *
     * @param templateVO
     * @return
     */
    public Integer savePropertyTemplate(PropertyTemplateVO templateVO) {
        BaseProjectAllProperty allProperty = new BaseProjectAllProperty();
        BaseProjectPropertyTemplate propertyTemplate = new BaseProjectPropertyTemplate();
        propertyTemplate.setName(templateVO.getName());
        propertyTemplate.setAllProperty(JSON.toJSONString(allProperty));
        propertyTemplate.setUseProperty(templateVO.getUseProperty());
        propertyTemplate.setListProperty(templateVO.getListProperty());
        propertyTemplate.setMarkProperty(templateVO.getMarkProperty());
        propertyTemplate.setCreateUser(ThreadLocalManager.getUserId());
        propertyTemplate.setCreateTime(new Date());
        return templateDao.insert(propertyTemplate);
    }

    /**
     * 查询项目属性模板
     *
     * @return
     */
    public List<BaseProjectPropertyTemplate> getPropertyTemplate() {
        Example example = new Example(BaseProjectPropertyTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        return templateDao.selectByExample(example);
    }

    /**
     * 修改项目属性别名
     *
     * @param samplePropertySaveVO
     * @return
     */
    public Integer saveProjectPropertyAlisa(SamplePropertySaveVO samplePropertySaveVO) {
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        projectProperty.setAllProperty(samplePropertySaveVO.getAllProperty());
        Example example = new Example(BaseProjectProperty.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", samplePropertySaveVO.getProjectId());
        return propertyDao.updateByExampleSelective(projectProperty, example);
    }

}
