package com.monetware.ringinterview.business.pojo.po;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "rs_permission")
public class BasePermission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer role;

  private Integer dataStoreTime;

  private Integer dataStoreSpace;

  private Integer ifGroup;

  private Integer uploadSize;

  private Integer ifAnalysis;

  private Integer teamNum;

  private Integer freeAiTime;

  private Integer ifTeamAnalysis;

  private Integer ifInterviewer;

  private Integer ifDeploy;

  private Double price;



}
