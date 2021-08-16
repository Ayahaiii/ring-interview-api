package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/4/9 14:46
 */
@Data
public class FileUpdateVO extends BaseVO {

    private Integer fileId;

    private String name;

}
