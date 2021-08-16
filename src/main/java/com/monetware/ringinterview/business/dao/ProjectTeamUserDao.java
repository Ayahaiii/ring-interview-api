package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.sample.TeamMemberDTO;
import com.monetware.ringinterview.business.pojo.dto.stat.AnalystUserDTO;
import com.monetware.ringinterview.business.pojo.dto.team.TeamGroupUserCountDTO;
import com.monetware.ringinterview.business.pojo.dto.team.TeamGroupUserDTO;
import com.monetware.ringinterview.business.pojo.dto.team.TeamUserDTO;
import com.monetware.ringinterview.business.pojo.dto.team.TeamUserInfoDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectTeamUser;
import com.monetware.ringinterview.business.pojo.po.BaseProjectTeamUserRole;
import com.monetware.ringinterview.business.pojo.po.BaseProjectTeamUserToGroup;
import com.monetware.ringinterview.business.pojo.vo.sample.TeamMemberSearchVO;
import com.monetware.ringinterview.business.pojo.vo.team.TeamGroupUserSearchVO;
import com.monetware.ringinterview.business.pojo.vo.team.TeamUserInfoVO;
import com.monetware.ringinterview.business.pojo.vo.team.TeamUserSearchVO;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:20
 */
@Mapper
@Repository
public interface ProjectTeamUserDao extends MyMapper<BaseProjectTeamUser> {

    List<TeamUserDTO> getTeamUserList(TeamUserSearchVO userSearchVO);

    List<TeamUserDTO> getTeamUserListByIds(@Param("projectId") Integer projectId, @Param("list") List<Integer> list);

    List<TeamGroupUserDTO> getTeamGroupUserList(TeamGroupUserSearchVO groupUserSearchVO);

    /**
     * 查询所有团队成员
     *
     * @param teamMemberVO
     * @return
     */
    List<TeamMemberDTO> getTeamMemberList(TeamMemberSearchVO teamMemberVO);

    TeamUserInfoDTO getTeamUserInfo(TeamUserInfoVO teamUserInfoVO);

    List<BaseProjectTeamUserRole> getTeamUserRole(TeamUserInfoVO teamUserInfoVO);

    List<BaseProjectTeamUserToGroup> getTeamUserGroup(TeamUserInfoVO teamUserInfoVO);

    List<TeamGroupUserCountDTO> getTeamUserCount(@Param("projectId") Integer projectId);

    Integer checkTeamUser(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    List<AnalystUserDTO> getAnalystUserList(@Param("projectId") Integer projectId);

    Integer getTeamUserIdByName(@Param("projectId") Integer projectId, @Param("name") String name, @Param("roleId") Integer roleId);

}
