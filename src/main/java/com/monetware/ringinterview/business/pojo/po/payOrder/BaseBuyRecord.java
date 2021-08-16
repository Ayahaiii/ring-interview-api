package com.monetware.ringinterview.business.pojo.po.payOrder;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 购买记录
 */
@Data
@Table(name = "rs_buy_record")
public class BaseBuyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 购买类型
     */
    private Integer type;

    /**
     * 支付类型
     * 1：支出
     * 2：收入
     */
    private Integer payType;

    /**
     * 用户编号
     */
    private Integer userId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 订单表编号
     */
    private Integer payOrderId;

    /**
     * 删除标志
     */
    private Integer isDelete;

    /**
     * 消费记录
     */
    private String message;


    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
