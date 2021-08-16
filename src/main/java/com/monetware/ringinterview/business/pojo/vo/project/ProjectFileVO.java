package com.monetware.ringinterview.business.pojo.vo.project;

import lombok.Data;
/**
 * @ClassName ProjectFileVO
 * @Author: zxy
 * @Description: 获取文件参数
 * @Date: Created in 2021/1/16
 */
@Data
public class ProjectFileVO {
    private Integer fileId;

    /**
     * 状态:
     * 1.录音 2.附件
     */
    private Integer type;

}
