package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;

public class WaterSupplyDailyConnectionStats implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long totalMeters;
	
	private long noSupplyCount;
	
	private long noResponseCount;

	public long getTotalMeters() {
		return totalMeters;
	}

	public void setTotalMeters(long totalMeters) {
		this.totalMeters = totalMeters;
	}

	public long getNoSupplyCount() {
		return noSupplyCount;
	}

	public void setNoSupplyCount(long noSupplyCount) {
		this.noSupplyCount = noSupplyCount;
	}

	public long getNoResponseCount() {
		return noResponseCount;
	}

	public void setNoResponseCount(long noResponseCount) {
		this.noResponseCount = noResponseCount;
	}

}
