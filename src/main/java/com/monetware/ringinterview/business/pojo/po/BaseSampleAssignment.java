package com.monetware.ringinterview.business.pojo.po;


import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/2/18 10:44
 */
@Data
public class BaseSampleAssignment {

  private Integer id;

  private Integer sampleId;

  private Integer teamUserId;

  private Integer type;

  private Integer createUser;

  private Date createTime;


}
