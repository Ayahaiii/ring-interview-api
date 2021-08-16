package com.monetware.ringinterview.business.pojo.vo.stat;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
public class InterviewCodeVO extends BaseVO {

    private Integer id;

    private String name;

    private String rule;

    private Integer type;

}
