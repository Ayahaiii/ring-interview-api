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
@Table(name = "rs_project_interview_file")
public class BaseProjectInterviewFile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer interviewId;

  /**
   * 1.录音 2.附件
   */
  private Integer type;

  /**
   * 文件名称
   */
  private String name;

  /**
   * 文件路径
   */
  private String filePath;

  /**
   * 文件大小
   */
  private Long fileSize;

  /**
   * 录音文件时长
   */
  private Integer duration;

  private Date uploadTime;

  private Integer createUser;

  private Date createTime;


}
