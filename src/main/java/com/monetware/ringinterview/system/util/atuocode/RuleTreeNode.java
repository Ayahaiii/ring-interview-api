package com.monetware.ringinterview.system.util.atuocode;

import java.util.Set;

/**
 * @ClassName JsonNode
 * @Disciption TODO
 * @Author Yuka
 * @Date 2019/3/25 15:19
 * @Version 1.0
 **/

public class RuleTreeNode {
	RuleTreeNode parent = null;
	RuleTreeNode leftChild = null;
	RuleTreeNode rightChild = null;
	String value = "";
	boolean actived = false;
	boolean reversed = false;
	float score = 1f;





	public RuleTreeNode(String value, float score) {
		this.value = value.startsWith("!")? value.substring(1, value.length()): value;
		this.score = score;
		this.reversed = value.startsWith("!");
		this.actived = reversed;
	}


	public RuleTreeNode(String value) {
		this.value = value.startsWith("!")? value.substring(1, value.length()): value;
		this.score = 0f;
		this.reversed = value.startsWith("!");
		this.actived = reversed;
	}

	public float getScore() {
		if(!actived){
			return 0;
		}
		if(rightChild == null && leftChild == null){
			return score;
		}
		else{
			float leftScore = leftChild == null? 0f : leftChild.getScore();
			float rightScore = rightChild == null? 0f : rightChild.getScore();
			if(this.value.equals("&")){
				return leftScore + rightScore;
			}
			else {
				return leftScore > rightScore? leftScore : rightScore;
			}
		}

	}

	public void setScore(String value, float score) {
		if(this.value.equals("")){
			return;
		}
		if(this.value.equals(value)){
			this.score = score;
		}
		else{
			if(leftChild != null){
				leftChild.setScore(value, score);
			}
			if(rightChild != null){
				rightChild.setScore(value, score);
			}
		}
	}

	public String getValue() {
		return value;
	}



	public RuleTreeNode getParent() {
		return parent;
	}

	public void setParent(RuleTreeNode parent) {
		this.parent = parent;
	}

	public RuleTreeNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(RuleTreeNode leftChild) {
		this.leftChild = leftChild;
	}

	public RuleTreeNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(RuleTreeNode rightChild) {
		this.rightChild = rightChild;
	}

	public boolean isActived() {
		return actived;
	}

	public void setActived(boolean actived) {
		this.actived = reversed != actived;
		if(parent != null)parent.checkActived();
	}

	public void checkActived(){
		if(value.equals("|")){
			setActived(leftChild.isActived() || rightChild.isActived());
		}
		else if(value.equals("&")){
			setActived(leftChild.isActived() && rightChild.isActived());
		}
	}

	public void active(String value){
		if(this.value.equals(value)){
			setActived(true);
		}
		else{
			if(leftChild != null)leftChild.active(value);
			if(rightChild != null)rightChild.active(value);
		}
	}

	public void inactiveAll(){
		this.actived = false;
		if(leftChild != null)leftChild.inactiveAll();
		if(rightChild != null)rightChild.inactiveAll();
	}

	public void activeAll(){
		this.actived = true;
		if(leftChild != null)leftChild.activeAll();
		if(rightChild != null)rightChild.activeAll();
	}

	public void keyWordSet(Set<String> set){
		if(!value.equals("|") && !value.equals("&") && !value.equals("")){
			set.add(value);
		}
		if(leftChild != null){
			leftChild.keyWordSet(set);
		}
		if(rightChild != null){
			rightChild.keyWordSet(set);
		}

	}
}
