package com.monetware.ringinterview.business.pojo.vo.sample;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/2/18 13:40
 */
@Data
public class SampleListVO extends PageParam {

    private Integer status;

    private String managerName;

    private String sampleCondition;

    private Integer userId;

    private String keyword;
}
