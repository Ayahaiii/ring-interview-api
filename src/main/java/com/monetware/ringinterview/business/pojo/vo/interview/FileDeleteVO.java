package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/16 16:03
 */
@Data
public class FileDeleteVO extends BaseVO {

    private List<Integer> fileIds;

}
