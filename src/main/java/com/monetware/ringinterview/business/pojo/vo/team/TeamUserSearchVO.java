package com.monetware.ringinterview.business.pojo.vo.team;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamUserSearchVO extends PageParam {

    private String role;

    private Integer status;

    private Integer group;

    private String keyword;

    private Integer userId;

    private Integer groupId;

}
