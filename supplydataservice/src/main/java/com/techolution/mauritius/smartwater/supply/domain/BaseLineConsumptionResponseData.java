package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;
import java.util.List;

public class BaseLineConsumptionResponseData implements Serializable {
	
	private List<MeterTrendData> trendResult;

	public List<MeterTrendData> getTrendResult() {
		return trendResult;
	}

	public void setTrendResult(List<MeterTrendData> trendResult) {
		this.trendResult = trendResult;
	}

}
