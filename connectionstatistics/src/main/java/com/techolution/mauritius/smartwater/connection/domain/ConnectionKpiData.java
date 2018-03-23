package com.techolution.mauritius.smartwater.connection.domain;

import java.util.List;

public class ConnectionKpiData {
	
	private int house_id;
	
	private List<Kpi> series;

	public int getHouse_id() {
		return house_id;
	}

	public void setHouse_id(int house_id) {
		this.house_id = house_id;
	}

	public List<Kpi> getSeries() {
		return series;
	}

	public ConnectionKpiData(int house_id, List<Kpi> series) {
		super();
		this.house_id = house_id;
		this.series = series;
	}

	public void setSeries(List<Kpi> series) {
		this.series = series;
	}

}
