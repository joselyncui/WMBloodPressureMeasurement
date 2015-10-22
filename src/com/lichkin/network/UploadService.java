package com.lichkin.network;

import retrofit.http.Body;
import retrofit.http.POST;

import com.lichkin.entity.BPResult;
import com.lichkin.entity.BSResult;
import com.lichkin.entity.FHResult;
import com.lichkin.entity.RequestEntity;
import com.lichkin.entity.Response;

public interface UploadService {

	@POST("/upload/bloodPressure/2J.do")
	Response uploadBloodpressure(@Body RequestEntity<BPResult> bpResults);

	@POST("/upload/fetalHeart/2J.do")
	Response uploadFetalHeart(@Body RequestEntity<FHResult> fhResults);

	@POST("/upload/bloodGlucose/2J.do")
	Response uploadBloodGlucose(@Body RequestEntity<BSResult> bsResults);

}
