package com.monetware.ringinterview.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringinterview.business.dao.*;
import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.constant.ProjectConstants;
import com.monetware.ringinterview.business.pojo.dto.team.*;
import com.monetware.ringinterview.business.pojo.po.*;
import com.monetware.ringinterview.business.pojo.vo.team.*;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.PageParam;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.ringinterview.system.util.file.*;
import com.monetware.ringinterview.system.util.redis.RedisUtil;
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
import java.io.IOException;
import java.util.*;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Slf4j
@Service
public class TeamService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ProjectTeamUserDao teamUserDao;

    @Autowired
    private ProjectTeamGroupDao teamGroupDao;

    @Autowired
    private ProjectTeamUserToGroupDao teamUserToGroupDao;

    @Autowired
    private ProjectTeamUserRoleDao teamUserRoleDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ProjectExportDao exportDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectDao projectDao;

    @Value("${upload.dir}")
    private String filePath;

    /**
     * 获取所有角色
     * @return
     */
    public List<BaseRole> getRoleList() {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(0); // 系统自带
        userIds.add(ThreadLocalManager.getUserId());
        Example example = new Example(BaseRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("createUser", userIds);
        criteria.andNotEqualTo("id", AuthorizedConstants.ROLE_OWNER);
        return roleDao.selectByExample(example);
    }

    /**
     * 添加邀请成员
     * @param teamUsers
     * @return
     */
    public List<Integer> saveTeamUser(TeamUserListVO teamUsers) {
        List<Integer> res = new ArrayList<>();
        int row = 0;
        for (TeamUserVO teamUserVO : teamUsers.getTeamUsers()) {
            if (teamUserVO.getId() == null) {
                // 判断用户是否已加入
                Example example = new Example(BaseProjectTeamUser.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("userId", teamUserVO.getUserId());
                criteria.andEqualTo("projectId", teamUserVO.getProjectId());
                BaseProjectTeamUser checkTeamUser = teamUserDao.selectOneByExample(example);
                if (checkTeamUser != null) {
                    res.add(teamUserVO.getUserId());
                    continue;
                    // throw new ServiceException(ErrorCode.CUSTOM_MSG, "该用户已加入项目");
                }
                // 加入项目
                Date now = new Date();
                BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
                teamUser.setProjectId(teamUserVO.getProjectId());
                teamUser.setUserId(teamUserVO.getUserId());
                teamUser.setAuthType(teamUserVO.getAuthType());
                teamUser.setAuthEndTime(teamUserVO.getAuthDate());
                teamUser.setApproveUser(ThreadLocalManager.getUserId());
                teamUser.setApproveTime(now);
                teamUser.setApplyTime(now);
                teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
                // 添加角色
                if (teamUserVO.getRoles().size() > 0) {
                    teamUserRoleDao.insertList(teamUserVO.getRoles());
                }
                // 添加组
                if (teamUserVO.getGroups().size() > 0) {
                    teamUserToGroupDao.insertList(teamUserVO.getGroups());
                }
                row += teamUserDao.insert(teamUser);
            } else {
                BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
                teamUser.setId(teamUserVO.getId());
                teamUser.setAuthType(teamUserVO.getAuthType());
                teamUser.setAuthEndTime(teamUserVO.getAuthDate());
                // 添加角色
                if (teamUserVO.getRoles().size() > 0) {
                    teamUserRoleDao.insertList(teamUserVO.getRoles());
                }
                // 删除角色
                if (teamUserVO.getDelRoleIds().size() > 0) {
                    Example example = new Example(BaseProjectTeamUserRole.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("projectId", teamUserVO.getProjectId());
                    criteria.andEqualTo("userId", teamUserVO.getUserId());
                    criteria.andIn("roleId", teamUserVO.getDelRoleIds());
                    teamUserRoleDao.deleteByExample(example);
                }
                // 添加组
                if (teamUserVO.getGroups().size() > 0) {
                    teamUserToGroupDao.insertList(teamUserVO.getGroups());
                }
                // 删除组
                if (teamUserVO.getDelGroupIds().size() > 0) {
                    Example example = new Example(BaseProjectTeamUserToGroup.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("projectId", teamUserVO.getProjectId());
                    criteria.andEqualTo("userId", teamUserVO.getUserId());
                    criteria.andIn("groupId", teamUserVO.getDelGroupIds());
                    teamUserToGroupDao.deleteByExample(example);
                }
                // 删除redis中存在的记录
                String authKey = "RI_AUTH_" + teamUserVO.getProjectId() + "_" + ThreadLocalManager.getUserId();
                if (redisUtil.hasKey(authKey)) redisUtil.remove(authKey);
                String roleKey = "RI_ROLE_" + teamUserVO.getProjectId() + "_" + ThreadLocalManager.getUserId();
                if (redisUtil.hasKey(roleKey)) redisUtil.remove(roleKey);
                teamUserDao.updateByPrimaryKeySelective(teamUser);
            }
        }
        if (row > 0) {
            // 回写团队总数
            BaseProject project = new BaseProject();
            project.setNumOfTeam(row);
            project.setUpdateUser(ThreadLocalManager.getUserId());
            project.setUpdateTime(new Date());
            project.setId(teamUsers.getProjectId());
            projectDao.updateProjectAdd(project);
        }
        return res;
    }

    /**
     * 获取团队用户详情
     * @param teamUserInfoVO
     * @return
     */
    public TeamUserInfoDTO getTeamUserInfo(TeamUserInfoVO teamUserInfoVO) {
        TeamUserInfoDTO res = teamUserDao.getTeamUserInfo(teamUserInfoVO);
        res.setGroups(teamUserDao.getTeamUserGroup(teamUserInfoVO));
        res.setRoles(teamUserDao.getTeamUserRole(teamUserInfoVO));
        return res;
    }

    /**
     * 批量导入用户
     * @param importVO
     * @return
     */
    public List<TeamUserImportInfoVO> insertTeamUserByImport(TeamUserImportVO importVO) {
        Date now = new Date();
        List<TeamUserImportInfoVO> errorData = new ArrayList<>();
        int row = 0;
        for (TeamUserImportInfoVO infoVO : importVO.getUserInfos()) {
            // 判断信息是否正确
            if (StringUtils.isEmpty(infoVO.getEmail())
                    && StringUtils.isEmpty(infoVO.getTelephone())
                    && StringUtils.isEmpty(infoVO.getUserName())) {
                errorData.add(infoVO);
                continue;
            }
            // 判断用户是否存在
            Integer userId = userDao.checkUser(infoVO);
            if (userId == null) {
                errorData.add(infoVO);
                continue;
            }
            // 判断用户是否已经在团队中
            Integer count = teamUserDao.checkTeamUser(importVO.getProjectId(), userId);
            if (count > 0) {
                errorData.add(infoVO);
                continue;
            }
            // 添加团队用户信息
            BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
            teamUser.setUserId(userId);
            teamUser.setApplyTime(now);
            teamUser.setProjectId(importVO.getProjectId());
            if (infoVO.getAuthDate() == null) {
                teamUser.setAuthType(ProjectConstants.AUTH_TYPE_FOREVER);
            } else {
                teamUser.setAuthType(ProjectConstants.AUTH_TYPE_TIME);
                teamUser.setAuthEndTime(infoVO.getAuthDate());
            }
            teamUser.setApproveTime(now);
            teamUser.setApproveUser(ThreadLocalManager.getUserId());
            teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
            // 初始化角色
            BaseProjectTeamUserRole teamUserRole = new BaseProjectTeamUserRole();
            teamUserRole.setProjectId(importVO.getProjectId());
            teamUserRole.setUserId(userId);
            teamUserRole.setRoleId(AuthorizedConstants.ROLE_INTERVIEWER);
            teamUserRoleDao.insert(teamUserRole);
            row += teamUserDao.insertSelective(teamUser);
        }
        if (row > 0) {
            // 回写团队总数
            BaseProject p = new BaseProject();
            p.setNumOfTeam(row);
            p.setUpdateUser(ThreadLocalManager.getUserId());
            p.setUpdateTime(now);
            p.setId(importVO.getProjectId());
            projectDao.updateProjectAdd(p);
        }
        return errorData;
    }

    /**
     * 删除用户
     * @param teamUserDelVO
     */
    public void deleteTeamUser(TeamUserDelVO teamUserDelVO) {
        // 删除用户组
        Example gExample = new Example(BaseProjectTeamUserToGroup.class);
        Example.Criteria gCriteria = gExample.createCriteria();
        gCriteria.andEqualTo("projectId", teamUserDelVO.getProjectId());
        gCriteria.andIn("userId", teamUserDelVO.getUserIds());
        teamUserToGroupDao.deleteByExample(gExample);
        // 删除用户角色
        Example rExample = new Example(BaseProjectTeamUserRole.class);
        Example.Criteria rCriteria = rExample.createCriteria();
        rCriteria.andEqualTo("projectId", teamUserDelVO.getProjectId());
        rCriteria.andIn("userId", teamUserDelVO.getUserIds());
        teamUserRoleDao.deleteByExample(rExample);
        // 删除用户
        Example uExample = new Example(BaseProjectTeamUser.class);
        Example.Criteria uCriteria = uExample.createCriteria();
        uCriteria.andEqualTo("projectId", teamUserDelVO.getProjectId());
        uCriteria.andIn("userId", teamUserDelVO.getUserIds());
        int row = teamUserDao.deleteByExample(uExample);
        if (row > 0) {
            // 回写团队总数
            BaseProject project = new BaseProject();
            project.setNumOfTeam(row);
            project.setUpdateUser(ThreadLocalManager.getUserId());
            project.setUpdateTime(new Date());
            project.setId(teamUserDelVO.getProjectId());
            projectDao.updateProjectDel(project);
        }
    }

    /**
     * 获取团队用户列表
     * @param teamUserSearchVO
     * @return
     */
    public PageList<Page> getTeamUserList(TeamUserSearchVO teamUserSearchVO) {
        Page page = PageHelper.startPage(teamUserSearchVO.getPageNum(), teamUserSearchVO.getPageSize());
        teamUserSearchVO.setUserId(ThreadLocalManager.getUserId());
        teamUserDao.getTeamUserList(teamUserSearchVO);
        return new PageList<>(page);
    }

    /**
     * 同意申请
     * @param teamUserStatusVO
     * @return
     */
    public int updateTeamUserAgree(TeamUserStatusVO teamUserStatusVO) {
        BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
        teamUser.setId(teamUserStatusVO.getId());
        teamUser.setApproveTime(new Date());
        teamUser.setApproveUser(ThreadLocalManager.getUserId());
        teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
        return teamUserDao.updateByPrimaryKeySelective(teamUser);
    }

    /**
     * 禁用
     * @param teamUserStatusVO
     * @return
     */
    public int updateTeamUserDisable(TeamUserStatusVO teamUserStatusVO) {
        BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
        teamUser.setId(teamUserStatusVO.getId());
        teamUser.setStatus(ProjectConstants.USER_STATUS_DISABLE);
        return teamUserDao.updateByPrimaryKeySelective(teamUser);
    }

    /**
     * 保存分组
     * @param teamGroupVO
     * @return
     */
    public int saveTeamGroup(TeamGroupVO teamGroupVO) {
        BaseProjectTeamGroup teamGroup = new BaseProjectTeamGroup();
        BeanUtils.copyProperties(teamGroupVO, teamGroup);
        Example example = new Example(BaseProjectTeamGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", teamGroupVO.getProjectId());
        criteria.andEqualTo("parentId", teamGroupVO.getParentId());
        criteria.andEqualTo("name", teamGroupVO.getName());
        int count = teamGroupDao.selectCountByExample(example);
        if (count > 0) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "分组名已存在");
        }
        if (teamGroup.getId() == null) {
            teamGroup.setCreateUser(ThreadLocalManager.getUserId());
            teamGroup.setCreateTime(new Date());
            return teamGroupDao.insert(teamGroup);
        } else {
            return teamGroupDao.updateByPrimaryKeySelective(teamGroup);
        }
    }

    /**
     * 获取分组树形结构
     * @param projectId
     * @return
     */
    public TeamGroupResDTO getTeamGroupList(Integer projectId) {
        TeamGroupResDTO teamGroupResDTO = new TeamGroupResDTO();
        BaseProjectTeamGroup teamGroup = new BaseProjectTeamGroup();
        teamGroup.setProjectId(projectId);
        List<BaseProjectTeamGroup> teamGroups = teamGroupDao.select(teamGroup);
        List<TeamGroupDTO> result = new ArrayList<>();
        BaseProjectTeamGroup parentGroup;
        for (int i = 0; i < teamGroups.size(); i++) {
            parentGroup = teamGroups.get(i);
            if (parentGroup.getParentId() == null) {
                TeamGroupDTO teamGroupDTO = new TeamGroupDTO();
                teamGroupDTO.setId(parentGroup.getId());
                teamGroupDTO.setLabel(parentGroup.getName());
                teamGroupDTO.setChildren(getTeamGroupTree(teamGroups, parentGroup.getId()));
                result.add(teamGroupDTO);
            }
        }
        teamGroupResDTO.setTreeGroup(result);
        List<TeamGroupUserCountDTO> countDTOS = teamUserDao.getTeamUserCount(projectId);
        Map<Integer, Integer> resultCount = new HashMap<>();
        for (int i = 0; i < countDTOS.size(); i++) {
            resultCount.put(countDTOS.get(i).getId(), countDTOS.get(i).getCount());
        }
        teamGroupResDTO.setTreeValue(resultCount);
        return teamGroupResDTO;
    }

    /**
     * 递归处理分组
     * @param teamGroups
     * @param parentId
     * @return
     */
    private List<TeamGroupDTO> getTeamGroupTree(List<BaseProjectTeamGroup> teamGroups, Integer parentId) {
        List<TeamGroupDTO> result = new ArrayList<>();
        BaseProjectTeamGroup childGroup;
        for (int i = 0; i < teamGroups.size(); i++) {
            childGroup = teamGroups.get(i);
            if (childGroup.getParentId() == parentId) {
                TeamGroupDTO teamGroupDTO = new TeamGroupDTO();
                teamGroupDTO.setId(childGroup.getId());
                teamGroupDTO.setLabel(childGroup.getName());
                teamGroupDTO.setChildren(getTeamGroupTree(teamGroups, childGroup.getId()));
                result.add(teamGroupDTO);
            }
        }
        return result;
    }

    /**
     * 删除分组
     * @param teamGroupDelVO
     * @return
     */
    public void deleteTeamGroup(TeamGroupDelVO teamGroupDelVO) {
        // 删除组对应用户数据
        Example example1 = new Example(BaseProjectTeamUserToGroup.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andIn("groupId", teamGroupDelVO.getIds());
        teamUserToGroupDao.deleteByExample(example1);
        // 删除组
        Example example = new Example(BaseProjectTeamGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", teamGroupDelVO.getIds());
        teamGroupDao.deleteByExample(example);
    }

    /**
     * 批量添加用户到分组
     * @param userToGroupVO
     * @return
     */
    public int insertTeamUserToGroup(TeamUserToGroupVO userToGroupVO) {
        return teamUserToGroupDao.insertList(userToGroupVO.getUserToGroups());
    }

    /**
     * 查询项目组用户列表
     * @param teamGroupUserSearchVO
     * @return
     */
    public PageList<Page> getTeamGroupUserList(TeamGroupUserSearchVO teamGroupUserSearchVO) {
        Page page = PageHelper.startPage(teamGroupUserSearchVO.getPageNum(), teamGroupUserSearchVO.getPageSize());
        teamUserDao.getTeamGroupUserList(teamGroupUserSearchVO);
        return new PageList<>(page);
    }

    /**
     * 删除组用户数据
     * @param userToGroupVO
     * @return
     */
    public int deleteTeamUserToGroup(TeamUserToGroupVO userToGroupVO) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < userToGroupVO.getUserToGroups().size(); i++) {
            ids.add(userToGroupVO.getUserToGroups().get(i).getId());
        }
        Example example = new Example(BaseProjectTeamUserToGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        return teamUserToGroupDao.deleteByExample(example);
    }

    /**
     * 导出
     * @param exportVO
     * @return
     * @throws Exception
     */
    public int exportTeamUser(TeamUserExportVO exportVO) throws Exception {
        List<TeamUserDTO> data = new ArrayList<>();
        if ("ALL".equals(exportVO.getOpt())) {
            TeamUserSearchVO searchVO = new TeamUserSearchVO();
            searchVO.setUserId(ThreadLocalManager.getUserId());
            searchVO.setCheckRole(projectService.getProjectRole(exportVO.getProjectId()));
            searchVO.setProjectId(exportVO.getProjectId());
            data = teamUserDao.getTeamUserList(searchVO);
        } else if ("SEARCH".equals(exportVO.getOpt())) {
            exportVO.getSearchVO().setUserId(ThreadLocalManager.getUserId());
            exportVO.getSearchVO().setCheckRole(projectService.getProjectRole(exportVO.getProjectId()));
            data = teamUserDao.getTeamUserList(exportVO.getSearchVO());
        } else {
            data = teamUserDao.getTeamUserListByIds(exportVO.getProjectId(), exportVO.getTeamUserIds());
        }
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        JSONArray array = JSON.parseArray(JSON.toJSONString(data, filter, SerializerFeature.WriteDateUseDateFormat));
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.get("validDate").equals("")) {
                obj.put("validDate", "永久");
            }
            obj.put("role", obj.get("role").toString().replace(",", ";"));
        }
        String pre = "/export/team/" + exportVO.getProjectId() + "/";
        String path = filePath + pre;
        Map<String, Object> res = new HashMap<>();
        if ("EXCEL".equals(exportVO.getFileType())) {
            res = ExcelUtil.createExcelFile("TEAM", exportVO.getProperties(), array, path);
        } else if ("CSV".equals(exportVO.getFileType())) {
            res = CsvUtil.createCsvFile("TEAM", exportVO.getProperties(), array, path);
        } else if ("TXT".equals(exportVO.getFileType())) {
            res = TxtUtil.createTextFile("TEAM", exportVO.getProperties(), array, path);
        }
        BaseProjectExportHistory exportHistory = new BaseProjectExportHistory();
        exportHistory.setName(res.get("fileName").toString());
        exportHistory.setFileSize(Long.parseLong(res.get("fileSize").toString()));
        exportHistory.setFilePath(pre + res.get("fileName").toString());
        exportHistory.setType("TEAM");
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

    // JSON转化过程中将null值过滤为空字符串
    private ValueFilter filter = new ValueFilter() {
        @Override
        public Object process(Object obj, String s, Object v) {
            if(v==null)
                return "";
            return v;
        }
    };

    /**
     * 获取下载历史记录表
     * @param pageParam
     * @return
     */
    public PageList<Page> getTeamDownList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        Example example = new Example(BaseProjectExportHistory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", pageParam.getProjectId());
        criteria.andEqualTo("type", "TEAM");
        example.orderBy("createTime").desc();
        exportDao.selectByExample(example);
        return new PageList<>(page);
    }

    /**
     * 下载团队用户文件
     * @param id
     * @param response
     * @throws Exception
     */
    public void downloadTeamUser(Integer id, HttpServletResponse response) throws Exception {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/team/" + exportHistory.getProjectId() + "/" + exportHistory.getName();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * 删除文件
     * @param delVO
     * @return
     */
    public int deleteTeamUserFile(TeamUserExportDelVO delVO) {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(delVO.getId());
        String path = filePath + "/export/team/" + exportHistory.getProjectId() + "/";
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

}
