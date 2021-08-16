package com.monetware.ringinterview.business.pojo.vo.stat;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-11
 */
@Data
public class AnalystParagraphVO extends PageParam {

    private Integer sampleId;

    private Integer speakerId;

    private Integer speakType;

    private String keyword;

}
