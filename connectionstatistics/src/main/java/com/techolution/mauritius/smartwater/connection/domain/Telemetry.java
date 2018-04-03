package com.techolution.mauritius.smartwater.connection.domain;

import java.io.Serializable;
import java.util.Date;

public class Telemetry implements Serializable {
	
	
	public Double getReading() {
		return reading;
	}
	public void setReading(Double reading) {
		this.reading = reading;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int meter_id;
	private Double flow;
	private Long battery;
	private Long flowrate;
	private Double reading;
	private Date date;
	public int getMeter_id() {
		return meter_id;
	}
	public void setMeter_id(int meter_id) {
		this.meter_id = meter_id;
	}
	
	public Double getFlow() {
		return flow;
	}
	public void setFlow(Double flow) {
		this.flow = flow;
	}
	public Long getBattery() {
		return battery;
	}
	public void setBattery(Long battery) {
		this.battery = battery;
	}
	public Long getFlowrate() {
		return flowrate;
	}
	public void setFlowrate(Long flowrate) {
		this.flowrate = flowrate;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
