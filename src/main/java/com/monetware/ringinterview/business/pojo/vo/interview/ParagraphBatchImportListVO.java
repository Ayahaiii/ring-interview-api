package com.monetware.ringinterview.business.pojo.vo.interview;

import lombok.Data;

import java.util.List;

/**
 * @author budi
 * @version 1.0
 * @create 2020/11/10 2:49 下午
 * @description 访谈文本批量录入传入列表VO
 */
@Data
public class ParagraphBatchImportListVO {

    /**
     * 传入列表
     */
    List<ParagraphBatchImportVO> list;

}
