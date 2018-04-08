package com.techolution.mauritius.smartwater.reports.domain;

import java.io.Serializable;

public class ReportData implements Serializable {
	
	private String reportPath;

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

}
