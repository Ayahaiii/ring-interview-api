package com.monetware.ringinterview.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
@Table(name = "rs_project_interview_mark")
public class BaseProjectInterviewMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private Integer interviewId;

    private Integer paragraphId;

    private Integer sampleId;

    private Integer codeId;

    private Integer begin;

    private Integer len;

    private String note;

    private Date createTime;

    private Integer createUser;


}
