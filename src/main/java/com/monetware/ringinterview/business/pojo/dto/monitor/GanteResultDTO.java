package com.monetware.ringinterview.business.pojo.dto.monitor;

import com.github.pagehelper.Page;
import com.monetware.ringinterview.system.base.PageList;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-06
 */
@Data
public class GanteResultDTO {

    private String startDate;

    private String endDate;

    private PageList<Page> pageList;
}
