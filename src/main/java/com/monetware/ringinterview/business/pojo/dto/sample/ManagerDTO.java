package com.monetware.ringinterview.business.pojo.dto.sample;

import lombok.Data;

/**
 * @author Linked
 * 封装负责人 协助者 研究对象基本信息对象(用于讲话者创建)
 * @date 2020/2/28 15:17
 */
@Data
public class ManagerDTO {

    private Integer id;

    private String name;

    private String gender;

    private String code;

    private String assistantId;
}
