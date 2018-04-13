package com.techolution.mauritius.smartwater.connection.domain;

import java.io.Serializable;
import java.util.Date;

public class Data implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int getDevid() {
		return devid;
	}
	public void setDevid(int devid) {
		this.devid = devid;
	}
	public String getSensor_locationname() {
		return sensor_locationname;
	}
	public void setSensor_locationname(String sensor_locationname) {
		this.sensor_locationname = sensor_locationname;
	}
	
	public Object getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	private String date;

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setName(Object name) {
		this.name = name;
	}

	private int devid;
	private String sensor_locationname;
	private Object name;
	private double value;

}
