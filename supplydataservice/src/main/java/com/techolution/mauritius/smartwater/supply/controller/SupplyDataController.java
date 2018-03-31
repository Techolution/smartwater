package com.techolution.mauritius.smartwater.supply.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techolution.mauritius.smartwater.supply.domain.DailyWaterSupplyData;
import com.techolution.mauritius.smartwater.supply.domain.SupplyStatisticsRequestData;
import com.techolution.mauritius.smartwater.supply.domain.WaterSupplyData;
import com.techolution.mauritius.smartwater.supply.service.SupplyDataService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/supply")

public class SupplyDataController {
	
	@Autowired
	private SupplyDataService supplyDataService;
	private Log log = LogFactory.getLog(SupplyDataController.class);
	
	@RequestMapping(method=RequestMethod.GET,value="/last/{meterId}")
	public @ResponseBody WaterSupplyData getLastSuppliedDetails(@PathVariable int meterId){
		
		log.info("Entering SupplyDataController.getLastSuppliedDetails");
		WaterSupplyData returnData = null;
		try {
			returnData = supplyDataService.getLatestWaterSupplyData(meterId);
		} catch (IOException | JSONException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("Exiting SupplyDataController.getLastSuppliedDetails");
		return returnData;
		
	}
	
	@PostMapping("/data")
	public @ResponseBody List<DailyWaterSupplyData> insertTelemetryDataGeneric(@RequestBody SupplyStatisticsRequestData requestData) throws ParseException, JSONException
	
	{
		log.info("Entering SupplyDataController.insertTelemetryDataGeneric");
		
		
		List<DailyWaterSupplyData> result=supplyDataService.getDataForList(requestData);
		
		log.info("Exiting SupplyDataController.insertTelemetryDataGeneric");
		return result;
		
	}

}
