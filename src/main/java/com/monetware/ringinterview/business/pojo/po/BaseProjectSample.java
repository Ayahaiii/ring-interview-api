package com.monetware.ringinterview.business.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Lin
 * @date 2020/2/18 17:44
 */
@Data
public class BaseProjectSample {

    private Integer id;

    private String name;

    /**
     * 编号
     */
    private String code;

    private String gender;

    private Integer age;

    /**
     * 出生日期
     */
    private String birth;

    /**
     * 婚姻状况
     */
    private String marriageStatus;

    /**
     * 学历
     */
    private String education;

    /**
     * 单位
     */
    private String organization;

    /**
     * 职业
     */
    private String profession;

    /**
     * 职务
     */
    private String position;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 宗教
     */
    private String religion;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 语言
     */
    private String language;

    /**
     * 籍贯
     */
    private String placeOfBirth;

    /**
     * 方言
     */
    private String dialects;

    /**
     * 备注
     */
    private String description;

    /**
     * 详细介绍
     */
    private String detail;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话号码
     */
    private String mobile;

    /**
     * 手机号码
     */
    private String phone;

    private String weixin;

    private String qq;

    private String weibo;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 镇
     */
    private String town;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 自定义
     */
    private String custom1;

    private String custom2;

    private String custom3;

    private String custom4;

    private String custom5;

    private String custom6;

    private String custom7;

    private String custom8;

    private String custom9;

    private String custom10;

    private String custom11;

    private String custom12;

    private String custom13;

    private String custom14;

    private String custom15;

    /**
     * 样本状态
     * 0:初始化，1:已分配，2:进行中 3:已完成
     */
    private Integer status;

    /**
     * 最后修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifyTime;

    /**
     * 最后修改用户
     */
    private Integer lastModifyUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createUser;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 删除用户
     */
    private Integer deleteUser;


}
