package com.monetware.ringinterview.business.pojo.vo.sample;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/20 10:31
 */
@Data
public class AssignTeamVO {

    private Integer projectId;

    /**
     * 研究对象
     */
    private List<Integer> sampleIds;

    /**
     * 负责人
     */
    private Integer managerId;

    /**
     * 协助者
     */
    private List<Integer> assistantId;

    /**
     * 选项
     */
    private String opt;

}
