package com.techolution.mauritius.smartwater.connection.domain;

import lombok.Getter;
import lombok.Setter;

public class TelemetryDerivedRequestData extends TelemetryRequestData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter @Setter
	private String operation;

}
