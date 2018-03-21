package com.techolution.mauritius.smartwater.connection.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Dfj implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String Status;
	private String Error_Code;
	private String Error_Dis;
	
	
	
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getError_Code() {
		return Error_Code;
	}
	public void setError_Code(String error_Code) {
		Error_Code = error_Code;
	}
	public String getError_Dis() {
		return Error_Dis;
	}
	public void setError_Dis(String error_Dis) {
		Error_Dis = error_Dis;
	}
	
	

}
