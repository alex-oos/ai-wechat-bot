package com.wechat.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alex
 * @since 2025/3/18 10:50
 * <p>
 * 分词匹配：使用HanLP分词，进行关键词匹配
 * </p>
 */
public class WordParticipleMatch {


    /**
     * 基于分词检查句子是否包含所有关键词
     *
     * @param sentence         待检测的句子
     * @param requiredKeywords 必须包含的关键词集合
     * @return 包含两个关键字以上才会触发
     */
    public static boolean containsPartKeywords(String sentence, Set<String> requiredKeywords, int minKeywordCount) {

        if (requiredKeywords == null || requiredKeywords.isEmpty()) {
            return true;
        }
        Set<String> result = new HashSet<String>();

        // 分词并提取词语
        Set<String> words = new HashSet<>();
        for (Term term : HanLP.segment(sentence)) {
            words.add(term.word);
        }
        // 两个set 取交集
        // 检查是否包含所有关键词
        result.addAll(requiredKeywords);
        result.retainAll(words);
        return result.size() >= minKeywordCount;
    }

    public static boolean containsPartKeywords(String sentence, List<String> requiredKeywords, int minKeywordCount) {

        if (requiredKeywords == null || requiredKeywords.isEmpty()) {
            return true;
        }
        Set<String> requiredKeywordsSet = new HashSet<>(requiredKeywords);
        Set<String> result = new HashSet<String>();

        // 分词并提取词语
        Set<String> words = new HashSet<>();
        for (Term term : HanLP.segment(sentence)) {
            words.add(term.word);
        }
        // 两个set 取交集
        // 检查是否包含所有关键词
        result.addAll(requiredKeywordsSet);
        result.retainAll(words);
        return result.size() >= minKeywordCount;
    }


    public static void main(String[] args) {

        //Set<String> keywords = new HashSet<>(Arrays.asList("助理", "说明书", "AI"));
        List<String> keywords =Arrays.asList("助理", "说明书", "AI");
        String test1 = "AI助理的说明书已发布"; // 全部包含 → true
        String test2 = "请下载说明书和教程";  // 缺少"AI" → false

        System.out.println(containsPartKeywords(test1, keywords, 2)); // true
        System.out.println(containsPartKeywords(test2, keywords, 1)); // false
    }

}
