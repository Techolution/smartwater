package com.techolution.mauritius.smartwater.supply.controller;

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
import com.techolution.mauritius.smartwater.supply.service.SupplyAnalyticsService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/supply")

public class AnalyticsDataController {
	
	private Log log = LogFactory.getLog(AnalyticsDataController.class);
	
	@Autowired
	SupplyAnalyticsService analyticService;
	
	@RequestMapping(method=RequestMethod.GET,value="/daily/baseline/analysis/increase")
	public @ResponseBody List<MeterTrendData> getConnectionsAboveDailyBaseline()
	{
		log.info("Entering ConsolidatedDataController.getAllConnections");
		List<MeterTrendData> resultList=analyticService.getConnectionsAboveDailyBaseline();
		log.info("Exiting ConsolidatedDataController.getAllConnections");
		return resultList;
	}

}
