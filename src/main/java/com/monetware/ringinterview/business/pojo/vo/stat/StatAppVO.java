package com.monetware.ringinterview.business.pojo.vo.stat;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @description:
 * @author: twitch
 * @param:
 * @Date: 2021/1/11
 */
@Data
public class StatAppVO extends BaseVO {
    private Integer userId;

    private String keyword;
}
