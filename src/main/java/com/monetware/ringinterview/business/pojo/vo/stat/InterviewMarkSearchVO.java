package com.monetware.ringinterview.business.pojo.vo.stat;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-11
 */
@Data
public class InterviewMarkSearchVO extends PageParam {

    private Integer userId;

    private String keyword;

    private String sampleName;

    private String note;

}
