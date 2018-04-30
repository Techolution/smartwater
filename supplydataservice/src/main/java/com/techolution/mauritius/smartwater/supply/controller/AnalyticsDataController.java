package com.techolution.mauritius.smartwater.supply.controller;

import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techolution.mauritius.smartwater.supply.domain.MeterTrendData;
import com.techolution.mauritius.smartwater.supply.domain.WaterSupplyDailyConnectionStats;
import com.techolution.mauritius.smartwater.supply.service.SupplyAnalyticsService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/supply")

public class AnalyticsDataController {
	
	private Log log = LogFactory.getLog(AnalyticsDataController.class);
	
	@Autowired
	SupplyAnalyticsService analyticService;
	
	@RequestMapping(method=RequestMethod.GET,value="/daily/baseline/analysis/increase")
	public @ResponseBody List<MeterTrendData> getConnectionsAboveDailyBaseline() throws UnknownHostException
	{
		log.info("Entering ConsolidatedDataController.getConnectionsAboveDailyBaseline");
		List<MeterTrendData> resultList=analyticService.getConnectionsAboveDailyBaseline();
		log.info("Exiting ConsolidatedDataController.getConnectionsAboveDailyBaseline");
		return resultList;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/daily/baseline/analysis/decrease")
	public @ResponseBody List<MeterTrendData> getConnectionsbelowDailyBaseline() throws UnknownHostException
	{
		log.info("Entering ConsolidatedDataController.getConnectionsAboveDailyBaseline");
		List<MeterTrendData> resultList=analyticService.getConnectionsBelowDailyBaseline();
		log.info("Exiting ConsolidatedDataController.getConnectionsAboveDailyBaseline");
		return resultList;
	}
	
	
	@RequestMapping(method=RequestMethod.GET,value="/stats")
	public @ResponseBody WaterSupplyDailyConnectionStats getStats() throws UnknownHostException
	{
		log.info("Entering ConsolidatedDataController.getStats");
		WaterSupplyDailyConnectionStats output=analyticService.getStats();
		log.info("Exiting ConsolidatedDataController.getStats");
		return output;
	}

}
