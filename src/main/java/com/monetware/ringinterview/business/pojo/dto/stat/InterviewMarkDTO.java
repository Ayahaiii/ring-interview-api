package com.monetware.ringinterview.business.pojo.dto.stat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-03-11
 */
@Data
public class InterviewMarkDTO {

    private Integer id;

    private Integer codeId;

    private String codeName;

    private String note;

    private String text;

    private Integer sampleId;

    private String sampleName;

    private String sampleCode;

    private Integer createUser;

    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
