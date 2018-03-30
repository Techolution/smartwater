package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;

public class MeterTrendData implements Serializable {
	
	private int meterId;
	private String location;
	private String dateTime;
	private long consumption;
	private long previousConsumption;
	
	
	public long getPreviousConsumption() {
		return previousConsumption;
	}
	public void setPreviousConsumption(long previousConsumption) {
		this.previousConsumption = previousConsumption;
	}
	private String analysisStartTime;
	private String analysisEndTime;
	private String comparisonResult;
	public int getMeterId() {
		return meterId;
	}
	public void setMeterId(int meterId) {
		this.meterId = meterId;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public long getConsumption() {
		return consumption;
	}
	public void setConsumption(long consumption) {
		this.consumption = consumption;
	}
	public String getAnalysisStartTime() {
		return analysisStartTime;
	}
	public void setAnalysisStartTime(String analysisStartTime) {
		this.analysisStartTime = analysisStartTime;
	}
	public String getAnalysisEndTime() {
		return analysisEndTime;
	}
	public void setAnalysisEndTime(String analysisEndTime) {
		this.analysisEndTime = analysisEndTime;
	}
	public String getComparisonResult() {
		return comparisonResult;
	}
	public void setComparisonResult(String comparisonResult) {
		this.comparisonResult = comparisonResult;
	}

}
