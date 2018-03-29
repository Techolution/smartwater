package com.techolution.mauritius.smartwater.connection.domain;

public class BatteryData extends Data {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int totalCapacity;
	private int currentCapacity;
	
	private int totalPower;
	private int currentPower;
	
	private int currentHealthPercentage;
	
	private String healthStatus;
	
	public String getHealthStatus() {
		return healthStatus;
	}

	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}

	private double temperature;

	public int getTotalCapacity() {
		return totalCapacity;
	}

	public void setTotalCapacity(int totalCapacity) {
		this.totalCapacity = totalCapacity;
	}

	public int getCurrentCapacity() {
		return currentCapacity;
	}

	public void setCurrentCapacity(int currentCapacity) {
		this.currentCapacity = currentCapacity;
	}

	public int getTotalPower() {
		return totalPower;
	}

	public void setTotalPower(int totalPower) {
		this.totalPower = totalPower;
	}

	public int getCurrentPower() {
		return currentPower;
	}

	public void setCurrentPower(int currentPower) {
		this.currentPower = currentPower;
	}

	public int getCurrentHealthPercentage() {
		return currentHealthPercentage;
	}

	public void setCurrentHealthPercentage(int currentHealthPercentage) {
		this.currentHealthPercentage = currentHealthPercentage;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

}
