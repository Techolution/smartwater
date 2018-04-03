package com.techolution.mauritius.smartwater.supply.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import com.techolution.mauritius.smartwater.supply.domain.WaterSupplyUIData;
import com.techolution.mauritius.smartwater.supply.service.SupplyDataService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/supply")

public class SupplyDataController {
	
	private static String DEFAULT_EVENTTITLE="Usage Timings";
	
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
	public @ResponseBody List<WaterSupplyUIData> getSupplyStats(@RequestBody SupplyStatisticsRequestData requestData) throws ParseException, JSONException
	
	{
		log.info("Entering SupplyDataController.getSupplyStats");
		
		
		List<DailyWaterSupplyData> result=supplyDataService.getDataForList(requestData);
		
		List<WaterSupplyUIData> uidatalist = new ArrayList<WaterSupplyUIData>();
		
		
		
		result.parallelStream().forEach(waterSupplyData -> {
			WaterSupplyUIData waterSupplyUIData=new WaterSupplyUIData();
			Date dayMinOnTime = null;
			Date dayMaxOffTime =null;
			if(waterSupplyData.getOntimelist() !=null && !waterSupplyData.getOntimelist().isEmpty()){
				dayMinOnTime=waterSupplyData.getOntimelist().get(0);				
			}
			
			if(waterSupplyData.getOffTimeList() !=null && !waterSupplyData.getOffTimeList().isEmpty()){
				dayMaxOffTime=waterSupplyData.getOffTimeList().get(waterSupplyData.getOffTimeList().size()-1);				
			}
			
			
			
			if(dayMinOnTime !=null || dayMaxOffTime!=null){
				if((dayMaxOffTime!=null) &&(dayMinOnTime == null || dayMinOnTime.after(dayMaxOffTime))){
					
					Calendar ontime=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
					ontime.setTime(dayMaxOffTime);
					ontime.set(Calendar.HOUR_OF_DAY, 0);
					ontime.set(Calendar.MINUTE, 0);
					ontime.set(Calendar.SECOND, 0);
					dayMinOnTime=ontime.getTime();
				}else if ((dayMinOnTime !=null) && (dayMaxOffTime == null || dayMinOnTime.after(dayMaxOffTime))){
					
					Calendar offtime=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
					offtime.setTime(dayMinOnTime);
					offtime.set(Calendar.HOUR_OF_DAY, 23);
					offtime.set(Calendar.MINUTE, 59);
					offtime.set(Calendar.SECOND, 59);
					dayMaxOffTime=offtime.getTime();
					
				}
				
				else if(dayMinOnTime.getDate() != dayMaxOffTime.getDate()){
					
					Calendar ontime=Calendar.getInstance();
					ontime.setTime(dayMaxOffTime);
					ontime.set(Calendar.HOUR_OF_DAY, 0);
					ontime.set(Calendar.MINUTE, 0);
					ontime.set(Calendar.SECOND, 0);
					dayMinOnTime=ontime.getTime();
					
				}
				waterSupplyUIData.setStart(dayMinOnTime);
				waterSupplyUIData.setEnd(dayMaxOffTime);
				waterSupplyUIData.setTitle(DEFAULT_EVENTTITLE);
				uidatalist.add(waterSupplyUIData);
			}
			
			
			
			
		});
		
		log.info("Exiting SupplyDataController.getSupplyStats");
		return uidatalist;
		
	}

}
