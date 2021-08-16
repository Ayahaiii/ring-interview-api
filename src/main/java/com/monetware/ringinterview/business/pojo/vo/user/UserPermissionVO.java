package com.monetware.ringinterview.business.pojo.vo.user;

import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/11/24 11:38
 */
@Data
public class UserPermissionVO {

    private Integer id;

    private Integer role;

    private Date expireTime;

    private String name;

    private String telephone;

    private String email;

}
