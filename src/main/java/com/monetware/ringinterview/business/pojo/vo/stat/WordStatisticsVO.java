package com.monetware.ringinterview.business.pojo.vo.stat;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-09
 */
@Data
public class WordStatisticsVO {

    private Integer projectId;

    private List<Integer> sampleIds;

    private List<String> natures;

    private List<String> customWords;

}
