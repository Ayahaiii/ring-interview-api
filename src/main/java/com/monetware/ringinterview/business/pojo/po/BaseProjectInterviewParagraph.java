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
@Table(name = "rs_project_interview_paragraph")
public class BaseProjectInterviewParagraph {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private Integer interviewId;

  private Integer speakId;

  private Integer fileId;

  private String beginTime;

  private String endTime;

  /**
   * 文本
   */
  private String paragraph;

  /**
   * 时长
   */
  private Long duration;

  private Integer auditUser;

  private Date auditTime;

  /**
   * 审核备注
   */
  private String auditNote;

  /**
   * 审核状态 1:通过 2:退回整理 3:退回访谈
   */
  private Integer valid;

  private Integer createUser;

  private Date createTime;


}
