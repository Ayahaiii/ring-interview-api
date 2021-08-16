package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Linked
 * @date 2020/3/11 22:29
 */
@Data
public class ParagraphImportVO extends BaseVO {


    private Integer interviewId;


    private Integer fileId;


    List<BatchImportVO> batchImport;

    Map<String, List<BatchImportVO>> map;

}
