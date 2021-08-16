package com.monetware.ringinterview.business.pojo.vo.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Simo
 * @date 2019-09-18
 */
@Data
public class UserBuyVO {

    private BigDecimal orderAmount;

    private Integer type;

}
