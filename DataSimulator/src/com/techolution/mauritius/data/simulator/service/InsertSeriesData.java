package com.techolution.mauritius.data.simulator.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.techolution.mauritus.data.KeyValue;

public class InsertSeriesData {
	
	
	protected void sendData(String name,Date date,List<KeyValue> tags,List<KeyValue> values){
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String dateVal=myFormat.format(date);
		JSONObject json = new JSONObject();
		
		
		json.put("name", name);    
		json.put("timestamp", dateVal);
		
		
		json.put("tags", tags);
		json.put("values", values);

		System.out.println("JSON is:"+json.toString());
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		try {
		    HttpPost request = new HttpPost("http://localhost:8085/insert/data/");
		    StringEntity params = new StringEntity(json.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    httpClient.execute(request);
		// handle response here...
		} catch (Exception ex) {
		    // handle exception here
		} finally {
		    try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


}
