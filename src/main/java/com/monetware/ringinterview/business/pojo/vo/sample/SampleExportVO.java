package com.monetware.ringinterview.business.pojo.vo.sample;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-04
 */
@Data
public class SampleExportVO extends BaseVO {

    private Integer projectId;

    private String opt;

    private SampleListVO searchVO;

    private List<Integer> sampleIds;

    private List<String>  properties;

    private String fileType;

    private String description;

}
