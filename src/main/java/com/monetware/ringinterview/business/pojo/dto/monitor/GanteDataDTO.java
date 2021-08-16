package com.monetware.ringinterview.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-06
 */
@Data
public class GanteDataDTO {

    private Integer id;

    private String code;

    private String name;

    private String managerName;

    private Integer status;

    private String startDate;

    private String endDate;

    private String realStartDate;

    private String realEndDate;

    private String ingStartDate;

    private String ingEndDate;

    private String doneStartDate;

    private String doneEndDate;

}
