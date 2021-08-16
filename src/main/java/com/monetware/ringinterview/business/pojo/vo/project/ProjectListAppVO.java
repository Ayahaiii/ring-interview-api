package com.monetware.ringinterview.business.pojo.vo.project;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectListAppVO extends BaseVO {

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
