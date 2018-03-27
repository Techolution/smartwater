package com.techolution.mauritius.smartwater.domain;

import java.io.Serializable;

public class TotalConsolidatedDeviceStatus implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int totalDevicesCount;
	private int numberOfDevicesWorking;
	private int numberOfDevicesNotWorking;
	private int numberOfDevicesWarning;
	private int numberOfDevicesInactive;
	private int numberOfDevicesNotWorkingPreviousBucket;
	public int getNumberOfDevicesNotWorkingPreviousBucket() {
		return numberOfDevicesNotWorkingPreviousBucket;
	}
	public void setNumberOfDevicesNotWorkingPreviousBucket(int numberOfDevicesNotWorkingPreviousBucket) {
		this.numberOfDevicesNotWorkingPreviousBucket = numberOfDevicesNotWorkingPreviousBucket;
	}
	public TotalConsolidatedDeviceStatus(int totalDevices, int workingDevices, int notworkingdevices,
			int numberOfDevicesNotWorkingPreviousBucket,int numberOFDevicesInactive,int numberOFDevicesWarning) {
		super();
		this.totalDevicesCount = totalDevices;
		this.numberOfDevicesWorking = workingDevices;
		this.numberOfDevicesNotWorking = notworkingdevices;
		this.numberOfDevicesNotWorkingPreviousBucket = numberOfDevicesNotWorkingPreviousBucket;
		this.numberOfDevicesInactive=numberOFDevicesInactive;
		this.numberOfDevicesWarning=numberOFDevicesWarning;
	}
	public int getNumberOfDevicesWarning() {
		return numberOfDevicesWarning;
	}
	public void setNumberOfDevicesWarning(int numberOfDevicesWarning) {
		this.numberOfDevicesWarning = numberOfDevicesWarning;
	}
	public int getNumberOfDevicesInactive() {
		return numberOfDevicesInactive;
	}
	public void setNumberOfDevicesInactive(int numberOfDevicesInactive) {
		this.numberOfDevicesInactive = numberOfDevicesInactive;
	}
	public int getTotalDevicesCount() {
		return totalDevicesCount;
	}
	public void setTotalDevicesCount(int totalDevicesCount) {
		this.totalDevicesCount = totalDevicesCount;
	}
	public int getNumberOfDevicesWorking() {
		return numberOfDevicesWorking;
	}
	public void setNumberOfDevicesWorking(int numberOfDevicesWorking) {
		this.numberOfDevicesWorking = numberOfDevicesWorking;
	}
	public int getNumberOfDevicesNotWorking() {
		return numberOfDevicesNotWorking;
	}
	public void setNumberOfDevicesNotWorking(int numberOfDevicesNotWorking) {
		this.numberOfDevicesNotWorking = numberOfDevicesNotWorking;
	}
	
	

}
