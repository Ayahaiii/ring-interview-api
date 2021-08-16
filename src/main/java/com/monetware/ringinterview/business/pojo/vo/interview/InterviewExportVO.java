package com.monetware.ringinterview.business.pojo.vo.interview;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/3 1:08
 */
@Data
public class InterviewExportVO {

    private Integer projectId;

    private String opt;

    private InterviewVO interviewVO;

    private List<Integer> interviewId;

    private List<String> properties;

    private String fileType;

    private String description;
}
