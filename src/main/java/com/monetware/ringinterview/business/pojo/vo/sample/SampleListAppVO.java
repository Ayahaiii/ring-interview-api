package com.monetware.ringinterview.business.pojo.vo.sample;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

@Data
public class SampleListAppVO extends BaseVO {
    private Integer status;

    private String managerName;

    private String sampleCondition;

    private Integer userId;

    private String keyword;
}