package com.monetware.ringinterview.business.pojo.vo.project;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-18
 */
@Data
public class ProjectListVO extends PageParam {

    private String searchType;

    private Integer userType;

    private Integer userId;

    private String keyword;

    private Integer typeId;

    private String labelText;

    private Integer status;

    private String name;

    private String userName;

    private Date startRunTime;

    private Date endRunTime;

    private Date startCreateTime;

    private Date endCreateTime;

    private Date startStopTime;

    private Date endStopTime;

}
