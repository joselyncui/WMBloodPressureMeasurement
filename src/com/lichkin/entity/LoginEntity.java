package com.lichkin.entity;

import com.google.gson.annotations.Expose;

public class LoginEntity {

	@Expose
	public String loginName;
	@Expose
	public String pwd;

	public LoginEntity() {
		super();
	}

	public LoginEntity(String loginName, String pwd) {
		super();
		this.loginName = loginName;
		this.pwd = pwd;
	}

}
