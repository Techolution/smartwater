package com.techolution.mauritius.smartwater.connection.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class RequestData implements Serializable{
	
	
	public Integer getBlock_ID() {
		return Block_ID;
	}
	public void setBlock_ID(Integer block_ID) {
		Block_ID = block_ID;
	}
	public Integer getCustomer_ID() {
		return Customer_ID;
	}
	public void setCustomer_ID(Integer customer_ID) {
		Customer_ID = customer_ID;
	}
	
	public Integer getHouse_ID() {
		return house_ID;
	}
	public void setHouse_ID(Integer house_ID) {
		this.house_ID = house_ID;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	public String getEnd_Time() {
		return End_Time;
	}
	public void setEnd_Time(String end_Time) {
		End_Time = end_Time;
	}
	
	public String getStart_Time() {
		return Start_Time;
	}
	public void setStart_Time(String start_Time) {
		Start_Time = start_Time;
	}
	public String getSample_Distance() {
		return Sample_Distance;
	}
	public void setSample_Distance(String sample_Distance) {
		Sample_Distance = sample_Distance;
	}
	public Integer getVendor_ID() {
		return Vendor_ID;
	}
	public void setVendor_ID(Integer vendor_ID) {
		Vendor_ID = vendor_ID;
	}
	public Integer getSample_Distance_value() {
		return Sample_Distance_value;
	}
	public void setSample_Distance_value(Integer sample_Distance_value) {
		Sample_Distance_value = sample_Distance_value;
	}
	private Integer Block_ID;
	private Integer Customer_ID;
	private String End_Time;
	private Integer house_ID;
	private String Start_Time;
	private String Sample_Distance;
	private Integer Vendor_ID;
	private Integer Sample_Distance_value;
	/*public Integer getHouseId() {
		return houseId;
	}
	public void setHouseId(Integer houseId) {
		this.houseId = houseId;
	}*/

}
