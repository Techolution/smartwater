package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;
import java.util.Date;

public class ConsumptionLeakage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String meterId;
	private Date date;
	private double  leakageVolume;
	private String location;
	private String startTime;
	private String endTime;
	
	
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getMeterId() {
		return meterId;
	}
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getLeakageVolume() {
		return leakageVolume;
	}
	public void setLeakageVolume(double leakageVolume) {
		this.leakageVolume = leakageVolume;
	}
	

}
