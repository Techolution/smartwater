package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;
import java.util.Date;

public class LeakageData implements Serializable {
	
	private Date metricsDate;
	
	private double consumptionLeakage;
	
	private double networkLeakage;

	public Date getMetricsDate() {
		return metricsDate;
	}

	public void setMetricsDate(Date metricsDate) {
		this.metricsDate = metricsDate;
	}

	public double getConsumptionLeakage() {
		return consumptionLeakage;
	}

	public void setConsumptionLeakage(double consumptionLeakage) {
		this.consumptionLeakage = consumptionLeakage;
	}

	public double getNetworkLeakage() {
		return networkLeakage;
	}

	public void setNetworkLeakage(double networkLeakage) {
		this.networkLeakage = networkLeakage;
	}
	

}
