package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.po.BaseSampleAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/18 11:20
 */
@Mapper
@Repository
public interface SampleAssignmentDao {


    /**
     * 创建研究对象分派表
     *
     * @param projectId
     */
    void createSampleAssignmentTable(@Param("projectId") Integer projectId);

    /**
     * 分派研究对象
     * @param projectId
     * @param assignmentList
     * @return
     */
    Integer insertAssign(@Param("projectId") Integer projectId, @Param("assignmentList") List<BaseSampleAssignment> assignmentList);

    /**
     * 删除分派表
     * @param sampleIds
     * @return
     */
    Integer deleteAssignmentBySampleId(@Param("projectId") Integer projectId,@Param("sampleIds") List<Integer> sampleIds);
}
