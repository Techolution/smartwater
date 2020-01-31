package com.techolution.smartoffice.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import com.techolution.smartoffice.adapter.callback.BMSMqttCallBack;
import com.techolution.smartoffice.adapter.callback.DefaultMqttCallBack;
import com.techolution.smartoffice.adapter.callback.SmartOfficeMqttCallBack;



@SpringBootApplication
public class MqttkafkaadapterApplication implements CommandLineRunner{
	
	 public static Logger logger = LoggerFactory.getLogger(MqttkafkaadapterApplication.class);
	
	 
/*	 @Autowired
	 private CustomProperties customProperties;*/
	 
	 @Autowired
	 SmartOfficeMqttCallBack smartOfficeMqttCallBack;
	 
	/* @Autowired
	 BMSMqttCallBack bmsMqttCallBack;*/
	 
	 @Autowired
	 DefaultMqttCallBack bmsMqttCallBack;
	 
	 @Autowired
	 DefaultMqttCallBack cescMqttCallBack;
	 
	 
	 @Autowired
	 DefaultMqttCallBack aaMqttCallBack;
	 
	 @Autowired
	 private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(MqttkafkaadapterApplication.class, args);
		
	}



	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		logger.debug("Profiles:"+environment.getActiveProfiles()[0]);
		if((environment.getActiveProfiles())[0].contains("bms")){
			bmsMqttCallBack.connect();	
			logger.debug("Registed BMS callback");
		} else if((environment.getActiveProfiles())[0].contains("cesc")){
			cescMqttCallBack.connect();
			logger.debug("Registed cescMqttCallBack callback");
		}
		else if((environment.getActiveProfiles())[0].contains("aa")){
			aaMqttCallBack.connect();
			logger.debug("Registed cescMqttCallBack callback");
		}
		else{
			bmsMqttCallBack.connect();	
			logger.debug("Registed smartoffice callback");
		}
		
		
		
		//smartOfficeMqttCallBack.subscribe();
	}
	
	
	
	
	
}
