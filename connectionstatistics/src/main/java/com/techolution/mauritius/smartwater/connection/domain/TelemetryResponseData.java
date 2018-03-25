package com.techolution.mauritius.smartwater.connection.domain;

import java.io.Serializable;
import java.util.List;

public class TelemetryResponseData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Data> getSeries() {
		return series;
	}

	public void setSeries(List<Data> series) {
		this.series = series;
	}

	

	private List<Data> series;

}
