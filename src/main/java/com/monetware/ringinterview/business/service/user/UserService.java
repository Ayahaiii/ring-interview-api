package com.monetware.ringinterview.business.service.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringinterview.business.dao.UserDao;
import com.monetware.ringinterview.business.pojo.constant.UserConstants;
import com.monetware.ringinterview.business.pojo.dto.user.UserPermissionDTO;
import com.monetware.ringinterview.business.pojo.dto.user.UserSearchDTO;
import com.monetware.ringinterview.business.pojo.po.BaseUser;
import com.monetware.ringinterview.business.pojo.vo.user.UserListVO;
import com.monetware.ringinterview.business.pojo.vo.user.UserPermissionVO;
import com.monetware.ringinterview.business.pojo.vo.user.UserSearchVO;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    /**
     * 返回用户前台权限
     * @return
     */
    public UserPermissionDTO getUserPermission() {
        Date now = new Date();
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        if (user == null) {
            user = new BaseUser();
            user.setId(ThreadLocalManager.getUserId());
            user.setName(ThreadLocalManager.getTokenContext().getName());
            user.setRole(UserConstants.ROLE_COMMON);
            user.setStatus(UserConstants.STATUS_ENABLE);
            user.setCreateTime(now);
            userDao.insertSelective(user);
        }
        user.setName(ThreadLocalManager.getTokenContext().getName());
        user.setTelephone(ThreadLocalManager.getTokenContext().getTelPhone());
        user.setEmail(ThreadLocalManager.getTokenContext().getEmail());
        user.setLastLoginTime(now);
        // 首次登录更新用户信息
        userDao.updateByPrimaryKeySelective(user);
        UserPermissionDTO userPermissionDTO = new UserPermissionDTO();
        return userPermissionDTO;
    }



    /**
     * 查询用户列表
     * @param userSearchVO
     * @return
     */
    public UserSearchDTO searchUser(UserSearchVO userSearchVO) {
        return userDao.searchUser(userSearchVO);
    }


    public Integer updateUserPermission(UserPermissionVO userPermission) {
        Date now = new Date();
        BaseUser user = userDao.selectByPrimaryKey(userPermission.getId());
        if (user == null) {
            user = new BaseUser();
            user.setId(userPermission.getId());
            user.setName(userPermission.getName());
            user.setTelephone(userPermission.getTelephone());
            user.setEmail(userPermission.getEmail());
            user.setCreateTime(now);
            user.setStatus(UserConstants.STATUS_ENABLE);
            userDao.insertSelective(user);
        }
        user.setRole(userPermission.getRole());
        user.setExpireTime(userPermission.getExpireTime());
        user.setLastLoginTime(now);
        // 首次登录更新用户信息
        return userDao.updateByPrimaryKeySelective(user);

    }

    public PageList<Page> getUserList(UserListVO listVO) {
        Page page = PageHelper.startPage(listVO.getPageNum(), listVO.getPageSize());
        userDao.getUserList(listVO);
        return new PageList<>(page);
    }
}
