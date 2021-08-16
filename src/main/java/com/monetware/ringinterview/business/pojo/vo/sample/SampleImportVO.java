package com.monetware.ringinterview.business.pojo.vo.sample;

import com.monetware.ringinterview.business.pojo.po.BaseProjectSample;
import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-04
 */
@Data
public class SampleImportVO extends BaseVO {

    private List<BaseProjectSample> sampleList;

}
