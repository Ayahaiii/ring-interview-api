package com.monetware.ringinterview.business.controller.test;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringinterview.business.dao.ProjectDao;
import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectListDTO;
import com.monetware.ringinterview.business.pojo.po.BaseUser;
import com.monetware.ringinterview.business.pojo.vo.project.ProjectListVO;
import com.monetware.ringinterview.business.service.user.UserService;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.ResultData;
import com.monetware.ringinterview.system.util.date.DateUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import com.monetware.threadlocal.TokenContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author Linked
 * @date 2020/6/5 17:01
 */
@Api("项目管理")
@RestController
@RequestMapping("/test/w1")
public class TestController {

    @Autowired
    private ProjectDao projectDao;


    /**
     * 获取项目列表
     *
     * @param
     * @return
     */
    @PostMapping("list/page")
    @ApiOperation(value = "分页获取项目列表")
    public ResultData<PageList<Page>> getProjectList() {
        TokenContext tokenContext = new TokenContext();
        tokenContext.setUserId(212);
        ThreadLocalManager.setTokenContext(tokenContext);
        ProjectListVO projectListVO = new ProjectListVO();
        Date now = new Date();
        Page page = PageHelper.startPage(1, 10);
        projectListVO.setUserId(212);
        List<ProjectListDTO> res = projectDao.getProjectList(projectListVO);
        if (res != null || res.size() > 0) {
            for (ProjectListDTO projectListDTO : res) {
                if (projectListDTO.getCreateUser().equals(212)) {
                    projectListDTO.setRole(AuthorizedConstants.R_ALL);
                    projectListDTO.setUserName("我");
                }
                Long t = DateUtil.getDateDuration(projectListDTO.getCreateTime(), now);
                if (t < 7 * 24 * 60 * 60 * 1000) {
                    projectListDTO.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(t));
                }
                Long ut = DateUtil.getDateDuration(projectListDTO.getUpdateTime(), now);
                if (ut < 7 * 24 * 60 * 60 * 1000) {
                    projectListDTO.setUpdateTimeStr(DateUtil.secondToHourChineseStrByLong(ut));
                }
            }
        }
        return new ResultData<>(new PageList<>(page));
    }

}
