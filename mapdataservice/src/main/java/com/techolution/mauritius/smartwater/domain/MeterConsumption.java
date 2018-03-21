package com.techolution.mauritius.smartwater.domain;

import java.util.Date;

public class MeterConsumption extends MeterConnection {
	
	public MeterConsumption(){
		super();
	}
	public MeterConsumption(MeterConnection connection){
		super();
		this.setBlock_id(connection.getBlock_id());
		this.setConsumer_id(connection.getConsumer_id());
		this.setCustomer_id(connection.getCustomer_id());
		this.setDevice_latitude(connection.getDevice_latitude());
		this.setDevice_longitude(connection.getDevice_longitude());
		this.setHouse_id(connection.getHouse_id());
		this.setHouse_namenum(connection.getHouse_namenum());
		this.setId(connection.getId());
		this.setIsprivate(connection.isIsprivate());
		this.setVendor_id(connection.getVendor_id());
		
	}
	
	Date endTime;
	Double wdata;
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Double getWdata() {
		return wdata;
	}
	public void setWdata(Double wdata) {
		this.wdata = wdata;
	}

}
