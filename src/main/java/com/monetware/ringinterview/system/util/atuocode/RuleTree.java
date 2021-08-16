package com.monetware.ringinterview.system.util.atuocode;

import java.util.*;

/**
 * @ClassName RuleTree
 * @Disciption 规则匹配二叉树。现在规则字符串支持括号、&（and）、|（or）、!（not）
 * @Author Yuka
 * @Date 2019/3/25 15:25
 * @Version 1.0
 **/

public class RuleTree {



	String name = "";
	RuleTreeNode root = null;
	float max = 1f;



	public RuleTree(String name, String rule){
		List<String> postFix = RuleTree.toPostFix(rule);
		this.name = name;
		Stack<RuleTreeNode> stack = new Stack<>();

		for(String str : postFix){
			if((!str.equals("|")) &&  (!str.equals("&"))){
				RuleTreeNode treeNode = new RuleTreeNode(str);
				stack.push(treeNode);
			}
			else{
				RuleTreeNode left = stack.pop();
				RuleTreeNode right = stack.pop();
				RuleTreeNode treeNode = new RuleTreeNode(str);
				treeNode.setLeftChild(left);
				left.setParent(treeNode);
				treeNode.setRightChild(right);
				right.setParent(treeNode);
				stack.push(treeNode);
			}
		}
		root = stack.pop();

	}

	public float getMax() {
		return max;
	}
	public String getName() {
		return name;
	}

	/**
	 * 重置所有节点为未激活的状态
	 */
	public void reset(){
		root.inactiveAll();
	}


	/**
	 * 用于将标准的中缀规则表达式转换为生成规则二叉树所需的后缀表达式
	 * @param inFix 规则中缀表达式，支持括号、|、&，形如"互联网教育&(互联网|教育)"
	 * @return
	 */
	public static List<String> toPostFix(String inFix){
		List<String> queue = new ArrayList<>();
		List<Character> stack = new ArrayList<>();

		char[] charArray = inFix.trim().toCharArray();
		String standard = "()|&";
		char chTemp = '@';

		int length = 0;

		for(int i = 0; i < charArray.length; i++){
			chTemp = charArray[i];
			if(standard.indexOf(chTemp) == -1){
				length++;
			}
			else {
				if(length > 0){
					queue.add(String.valueOf(Arrays.copyOfRange(charArray, i - length, i)));
					length = 0;
				}
				if(chTemp == '('){
					stack.add(chTemp);
					continue;
				}
				if(chTemp != ')'){
					stack.add(chTemp);
					continue;
				}
				else{
					int size = stack.size() - 1;
					while(stack.get(size) != '('){
						char stackTop = stack.remove(size);
						size --;
						queue.add(String.valueOf(stackTop));
					}
					stack.remove(size);
				}


			}

			if(i == charArray.length - 1){
				if(length > 0){
					queue.add(String.valueOf(Arrays.copyOfRange(charArray, i - length + 1, i + 1)));
				}
				int size = stack.size() - 1;
				while (size >= 0){
					queue.add(String.valueOf(stack.remove(size)));
					size--;
				}
			}
		}
		return queue;
	}

	public void setScore(String value, float score) throws Exception {
		root.setScore(value, score);
		root.activeAll();
		max = root.getScore();
		if(max - 0 <= 0.00001 || max < 0){
			throw new Exception("规则树节点分值错误。请检查是否赋予了负分值，抑或对不存在的节点进行了赋值。");
		}
		reset();

	}

	public float getScore(){
		return root.getScore();
	}

	public void active(String nodeValue){
		root.active(nodeValue);
	}

	public boolean actived(){
		return root.isActived();
	}

	public void keyWordSet(Set<String> set){
		root.keyWordSet(set);
	}


}
