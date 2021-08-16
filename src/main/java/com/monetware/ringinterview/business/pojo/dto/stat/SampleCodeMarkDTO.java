package com.monetware.ringinterview.business.pojo.dto.stat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewMark;
import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/3/22 23:11
 */
@Data
public class SampleCodeMarkDTO extends BaseProjectInterviewMark {

    private String name;

    private String paragraph;

    private String createName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
