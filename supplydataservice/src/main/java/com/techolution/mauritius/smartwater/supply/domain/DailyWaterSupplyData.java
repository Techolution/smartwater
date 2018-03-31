package com.techolution.mauritius.smartwater.supply.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyWaterSupplyData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Date> ontimelist;
	private List<Date> offTimeList;
	
	private String day;
	
	private Date dateVal;
	
	
	public Date getDateVal() {
		return dateVal;
	}

	public void setDateVal(Date dateVal) {
		this.dateVal = dateVal;
	}
	private WaterSupplyData supplydata;

	public List<Date> getOntimelist() {
		return ontimelist;
	}

	public void setOntimelist(List<Date> ontimelist) {
		this.ontimelist = ontimelist;
	}

	public List<Date> getOffTimeList() {
		return offTimeList;
	}

	public void setOffTimeList(List<Date> offTimeList) {
		this.offTimeList = offTimeList;
	}

	

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public WaterSupplyData getSupplydata() {
		return supplydata;
	}

	public void setSupplydata(WaterSupplyData supplydata) {
		this.supplydata = supplydata;
	}
	
	public void addToOnTimeList(Date date){
		List<Date> otlist=this.getOntimelist();
		if(otlist== null){
			otlist = new ArrayList<Date>();
			this.setOntimelist(otlist);
		}
		otlist.add(date);
	}
	public void addToOffTimeList(Date date){
		List<Date> otlist=this.getOffTimeList();
		if(otlist== null){
			otlist = new ArrayList<Date>();
			this.setOffTimeList(otlist);
		}
		otlist.add(date);
	}
}
