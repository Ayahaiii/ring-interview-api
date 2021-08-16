package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.dto.sample.ManagerDTO;
import lombok.Data;
import org.apache.catalina.Manager;

import java.util.Date;
import java.util.List;

/**
 * @author Linked
 * @date 2020/2/24 14:21
 */
@Data
public class SaveInterviewVO {


    private Integer id;


    private Integer projectId;

    /**
     * 访谈标题
     */
    private String name;

    /**
     * 位置
     */
    private String location;

    /**
     * 研究对象列表
     */
    private List<ManagerDTO> sample;

    /**
     * 负责人
     */
    private ManagerDTO manager;

    /**
     * 协助者
     */
    private List<ManagerDTO> assistant;

    /**
     * 计划开始时间
     */
    private Date planStartTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;

    /**
     * 访谈地点
     */
    private String address;

    /**
     * 访谈计划
     */
    private String planText;

    /**
     * 备注
     */
    private String description;

    /**
     * 旧样本
     */
    private List<ManagerDTO> oldSample;

    /**
     * 旧协作者
     */
    private List<ManagerDTO> oldAssistant;
}
