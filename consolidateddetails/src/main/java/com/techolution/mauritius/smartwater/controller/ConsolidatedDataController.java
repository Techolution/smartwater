package com.techolution.mauritius.smartwater.controller;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techolution.mauritius.smartwater.domain.MeterConnection;
import com.techolution.mauritius.smartwater.domain.TotalConsolidatedConsumption;
import com.techolution.mauritius.smartwater.domain.TotalConsolidatedDeviceStatus;
import com.techolution.mauritius.smartwater.service.ConsolidatedDataService;
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/consolidateddata")


public class ConsolidatedDataController {
	
	private Log log = LogFactory.getLog(ConsolidatedDataController.class);
	
	@Autowired
	private ConsolidatedDataService consolidatedDataService;
	
	@RequestMapping(method=RequestMethod.GET,value="/connections")
	public @ResponseBody List<MeterConnection> getAllConnections() throws UnknownHostException
	{
		log.info("Entering ConsolidatedDataController.getAllConnections");
		List<MeterConnection> resultList=consolidatedDataService.getAllConnections();
		log.info("Exiting ConsolidatedDataController.getAllConnections");
		return resultList;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/monthly/consumption")
	public @ResponseBody TotalConsolidatedConsumption getTillDateMonthlyConsumption() throws ClientProtocolException, IOException, JSONException
	{
		log.info("Entering ConsolidatedDataController.getTillDateMonthlyConsumption");
		TotalConsolidatedConsumption  result=consolidatedDataService.getConsumptionForThisMonth();
		log.info("Exiting ConsolidatedDataController.getAllConnections");
		return result;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/daily/consumption")
	public @ResponseBody TotalConsolidatedConsumption getTodayConsumption() throws ClientProtocolException, IOException, JSONException
	{
		log.info("Entering ConsolidatedDataController.getTillDateMonthlyConsumption");
		TotalConsolidatedConsumption  result=consolidatedDataService.getConsumptionForToday();
		log.info("Exiting ConsolidatedDataController.getAllConnections");
		return result;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/monthly/devicestatus")
	public @ResponseBody TotalConsolidatedDeviceStatus getTillDateMonthlyDeviceStatus() throws ClientProtocolException, IOException, JSONException
	{
		log.info("Entering ConsolidatedDataController.getTillDateMonthlyDeviceStatus");
		TotalConsolidatedDeviceStatus  result=consolidatedDataService.getDeviceStatusForThisMonth();
		log.info("Exiting ConsolidatedDataController.getTillDateMonthlyDeviceStatus");
		return result;
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/daily/devicestatus")
	public @ResponseBody TotalConsolidatedDeviceStatus getTodayDeviceStatus() throws ClientProtocolException, IOException, JSONException
	{
		log.info("Entering ConsolidatedDataController.getTodayDeviceStatus");
		TotalConsolidatedDeviceStatus  result=consolidatedDataService.getDeviceStatusForToday();
		log.info("Exiting ConsolidatedDataController.getTodayDeviceStatus");
		return result;
	}

}
