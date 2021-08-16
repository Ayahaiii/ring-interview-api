package com.monetware.ringinterview.business.pojo.dto.interview;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName ProjectInterviewFileDTO
 * @Author: zhangd
 * @Description: 获取访谈项目文件列表数据返回参数
 * @Date: Created in 15:23 2020/2/21
 */
@Data
public class ProjectInterviewFileDTO {

    /**
     * 文件id
     */
    private Integer id;

    /**
     * 录音(附件)名称
     */
    private String name;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 上传人
     */
    private String userName;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date uploadTime;

    /**
     * 录音开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 时长
     */
    private Integer duration;

    /**
     * 文件大小
     */
    private String fileSize;

    private String backGround;

}
