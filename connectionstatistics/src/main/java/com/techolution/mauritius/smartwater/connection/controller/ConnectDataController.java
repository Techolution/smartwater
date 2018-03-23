package com.techolution.mauritius.smartwater.connection.controller;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


import com.techolution.mauritius.smartwater.connection.domain.Dfj;
import com.techolution.mauritius.smartwater.connection.domain.ResponseData;
import com.techolution.mauritius.smartwater.connection.domain.Telemetry;
import com.techolution.mauritius.smartwater.connection.service.ConnectionStatisticsService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/insert")
public class ConnectDataController {
	
	@Autowired
	private ConnectionStatisticsService connectionStatisticsService;
	private Log log = LogFactory.getLog(ConnectDataController.class);
	
	/**
	 * Not using meter_id from telemetery as the meter-id should be identified in url
	 * @param telemetry
	 * @param meter_id
	 * @return
	 * @throws ParseException
	 * @throws JSONException
	 */
	@PostMapping("/telemetry/data/{meter_id}")
	public @ResponseBody ResponseData insertTelemetryData(@RequestBody Telemetry telemetry,@PathVariable int meter_id) throws ParseException, JSONException
	
	{
		log.info("Entering ConnectDataController.getConsumptionDetails");
		
		telemetry.setMeter_id(meter_id);
		connectionStatisticsService.insertData(telemetry);
		log.info("Exiting ConnectDataController.getConsumptionDetails");
		ResponseData data=new ResponseData();
		Dfj dfj=new Dfj();
		dfj.setStatus("REQUEST_SUBMITTED");
		data.setDfj(dfj);
		return data;
		
	}
	

}
