package com.monetware.ringinterview.business.pojo.vo.stat;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-11
 */
@Data
public class InterviewMarkUpdateVO extends BaseVO {

    private Integer id;

    private Integer codeId;

    private String codeName;

    private String note;

}
