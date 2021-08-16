package com.monetware.ringinterview.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringinterview.business.dao.*;
import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.constant.ProjectConstants;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectHeadDTO;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectInfoDTO;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectListDTO;
import com.monetware.ringinterview.business.pojo.po.*;
import com.monetware.ringinterview.business.pojo.vo.project.*;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.ringinterview.system.util.date.DateUtil;
import com.monetware.ringinterview.system.util.redis.RedisUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Slf4j
@Service
public class ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectSampleDao sampleDao;

    @Autowired
    private SampleAssignmentDao assignmentDao;

    @Autowired
    private ProjectTeamUserRoleDao teamUserRoleDao;

    @Autowired
    private ProjectPermissionDao permissionDao;

    @Autowired
    private ProjectTeamUserDao teamUserDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ProjectPropertyDao propertyDao;

    // ******************** 项目相关 ********************

    /**
     * 新建项目
     * @param projectVO
     */
    public void createProject(ProjectVO projectVO) {
        Date now = new Date();
        BaseProject project = new BaseProject();
        BeanUtils.copyProperties(projectVO, project);
        // 添加项目初始参数
        BaseProjectConfig projectConfig = new BaseProjectConfig();
        projectConfig.setClientPosition(ProjectConstants.INIT_VALUE);
        projectConfig.setClientTrack(ProjectConstants.INIT_VALUE);
        projectConfig.setTrackInterview(ProjectConstants.INIT_VALUE);
        projectConfig.setAllowAddSample(ProjectConstants.INIT_VALUE);
        project.setConfig(JSON.toJSONString(projectConfig));
        // 添加项目邀请码
        project.setInviteCode(getCode());
        project.setCodeAutoAudit(ProjectConstants.INIT_VALUE);
        // 默认7天过期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        project.setCodeExpireTime(calendar.getTime());
        project.setNumOfSample(ProjectConstants.INIT_VALUE);
        project.setNumOfTeam(1);
        project.setFileSize(ProjectConstants.INIT_VALUE.longValue());
        project.setNumOfInterview(ProjectConstants.INIT_VALUE);
        project.setInterviewTimeLen(ProjectConstants.INIT_VALUE.longValue());
        project.setIsDelete(ProjectConstants.INIT_VALUE);
        project.setCreateUser(ThreadLocalManager.getUserId());
        project.setCreateTime(now);
        project.setUpdateUser(ThreadLocalManager.getUserId());
        project.setUpdateTime(now);
        project.setStatus(ProjectConstants.STATUS_WAIT);
        int row = projectDao.insertSelective(project);
        // 添加研究对象表以及分派表
        if (row > 0) {

            // 添加系统默认样本属性
            BaseProjectProperty baseSampleProperty = new BaseProjectProperty();
            baseSampleProperty.setProjectId(project.getId());
            BaseProjectAllProperty allProperty = new BaseProjectAllProperty();
            baseSampleProperty.setAllProperty(JSON.toJSONString(allProperty));
            baseSampleProperty.setUseProperty("[\"name\", \"code\"]");
            baseSampleProperty.setListProperty("[\"name\", \"code\"]");
            baseSampleProperty.setMarkProperty("[\"name\", \"code\"]");
            baseSampleProperty.setCreateUser(ThreadLocalManager.getUserId());
            baseSampleProperty.setCreateTime(now);
            propertyDao.insertSelective(baseSampleProperty);

            sampleDao.createProjectSampleTable(project.getId());
            assignmentDao.createSampleAssignmentTable(project.getId());
            // 将自己添加到团队
            BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
            teamUser.setProjectId(project.getId());
            teamUser.setUserId(ThreadLocalManager.getUserId());
            teamUser.setAuthType(ProjectConstants.AUTH_TYPE_FOREVER);
            teamUser.setApproveUser(ThreadLocalManager.getUserId());
            teamUser.setApproveTime(now);
            teamUser.setApplyTime(now);
            teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
            teamUserDao.insert(teamUser);
            BaseProjectTeamUserRole userRole = new BaseProjectTeamUserRole();
            userRole.setProjectId(project.getId());
            userRole.setUserId(ThreadLocalManager.getUserId());
            userRole.setRoleId(AuthorizedConstants.ROLE_ADMIN);
            teamUserRoleDao.insert(userRole);
        } else {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目创建失败");
        }
    }

    /**
     * 修改项目基本信息
     * @return
     */
    public int updateProject(ProjectVO projectVO) {
        BaseProject project = new BaseProject();
        BeanUtils.copyProperties(projectVO, project);
        project.setUpdateTime(new Date());
        project.setUpdateUser(ThreadLocalManager.getUserId());
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 更改项目状态
     * @param statusVO
     * @return
     */
    public int updateProjectStatus(ProjectStatusVO statusVO) {
        BaseProject project = new BaseProject();
        BeanUtils.copyProperties(statusVO, project);
        Date now = new Date();
        if (ProjectConstants.STATUS_WAIT.equals(statusVO.getOldStatus()) && ProjectConstants.STATUS_RUN.equals(statusVO.getStatus())) {
            project.setBeginDate(now);
        } else if (ProjectConstants.STATUS_PAUSE.equals(statusVO.getStatus())) {
            project.setPauseTime(now);
        } else if (ProjectConstants.STATUS_FINISH.equals(statusVO.getStatus())) {
            project.setEndDate(now);
        }
        project.setUpdateTime(now);
        project.setUpdateUser(ThreadLocalManager.getUserId());
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 获取项目基本信息
     * @param projectId
     * @return
     */
    public ProjectInfoDTO getProjectInfo(Integer projectId) {
        return projectDao.getProjectInfo(projectId, ThreadLocalManager.getUserId());
    }

    /**
     * 获取项目基本信息
     * @param projectId
     * @return
     */
    public ProjectHeadDTO getProjectNameAndRole(Integer projectId) {
        ProjectHeadDTO projectHeadDTO = projectDao.getProjectNameAndRole(projectId, ThreadLocalManager.getUserId());
        return projectHeadDTO;
    }

    /**
     * 获取项目配置
     * @param projectId
     * @return
     */
    public BaseProjectConfig getProjectConfig(Integer projectId) {
        BaseProject project = projectDao.selectByPrimaryKey(projectId);
        return JSONObject.parseObject(project.getConfig(), BaseProjectConfig.class);
    }

    /**
     * 修改项目配置
     * @param projectConfigVO
     * @return
     */
    public int updateProjectConfig(ProjectConfigVO projectConfigVO) {
        BaseProject checkProject = projectDao.selectByPrimaryKey(projectConfigVO.getId());
        BaseProjectConfig config = JSONObject.parseObject(checkProject.getConfig(), BaseProjectConfig.class);
        if (!config.getAllowAddSample().equals(projectConfigVO.getAllowAddSample())) {
            String authKey = "RI_AUTH_" + projectConfigVO.getId() + "_*";
            redisUtil.deleteByPrex(authKey);
        }
        BaseProjectConfig projectConfig = new BaseProjectConfig();
        BeanUtils.copyProperties(projectConfigVO, projectConfig);
        BaseProject project = new BaseProject();
        project.setId(projectConfigVO.getId());
        project.setConfig(JSON.toJSONString(projectConfig));
        project.setUpdateTime(new Date());
        project.setUpdateUser(ThreadLocalManager.getUserId());
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 获取项目列表
     * @param projectListVO
     * @return
     */
    public PageList<Page> getProjectList(ProjectListVO projectListVO) {
        Date now = new Date();
        Page page = PageHelper.startPage(projectListVO.getPageNum(), projectListVO.getPageSize());
        projectListVO.setUserId(ThreadLocalManager.getUserId());
        List<ProjectListDTO> res = projectDao.getProjectList(projectListVO);
        //projectListVO.setUserId(1);
        //res.addAll(projectDao.getProjectList(projectListVO));
        Boolean flag=true;
        if (res != null || res.size() > 0) {
            for (ProjectListDTO projectListDTO : res) {
                if(projectListDTO.getId() == ProjectConstants.SAMPLE_PROJECT_ID){
                    flag=false;
                }
            }
        }
        if(flag){
            insertSampleTeam();
        }

        if (res != null || res.size() > 0) {
            for (ProjectListDTO projectListDTO : res) {
                if (projectListDTO.getCreateUser().equals(ThreadLocalManager.getUserId())) {
                    projectListDTO.setRole(AuthorizedConstants.R_ALL);
                    projectListDTO.setUserName("我");
                }
                Long t = DateUtil.getDateDuration(projectListDTO.getCreateTime(), now);
                if (t < 7 * 24 * 60 * 60 ) {
                    projectListDTO.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(t));
                }else{
                    projectListDTO.setCreateTimeStr(DateUtil.DateToString(projectListDTO.getCreateTime(), "yyyy-MM-dd"));
                }
                Long ut = DateUtil.getDateDuration(projectListDTO.getUpdateTime(), now);
                if (ut < 7 * 24 * 60 * 60 ) {
                    projectListDTO.setUpdateTimeStr(DateUtil.secondToHourChineseStrByLong(ut));
                }else {
                    projectListDTO.setUpdateTimeStr(DateUtil.DateToString(projectListDTO.getUpdateTime(), "yyyy-MM-dd"));
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * 获取项目列表
     * @param projectListAppVO
     * @return
     */
    public List<ProjectListDTO> getProjectListApp(ProjectListAppVO projectListAppVO) {
        Date now = new Date();
        //Page page = PageHelper.startPage(projectListAppVO.getPageNum(), projectListAppVO.getPageSize());
        projectListAppVO.setUserId(ThreadLocalManager.getUserId());
        List<ProjectListDTO> res = projectDao.getProjectListApp(projectListAppVO);
        if (res != null || res.size() > 0) {
            for (ProjectListDTO projectListDTO : res) {
                if (projectListDTO.getCreateUser().equals(ThreadLocalManager.getUserId())) {
                    projectListDTO.setRole(AuthorizedConstants.R_ALL);
                    projectListDTO.setUserName("我");
                }
                Long t = DateUtil.getDateDuration(projectListDTO.getCreateTime(), now);
                if (t < 7 * 24 * 60 * 60 ) {
                    projectListDTO.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(t));
                }else{
                    projectListDTO.setCreateTimeStr(DateUtil.DateToString(projectListDTO.getCreateTime(), "yyyy-MM-dd"));
                }
                Long ut = DateUtil.getDateDuration(projectListDTO.getUpdateTime(), now);
                if (ut < 7 * 24 * 60 * 60 ) {
                    projectListDTO.setUpdateTimeStr(DateUtil.secondToHourChineseStrByLong(ut));
                }else {
                    projectListDTO.setUpdateTimeStr(DateUtil.DateToString(projectListDTO.getUpdateTime(), "yyyy-MM-dd"));
                }
            }
        }
        return res;
    }

    /**
     * 获取六位邀请码
     * @return
     */
    private String getCode(){
        String code = "";
        for(int i = 0;i < 6;i++){
            boolean boo = (int)(Math.random()*2) == 0;
            if(boo){
                code += String.valueOf((int)(Math.random() * 10));
            }else {
                int temp = (int)(Math.random() * 2 ) == 0 ? 65 : 97;
                char ch = (char)(Math.random() * 26 + temp);
                code += String.valueOf(ch);
            }
        }
        BaseProject project = new BaseProject();
        project.setInviteCode(code);
        if (projectDao.selectOne(project) != null) {
            getCode();
        }
        return code;
    }

    /**
     * 申请加入项目
     * @param projectApplyVO
     * @return
     */
    public int insertApplyTeam(ProjectApplyVO projectApplyVO) {
        Date now = new Date();
        // 判断code码是否正确并且是否有效
        BaseProject checkProject = new BaseProject();
        checkProject.setInviteCode(projectApplyVO.getInviteCode());
        BaseProject project = projectDao.selectOne(checkProject);
        if (project == null) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目不存在，邀请码有误");
        }
        if (project.getCreateUser().equals(ThreadLocalManager.getUserId())) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "不能申请自己的项目");
        }
        if (now.after(project.getCodeExpireTime())) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "邀请码已过期");
        }
        // 添加团队用户信息
        BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
        teamUser.setUserId(ThreadLocalManager.getUserId());
        teamUser.setApplyTime(now);
        teamUser.setProjectId(project.getId());
        teamUser.setAuthType(ProjectConstants.AUTH_TYPE_FOREVER);
        // 是否自动审核
        if (project.getCodeAutoAudit() == ProjectConstants.AUTO_AUDIT_YES) {
            teamUser.setApproveTime(now);
            teamUser.setApproveUser(project.getCreateUser());
            teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
        } else {
            teamUser.setStatus(ProjectConstants.USER_STATUS_WAIT);
        }
        // 初始化角色
        BaseProjectTeamUserRole teamUserRole = new BaseProjectTeamUserRole();
        teamUserRole.setProjectId(project.getId());
        teamUserRole.setUserId(ThreadLocalManager.getUserId());
        teamUserRole.setRoleId(AuthorizedConstants.ROLE_INTERVIEWER);
        teamUserRoleDao.insert(teamUserRole);
        int row = teamUserDao.insertSelective(teamUser);
        if (row > 0) {
            // 回写团队总数
            BaseProject p = new BaseProject();
            p.setNumOfTeam(row);
            p.setUpdateUser(ThreadLocalManager.getUserId());
            p.setUpdateTime(now);
            p.setId(project.getId());
            projectDao.updateProjectAdd(p);
        }
        return row;
    }

    /**
     * 自动加入样例项目
     * @param
     * @return
     */
    public int insertSampleTeam() {
        Date now = new Date();
        // 自动加入样例项目
        BaseProject checkProject = new BaseProject();
        checkProject.setId(ProjectConstants.SAMPLE_PROJECT_ID);
        BaseProject project = projectDao.selectOne(checkProject);

        // 添加团队用户信息
        BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
        teamUser.setUserId(ThreadLocalManager.getUserId());
        teamUser.setApplyTime(now);
        teamUser.setProjectId(project.getId());
        teamUser.setAuthType(ProjectConstants.AUTH_TYPE_FOREVER);
        teamUser.setApproveTime(now);
        teamUser.setApproveUser(project.getCreateUser());
        teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);

        // 初始化角色
        BaseProjectTeamUserRole teamUserRole = new BaseProjectTeamUserRole();
        teamUserRole.setProjectId(ProjectConstants.SAMPLE_PROJECT_ID);
        teamUserRole.setUserId(ThreadLocalManager.getUserId());
        teamUserRole.setRoleId(AuthorizedConstants.ROLE_ADMIN);
        teamUserRoleDao.insert(teamUserRole);
        int row = teamUserDao.insertSelective(teamUser);
        if (row > 0) {
            // 回写团队总数
            BaseProject p = new BaseProject();
            p.setNumOfTeam(row);
            p.setUpdateUser(ThreadLocalManager.getUserId());
            p.setUpdateTime(now);
            p.setId(project.getId());
            projectDao.updateProjectAdd(p);
        }
        return row;
    }

    /**
     * 获取项目用户权限
     * @param projectId
     * @return
     */
    public List<String> getProjectPermission(Integer projectId) {
        List<String> res = new ArrayList<>();
        ProjectHeadDTO project = projectDao.getProjectNameAndRole(projectId, ThreadLocalManager.getUserId());
        String roleKey = "RI_ROLE_" + projectId + "_" + ThreadLocalManager.getUserId();
        if (project.getCreateUser().equals(ThreadLocalManager.getUserId())) {
            res.add(AuthorizedConstants.R_ALL);
            redisUtil.set(roleKey, AuthorizedConstants.ROLE_ADMIN);
            return res;
        } else {
            res = permissionDao.getProjectPermissionForUser(projectId, ThreadLocalManager.getUserId());
            BaseProjectConfig config = JSONObject.parseObject(project.getConfig(), BaseProjectConfig.class);
            if (!config.getAllowAddSample().equals(ProjectConstants.INIT_VALUE)) {
                res.add(AuthorizedConstants.RS_SAMPLE_ADD);
                res.add(AuthorizedConstants.RS_SAMPLE_EDIT);
                res.add(AuthorizedConstants.RS_SAMPLE_DELETE);
            }
            redisUtil.set(roleKey, project.getRoleId());
            return res;
        }
    }

    /**
     * 获取项目用户角色
     * @param projectId
     * @return
     */
    public Integer getProjectRole(Integer projectId) {
        String roleKey = "RI_ROLE_" + projectId + "_" + ThreadLocalManager.getUserId();
        if (redisUtil.hasKey(roleKey)) {
            return Integer.parseInt(redisUtil.get(roleKey).toString());
        }
        return AuthorizedConstants.ROLE_INTERVIEWER;
    }

    /**
     * 删除项目
     * @param projectId
     */
    public Integer deleteProject(Integer projectId) {
        BaseProject project = new BaseProject();
        project.setIsDelete(ProjectConstants.DELETE_YES);
        project.setDeleteTime(new Date());
        Example example = new Example(BaseProject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        criteria.andEqualTo("id", projectId);
        return projectDao.updateByExampleSelective(project, example);
    }

    /**
     * 更新邀请码信息
     * @param invitedVO
     * @return
     */
    public Integer updateInvitedCodeInfo(ProjectInvitedVO invitedVO) {
        BaseProject project = new BaseProject();
        BeanUtils.copyProperties(invitedVO, project);
        project.setUpdateTime(new Date());
        project.setUpdateUser(ThreadLocalManager.getUserId());
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 更新邀请码信息
     * @param projectId
     * @return
     */
    public Integer updateInvitedCode(Integer projectId) {
        BaseProject project = new BaseProject();
        project.setId(projectId);
        project.setInviteCode(getCode());
        project.setUpdateTime(new Date());
        project.setUpdateUser(ThreadLocalManager.getUserId());
        return projectDao.updateByPrimaryKeySelective(project);
    }

    /**
     * 查看样例项目
     * @param projectId
     * @return
     */
    public Integer getVisit(Integer projectId){
        if(projectId==ProjectConstants.SAMPLE_PROJECT_ID && ThreadLocalManager.getUserId()!=1){
            return 1;
        }else{
            return 0;
        }
    }

}
