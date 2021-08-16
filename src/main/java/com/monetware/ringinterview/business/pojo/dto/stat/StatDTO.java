package com.monetware.ringinterview.business.pojo.dto.stat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
public class StatDTO {

    private Integer id;

    private Integer interviewId;

    private String name;

    private String code;

    private Integer codeCount;

    private Integer markCount;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

}
