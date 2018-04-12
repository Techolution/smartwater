package com.techolution.mauritius.smartwater.reports.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.techolution.mauritius.smartwater.reports.CustomProperties;
import com.techolution.mauritius.smartwater.reports.domain.Data;
import com.techolution.mauritius.smartwater.reports.domain.SupplyStatisticsRequestData;
import com.techolution.mauritius.smartwater.reports.domain.TelemetryRequestData;
import com.techolution.mauritius.smartwater.reports.domain.TelemetryResponseData;


@Component
public class GenerateReportsService {

	
	@Autowired
    CustomProperties customProperties;
	
	@Autowired
	AzureFileService azureFileService;
	
	private Log log = LogFactory.getLog(GenerateReportsService.class);
	
	private static String COMMA= ",";
	private static String UNDERSCORE= "_";
	private static String NL= "\n";
	 public RestTemplate restTemplate() {
		    return new RestTemplate();
		}
	 
	 
	 public String generateReport(SupplyStatisticsRequestData data) throws JSONException, IOException{
		 
		 	log.info("Entering GenerateReportsService.generateReport ");
		 	SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		 	SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 	JSONObject json = new JSONObject();
			json.put("Vendor_ID", 123);
			json.put("Customer_ID", 123);
			json.put("Block_ID", 123);
			json.put("House_ID", data.getMeterId());
			json.put("Start_Time", myFormat.format(data.getStartDate()));
			json.put("End_Time", myFormat.format(data.getEndDate()));
			json.put("Location_Details", "Yes");
			json.put("Sample_Distance", "Hour");
			json.put("Sample_Distance_value",1);
			json.put("Totals", "No");
			
			
			
			
			ResponseEntity<Data[]> responseEntity = restTemplate().postForEntity(customProperties.getDataserviceurl(),json.toString(),Data[].class);
			Calendar cal=Calendar.getInstance();
			
			
			Data[] returnedobjects = (Data[])responseEntity.getBody();
			log.debug("Output size is:"+returnedobjects.length);
			
			StringBuffer fileName=new StringBuffer();
			fileName.append(data.getMeterId());
			fileName.append(GenerateReportsService.UNDERSCORE);
			fileName.append(fileNameFormat.format(cal.getTime()));
			fileName.append(".csv");
			
			
			StringBuffer filePath=new StringBuffer(customProperties.getCsvpath());
			filePath.append(fileName);
			File file=new File(filePath.toString());
			
			
			Path path = Paths.get(filePath.toString());
			 
			//Use try-with-resource to get auto-closeable writer instance
			BufferedWriter writer = Files.newBufferedWriter(path);
		
			writer.write("Hourly Cosumption Report For meter:"+data.getMeterId());
			writer.write(GenerateReportsService.NL);
			writer.write("Time");
			writer.write(GenerateReportsService.COMMA);
			writer.write("Consumption in (m^3))");
			writer.write(GenerateReportsService.NL);
			for(Data dataobj:returnedobjects){
				writer.write((String)dataobj.getName());
				writer.write(GenerateReportsService.COMMA);
				writer.write(new Double(dataobj.getValue()).toString());
				writer.write(GenerateReportsService.NL);
			}
			writer.flush();
			 writer.close();
			
			 String cloudpath=azureFileService.uploadFile(filePath.toString(), fileName.toString());
			log.info("Entering GenerateReportsService.generateReport ");
			return cloudpath;
		 
	 }
	 
	 
	 
	 public String generateConsumptionReport(SupplyStatisticsRequestData data) throws JSONException, IOException{
		 
		 	log.info("Entering GenerateReportsService.generateReport ");
		 	SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
		 	SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		 	
			
			
			String cloudpath=null;
			
			TelemetryRequestData  requestData=new TelemetryRequestData();
			int defautlVal=123;
			requestData.setBlockId(defautlVal);
			requestData.setCustomerId(defautlVal);
			requestData.setEndTime(myFormat.format(data.getEndDate()));
			requestData.setHouseId(data.getMeterId());
			requestData.setMetrics("readings");
			requestData.setSampleDistance("Hour");
			requestData.setSampleDistanceValue(1);
			requestData.setStartTime(myFormat.format(data.getStartDate()));
			requestData.setVendorId(defautlVal);
			
			
			ResponseEntity<TelemetryResponseData[]> responseEntity = restTemplate().postForEntity(customProperties.getReadingserviceurl(),requestData,TelemetryResponseData[].class);
			Calendar cal=Calendar.getInstance();
			
			TelemetryResponseData[] outputobjects =(TelemetryResponseData[])responseEntity.getBody();;
			if(outputobjects!=null && outputobjects.length >0){
				
			
				List<Data> returnedobjects = outputobjects[0].getSeries();
				log.debug("Output size is:"+returnedobjects.size());
				
				StringBuffer fileName=new StringBuffer();
				fileName.append(data.getMeterId());
				fileName.append(GenerateReportsService.UNDERSCORE);
				fileName.append(fileNameFormat.format(cal.getTime()));
				fileName.append(".csv");
				
				
				StringBuffer filePath=new StringBuffer(customProperties.getCsvpath());
				filePath.append(fileName);
				File file=new File(filePath.toString());
				
				
				Path path = Paths.get(filePath.toString());
				 
				//Use try-with-resource to get auto-closeable writer instance
				BufferedWriter writer = Files.newBufferedWriter(path);
			
				writer.write("Hourly Cosumption Report For meter:"+data.getMeterId());
				writer.write(GenerateReportsService.NL);
				writer.write("Time");
				writer.write(GenerateReportsService.COMMA);
				writer.write("Meter Reading");
				writer.write(GenerateReportsService.NL);
				for(Data dataobj:returnedobjects){
					writer.write((String)dataobj.getName());
					writer.write(GenerateReportsService.COMMA);
					writer.write(new Double(dataobj.getValue()).toString());
					writer.write(GenerateReportsService.NL);
				}
				writer.flush();
				 writer.close();
				 cloudpath=azureFileService.uploadFile(filePath.toString(), fileName.toString());
			}
			
			log.info("Entering GenerateReportsService.generateReport ");
			return cloudpath;
		 
	 }
	 
}
