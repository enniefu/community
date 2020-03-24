package com.ennie.community.Util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {



    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //敏感词被替换的常量
    private static final String REPLACEMENT = "***";

    //根据敏感词，初始化前缀树
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while((keyword = reader.readLine())!=null){
                this.addKeyword(keyword);
            }


        } catch (Exception e) {
            logger.error("加载敏感词文件失败:"+e.getMessage());
        }
    }


    //将一个敏感词添加到前缀树当中去
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i=0; i <keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if(subNode==null){
                //初始化
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //让指针指向子节点，进入下一层循环
            tempNode = subNode;

            //设置结束的标识
            if (i == keyword.length() -1){
                tempNode.setKeyWordEnd(true);
            }
        }

    }

    /**
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                // 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                if (position < text.length() - 1) {
                    position++;
                }
            }
        }

        // 将最后一批字符计入结果
        //sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }




    private class TrieNode{

        //是否是敏感词
        private boolean isKeyWordEnd = false;

        //子节点 k:下级节点字符   v:下级节点
        private Map<Character, TrieNode> subnodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character c, TrieNode node){
            subnodes.put(c,node);
        }

        //获取子节点
        public TrieNode getSubNode(Character c){
            return subnodes.get(c);
        }







    }

}
