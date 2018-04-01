package com.techolution.mauritius.smartwater.notification.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.notification.domain.NotificationDetails;
import com.techolution.mauritius.smartwater.notification.repository.NotificationDetailsRepository;

@Component
public class NotificationService {
	
	private Log log = LogFactory.getLog(NotificationService.class);
	
	@Autowired
	private NotificationDetailsRepository notificationDetailsRepository;
	
	private static String ISSUE_STATUS_OPEN="OPEN";
	
	public List<NotificationDetails> getAllOpenNotifcations(){
		
		log.info("Entering NotificationService.getAllOpenNotifcations");
		
		List<NotificationDetails> results=notificationDetailsRepository.findByIssuestatus(ISSUE_STATUS_OPEN);
		//List<NotificationDetails> results=(List<NotificationDetails>)notificationDetailsRepository.findAll();
		
		if(results!=null){
			log.debug("Result size is:"+results.size());	
		}
		
		
		log.info("Exiting NotificationService.getAllOpenNotifcations");
		
		return results;
	}

}
