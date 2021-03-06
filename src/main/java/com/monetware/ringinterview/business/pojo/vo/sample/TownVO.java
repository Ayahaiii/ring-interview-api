package com.monetware.ringinterview.business.pojo.vo.sample;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-11
 */
@Data
public class TownVO extends BaseVO {

    private String provinceName;

    private String cityName;

    private String distName;
}
