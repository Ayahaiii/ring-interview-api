package com.monetware.ringinterview.business.pojo.dto.sample;

import com.monetware.ringinterview.business.pojo.po.BaseProjectSample;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/2/18 13:59
 */
@Data
public class SampleListDTO extends BaseProjectSample {

    private String managerName;

    private Integer managerId;

}
