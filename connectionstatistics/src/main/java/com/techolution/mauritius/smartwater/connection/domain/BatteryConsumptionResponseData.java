package com.techolution.mauritius.smartwater.connection.domain;

import java.io.Serializable;
import java.util.List;

public class BatteryConsumptionResponseData extends TelemetryResponseData {
	
	private List<BatteryData> series;

	@Override
	public void setSeries(List<Data> series) {
		// TODO Auto-generated method stub
		super.setSeries(series);
	}

	

	@Override
	public List<Data> getSeries() {
		// TODO Auto-generated method stub
		return super.getSeries();
	}
	
}
