package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/3/10 13:44
 */
@Data
public class ParagraphListVO extends PageParam {

    private Integer interviewId;

    private Integer fileId;

    private Integer speakId;

    private Integer speakType;

    private String keyword;
}
