package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.system.base.PageParam;
import lombok.Data;

/**
 * @ClassName ProjectInterviewFileVO
 * @Author: zhangd
 * @Description: 获取访谈文件列表请求参数
 * @Date: Created in 15:42 2020/2/21
 */
@Data
public class ProjectInterviewFileVO extends PageParam {


    private Integer interviewId;


    /**
     * 状态:
     * 1.录音 2.附件
     */
    private Integer type;
}
