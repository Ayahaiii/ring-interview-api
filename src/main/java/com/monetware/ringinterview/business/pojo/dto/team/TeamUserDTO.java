package com.monetware.ringinterview.business.pojo.dto.team;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamUserDTO {

    private Integer id;

    private Integer userId;

    private String userName;

    private String email;

    private String telephone;

    private Integer sampleNum;

    private String role;

    private Integer groups;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date joinDate;

    private Integer authType;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date validDate;

    private Integer status;

    private String groupName;
}
