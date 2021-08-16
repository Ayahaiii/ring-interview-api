package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.monitor.GanteDataDTO;
import com.monetware.ringinterview.business.pojo.dto.interview.SampleAssignedDTO;
import com.monetware.ringinterview.business.pojo.dto.sample.ProjectSampleDTO;
import com.monetware.ringinterview.business.pojo.dto.sample.SampleListDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProjectSample;
import com.monetware.ringinterview.business.pojo.vo.monitor.GanteDataVO;
import com.monetware.ringinterview.business.pojo.vo.sample.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Linked
 * @date 2020/2/18 10:44
 */
@Mapper
@Repository
public interface ProjectSampleDao {

    /**
     * 创建研究对象表
     *
     * @param projectId
     */
    void createProjectSampleTable(@Param("projectId") Integer projectId);

    /**
     * 保存研究对象
     *
     * @param projectSampleVO
     */
    Integer saveProjectSample(ProjectSampleVO projectSampleVO);

    Integer insertSampleList(@Param("projectId") Integer projectId, @Param("list") List<BaseProjectSample> list);

    Integer updateSampleList(@Param("projectId") Integer projectId, @Param("list") List<BaseProjectSample> list);

    /**
     * 查询研究对象列表
     *
     * @param sampleListVO
     * @return
     */
    List<SampleListDTO> getSampleList(SampleListVO sampleListVO);

    List<SampleListDTO> getSampleListApp(SampleListAppVO sampleListAppVO);

    List<SampleListDTO> getSampleListByIds(@Param("projectId") Integer projectId, @Param("list") List<Integer> list);

    /**
     * 删除研究对象包括批量删除
     *
     * @param deleteSampleVO
     * @return
     */
    Integer deleteSample(DeleteSampleVO deleteSampleVO);

    /**
     * 更新研究对象
     *
     * @param projectSampleVO
     * @return
     */
    Integer updateProjectSample(ProjectSampleVO projectSampleVO);

    /**
     * 研究对象详情
     *
     * @param projectId
     * @return
     */
    ProjectSampleDTO getSampleDetail(@Param("projectId") Integer projectId, @Param("id") Integer id);

    /**
     * 更新研究对象状态
     * @param projectId
     * @param sampleIds
     * @param status
     * @return
     */
    Integer updateSampleStatus(@Param("projectId") Integer projectId,@Param("sampleIds") List<Integer>sampleIds, Integer status);

    Integer checkSampleExistByCode(@Param("projectId") Integer projectId, @Param("code") String code,@Param("sampleId") Integer sampleId);

    BaseProjectSample getSampleByCode(@Param("projectId") Integer projectId, @Param("code") String code);

    Integer getStatusById(@Param("projectId") Integer projectId, @Param("id") Integer id);

    List<GanteDataDTO> getGanteData(GanteDataVO ganteDataVO);

    Map<String, Object> getSampleStatData(@Param("projectId") Integer projectId);

    /**
     * 已分派样本
     * @param assignedVO
     * @return
     */
    List<SampleAssignedDTO> getSampleAssigned(SampleAssignedVO assignedVO);


    List<Integer> getSampleIdsAssigned(@Param("projectId")Integer projectId);
}
