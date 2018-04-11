package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;

public class WaterSupplyData implements Serializable {
	
	private String lastOnTime;
	
	private String lastOffTime;
	
	private String metertype;
	
	private String currentstatus;
	
	private String image;
	private double pipesize;
	private String pipesizeunit;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public double getPipesize() {
		return pipesize;
	}

	public void setPipesize(double pipesize) {
		this.pipesize = pipesize;
	}

	public String getPipesizeunit() {
		return pipesizeunit;
	}

	public void setPipesizeunit(String pipesizeunit) {
		this.pipesizeunit = pipesizeunit;
	}

	public String getCurrentstatus() {
		return currentstatus;
	}

	public void setCurrentstatus(String currentstatus) {
		this.currentstatus = currentstatus;
	}

	public String getMetertype() {
		return metertype;
	}

	public void setMetertype(String metertype) {
		this.metertype = metertype;
	}

	public String getLastOnTime() {
		return lastOnTime;
	}

	public void setLastOnTime(String lastOnTime) {
		this.lastOnTime = lastOnTime;
	}

	public String getLastOffTime() {
		return lastOffTime;
	}

	public void setLastOffTime(String lastOffTime) {
		this.lastOffTime = lastOffTime;
	}
	
	private String location;
	
	private long meterId;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getMeterId() {
		return meterId;
	}

	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}
	
	private long customerId;

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	
	

}
