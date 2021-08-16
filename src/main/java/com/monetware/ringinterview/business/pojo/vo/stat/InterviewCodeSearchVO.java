package com.monetware.ringinterview.business.pojo.vo.stat;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-02-25
 */
@Data
public class InterviewCodeSearchVO extends PageParam {

    private Integer userId;

    private Integer type;

    private String name;

}
