package com.techolution.mauritius.smartwater.connection.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techolution.mauritius.smartwater.connection.domain.BatteryConsumptionResponseData;
import com.techolution.mauritius.smartwater.connection.domain.ConnectionKpiData;
import com.techolution.mauritius.smartwater.connection.domain.Data;
import com.techolution.mauritius.smartwater.connection.domain.Dfj;
import com.techolution.mauritius.smartwater.connection.domain.RequestData;
import com.techolution.mauritius.smartwater.connection.domain.ResponseData;
import com.techolution.mauritius.smartwater.connection.domain.Telemetry;
import com.techolution.mauritius.smartwater.connection.service.ConnectionStatisticsService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/read")

public class ConnectionStatisticsController {
	
	@Autowired
	private ConnectionStatisticsService connectionStatisticsService;
	private Log log = LogFactory.getLog(ConnectionStatisticsController.class);
	
	private static String CONSUMPTION ="Consumption";
	
	/**
	 * Taking as string as Senseworx api doesnot take json key confirming to java bean format.
	 *  If name in json and class does not match, object is not getting populated.
	 *  Hence taking as STring and creating objects manually
	 * @param data
	 * @return
	 * @throws ParseException
	 * @throws JSONException 
	 */
	@PostMapping("/r/GetData")
	public @ResponseBody List<Data> getConsumptionDetails(@RequestBody String data) throws ParseException, JSONException
	
	{
		log.info("Entering ConnectionStatisticsController.getConsumptionDetails ");
		log.info("Request String is:"+data);
		JSONObject object=new JSONObject(data);
		RequestData requestData =new RequestData();
		requestData.setHouse_ID(object.getInt("House_ID"));
		requestData.setBlock_ID(object.getInt("Block_ID"));
		requestData.setCustomer_ID(object.getInt("Customer_ID"));
		requestData.setEnd_Time(object.getString("End_Time"));
		requestData.setStart_Time(object.getString("Start_Time"));
		requestData.setSample_Distance(object.getString("Sample_Distance"));
		requestData.setSample_Distance_value(object.getInt("Sample_Distance_value"));
		requestData.setVendor_ID(object.getInt("Vendor_ID"));
	
		log.info("Input RequestData is not null");
		log.info("Input RequestData houseID:"+requestData.getHouse_ID());
		/*log.info("Input RequestData start time:"+requestData.getStart_Time());
		log.info("Input RequestData End time:"+requestData.getEnd_Time());*/
		
		//Gson gsonObj = new Gson();
        // converts object to json string
        //String jsonStr = gsonObj.toJson(requestData);
		log.info(requestData.toString());
		
		
     
		List<Data> resultList=connectionStatisticsService.getData(requestData);
		
		
		/*WaterConsumptionResponseData responseData=new WaterConsumptionResponseData();
		responseData.setName(CONSUMPTION);
		responseData.setSeries(resultList);
		
		List<WaterConsumptionResponseData> waterconumptionData=new ArrayList<WaterConsumptionResponseData>(1);
		waterconumptionData.add(responseData);*/
		
		log.info("Exiting ConnectionStatisticsController.getConsumptionDetails ");
		return resultList;
	}
	
	
	/**
	 * Taking as string as Senseworx api doesnot take json key confirming to java bean format.
	 *  If name in json and class does not match, object is not getting populated.
	 *  Hence taking as STring and creating objects manually
	 * @param data
	 * @return
	 * @throws ParseException
	 * @throws JSONException 
	 */
	@PostMapping("/battery/GetData")
	public @ResponseBody List<BatteryConsumptionResponseData> getBatteryDetails(@RequestBody String data) throws ParseException, JSONException
	
	{
		log.info("Entering ConnectionStatisticsController.getBatteryDetails ");
		log.info("Request String is:"+data);
		JSONObject object=new JSONObject(data);
		RequestData requestData =new RequestData();
		requestData.setHouse_ID(object.getInt("House_ID"));
		requestData.setBlock_ID(object.getInt("Block_ID"));
		requestData.setCustomer_ID(object.getInt("Customer_ID"));
		requestData.setEnd_Time(object.getString("End_Time"));
		requestData.setStart_Time(object.getString("Start_Time"));
		requestData.setSample_Distance(object.getString("Sample_Distance"));
		requestData.setSample_Distance_value(object.getInt("Sample_Distance_value"));
		requestData.setVendor_ID(object.getInt("Vendor_ID"));
	
		log.info("Input RequestData is not null");
		log.info("Input RequestData houseID:"+requestData.getHouse_ID());
		/*log.info("Input RequestData start time:"+requestData.getStart_Time());
		log.info("Input RequestData End time:"+requestData.getEnd_Time());*/
		
		//Gson gsonObj = new Gson();
        // converts object to json string
        //String jsonStr = gsonObj.toJson(requestData);
		log.info(requestData.toString());
		
		
     
		List<Data> resultList=connectionStatisticsService.geBatterytData(requestData);
		/*Dfj dfj=new Dfj();
		dfj.setStatus("Success");
		
		ResponseData responseData=new ResponseData();
		responseData.setData(resultList);
		responseData.setDfj(dfj);*/
		
		
		BatteryConsumptionResponseData responseData=new BatteryConsumptionResponseData();
		responseData.setName(CONSUMPTION);
		responseData.setSeries(resultList);
		
		List<BatteryConsumptionResponseData> waterconumptionData=new ArrayList<BatteryConsumptionResponseData>(1);
		waterconumptionData.add(responseData);
		
		log.info("Exiting ConnectionStatisticsController.getConsumptionDetails ");
		return waterconumptionData;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/daily/data/{meter_id}")
	public @ResponseBody ConnectionKpiData getDataForDay(@PathVariable int meter_id) throws ParseException, JSONException
	
	{
		log.info("Entering ConnectDataController.getDataForDay");
		
		
		ConnectionKpiData data=connectionStatisticsService.getAllMetricsForConnectionForDay(meter_id);
		log.info("Exiting ConnectDataController.getDataForDay");
		
		return data;
		
	}

}
