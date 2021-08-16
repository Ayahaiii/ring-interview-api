package com.monetware.ringinterview.business.pojo.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-18
 */
@Data
public class ProjectListDTO {

    private Integer id;

    private String name;

    private Integer typeId;

    private String labelText;

    private String role;

    private String roleName;

    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String createTimeStr;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String updateTimeStr;

    private Integer status;

    private Integer createUser;

}
