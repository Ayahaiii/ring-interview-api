package com.monetware.ringinterview.business.pojo.dto.interview;

import com.monetware.ringinterview.business.pojo.dto.sample.ManagerDTO;
import lombok.Data;

import java.util.List;


/**
 * @author Linked
 * @date 2020/3/6 12:20
 */
@Data
public class SampleAssignedDTO {

    private ManagerDTO sample;

    private List<ManagerDTO> assistant;

    private ManagerDTO manager;

}
