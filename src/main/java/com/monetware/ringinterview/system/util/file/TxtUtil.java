package com.monetware.ringinterview.system.util.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Cleanup;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-02-28
 */
public class TxtUtil {

    public static Map<String, String> teamHeadMap = new HashMap<>();

    public static Map<String, String> sampleHeadMap = new HashMap<>();

    public static Map<String, String> interviewHeadMap = new HashMap<>();

    public static Map<String, String> itwtHeadMap = new HashMap<>();


    static {
        // 定义团队导出所有标题
        teamHeadMap.put("id", "团队成员ID");
        teamHeadMap.put("userName", "用户名");
        teamHeadMap.put("email", "邮箱");
        teamHeadMap.put("telephone", "手机");
        teamHeadMap.put("sampleNum", "样本数量");
        teamHeadMap.put("role", "角色");
        teamHeadMap.put("joinDate", "加入日期");
        teamHeadMap.put("validDate", "授权日期");

        // 定义样本导出所有标题
        sampleHeadMap.put("id", "样本ID");
        sampleHeadMap.put("name", "样本名称");
        sampleHeadMap.put("code", "样本编号");
        sampleHeadMap.put("gender", "性别");
        sampleHeadMap.put("organization", "单位");
        sampleHeadMap.put("birth", "出生日期");
        sampleHeadMap.put("age", "年龄");
        sampleHeadMap.put("mobile", "电话");
        sampleHeadMap.put("weixin", "微信");
        sampleHeadMap.put("phone", "手机");
        sampleHeadMap.put("qq", "qq");
        sampleHeadMap.put("email", "邮箱");
        sampleHeadMap.put("weibo", "微博");
        sampleHeadMap.put("province", "省");
        sampleHeadMap.put("city", "市");
        sampleHeadMap.put("district", "区/县");
        sampleHeadMap.put("town", "街道/镇");
        sampleHeadMap.put("address", "详细地址");
        sampleHeadMap.put("marriageStatus", "婚姻状况");
        sampleHeadMap.put("education", "学历");
        sampleHeadMap.put("politicalStatus", "政治面貌");
        sampleHeadMap.put("nationality", "国籍");
        sampleHeadMap.put("profession", "职业");
        sampleHeadMap.put("position", "职务");
        sampleHeadMap.put("placeOfBirth", "籍贯");
        sampleHeadMap.put("religion", "宗教信仰");
        sampleHeadMap.put("language", "语言");
        sampleHeadMap.put("dialects", "方言");
        sampleHeadMap.put("description", "备注");
        sampleHeadMap.put("detail", "详细介绍");
        sampleHeadMap.put("custom1", "自定义1");
        sampleHeadMap.put("custom2", "自定义2");
        sampleHeadMap.put("custom3", "自定义3");
        sampleHeadMap.put("custom4", "自定义4");
        sampleHeadMap.put("custom5", "自定义5");
        sampleHeadMap.put("custom6", "自定义6");
        sampleHeadMap.put("custom7", "自定义7");
        sampleHeadMap.put("custom8", "自定义8");
        sampleHeadMap.put("custom9", "自定义9");
        sampleHeadMap.put("custom10", "自定义10");
        sampleHeadMap.put("custom11", "自定义11");
        sampleHeadMap.put("custom12", "自定义12");
        sampleHeadMap.put("custom13", "自定义13");
        sampleHeadMap.put("custom14", "自定义14");
        sampleHeadMap.put("custom15", "自定义15");

        //访谈导出所有标题
        interviewHeadMap.put("id", "访谈ID");
        interviewHeadMap.put("sample", "研究对象");
        interviewHeadMap.put("manager", "负责人");
        interviewHeadMap.put("assistant", "协助者");
        interviewHeadMap.put("interviewTimeLen", "访谈时长");
        interviewHeadMap.put("fileSize", "信息量");
        interviewHeadMap.put("planStartTime", "计划时间");
        interviewHeadMap.put("beginTime", "执行时间");
        interviewHeadMap.put("docEndTime", "整理时间");
        interviewHeadMap.put("auditTime", "审核时间");

        // 对话文本标题
        itwtHeadMap.put("speakName", "讲话者");
        itwtHeadMap.put("paragraph", "文本内容");
    }

    /**
     * 写入csv
     *
     * @param type
     * @param properties
     * @param datas
     * @param path
     * @return
     * @throws Exception
     */
    public static Map<String, Object> createTextFile(String type, List<String> properties, JSONArray datas, String path) throws Exception {
        Map<String, Object> res = new HashMap<>();
        Date now = new Date();
        String fileName;
        if ("ITWT".equals(type)) {
            // 对话文本使用录音名
            fileName = datas.getJSONObject(0).getString("fileName") + ".txt";
        } else {
            fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(now) + ".txt";
        }
        //创建text文件
        File file = new File(path + fileName);
        //获取txt文件父文件夹
        File parentFlie = file.getParentFile();
        //判断父文件是否存在，不存在新建
        if (parentFlie != null && !parentFlie.exists()) {
            parentFlie.mkdirs();
        }
        file.createNewFile();
        @Cleanup
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"), 1024);

        // 整理数据
        List<List<String>> dataList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            List<String> temp = new ArrayList<>();
            JSONObject object = datas.getJSONObject(i);
            for (String s : properties) {
                switch (type) {
                    case "TEAM":
                        temp.add(ExcelUtil.teamHeadMap.get(s) + "：" + object.getString(s));
                        break;
                    case "SAMPLE":
                        temp.add(ExcelUtil.sampleHeadMap.get(s) + "：" + object.getString(s));
                        break;
                    case "ITW":
                        temp.add(ExcelUtil.interviewHeadMap.get(s) + "：" + object.getString(s));
                        break;
                    case "ITWT":
                        temp.add(ExcelUtil.itwtHeadMap.get(s) + "：" + object.getString(s));
                        break;
                }
            }
            dataList.add(temp);
        }

        for (List<String> data : dataList) {
            for (String s : data) {
                bw.write(s);
                bw.write("\r\n");
            }
            bw.write("\r\n");
        }
        res.put("fileName", fileName);
        res.put("fileSize", new File(path + fileName).length());
        return res;
    }


}
