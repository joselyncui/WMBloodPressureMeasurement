package com.lichkin.entity;

import com.google.gson.annotations.Expose;

public class UserInfo {

	@Expose
	public String loginId;
	@Expose
	public String userName;
	@Expose
	public String cellphone;
	@Expose
	public String userCard;

	public UserInfo() {
		super();
	}

	public UserInfo(String loginId, String userName, String cellphone,
			String userCard) {
		super();
		this.loginId = loginId;
		this.userName = userName;
		this.cellphone = cellphone;
		this.userCard = userCard;
	}

}
