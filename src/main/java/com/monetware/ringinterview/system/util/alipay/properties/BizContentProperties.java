package com.monetware.ringinterview.system.util.alipay.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BizContentProperties {

    /**
     * 订单Id
     */
    private String out_trade_no;

    /**
     * 销售产品码
     */
    private String product_code = "FAST_INSTANT_TRADE_PAY";

    /**
     * 金额（单位：元）
     */
    private Double total_amount;

    /**
     * 标题
     */
    private String subject;

    /**
     * 描述
     */
    private String body;

    /**
     * 抵扣的余额
     */

    @JsonProperty("passback_params")
    private Double passback_params;

    /**
     * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天
     * 1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
     * 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
     * 该参数在请求到支付宝时开始计时。
     */
    @JsonProperty("timeout_express")
    private String timeout_express = "3m";

    /**
     *商品主类型：0 虚拟类商品，1 实物类商品（默认）
     */
    @JsonProperty("goods_type")
    private Integer goods_type;
}
