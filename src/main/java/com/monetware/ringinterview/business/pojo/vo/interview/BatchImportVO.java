package com.monetware.ringinterview.business.pojo.vo.interview;

import lombok.Data;

/**
 * @author Linked
 * @date 2020/3/12 9:08
 */
@Data
public class BatchImportVO {

    private Integer id;
    /**
     * 讲话者名字
     */
    private String name;

    private String paragraph;

    private String beginTime;

    private String endTime;

    private Long duration;

    private String realName;

}
