package com.lichkin.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HttpUtils {
	
	public static final String base_url = "http://120.26.91.90:38050/V0100";
//	public static final String base_url = "http://192.168.1.107:8080/V0100";
	public static final String reg_url = "/register/2J.do";
	
	public static String post(JSONObject data, String uri) {
		  uri=uri.replace(" ", "%20");
		  String result = "";
		  try {
		   HttpClient client = new DefaultHttpClient();
		   client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30*1000);
		   HttpPost httpPost = new HttpPost(uri);
		   httpPost.addHeader("charset", "gbk");  
		   httpPost.addHeader("Accept-Charset","gbk");
		   StringEntity entity = new StringEntity(data.toString(), "gbk");
		   entity.setContentType("application/json");
		   httpPost.setEntity(entity);
		   HttpResponse response = client.execute(httpPost);
		   result = EntityUtils.toString(response.getEntity(), "utf-8");
		  } catch (Exception e) {
		   return "-1";
		  }
		  return result;
		 }

}
