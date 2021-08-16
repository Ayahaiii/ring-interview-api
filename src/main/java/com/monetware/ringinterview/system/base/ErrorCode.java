package com.monetware.ringinterview.system.base;


public enum ErrorCode {


	ROLE_WITHOUT(1000,"您没有操作权限"),

	//Token验证
    TOKEN_WITHOUT(10001,"无效的凭证"),
	TOKEN_INVALID(10002,"TOKEN失效"),

	UPLOAD_FAIL(50004,"上传失败"),
	//用户相关
    EMAIL_HAVE_BEEN_USED(20001,"邮箱已被占用"),
    PHONE_HAVE_BEEN_USED(20002,"手机已被占用"),
    NAME_HAVE_BEEN_USED(20003,"昵称已被占用"),
	SMS_CODE_SEND_FAIL(20004,"发送短信验证码失败"),
	SMS_CODE_FAILURE(20005,"验证码已过期"),
    SMS_CODE_WRONG(20006,"验证码错误"),
    USER_LOGIN_FAIL(20007,"登录错误，请输入正确账号密码"),
    EMAIL_SEND_FAIL(20008,"邮件发送失败"),
    OLD_PASSWORD_WRONG(20009,"旧密码错误"),

    //自定义错误
	CUSTOM_MSG(10000,""),

	CODE_HAVE_BEEN_USED(30002, "样本编号被占用"),

	PARAGRAPH_EDIT_FAIL(30000, "访谈审核完成,无法编辑文本"),
	FILE_UPLOAD_FAIL(30001, "访谈审核完成,无法上传录音和附件"),

	//余额
	AMOUNT_NOT_ARRIVE(40001,"余额不足");



	private String msg;
    private int code;
    
    ErrorCode(int code,String msg)
    {
    	this.code=code;
        this.msg=msg;
    }

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}

