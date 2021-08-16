package com.monetware.ringinterview.business.pojo.vo.sample;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/30 18:31
 */
@Data
public class BatchAssignByImportVO extends BaseVO {

    private List<BatchAssignVO> assignList;

}
