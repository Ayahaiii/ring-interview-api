package com.monetware.ringinterview.business.pojo.vo.interview;

import com.monetware.ringinterview.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/26 16:33
 */
@Data
public class SpeakerDeleteVO extends BaseVO {

    List<Integer> speakerIds;

}
