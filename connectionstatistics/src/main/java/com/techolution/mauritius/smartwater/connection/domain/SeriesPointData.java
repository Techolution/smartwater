package com.techolution.mauritius.smartwater.connection.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SeriesPointData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	private Date timestamp;
	
	private List<KeyValue> tags;
	
	private List<KeyValue> values;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<KeyValue> getTags() {
		return tags;
	}

	public void setTags(List<KeyValue> tags) {
		this.tags = tags;
	}

	public List<KeyValue> getValues() {
		return values;
	}

	public void setValues(List<KeyValue> values) {
		this.values = values;
	}
	
}
