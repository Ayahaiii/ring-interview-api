package com.monetware.ringinterview.business.pojo.dto.team;

import com.monetware.ringinterview.business.pojo.po.BaseProjectTeamUserRole;
import com.monetware.ringinterview.business.pojo.po.BaseProjectTeamUserToGroup;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
public class TeamUserInfoDTO {

    private Integer id;

    private Integer userId;

    private String name;

    private Integer authType;

    private Date authDate;

    private List<BaseProjectTeamUserRole> roles;

    private List<BaseProjectTeamUserToGroup> groups;
}
