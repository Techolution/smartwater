package com.techolution.mauritius.smartwater.supply.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.supply.InfluxProperties;

@Component
public class InfluxDBUtils {

	@Autowired
    InfluxProperties influxProperties;
	
	//private static String INFLUX_CONNECTION_STRING="http://52.170.92.62:8086";
	/*private static String INFLUX_USERNAME="root";
	private static String INFLUX_PWD="root";*/
	
	
	private static String DBNAME = "mauritius_smartwater";
	//private static String QUERY_TEMPLATE=INFLUX_CONNECTION_STRING+"/query?db="+DBNAME+"&q=";
	//private static String QUERY_TEMPLATE=INFLUX_CONNECTION_STRING+"/query";
	public  JSONObject executeQuery(String query) throws ClientProtocolException, IOException, JSONException, URISyntaxException{
		
	//	CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String queryTemplate=influxProperties.getUrl()+"/query";
		URIBuilder ub = new URIBuilder(queryTemplate);
		ub.addParameter("db", DBNAME);
		ub.addParameter("q", query);
		
		/*String queryTemplate=influxProperties.getUrl()+"/query";
		String finalquery=queryTemplate+query;*/
		//Apche commons API always gave error with URL though URL was working fine independently
		/*HttpGet request=new HttpGet(finalquery);
		CloseableHttpResponse response=httpClient.execute(request);
		InputStream stream=response.getEntity().getContent();*/
		
		//URL obj = new URL(finalquery);
		URL obj = new URL(ub.toString());
		
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		//con.connect();
		//con
		
		InputStream stream=con.getInputStream();
		//System.out.println("Content is:"+con.getContent().getClass());
		
		String responseString=convertStreamToString(stream);
		JSONObject jsonObject=new JSONObject(responseString);
		con.disconnect();
		return jsonObject;
		//return null;
	}
	
	
	/*
	 * While multiple options are available, using the below option due to the performance benefits
	 * againsa options in JDK8 and IOUtils as mentioned in 
	 * https://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
	 */
	private  String convertStreamToString(InputStream inputStream) throws IOException{
		
		/*final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(inputStream, "UTF-8");
		for (; ; ) {
		    int rsz = in.read(buffer, 0, buffer.length);
		    if (rsz < 0)
		        break;
		    out.append(buffer, 0, rsz);
		}
		return out.toString();*/
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(inputStream));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
}
