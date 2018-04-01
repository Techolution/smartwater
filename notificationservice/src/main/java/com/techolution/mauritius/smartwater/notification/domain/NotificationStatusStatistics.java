package com.techolution.mauritius.smartwater.notification.domain;

import java.io.Serializable;

public class NotificationStatusStatistics implements Serializable {
	
	private long highPriorityCount;
	private long lowPriorityCount;
	private long mediumPriorityCount;
	
	private long numberOfMeters;

	public long getHighPriorityCount() {
		return highPriorityCount;
	}

	public void setHighPriorityCount(long highPriorityCount) {
		this.highPriorityCount = highPriorityCount;
	}

	public long getLowPriorityCount() {
		return lowPriorityCount;
	}

	public void setLowPriorityCount(long lowPriorityCount) {
		this.lowPriorityCount = lowPriorityCount;
	}

	public long getMediumPriorityCount() {
		return mediumPriorityCount;
	}

	public void setMediumPriorityCount(long mediumPriorityCount) {
		this.mediumPriorityCount = mediumPriorityCount;
	}

	public long getNumberOfMeters() {
		return numberOfMeters;
	}

	public void setNumberOfMeters(long numberOfMeters) {
		this.numberOfMeters = numberOfMeters;
	}

}
