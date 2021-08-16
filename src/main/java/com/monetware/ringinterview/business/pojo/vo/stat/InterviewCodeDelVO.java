package com.monetware.ringinterview.business.pojo.vo.stat;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
public class InterviewCodeDelVO {

    private Integer projectId;

    private List<Integer> ids;

}
