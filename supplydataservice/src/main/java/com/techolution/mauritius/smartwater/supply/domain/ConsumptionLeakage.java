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
	private double  leakage;
	
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
	public double getLeakage() {
		return leakage;
	}
	public void setLeakage(double leakage) {
		this.leakage = leakage;
	}

}
