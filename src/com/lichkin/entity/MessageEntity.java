package com.lichkin.entity;

import com.google.gson.annotations.Expose;

public class MessageEntity {

	@Expose
	public String callerName;
	@Expose
	public String password;
	@Expose
	public String cellphone;

	public MessageEntity() {
		super();
	}

	public MessageEntity(String callerName, String password, String cellphone) {
		super();
		this.callerName = callerName;
		this.password = password;
		this.cellphone = cellphone;
	}

}
