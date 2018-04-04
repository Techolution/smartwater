package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;

public class WaterSupplyData implements Serializable {
	
	private String lastOnTime;
	
	private String lastOffTime;

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
	
	

}
