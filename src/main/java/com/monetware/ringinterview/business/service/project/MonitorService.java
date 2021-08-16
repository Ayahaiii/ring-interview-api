package com.monetware.ringinterview.business.service.project;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringinterview.business.dao.ProjectDao;
import com.monetware.ringinterview.business.dao.ProjectInterviewDao;
import com.monetware.ringinterview.business.dao.ProjectSampleDao;
import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.constant.ProjectConstants;
import com.monetware.ringinterview.business.pojo.constant.SampleConstants;
import com.monetware.ringinterview.business.pojo.dto.interview.InterviewLocationDTO;
import com.monetware.ringinterview.business.pojo.dto.monitor.*;
import com.monetware.ringinterview.business.pojo.po.BaseProject;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterview;
import com.monetware.ringinterview.business.pojo.vo.monitor.GanteDataVO;
import com.monetware.ringinterview.business.pojo.vo.monitor.ProgressMonitoringVO;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.ringinterview.system.util.date.DateUtil;
import com.monetware.ringinterview.system.util.file.FileUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Slf4j
@Service
public class MonitorService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectInterviewDao projectInterviewDao;

    @Autowired
    private ProjectSampleDao sampleDao;

    @Autowired
    private ProjectService projectService;

    /**
     * @description: 获取样本分配数据
     * @author: twitch
     * @param:
     * @Date: 2021/1/7
     */
    public ProjectMonitoringSampleDTO getProjectMonitoringSample(Integer projectId){
        ProjectMonitoringSampleDTO monitoringSampleDTO = new ProjectMonitoringSampleDTO();
        //如果不是管理员，只能看见部分
        if (projectService.getProjectRole(projectId) > AuthorizedConstants.ROLE_ADMIN) {
            monitoringSampleDTO = projectDao.getProjectMonitoringSample(projectId, ThreadLocalManager.getUserId());
        } else {
            monitoringSampleDTO = projectDao.getProjectMonitoringSample(projectId, ThreadLocalManager.getUserId());
        }
        return monitoringSampleDTO;
    }

    /**
     * 获取项目监控报表数据
     * @param projectId
     * @return
     */
    public ProjectMonitoringReportDTO getProjectMonitoringReport(Integer projectId){
        ProjectMonitoringReportDTO monitoringReportDTO = new ProjectMonitoringReportDTO();
        if (projectService.getProjectRole(projectId) > AuthorizedConstants.ROLE_ADMIN) {
            monitoringReportDTO = projectDao.getProjectMonitor(projectId, ThreadLocalManager.getUserId());
        } else {
            BaseProject baseProject = projectDao.selectByPrimaryKey(projectId);
            monitoringReportDTO.setNumOfTeam(baseProject.getNumOfTeam());
            monitoringReportDTO.setNumOfSample(baseProject.getNumOfSample());
            monitoringReportDTO.setFileSize(baseProject.getFileSize());
            monitoringReportDTO.setNumOfInterview(baseProject.getNumOfInterview());
            monitoringReportDTO.setInterviewTimeLen(baseProject.getInterviewTimeLen());
        }
        monitoringReportDTO.setTimeStr(DateUtil.secondToHourMinuteSecondEnStrByLong(monitoringReportDTO.getInterviewTimeLen()));
        monitoringReportDTO.setFileSizeStr(FileUtil.byteFormat(monitoringReportDTO.getFileSize(), true));
        return monitoringReportDTO;
    }

    /**
     * 获取进度监控数据
     * @param progressMonitoringVO
     * @return
     */
    public LinkedHashMap<String, Object> getProgressMonitoring(ProgressMonitoringVO progressMonitoringVO){
        progressMonitoringVO.setUserId(ThreadLocalManager.getUserId());
        Date now = new Date();
        List<String> list;
        LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap();
        List<Map<String, Object>> res;
        if (progressMonitoringVO.getEndTime() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 29);
            list = this.getLastNDays(calendar.getTime(), now);
            progressMonitoringVO.setStartTime(calendar.getTime());
            progressMonitoringVO.setEndTime(now);
            res = projectInterviewDao.selectInterviewDayCount(progressMonitoringVO);
        } else {
            list = this.getLastNDays(progressMonitoringVO.getStartTime(), progressMonitoringVO.getEndTime());
            res = projectInterviewDao.selectInterviewDayCount(progressMonitoringVO);
        }
        Map<String, Integer> totalMap = new TreeMap<>();
        for (int i = 0; i < res.size(); i++) {
            totalMap.put(res.get(i).get("yearDay").toString(), Integer.parseInt(res.get(i).get("totalCount").toString()));
        }
        List<Integer> countList = new ArrayList<>();
        for (String s : list) {
            if (totalMap.containsKey(s)) {
                countList.add(totalMap.get(s));
            } else {
                countList.add(0);
            }
        }
        linkedHashMap.put("xData", list);
        linkedHashMap.put("yData", countList);
        return linkedHashMap;
    }

    /**
     * 获取当前时间前N天日期
     * @return
     */
    private List<String> getLastNDays(Date startDate, Date endDate) {
        Date today = startDate; // calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = dateFormat.format(today);

        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        try {
            Date start = dateFormat.parse(endTime);
            Date end = dateFormat.parse(dateFormat.format(endDate));

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            // 日期加1(包含结束)
            tempEnd.add(Calendar.DATE, +1);
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 获取访谈项目经纬度
     * @param projectId
     * @return
     */
    public List<InterviewLocationDTO> getInterviewLocations(Integer projectId){
        if(projectId == null){
            throw new ServiceException(ErrorCode.CUSTOM_MSG,"项目id不能为空");
        }
        List<InterviewLocationDTO> list = new ArrayList<>();
        Example example = new Example(BaseProjectInterview.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId",projectId);
        example.orderBy("id").desc();
        List<BaseProjectInterview> baseProjectInterviews = projectInterviewDao.selectByExample(example);
        if (baseProjectInterviews.size() > 0) {
            for (BaseProjectInterview interview : baseProjectInterviews) {
                if (!StringUtils.isEmpty(interview.getLocation())) {
                    InterviewLocationDTO interviewLocationDTO = new InterviewLocationDTO();
                    JSONObject fromObject = JSONObject.parseObject(interview.getLocation());
                    interviewLocationDTO.setLocations(fromObject.toString());
                    interviewLocationDTO.setId(interview.getId());
                    interviewLocationDTO.setName(interview.getName());
                    list.add(interviewLocationDTO);
                }
            }
        }
        return list;
    }

    /**
     * 获取甘特图数据列表
     * @param ganteDataVO
     * @return
     */
    public GanteResultDTO getProgressMonitoringList(GanteDataVO ganteDataVO){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        GanteResultDTO resultDTO = new GanteResultDTO();
        BaseProject project = projectDao.selectByPrimaryKey(ganteDataVO.getProjectId());
        // 判断项目是否超过30天
        Date endDate = new Date();
        if (project.getStatus().equals(ProjectConstants.STATUS_FINISH)) {
            endDate = project.getEndDate();
        }
        String endDateStr = sdf.format(endDate);
        resultDTO.setEndDate(endDateStr);
        if ((endDate.getTime() - project.getCreateTime().getTime()) > 30 * 24 * 60 * 60 * 1000L) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 29);
            resultDTO.setStartDate(sdf.format(calendar.getTime()));
        } else {
            resultDTO.setStartDate(sdf.format(project.getCreateTime()));
        }
        Page page = PageHelper.startPage(ganteDataVO.getPageNum(), ganteDataVO.getPageSize());
        List<GanteDataDTO> res = sampleDao.getGanteData(ganteDataVO);
        GanteDataDTO dataDTO;
        for (int i = 0; i < res.size(); i++) {
            dataDTO = res.get(i);
            if (dataDTO.getStatus().equals(SampleConstants.STATUS_INIT)) {
                dataDTO.setEndDate(endDateStr);
            } else if (dataDTO.getStatus().equals(SampleConstants.STATUS_ASSIGN)) {
                dataDTO.setEndDate(dataDTO.getRealStartDate());
                dataDTO.setRealEndDate(endDateStr);
            } else if (dataDTO.getStatus().equals(SampleConstants.STATUS_RUNNING)) {
                dataDTO.setEndDate(dataDTO.getRealStartDate());
                dataDTO.setRealEndDate(dataDTO.getIngStartDate());
                dataDTO.setIngEndDate(endDateStr);
            } else if (dataDTO.getStatus().equals(SampleConstants.STATUS_FINISH)) {
                dataDTO.setEndDate(dataDTO.getRealStartDate());
                dataDTO.setIngEndDate(dataDTO.getDoneStartDate());
                dataDTO.setRealEndDate(dataDTO.getIngStartDate());
                dataDTO.setDoneEndDate(endDateStr);
            }
        }
        resultDTO.setPageList(new PageList<>(page));
        return resultDTO;
    }

    /**
     * 获取指标仪表盘数据
     * @param projectId
     * @return
     */
    public PercentDataDTO getPercentData(Integer projectId) {
        PercentDataDTO dataDTO = new PercentDataDTO();
        Map<String, Object> sampleData = sampleDao.getSampleStatData(projectId);
        Double totalCount = Double.parseDouble(sampleData.get("totalCount").toString());
        if (totalCount == 0) {
            dataDTO.setUsePercent(0D);
            dataDTO.setValidPercent(0D);
            dataDTO.setSuccessPercent(0D);
            dataDTO.setFailPercent(0D);
        } else {
            dataDTO.setUsePercent(sampleData.get("useCount") == null ? 0D : Double.parseDouble(sampleData.get("useCount").toString())/totalCount * 1.0);
            dataDTO.setValidPercent(sampleData.get("validCount") == null ? 0D : Double.parseDouble(sampleData.get("validCount").toString())/totalCount * 1.0);
            dataDTO.setSuccessPercent(sampleData.get("successCount") == null ? 0D : Double.parseDouble(sampleData.get("successCount").toString())/totalCount * 1.0);
            dataDTO.setFailPercent(sampleData.get("failCount") == null ? 0D : Double.parseDouble(sampleData.get("failCount").toString())/totalCount * 1.0);
        }
        Map<String, Object> interviewData = projectInterviewDao.getInterviewStatData(projectId);
        Double iTotalCount = Double.parseDouble(interviewData.get("totalCount").toString());
        if (iTotalCount == 0) {
            dataDTO.setAuditPercent(0D);
            dataDTO.setAuditSuccessPercent(0D);
        } else {
            dataDTO.setAuditPercent(interviewData.get("auditCount") == null ? 0D : Double.parseDouble(interviewData.get("auditCount").toString())/iTotalCount * 1.0);
            dataDTO.setAuditSuccessPercent(interviewData.get("auditSuccessCount") == null ? 0D : Double.parseDouble(interviewData.get("auditSuccessCount").toString())/iTotalCount * 1.0);
        }
        return dataDTO;
    }

}
