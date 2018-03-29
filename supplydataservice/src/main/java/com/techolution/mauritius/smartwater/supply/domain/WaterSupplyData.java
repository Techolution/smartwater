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
	
	

}
