package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

@Data
public class ParagraphListAppVO extends BaseVO {

    private Integer interviewId;

    private Integer fileId;

    private Integer speakId;

    private Integer speakType;

    private String keyword;
}
