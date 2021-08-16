package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.user.UserSearchDTO;
import com.monetware.ringinterview.business.pojo.po.BaseUser;
import com.monetware.ringinterview.business.pojo.vo.team.TeamUserImportInfoVO;
import com.monetware.ringinterview.business.pojo.vo.user.UserListVO;
import com.monetware.ringinterview.business.pojo.vo.user.UserSearchVO;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:30
 */
@Mapper
@Repository
public interface UserDao extends MyMapper<BaseUser> {

    UserSearchDTO searchUser(UserSearchVO userSearchVO);

    /**
     * 判断讲话者是否存在
     * @param importInfoVO
     * @return
     */
    Integer checkUser(TeamUserImportInfoVO importInfoVO);

    List<UserSearchDTO> getUserList(UserListVO listVO);
}
