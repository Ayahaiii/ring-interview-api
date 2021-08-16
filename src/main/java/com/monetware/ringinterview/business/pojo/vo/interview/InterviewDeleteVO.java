package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/3 11:45
 */
@Data
public class InterviewDeleteVO extends BaseVO {

    List<Integer> interviewIds;

}
