package com.monetware.ringinterview.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-09
 */
@Data
public class PercentDataDTO {

    private Double usePercent;

    private Double validPercent;

    private Double successPercent;

    private Double failPercent;

    private Double auditPercent;

    private Double auditSuccessPercent;



}
