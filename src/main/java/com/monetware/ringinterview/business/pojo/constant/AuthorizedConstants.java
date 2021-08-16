package com.monetware.ringinterview.business.pojo.constant;

/**
 * @author Simo
 * @date 2020-02-20
 */
public class AuthorizedConstants {

    /**
     * 管理员
     */
    public static final Integer ROLE_OWNER = 1;

    /**
     * 管理员
     */
    public static final Integer ROLE_ADMIN = 2;

    /**
     * 分析员
     */
    public static final Integer ROLE_ANALYST = 3;

    /**
     * 督导员
     */
    public static final Integer ROLE_SUPERVISOR = 4;

    /**
     * 访问员
     */
    public static final Integer ROLE_INTERVIEWER = 5;

    // 项目拥有者
    public static final String R_ALL = "ALL";

    // 项目详情模块
    public static final String RP_PROJECT_VIEW = "PROJECT_VIEW"; // 项目基本信息详情
    public static final String RP_PROJECT_EDIT = "PROJECT_EDIT"; // 项目基本信息编辑
    public static final String RP_PROJECT_CONFIG_ADMIN = "PROJECT_CONFIG_ADMIN"; // 项目配置管理

    // 访谈对象模块
    public static final String RS_SAMPLE_LIST = "SAMPLE_LIST"; // 样本列表
    public static final String RS_SAMPLE_ADD = "SAMPLE_ADD"; // 样本添加
    public static final String RS_SAMPLE_VIEW = "SAMPLE_VIEW"; // 样本详情
    public static final String RS_SAMPLE_EDIT = "SAMPLE_EDIT"; // 样本编辑
    public static final String RS_SAMPLE_DELETE = "SAMPLE_DELETE"; // 样本删除
    public static final String RS_SAMPLE_EXPORT = "SAMPLE_EXPORT"; // 样本导出
    public static final String RS_SAMPLE_PROPERTY_ADMIN = "SAMPLE_PROPERTY_ADMIN"; // 样本属性
    public static final String RS_SAMPLE_ASSIGN_ADMIN = "SAMPLE_ASSIGN_ADMIN"; // 样本分派
    public static final String RS_SAMPLE_CONTACTRECORD_ADMIN = "SAMPLE_CONTACTRECORD_ADMIN"; // 样本接触记录

    // 团队模块
    public static final String RT_MEMBER_LIST = "MEMBER_LIST"; // 团队列表
    public static final String RT_MEMBER_ADD = "MEMBER_ADD"; // 团队添加
    public static final String RT_MEMBER_VIEW = "MEMBER_VIEW"; // 团队详情
    public static final String RT_MEMBER_EDIT = "MEMBER_EDIT"; // 团队编辑
    public static final String RT_MEMBER_DELETE = "MEMBER_DELETE"; // 团队删除
    public static final String RT_MEMBER_EXPORT = "MEMBER_EXPORT"; // 团队导出
    public static final String RT_MEMBER_GROUP_ADMIN = "MEMBER_GROUP_ADMIN"; // 团队分组管理
    public static final String RT_MEMBER_INVITECODE_ADMIN = "MEMBER_INVITECODE_ADMIN"; // 团队邀请码

    // 访谈模块
    public static final String RI_INTERVIEW_LIST = "INTERVIEW_LIST"; // 访谈列表
    public static final String RI_INTERVIEW_ADD = "INTERVIEW_ADD"; // 访谈新建
    public static final String RI_INTERVIEW_VIEW = "INTERVIEW_VIEW"; // 访谈详情
    public static final String RI_INTERVIEW_EDIT = "INTERVIEW_EDIT"; // 访谈编辑
    public static final String RI_INTERVIEW_DELETE = "INTERVIEW_DELETE"; // 访谈删除
    public static final String RI_INTERVIEW_EXPORT = "INTERVIEW_EXPORT"; // 访谈导出
    public static final String RI_INTERVIEW_TEXT_VIEW = "INTERVIEW_TEXT_VIEW"; // 访谈文本详情
    public static final String RI_INTERVIEW_TEXT_ADD = "INTERVIEW_TEXT_ADD"; // 访谈文本录入
    public static final String RI_INTERVIEW_TEXT_EDIT = "INTERVIEW_TEXT_EDIT"; // 访谈文本编辑
    public static final String RI_INTERVIEW_TEXT_DELETE = "INTERVIEW_TEXT_DELETE"; // 访谈文本删除
    public static final String RI_INTERVIEW_TEXT_EXPORT = "INTERVIEW_TEXT_EXPORT"; // 访谈文本导出
    public static final String RI_INTERVIEW_TEXT_AUDIT = "INTERVIEW_TEXT_AUDIT"; // 访谈文本审核
    public static final String RI_INTERVIEW_VOICE_VIEW = "INTERVIEW_VOICE_VIEW"; // 访谈录音
    public static final String RI_INTERVIEW_FILE_VIEW = "INTERVIEW_FILE_VIEW"; // 访谈附件
    public static final String RI_INTERVIEW_VOICE_ADMIN = "INTERVIEW_VOICE_ADMIN"; // 访谈录音
    public static final String RI_INTERVIEW_FILE_ADMIN = "INTERVIEW_FILE_ADMIN"; // 访谈附件

    // 分析模块
    public static final String RA_STAT_LIST = "STAT_LIST"; // 分析

    // 监控模块
    public static final String RM_MONITOR_LIST = "MONITOR_LIST"; // 监控


}
