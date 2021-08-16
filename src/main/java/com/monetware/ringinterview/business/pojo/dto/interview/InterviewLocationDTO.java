package com.monetware.ringinterview.business.pojo.dto.interview;

import lombok.Data;

/**
 * @ClassName InterviewLocationDTO
 * @Author: zhangd
 * @Description: 访谈经纬度返回参数
 * @Date: Created in 14:50 2020/2/27
 */
@Data
public class InterviewLocationDTO {

    private Integer id;


    private String name;
    /**
     * 访谈经纬度
     */
    private String locations;
}
