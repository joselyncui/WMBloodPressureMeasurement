package com.lichkin.entity;

import com.google.gson.annotations.Expose;

public class RegisterEntity {

	@Expose
	public String userName;
	@Expose
	public String cellphone;
	@Expose
	public String userCard;
	@Expose
	public String pwd;
	@Expose
	public String securityCode;

	public RegisterEntity() {
		super();
	}

	public RegisterEntity(String userName, String cellPhone, String userCard,
			String pwd, String securityCode) {
		super();
		this.userName = userName;
		this.cellphone = cellPhone;
		this.userCard = userCard;
		this.pwd = pwd;
		this.securityCode = securityCode;
	}

}
