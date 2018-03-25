package com.techolution.mauritius.smartwater.connection.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/* This class is same as RequestData, jus that that class attributes confirming 
 * to request structure of senseworx and here it is to generic
 * java patterns. In future, the services should use this
 * 
 * 
 */

public class TelemetryRequestData implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	
	
	private Integer blockId;
	
	
	private Integer customerId;
	
	
	private String endTime;
	
	
	public Integer getBlockId() {
		return blockId;
	}


	public void setBlockId(Integer blockId) {
		this.blockId = blockId;
	}


	public Integer getCustomerId() {
		return customerId;
	}


	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}


	

	public Integer getHouseId() {
		return houseId;
	}


	public void setHouseId(Integer houseId) {
		this.houseId = houseId;
	}


	

	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getSampleDistance() {
		return sampleDistance;
	}


	public void setSampleDistance(String sampleDistance) {
		this.sampleDistance = sampleDistance;
	}


	public Integer getVendorId() {
		return vendorId;
	}


	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}


	public Integer getSampleDistanceValue() {
		return sampleDistanceValue;
	}


	public void setSampleDistanceValue(Integer sampleDistanceValue) {
		this.sampleDistanceValue = sampleDistanceValue;
	}


	public String getMetrics() {
		return metrics;
	}


	public void setMetrics(String metrics) {
		this.metrics = metrics;
	}


	public Object getDefaultValueForMissingData() {
		return defaultValueForMissingData;
	}


	public void setDefaultValueForMissingData(Object defaultValueForMissingData) {
		this.defaultValueForMissingData = defaultValueForMissingData;
	}



	private Integer houseId;
	
	
	private String startTime;
	
	
	private String sampleDistance;
	
	
	private Integer vendorId;
	
	
	private Integer sampleDistanceValue;
	
	
	private String metrics;
	
	
	private Object defaultValueForMissingData;
	

}
