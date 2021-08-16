package com.monetware.ringinterview.business.pojo.vo.user;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/11/24 13:19
 */
@Data
public class UserListVO extends PageParam {

    private String name;

    private String email;

    private String telephone;
}
