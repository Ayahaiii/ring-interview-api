package com.monetware.ringinterview.business.pojo.po;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Linked
 * @date 2020/2/17 17:31
 */
@Data
@Table(name = "rs_project")
public class BaseProject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 名称
   */
  private String name;

  /**
   * 描述
   */
  private String description;

  /**
   * 邀请码
   */
  private String inviteCode;

  /**
   * 邀请码过期时间
   */
  private Date codeExpireTime;

  /**
   * 邀请自动审核
   */
  private Integer codeAutoAudit;

  /**
   *
   * 团队数量
   */
  private Integer numOfTeam;

  /**
   *
   * 研究对象数量
   */
  private Integer numOfSample;

  /**
   * 信息量
   */
  private Long fileSize;

  /**
   *
   * 访谈数量
   */
  private Integer numOfInterview;

  /**
   *访谈总时长
   */
  private Long interviewTimeLen;

  /**
   * 项目标签
   */
  private String labelText;

  /**
   * 项目类型id
   */
  private Integer typeId;

  /**
   * 项目配置
   */
  private String config;

  /**
   * 开始时间
   */
  private Date beginDate;

  /**
   * 结束时间
   */
  private Date endDate;

  /**
   * 暂停时间
   */
  private Date pauseTime;

  /**
   * 状态
   * 0:准备中，1:已启动，2:已暂停，3:已结束，已归档
   */
  private Integer status;

  private Integer createUser;

  private Date createTime;

  private Integer updateUser;

  private Date updateTime;

  /**
   * 删除标记
   */
  private Integer isDelete;

  private Date deleteTime;

}
