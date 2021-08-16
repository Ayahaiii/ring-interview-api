package com.monetware.ringinterview.business.dao;

import com.monetware.ringinterview.business.pojo.dto.monitor.ProjectMonitoringSampleDTO;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectHeadDTO;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectInfoDTO;
import com.monetware.ringinterview.business.pojo.dto.project.ProjectListDTO;
import com.monetware.ringinterview.business.pojo.dto.monitor.ProjectMonitoringReportDTO;
import com.monetware.ringinterview.business.pojo.po.BaseProject;
import com.monetware.ringinterview.business.pojo.vo.project.ProjectListAppVO;
import com.monetware.ringinterview.business.pojo.vo.project.ProjectListVO;
import com.monetware.ringinterview.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:03
 */
@Mapper
@Repository
public interface ProjectDao extends MyMapper<BaseProject> {

    ProjectHeadDTO getProjectNameAndRole(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    ProjectInfoDTO getProjectInfo(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    List<ProjectListDTO> getProjectList(ProjectListVO projectListVO);

    List<ProjectListDTO> getProjectListApp(ProjectListAppVO projectListAppVO);

    ProjectMonitoringReportDTO getProjectMonitor(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    ProjectMonitoringSampleDTO getProjectMonitoringSample(@Param("projectId")Integer projectId, @Param("userId")Integer userId);

    Integer updateProjectAdd(BaseProject project);

    Integer updateProjectDel(BaseProject project);

}
