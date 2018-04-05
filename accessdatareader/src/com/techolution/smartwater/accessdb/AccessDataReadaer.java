package com.techolution.smartwater.accessdb;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;





public class AccessDataReadaer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AccessDataReadaer accessDataReadaer=new AccessDataReadaer();
		try {
			accessDataReadaer.processNewlyInsertedData(args[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public void processNewlyInsertedData(String dbPath) throws IOException, ClassNotFoundException, SQLException{
		//Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		//String database = "jdbc:odbc:Driver={Microsoft Access Driver ("+dbPath+")};DBQ=myDB.mdb;";
		//Class.forName("net.ucanaccess.jdbc.UcanaccessDriver"); 
		String database = "jdbc:odbc:Driver={Microsoft Access Driver ("+dbPath+")};";

		
		String urlpath="jdbc:ucanaccess:"+dbPath+"";
		Connection conn=DriverManager.getConnection(urlpath);
		//Connection conn = DriverManager.getConnection(database, "", "");


		Statement st=conn.createStatement();
		String detailedQuery="SELECT ChData.UnitID, DistName.DistName, ChData.RF1, ChData.TF1, ChData.SampleTime, DATEDIFF(\"n\",ChData.SampleTime,Now()) AS timediffinmutes FROM ChData LEFT JOIN DistName ON ChData.UnitID = DistName.UnitID WHERE DATEDIFF(\"n\",ChData.SampleTime,Now()) <31 ORDER BY ChData.SampleTime";
		//ResultSet rs=st.executeQuery("select * from last30MinutesData");
		ResultSet rs=st.executeQuery(detailedQuery);
		CloseableHttpClient httpClient = null;
		while(rs.next()){
			try {
				 httpClient = HttpClientBuilder.create().build();
				System.out.println("RFQ:"+rs.getDouble("RF1"));
				System.out.println("timestamp"+rs.getTimestamp("SampleTime"));
				JSONObject jsonObject=getRowToFlowJson(rs);
				int meterId=rs.getInt("UnitID");
				sendRequest(jsonObject,httpClient,meterId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				httpClient.close();
			}
		}
		
		
		/*Database db = new DatabaseBuilder(new File(dbPath)).open();
		List<Query> queries=db.getQueries();
		Query querytouse=null;
		//db.
		for(Query query:queries){
			if(query.getName().equalsIgnoreCase("last30MinutesData")){
				querytouse = query;
				break;
			}
		}
		
		if(querytouse!= null){
		//	querytouse.
			List<Row> rows=querytouse.getRows();
			rows.parallelStream().forEach(row -> {
				System.out.println("Row is:"+row);
				
			});
			
		}else{
			System.out.println("Query to use is null");
		}*/


		
	}
	
	private JSONObject getRowToFlowJson(ResultSet rs) throws SQLException{
		Double flow=rs.getDouble("RF1");
		Timestamp timestamp=rs.getTimestamp("SampleTime");
		
		Date date=new Date(timestamp.getTime());
		SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String dateVal=myFormat.format(date);
		System.out.println("Date val is:"+dateVal);
		JSONObject json = new JSONObject();
		json.put("flow", flow);    
		json.put("date", dateVal);
		
		return json;
	}
	
	private void sendRequest(JSONObject json,CloseableHttpClient httpClient,int meterid ) throws ClientProtocolException, IOException{
		
		
		    HttpPost request = new HttpPost("http://localhost:8085/insert/telemetry/data/"+meterid);
		    StringEntity params = new StringEntity(json.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    httpClient.execute(request);
		 
		
	}

}
