package com.monetware.ringinterview.business.pojo.vo.interview;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/12 16:18
 */
@Data
public class ParagraphExportVO {

    private Integer projectId;

    private ParagraphListVO listVO;

    private String fileType;

    private String description;

    private List<String> properties;

}
