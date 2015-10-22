package com.lichkin.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class RequestEntity<T> implements IUploadEntity {

	public final static int TYPE_BP = 0;
	public final static int TYPE_BS = 1;
	public final static int TYPE_FH = 2;

	@Expose
	public String callerName;
	@Expose
	public String password;
	@Expose
	public List<T> requestDatas;

	public RequestEntity() {
		super();
	}

	public RequestEntity(String callerName, String password, T requestData) {
		this.callerName = callerName;
		this.password = password;
		List<T> requestDatas = new ArrayList<>();
		requestDatas.add(requestData);
		this.requestDatas = requestDatas;
	}

	public RequestEntity(String callerName, String password,
			List<T> requestDatas) {
		super();
		this.callerName = callerName;
		this.password = password;
		this.requestDatas = requestDatas;
	}

}
