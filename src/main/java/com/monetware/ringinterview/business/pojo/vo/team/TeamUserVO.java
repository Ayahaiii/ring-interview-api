package com.monetware.ringinterview.business.pojo.vo.team;

import com.monetware.ringinterview.business.pojo.po.BaseProjectTeamUserRole;
import com.monetware.ringinterview.business.pojo.po.BaseProjectTeamUserToGroup;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamUserVO {

    private Integer id;

    private Integer projectId;

    private Integer userId;

    private List<BaseProjectTeamUserRole> roles;

    private List<Integer> delRoleIds;

    private List<BaseProjectTeamUserToGroup> groups;

    private List<Integer> delGroupIds;

    private Integer authType;

    private Date authDate;

}
