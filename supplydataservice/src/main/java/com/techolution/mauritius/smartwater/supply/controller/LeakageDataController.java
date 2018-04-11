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

import com.techolution.mauritius.smartwater.supply.domain.ConsumptionLeakage;
import com.techolution.mauritius.smartwater.supply.domain.LeakageData;
import com.techolution.mauritius.smartwater.supply.service.SupplyAnalyticsService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/leakage")

public class LeakageDataController {
	
	private Log log = LogFactory.getLog(LeakageDataController.class);
	
	@Autowired
	SupplyAnalyticsService analyticService;
	
	@RequestMapping(method=RequestMethod.GET,value="/daily/metrics")
	public @ResponseBody LeakageData getDailyLeakageStats()
	{
		log.info("Entering LeakageDataController.getDailyLeakageStats");
		LeakageData resultdata=analyticService.getCurrentDayLeakageData();
		log.info("Exiting LeakageDataController.getDailyLeakageStats");
		return resultdata;
	}
	@RequestMapping(method=RequestMethod.GET,value="/daily/consumerleakage")
	public @ResponseBody List<ConsumptionLeakage> getDailyConsumerLeakageStats()
	{
		log.info("Entering LeakageDataController.getDailyConsumerLeakageStats");
		List<ConsumptionLeakage> resultList=analyticService.getCurrentDayConsumerLeakageDetails();
		log.info("Exiting LeakageDataController.getDailyConsumerLeakageStats");
		return resultList;
	}
	
	
}
