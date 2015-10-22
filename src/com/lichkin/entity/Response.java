package com.lichkin.entity;

import com.google.gson.annotations.Expose;

public class Response {

	@Expose
	public Datas datas;
	@Expose
	public String backUrl;
	@Expose
	public String requestTime;
	@Expose
	public String requestId;
	@Expose
	public int code;
	@Expose
	public String responseTime;
	@Expose
	public String info;

}
