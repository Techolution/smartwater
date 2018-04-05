package com.techolution.mauritius.smartwater.notification.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="connection_details")
public class MeterConnection implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private long id;
	private long consumer_id;
	private long customer_id;
	private long house_id;
	private long vendor_id;
	private long block_id;
	private String house_namenum;
	private boolean isprivate;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getConsumer_id() {
		return consumer_id;
	}
	public void setConsumer_id(long consumer_id) {
		this.consumer_id = consumer_id;
	}
	public long getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}
	public long getHouse_id() {
		return house_id;
	}
	public void setHouse_id(long house_id) {
		this.house_id = house_id;
	}
	public long getVendor_id() {
		return vendor_id;
	}
	public void setVendor_id(long vendor_id) {
		this.vendor_id = vendor_id;
	}
	public long getBlock_id() {
		return block_id;
	}
	public void setBlock_id(long block_id) {
		this.block_id = block_id;
	}
	public String getHouse_namenum() {
		return house_namenum;
	}
	public void setHouse_namenum(String house_namenum) {
		this.house_namenum = house_namenum;
	}
	public boolean isIsprivate() {
		return isprivate;
	}
	public void setIsprivate(boolean isprivate) {
		this.isprivate = isprivate;
	}
	
	public double getDevice_latitude() {
		return device_latitude;
	}
	public void setDevice_latitude(double device_latitude) {
		this.device_latitude = device_latitude;
	}
	public double getDevice_longitude() {
		return device_longitude;
	}
	public void setDevice_longitude(double device_longitude) {
		this.device_longitude = device_longitude;
	}
	public long getCurrent_usage() {
		return current_usage;
	}
	public void setCurrent_usage(long current_usage) {
		this.current_usage = current_usage;
	}
	private double device_latitude;
	private double device_longitude;
	private long current_usage;
	
	private long currentbatteryhealth;
	
	private String currentstatus;
	public long getCurrentbatteryhealth() {
		return currentbatteryhealth;
	}
	public void setCurrentbatteryhealth(long currentbatteryhealth) {
		this.currentbatteryhealth = currentbatteryhealth;
	}
	public String getCurrentstatus() {
		return currentstatus;
	}
	public void setCurrentstatus(String currentstatus) {
		this.currentstatus = currentstatus;
	}
	
	@OneToMany(mappedBy="house_id")
	Set<NotificationDetails> notifications;

}
