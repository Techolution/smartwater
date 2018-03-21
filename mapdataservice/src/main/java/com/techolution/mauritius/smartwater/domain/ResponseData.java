package com.techolution.mauritius.smartwater.domain;

import java.io.Serializable;
import java.util.List;

public class ResponseData implements Serializable {
	
	private Dfj dfj;
	
	public Dfj getDfj() {
		return dfj;
	}

	public void setDfj(Dfj dfj) {
		this.dfj = dfj;
	}

	public List<MeterConsumption> getData() {
		return data;
	}

	public void setData(List<MeterConsumption> data) {
		this.data = data;
	}

	private List<MeterConsumption> data;

}
