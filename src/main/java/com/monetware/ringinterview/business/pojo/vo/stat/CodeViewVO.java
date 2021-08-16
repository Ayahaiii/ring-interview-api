package com.monetware.ringinterview.business.pojo.vo.stat;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-10
 */
@Data
public class CodeViewVO {

    private Integer projectId;

    private List<Integer> sampleIds;

    private List<Integer> codeIds;

}
