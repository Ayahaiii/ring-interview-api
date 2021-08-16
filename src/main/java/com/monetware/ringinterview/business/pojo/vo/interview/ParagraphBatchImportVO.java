package com.monetware.ringinterview.business.pojo.vo.interview;

import lombok.Data;

/**
 * @author budi
 * @version 1.0
 * @create 2020/11/5 10:05 上午
 * @description 访谈文本批量录入传入VO
 */
@Data
public class ParagraphBatchImportVO {

    /**
     * 讲话者名称
     */
    private String name;

    /**
     * 访谈id
     */
    private Integer interviewId;

    /**
     * 讲话者id
     */
    private Integer speakerId;

    /**
     * 文件id
     */
    private Integer fileId;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 访谈文本
     */
    private String paragraph;

    /**
     * 持续时间
     */
    private Long duration;

}
