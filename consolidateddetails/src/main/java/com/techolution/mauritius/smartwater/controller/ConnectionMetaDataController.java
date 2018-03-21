package com.techolution.mauritius.smartwater.controller;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techolution.mauritius.smartwater.domain.Dfj;
import com.techolution.mauritius.smartwater.domain.MeterConnection;
import com.techolution.mauritius.smartwater.domain.ResponseData;
import com.techolution.mauritius.smartwater.service.ConsolidatedDataService;

@RestController
@RequestMapping(value="/read")

public class ConnectionMetaDataController {
	
	private Log log = LogFactory.getLog(ConsolidatedDataController.class);
	

	@Autowired
	private ConsolidatedDataService consolidatedDataService;
	

	/**
	 * Taking as string as Senseworx api doesnot take json key confirming to java bean format.
	 *  If name in json and class does not match, object is not getting populated.
	 *  Hence taking as STring and creating objects manually
	 *  
	 *  Currently not using it as we have only one customer and no multi tenancy
	 *  
	 * @param data
	 * @return
	 * @throws ParseException
	 * @throws JSONException 
	 */
	
	@PostMapping("/r/HList")
	public @ResponseBody ResponseData getAllConnections(@RequestBody String jsonRequest)
	{
		log.info("Entering ConsolidatedDataController.getAllConnections");
		List<MeterConnection> resultList=consolidatedDataService.getAllConnections();
		log.info("Exiting ConsolidatedDataController.getAllConnections");
		Dfj dfj=new Dfj();
		dfj.setStatus("Success");
		ResponseData data=new ResponseData();
		data.setDfj(dfj);
		data.setData(resultList);
		return data;
	}

}
