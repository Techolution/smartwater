package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;
import java.util.Date;

public class WaterSupplyUIData implements Serializable {
	
	private Date start;
	
	private Date end;
	
	private String title;

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
