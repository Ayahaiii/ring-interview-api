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
@Table(name = "rs_project_interview_speaker")
public class BaseProjectInterviewSpeaker {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer interviewId;

  /**
   * 讲话者身份
   * 1:主持人 2:助理 3:受访者 4:关系人
   */
  private Integer type;

  /**
   * 讲话者名字
   */
  private String name;

  /**
   * 昵称
   */
  private String petName;

  /**
   * 样本id
   */
  private Integer sampleId;

  /**
   * 负责人/协作者id
   */
  private Integer userId;

  /**
   * 样本归属
   */
  private Integer sampleOwner;


  private String description;

  private Integer createUser;

  private Date createTime;



}
