package com.monetware.ringinterview.business.pojo.vo.team;

import com.monetware.ringinterview.business.pojo.po.BaseProjectTeamUserToGroup;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamUserToGroupVO {

    private Integer projectId;

    private List<BaseProjectTeamUserToGroup> userToGroups;

}
