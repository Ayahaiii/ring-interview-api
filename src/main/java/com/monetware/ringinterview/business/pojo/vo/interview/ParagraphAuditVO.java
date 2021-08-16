package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/3/11 15:33
 */
@Data
public class ParagraphAuditVO extends BaseVO {

    private Integer interviewId;

    private Integer fileId;

    private Integer valid;

    private String auditNote;


}
