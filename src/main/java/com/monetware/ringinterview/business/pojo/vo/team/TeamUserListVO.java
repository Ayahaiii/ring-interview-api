package com.monetware.ringinterview.business.pojo.vo.team;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
public class TeamUserListVO extends BaseVO {

    private List<TeamUserVO> teamUsers;

}
