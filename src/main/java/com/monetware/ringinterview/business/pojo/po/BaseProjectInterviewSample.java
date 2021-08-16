package com.monetware.ringinterview.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Linked
 * @date 2020/3/2 18:18
 */
@Data
@Table(name = "rs_project_interview_sample")
public class BaseProjectInterviewSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private Integer interviewId;

    private Integer sampleId;

}
