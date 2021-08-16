package com.monetware.ringinterview.business.service.project;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringinterview.business.dao.ProjectInterviewCodeDao;
import com.monetware.ringinterview.business.dao.ProjectInterviewMarkDao;
import com.monetware.ringinterview.business.dao.ProjectInterviewParagraphDao;
import com.monetware.ringinterview.business.dao.ProjectTeamUserDao;
import com.monetware.ringinterview.business.pojo.constant.AuthorizedConstants;
import com.monetware.ringinterview.business.pojo.constant.ProjectConstants;
import com.monetware.ringinterview.business.pojo.dto.stat.*;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewCode;
import com.monetware.ringinterview.business.pojo.po.BaseProjectInterviewMark;
import com.monetware.ringinterview.business.pojo.vo.stat.*;
import com.monetware.ringinterview.system.base.ErrorCode;
import com.monetware.ringinterview.system.base.PageList;
import com.monetware.ringinterview.system.base.PageParam;
import com.monetware.ringinterview.system.exception.ServiceException;
import com.monetware.ringinterview.system.util.atuocode.AutoCodeUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author Simo
 * @date 2020-02-17
 */
@Slf4j
@Service
public class AnalyzerService {

    @Autowired
    private ProjectInterviewMarkDao markDao;

    @Autowired
    private ProjectInterviewCodeDao codeDao;

    @Autowired
    private ProjectInterviewParagraphDao paragraphDao;

    @Autowired
    private ProjectTeamUserDao teamUserDao;

    @Autowired
    private ProjectService projectService;

    /**
     * 管理员获取所有分析员
     * @param projectId
     * @return
     */
    public List<AnalystUserDTO> getAnalystUserList(Integer projectId) {
        if (AuthorizedConstants.ROLE_ADMIN.equals(projectService.getProjectRole(projectId))) {
            List<AnalystUserDTO> res = new ArrayList<>();
            AnalystUserDTO userDTO = new AnalystUserDTO();
            userDTO.setId(0);
            userDTO.setUserId(ThreadLocalManager.getUserId());
            userDTO.setName("我");
            res.add(userDTO);
            res.addAll(teamUserDao.getAnalystUserList(projectId));
            return res;
        }
        return null;
    }


    /**
     * 分页获取分析首页结果
     * @param statVO
     * @return
     */
    public PageList<Page> getProjectStatList(StatVO statVO) {
        if (!AuthorizedConstants.ROLE_ADMIN.equals(statVO.getCheckRole())) {
            statVO.setUserId(ThreadLocalManager.getUserId());
        }
        Page page = PageHelper.startPage(statVO.getPageNum(), statVO.getPageSize());
        markDao.getProjectStatList(statVO);
        return new PageList<>(page);
    }

    /**
     * @description:获取分析首页结果  -- 未完成
     * @author: twitch
     * @param:
     * @Date: 2021/1/11
     */
    public List<StatDTO> getProjectStatAppList(StatAppVO statAppVO){
        if (!AuthorizedConstants.ROLE_ADMIN.equals(statAppVO.getCheckRole())) {
            statAppVO.setUserId(ThreadLocalManager.getUserId());
        }
        List<StatDTO> list=markDao.getProjectStatAppList(statAppVO);
        return list;
    }
    /**
     * 编码保存
     * @param interviewCodeVO
     */
    public int saveProjectInterviewCode(InterviewCodeVO interviewCodeVO) {
        //List<BaseProjectInterviewCode> list = codeDao.selectAll();
        List<InterviewCodeSearchDTO> list=codeDao.getAllInterviewCodeList(interviewCodeVO);
        //for (BaseProjectInterviewCode code : list) {
        for (InterviewCodeSearchDTO code : list) {
            System.out.println(code.getName()+"===="+code.getRule());
            if (code.getName().equals(interviewCodeVO.getName())) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "编码名称重复");
            }
            if (interviewCodeVO.getRule().equals(code.getRule()) && code.getType()==2) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "自动编码规则重复");
            }
        }
        BaseProjectInterviewCode interviewCode = new BaseProjectInterviewCode();
        BeanUtils.copyProperties(interviewCodeVO, interviewCode);
        if (interviewCodeVO.getId() == null) {
            interviewCode.setCreateTime(new Date());
            interviewCode.setCreateUser(ThreadLocalManager.getUserId());
            int row = codeDao.insertSelective(interviewCode);
            // TODO 自动编码 执行编码程序
            if (ProjectConstants.CODE_AUTO.equals(interviewCodeVO.getType())) {
                Integer userId = null;
                if (!AuthorizedConstants.ROLE_ADMIN.equals(interviewCodeVO.getCheckRole())) {
                    userId = ThreadLocalManager.getUserId();
                }
                this.insertAutoProjectInterviewMark(interviewCodeVO.getProjectId(), interviewCode.getId(), interviewCode.getRule(), userId);
            }
            return row;
        } else {
            // TODO 暂时自动编码不能修改
           return codeDao.updateByPrimaryKeySelective(interviewCode);
        }
    }

    /**
     * 自动编码开启
      * @param projectId
     * @param codeId
     */
    private void insertAutoProjectInterviewMark(Integer projectId, Integer codeId, String rule, Integer userId) {
        // TODO 获取项目下所有访谈文本 记录 样本ID
        if (StringUtils.isEmpty(rule)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "请编辑规则");
        }
        List<AnalystParagraphAutoDTO> datas = paragraphDao.getParagraphsByProjectId(projectId, userId);
        if (datas != null && datas.size() > 0) {
            Date now = new Date();
            List<BaseProjectInterviewMark> insertList = new ArrayList<>();
            List<Pair<Long, Integer>> res;
            for (AnalystParagraphAutoDTO autoDTO : datas) {
                res = AutoCodeUtil.tag(autoDTO.getParagraph(), rule);
                if (res != null && res.size() > 0) {
                    for (Pair<Long, Integer> temp : res) {
                        BaseProjectInterviewMark interviewMark = new BaseProjectInterviewMark();
                        interviewMark.setInterviewId(autoDTO.getInterviewId());
                        interviewMark.setSampleId(autoDTO.getSampleId());
                        interviewMark.setParagraphId(autoDTO.getId());
                        interviewMark.setCodeId(codeId);
                        interviewMark.setProjectId(projectId);
                        interviewMark.setBegin(temp.getKey().intValue());
                        interviewMark.setLen(temp.getValue());
                        interviewMark.setCreateUser(ThreadLocalManager.getUserId());
                        interviewMark.setNote("自动编码提取");
                        interviewMark.setCreateTime(now);
                        insertList.add(interviewMark);
                    }
                }
            }
            if (insertList.size() > 0) {
                markDao.insertList(insertList);
            }
        }
    }

    /**
     * 分页获取编码列表
     * @param codeSearchVO
     * @return
     */
    public PageList<Page> getProjectInterviewCodeListPage(InterviewCodeSearchVO codeSearchVO) {
        if (!AuthorizedConstants.ROLE_ADMIN.equals(codeSearchVO.getCheckRole())) {
            codeSearchVO.setUserId(ThreadLocalManager.getUserId());
        }
        Page page = PageHelper.startPage(codeSearchVO.getPageNum(), codeSearchVO.getPageSize());
        codeDao.getInterviewCodeList(codeSearchVO);
        return new PageList<>(page);
    }

    /**
     * 获取词云图数据结构
     * @param codeSearchVO
     */
    public Map<String, Map<String, Integer>> getProjectInterviewCodeViewData(InterviewCodeSearchVO codeSearchVO) {
        Map<String, Map<String, Integer>> res = new HashMap<>();
        if (!AuthorizedConstants.ROLE_ADMIN.equals(codeSearchVO.getCheckRole())) {
            codeSearchVO.setUserId(ThreadLocalManager.getUserId());
        }
        List<InterviewCodeSearchDTO> data = codeDao.getInterviewCodeList(codeSearchVO);
        if (data != null && data.size() > 0) {
            // 整理词云图需要的数据结构
            Map<String, Integer> markMap = new HashMap<>();
            Map<String, Integer> sampleMap = new HashMap<>();
            String key;
            int markCount;
            int sampleCount;
            for (InterviewCodeSearchDTO temp : data) {
                key = temp.getName();
                markCount = temp.getMarkCount() == null ? 0 : temp.getMarkCount();
                sampleCount = temp.getSampleCount() == null ? 0 : temp.getSampleCount();
                if (markMap.containsKey(key)) {
                    markMap.put(key, Integer.parseInt(markMap.get(key).toString()) + markCount);
                } else {
                    markMap.put(key, markCount);
                }
                if (sampleMap.containsKey(key)) {
                    sampleMap.put(key, Integer.parseInt(sampleMap.get(key).toString()) + sampleCount);
                } else {
                    sampleMap.put(key, sampleCount);
                }
            }
            res.put("markCloud", sortDescend(markMap));
            res.put("sampleCloud", sortDescend(sampleMap));
        }
        return res;
    }

    /**
     * 获取编码
     * @param codeFindVO
     * @return
     */
    public List<BaseProjectInterviewCode> getProjectInterviewCodeList(InterviewCodeFindVO codeFindVO) {
        Example example = new Example(BaseProjectInterviewCode.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", codeFindVO.getProjectId());
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        if (!StringUtils.isEmpty(codeFindVO.getName())) {
            criteria.andLike("name", codeFindVO.getName());
        }
        return codeDao.selectByExample(example);
    }

    /**
     * 删除标签
     * @param codeDelVO
     * @return
     */
    public int deleteProjectInterviewCode(InterviewCodeDelVO codeDelVO) {
        // 判断编码下是否存在标注
        for (Integer codeId : codeDelVO.getIds()) {
            BaseProjectInterviewMark mark = new BaseProjectInterviewMark();
            mark.setProjectId(codeDelVO.getProjectId());
            mark.setCodeId(codeId);
            if (markDao.selectCount(mark) > 0) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "请先删除标注");
            }
        }

        Example example = new Example(BaseProjectInterviewCode.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", codeDelVO.getIds());
        criteria.andEqualTo("projectId", codeDelVO.getProjectId());
        return codeDao.deleteByExample(example);
    }

    /**
     * 获取当前样本下文本详情
     * @param paragraphVO
     * @return
     */
    public PageList<Page> getProjectInterviewParagraphBySampleId(AnalystParagraphVO paragraphVO) {
        Page page = PageHelper.startPage(paragraphVO.getPageNum(), paragraphVO.getPageSize());
        paragraphDao.getParagraphsBySampleId(paragraphVO);
        return new PageList<>(page);
    }

    /**
     * 获取当前样本下所有的标注
     * @param markFindVO
     * @return
     */
    public List<SampleCodeMarkDTO> getProjectInterviewMarkBySampleId(InterviewMarkFindVO markFindVO) {
//        Example example = new Example(BaseProjectInterviewMark.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("projectId", markFindVO.getProjectId());
//        criteria.andEqualTo("sampleId", markFindVO.getSampleId());
        return markDao.getSampleCodeMarkList(markFindVO);
    }

    /**
     * 保存标注
     * @param markVO
     * @return
     */
    public int saveProjectInterviewMark(InterviewMarkVO markVO) {
        if (markVO.getCodeId() == null) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "编码不能为空");
        }
        BaseProjectInterviewMark interviewMark = new BaseProjectInterviewMark();
        BeanUtils.copyProperties(markVO, interviewMark);
        if (markVO.getId() == null) {
            interviewMark.setCreateTime(new Date());
            interviewMark.setCreateUser(ThreadLocalManager.getUserId());
            markDao.insertSelective(interviewMark);
            return interviewMark.getId();
        } else {
            return markDao.updateByPrimaryKeySelective(interviewMark);
        }
    }

    /**
     * 获取标注列表
     * @param searchVO
     * @return
     */
    public PageList<Page> getInterviewMarkList(InterviewMarkSearchVO searchVO) {
        if (!AuthorizedConstants.ROLE_ADMIN.equals(searchVO.getCheckRole())) {
            searchVO.setUserId(ThreadLocalManager.getUserId());
        }
        Page page = PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<InterviewMarkDTO> res = markDao.getInterviewMarkList(searchVO);
        if (res != null && res.size() > 0) {
            for (InterviewMarkDTO markDTO : res) {
                if (markDTO.getCreateUser().equals(ThreadLocalManager.getUserId())) {
                    markDTO.setUserName("我");
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * 修改标注
     * @param updateVO
     * @return
     */
    public int updateInterviewMark(InterviewMarkUpdateVO updateVO) {
        Date now = new Date();
        // 判断是否新增编码
        Integer codeId = updateVO.getCodeId();
        if (codeId == null) {
            BaseProjectInterviewCode interviewCode = new BaseProjectInterviewCode();
            interviewCode.setProjectId(updateVO.getProjectId());
            interviewCode.setName(updateVO.getCodeName());
            interviewCode.setType(ProjectConstants.CODE_WORKER);
            interviewCode.setCreateUser(ThreadLocalManager.getUserId());
            interviewCode.setCreateTime(now);
            codeDao.insertSelective(interviewCode);
            codeId = interviewCode.getId();
        }
        BaseProjectInterviewMark interviewMark = new BaseProjectInterviewMark();
        interviewMark.setId(updateVO.getId());
        interviewMark.setCodeId(codeId);
        interviewMark.setNote(updateVO.getNote());
        interviewMark.setCreateTime(now);
        return markDao.updateByPrimaryKeySelective(interviewMark);
    }

    /**
     * 删除标注
     * @param delVO
     * @return
     */
    public int deleteInterviewMark(InterviewMarkDelVO delVO) {
        Example example = new Example(BaseProjectInterviewMark.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", delVO.getIds());
        return markDao.deleteByExample(example);
    }

    /**
     * 词频统计
     * @param statisticsVO
     * @return
     */
    public Map<String, Integer> runWordStatistics(WordStatisticsVO statisticsVO) {
        Map<String, Integer> res = new HashMap<>();
        // 获取访谈文本
        List<String> contents = paragraphDao.getParagraphsBySampleIds(statisticsVO.getProjectId(), statisticsVO.getSampleIds());
        if (contents != null && contents.size() > 0) {
            if (statisticsVO.getCustomWords() != null && statisticsVO.getCustomWords().size() > 0) {
                for (String d : statisticsVO.getCustomWords()) {
                    DicLibrary.insert(DicLibrary.DEFAULT, d, "udl", Integer.MAX_VALUE);
                }
            }
            for (String content : contents) {
                Result parse = ToAnalysis.parse(content);
                List<Term> terms = parse.getTerms();
                String name;
                if (terms != null && terms.size() != 0) {
                    for (Term term : terms) {
                        if (statisticsVO.getNatures() != null
                                && !statisticsVO.getNatures().contains(term.getNatureStr())) continue;
                        name = term.getName(); // + "_" + term.getNatureStr();
                        if (res.containsKey(name)) {
                            res.put(name, Integer.parseInt(res.get(name).toString()) + 1);
                        } else {
                            res.put(name, 1);
                        }
                    }
                }
            }
            res = this.sortDescend(res);
        }
        return res;
    }

    // Map的value值降序排序
    private <K, V extends Comparable<? super V>> Map<K, V> sortDescend(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });

        Map<K, V> returnMap = new LinkedHashMap<K, V>();
        int i = 1;
        for (Map.Entry<K, V> entry : list) {
            if (i == 100) break;
            returnMap.put(entry.getKey(), entry.getValue());
            i++;
        }
        return returnMap;
    }

    /**
     * 获取编码图谱数据
     * @return
     */
    public Map<String, Object> getCodeViewData(CodeViewVO codeViewVO) {
        Map<String, Object> res = new HashMap<>();
        List<CodeViewDTO> data = markDao.getCodeViewData(codeViewVO);
        Map<String, Integer> sourceMap = new HashMap<>();
        Map<String, Integer> sourceCodeMap = new HashMap<>();
        List<Map<String, Object>> sourceList = new ArrayList<>();
        if (data != null && data.size() > 0) {
            for (CodeViewDTO codeViewDTO : data) {
                if (!sourceMap.containsKey(codeViewDTO.getSource())) {
                    sourceMap.put(codeViewDTO.getSource(), 0);
                }
                if (sourceCodeMap.containsKey(codeViewDTO.getTarget())) {
                    sourceCodeMap.put(codeViewDTO.getTarget(), sourceCodeMap.get(codeViewDTO.getTarget()) + codeViewDTO.getValue());
                } else {
                    sourceCodeMap.put(codeViewDTO.getTarget(), codeViewDTO.getValue());
                }
            }
            Map<String, Object> temp;
            for (Map.Entry<String, Integer> m : sourceMap.entrySet()) {
                temp = new HashMap<>();
                temp.put("name", m.getKey());
                temp.put("type", 1);
                temp.put("value", m.getValue());
                sourceList.add(temp);
            }
            for (Map.Entry<String, Integer> m : sourceCodeMap.entrySet()) {
                temp = new HashMap<>();
                temp.put("name", m.getKey());
                temp.put("type", 2);
                temp.put("value", m.getValue());
                sourceList.add(temp);
            }
        }
        res.put("nodes", sourceList);
        res.put("edges", data);
        return res;
    }

}
