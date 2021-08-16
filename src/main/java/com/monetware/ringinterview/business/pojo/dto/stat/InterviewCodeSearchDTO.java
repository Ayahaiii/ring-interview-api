package com.monetware.ringinterview.business.pojo.dto.stat;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
public class InterviewCodeSearchDTO {

    private Integer id;

    private String name;

    private Integer type;

    private String rule;

    private Integer sampleCount;

    private Integer markCount;

}
