package com.techolution.mauritius.smartwater;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "influx")
public class InfluxProperties {
	
	private String url;
	private String username;
	private String password;
	private String datatimezone;
	
	

	public String getDatatimezone() {
		return datatimezone;
	}

	public void setDatatimezone(String datatimezone) {
		this.datatimezone = datatimezone;
	}

	private String retentionPolicyName;
	public String getRetentionPolicyName() {
		return retentionPolicyName;
	}

	public void setRetentionPolicyName(String retentionPolicyName) {
		this.retentionPolicyName = retentionPolicyName;
	}

	private String dbname;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

}
