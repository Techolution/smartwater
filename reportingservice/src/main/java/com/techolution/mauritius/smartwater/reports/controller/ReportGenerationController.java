package com.techolution.mauritius.smartwater.reports.controller;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techolution.mauritius.smartwater.reports.domain.ReportData;
import com.techolution.mauritius.smartwater.reports.domain.SupplyStatisticsRequestData;
import com.techolution.mauritius.smartwater.reports.service.GenerateReportsService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/report")
public class ReportGenerationController {
	
	private Log log = LogFactory.getLog(ReportGenerationController.class);
	@Autowired
	GenerateReportsService service;
	
	@PostMapping("/consumption/hourly")
	public @ResponseBody  ReportData getHourlyConsumptionReport(@RequestBody SupplyStatisticsRequestData data) throws JSONException 	
	{
		log.info("Entering ReportGenerationController.getHourlyConsumptionReport");
		
		String filePath=null;
		ReportData reportData=new ReportData();
		try {
			filePath = service.generateReport(data);
			log.debug("filePath obtained is:"+filePath);
			reportData.setReportPath(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("Exiting ReportGenerationController.getHourlyConsumptionReport");
		
		return reportData;
		
	}
	
	@PostMapping("/readings/hourly")
	public @ResponseBody  ReportData getHourlyRedingsReport(@RequestBody SupplyStatisticsRequestData data) throws JSONException 	
	{
		log.info("Entering ReportGenerationController.getHourlyConsumptionReport");
		
		String filePath=null;
		ReportData reportData=new ReportData();
		try {
			filePath = service.generateConsumptionReport(data);
			log.debug("filePath obtained is:"+filePath);
			reportData.setReportPath(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("Exiting ReportGenerationController.getHourlyConsumptionReport");
		
		return reportData;
		
	}

}
