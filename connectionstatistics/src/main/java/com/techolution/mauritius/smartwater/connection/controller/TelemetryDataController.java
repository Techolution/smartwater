package com.techolution.mauritius.smartwater.connection.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techolution.mauritius.smartwater.connection.domain.Data;
import com.techolution.mauritius.smartwater.connection.domain.Dfj;
import com.techolution.mauritius.smartwater.connection.domain.ResponseData;
import com.techolution.mauritius.smartwater.connection.domain.SeriesPointData;
import com.techolution.mauritius.smartwater.connection.domain.TelemetryRequestData;
import com.techolution.mauritius.smartwater.connection.domain.TelemetryResponseData;
import com.techolution.mauritius.smartwater.connection.service.ConnectionStatisticsService;

/*
 * The purpose of this class is same as ConnectionStaticController.
 * However it is designed to be generic, where as ConnectionStaticController
 * has methods towards Sensworx
 */

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping(value="/telemetry")

public class TelemetryDataController {
	

	@Autowired
	private ConnectionStatisticsService connectionStatisticsService;
	private Log log = LogFactory.getLog(TelemetryDataController.class);
	
	private static String METERREADING ="Meter Reading";
	
	@RequestMapping(method=RequestMethod.POST,value="/instance/data")
	public @ResponseBody List<TelemetryResponseData> getValuesAtTime(@RequestBody TelemetryRequestData requestData) throws ParseException{
		
		log.info("Entering TelemetryResponseData.getValuesAtTime");
		
		List<Data> resultList=connectionStatisticsService.geInstanceTelemetrytData(requestData);
		Double lastyearaverage=connectionStatisticsService.getAverageMonthlyForOneYear(requestData.getHouseId());
		
		TelemetryResponseData responseData=new TelemetryResponseData();
		responseData.setName(METERREADING);
		responseData.setSeries(resultList);
		responseData.setLast12monthsAverage(lastyearaverage.longValue());
		
		List<TelemetryResponseData> readingdata=new ArrayList<TelemetryResponseData>(1);
		readingdata.add(responseData);
	
		
		log.info("Exiting TelemetryResponseData.getValuesAtTime");
		return readingdata;
		
	}
	
	
	@RequestMapping(method=RequestMethod.POST,value="/insert/data")
	public @ResponseBody ResponseData insertTimeSeriesData(@RequestBody SeriesPointData seriesPointData) throws ParseException{
		
		log.info("Entering TelemetryResponseData.insertTimeSeriesData");
		
		connectionStatisticsService.insertTimeSeriesData(seriesPointData);;
		ResponseData data=new ResponseData();
		Dfj dfj=new Dfj();
		dfj.setStatus("REQUEST_SUBMITTED");
		data.setDfj(dfj);
		
		log.info("Exiting ConnectDataController.insertTimeSeriesData");
		return data;
		
		
	}


}
