package com.monetware.ringinterview.system.util.atuocode;

import javafx.util.Pair;

import java.util.*;

/**
 * @author Simo
 * @date 2020-02-28
 */
public class AutoCodeUtil {


    /*
		逐句对text进行分析，记录下符合特定规则语句rule的句子在原文中的起始点和该句子的长度。
		@ parameter
			text：将要进行分析的语料。无需预先进行分句，但需要注意不要包含符号#，否则可能引起分句的不正确。
			rule：规则语句。使用&、|、()符号来进行逻辑描述，例如"(互联网&教育)|互联网+教育"表示该规则匹配“或同时出现‘互联网’和‘教育’，或出现‘互联网+教育’的句子。”
		@ return
			List<Pair<Long, Integer>>：一个列表，记录了所有符合特定规则的语句在原文（text）中的起始点（Long）和该句子的长度（Integer）。
	 */
    public static List<Pair<Long, Integer>> tag(String text, String rule){
        text = text.replaceAll("。", "。#").replaceAll("！", "！#")
                .replaceAll("\\.", ".#").replaceAll("!", "!#");
//		text.replaceAll("（", "#（");
//		text.replaceAll("）", "）#");

        String[] splitResult = text.split("#");
        String[] keyWords = rule.split("&|\\||!|\\(|\\)");
        Set<String> keyWordsSet = new HashSet<>();
        Collections.addAll(keyWordsSet, keyWords);
        keyWordsSet.remove("");
        RuleTree rt = new RuleTree(rule, rule);
        List<Pair<Long, Integer>> tagged = new ArrayList<>();
        long start = 0;
        for (String sentence : splitResult) {
            rt.reset();
            for (String keyWord : keyWordsSet) {
                if (sentence.contains(keyWord)) {
                    rt.active(keyWord);
                }
            }
            if (rt.actived()) {
                tagged.add(new Pair<Long, Integer>(start, sentence.length()));
            }
            start += sentence.length();
        }
        return tagged;
    }

    public static void main(String args[]){
        String test = "（原标题：新冠肺炎遗体解剖已完成11例，取得重要发现）从2月16日至24日，华中科技大学团队完成了9例新冠肺炎遗体病理解剖，且已取得重要发现。该校法医学院刘良教授介绍，与临床专家沟通分析后，会对这种新型病毒有更深入的认识。华中科技大学病理团队由法医学院刘良教授以及附属同济医院病理科王国平教授组成，已进行9例遗体解剖，前3例病理研究结果已完成。20多位团队成员正加班加点，力求以最快速度完成病理研究。遗体解剖是揭开病毒真面目的最直接手段。王国平教授解释，遗体解剖是病理学研究的重要方法之一。通过病理学变化和临床变化对比研究，揭示发病机制，分析死亡原因，总结诊疗经验，提高临床救治效果和防控效果。";
        String rule = "(解剖&发现)|深入的认识|防控效果";
        List<Pair<Long, Integer>> result = AutoCodeUtil.tag(test, rule);
        System.out.println(result);
    }

}
