package com.monetware.ringinterview.business.pojo.dto.sample;

import com.monetware.ringinterview.business.pojo.dto.interview.InterviewListDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectSample;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/18 21:45
 */
@Data
public class ProjectSampleDTO extends BaseProjectSample {

    List<InterviewListDTO> interviewList;

}
