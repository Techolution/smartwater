package com.techolution.mauritius.smartwater.map.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techolution.mauritius.smartwater.domain.Dfj;
import com.techolution.mauritius.smartwater.domain.MeterConsumption;
import com.techolution.mauritius.smartwater.domain.RequestData;
import com.techolution.mauritius.smartwater.domain.ResponseData;
import com.techolution.mauritius.smartwater.map.service.MapDataService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/map")

public class MapDataController {
	
	@Autowired
	private MapDataService mapDataService;
	
	private Log log = LogFactory.getLog(MapDataController.class);
	
	@PostMapping("/r/Data")
	public @ResponseBody ResponseData getData(@RequestBody String jsonRequest) throws JSONException
	{
		log.info("Entering MapDataController.getData ");
		
		
		
		
		log.info("Request String is:"+jsonRequest);
		JSONObject object=new JSONObject(jsonRequest);
		RequestData requestData =new RequestData();
		requestData.setHouse_ID(object.getInt("House_ID"));
		requestData.setCustomer_ID(object.getInt("Customer_ID"));
		requestData.setEnd_Time(object.getString("End_Time"));
		requestData.setStart_Time(object.getString("Start_Time"));
		requestData.setVendor_ID(object.getInt("Vendor_ID"));
		
		List<MeterConsumption> consumptionList=mapDataService.getDataForAllConnections(requestData);
		
		Dfj dfj=new Dfj();
		dfj.setStatus("Success");
		
		ResponseData data=new ResponseData();
		data.setData(consumptionList);
		data.setDfj(dfj);
		return data;
	}
}


