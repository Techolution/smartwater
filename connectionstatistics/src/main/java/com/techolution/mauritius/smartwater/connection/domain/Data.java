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
	
	public String getName() {
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

	private int devid;
	private String sensor_locationname;
	private String name;
	private double value;

}
