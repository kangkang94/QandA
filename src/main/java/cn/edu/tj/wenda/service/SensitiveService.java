package cn.edu.tj.wenda.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kang on 2017/11/20.
 */
@Service
public class SensitiveService implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private static final String REPLACEMENT = "***";

    //初始化函数，根据resources构建trie树
    @Override
    public void afterPropertiesSet() throws Exception {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String lineText;
            while ((lineText = br.readLine()) != null){
                lineText.trim();
                addWord(lineText);
            }


        }catch (Exception e){
            logger.error("读取文件失败" + e.getMessage());
        }finally {
            br.close();
            isr.close();
            is.close();
        }
    }

    //一个Trie树的结构
    private class TrieNode{
        //是否是敏感词的结尾
        private boolean end = false;
        //存储子节点,key为关键字，value为节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();


        void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

        boolean isKeywordEnd(){
            return end;
        }
        void setKeywordEnd(boolean end){
            this.end=end;
        }
    }

    //新建根节点，且key为空
    private TrieNode rootNode = new TrieNode();

    //判定字符c是否为符号
    private boolean isSymbol(char c){
        int ic = (int)c;
        //不是英文字母 且 不是东亚文字（0x2E80-0x9FFF之间）
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    //过滤敏感词
    public String filter(String text){
        if(StringUtils.isBlank(text))
            return text;

        //存储过滤后的字符串
        StringBuilder result = new StringBuilder();

        int begin = 0;//开始比较的位置
        int position = 0;//当前比较的位置
        TrieNode tempNode = rootNode;

        while (position < text.length()){
            char c = text.charAt(position);
            //遇到空格等符号跳过
            if(isSymbol(c)){
                //如果还没匹配到敏感词，则直接加入result
                if(tempNode == rootNode){
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            //下一个节点没有匹配到
            if(tempNode == null){
                //以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                //从begin的下一个字符开始，继续匹配
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                //匹配到敏感词
                result.append(REPLACEMENT);
                //从begin到position之间都被替换
                position = position + 1;
                begin = position;
                tempNode = rootNode;

            }else {
                position++;
            }

        }
        result.append(text.substring(begin));

        return result.toString();
    }
    //添加敏感词到trie树
    private void addWord(String text){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < text.length(); i++) {
            Character c = text.charAt(i);
            //跳过空格
            if (isSymbol(c))
                continue;

            TrieNode node = tempNode.getSubNode(c);

            if (node == null){
                node = new TrieNode();
                tempNode.addSubNode(c,node);

            }
            tempNode = node;

            if (i == text.length() - 1){
                //设置结束标志
                tempNode.setKeywordEnd(true);
            }

        }
    }



}
