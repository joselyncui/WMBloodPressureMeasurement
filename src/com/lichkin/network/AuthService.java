package com.lichkin.network;

import retrofit.http.Body;
import retrofit.http.POST;

import com.lichkin.entity.LoginEntity;
import com.lichkin.entity.MessageEntity;
import com.lichkin.entity.RegisterEntity;
import com.lichkin.entity.RequestEntity;
import com.lichkin.entity.Response;

public interface AuthService {
	@POST("/register/2J.do")
	Response register(@Body RequestEntity<RegisterEntity> registerEntity);

	@POST("/login/2J.do")
	Response login(@Body RequestEntity<LoginEntity> registerEntity);

	@POST("/getRegisterSc/2J.do")
	Response sendMessage(@Body MessageEntity messageEntity);

}
