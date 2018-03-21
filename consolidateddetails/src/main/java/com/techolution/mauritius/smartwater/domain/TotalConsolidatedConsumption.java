package com.techolution.mauritius.smartwater.domain;

import java.io.Serializable;

public class TotalConsolidatedConsumption implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double getConsumptionInPreviousBucket() {
		return consumptionInPreviousBucket;
	}
	public void setConsumptionInPreviousBucket(double consumptionInPreviousBucket) {
		this.consumptionInPreviousBucket = consumptionInPreviousBucket;
	}
	private Double totalConsumption;
	private double normalPercentage;
	private double abnormalPercentage;
	private double consumptionInPreviousBucket;
	public TotalConsolidatedConsumption(Double totalConsumption, double normalPercentage, double abnormalPercentage,
			double trend) {
		super();
		this.totalConsumption = totalConsumption;
		this.normalPercentage = normalPercentage;
		this.abnormalPercentage = abnormalPercentage;
		this.consumptionInPreviousBucket = trend;
	}
	public Double getTotalConsumption() {
		return totalConsumption;
	}
	public void setTotalConsumption(Double totalConsumption) {
		this.totalConsumption = totalConsumption;
	}
	public double getNormalPercentage() {
		return normalPercentage;
	}
	public void setNormalPercentage(double normalPercentage) {
		this.normalPercentage = normalPercentage;
	}
	public double getAbnormalPercentage() {
		return abnormalPercentage;
	}
	public void setAbnormalPercentage(double abnormalPercentage) {
		this.abnormalPercentage = abnormalPercentage;
	}
	

}
