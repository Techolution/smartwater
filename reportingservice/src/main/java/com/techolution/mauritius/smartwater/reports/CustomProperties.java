package com.techolution.mauritius.smartwater.reports;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
	
	private String dataserviceurl;
	private String csvpath;
	private String containername;
	
	public String getContainername() {
		return containername;
	}
	public void setContainername(String containername) {
		this.containername = containername;
	}
	private String filesharename;
	
	public String getFilesharename() {
		return filesharename;
	}
	public void setFilesharename(String filesharename) {
		this.filesharename = filesharename;
	}
	public String getDataserviceurl() {
		return dataserviceurl;
	}
	public void setDataserviceurl(String dataserviceurl) {
		this.dataserviceurl = dataserviceurl;
	}
	public String getCsvpath() {
		return csvpath;
	}
	public void setCsvpath(String csvpath) {
		this.csvpath = csvpath;
	}
	private String storageconnectionstring;
	public String getStorageconnectionstring() {
		return storageconnectionstring;
	}
	public void setStorageconnectionstring(String storageconnectionstring) {
		this.storageconnectionstring = storageconnectionstring;
	}

}
