package com.monetware.ringinterview.business.pojo.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-24
 */
@Data
public class ProjectHeadDTO {

    private String name;

    private String config;

    private Integer roleId;

    private String inviteCode;

    private Integer autoAudit;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    private Integer createUser;

}
