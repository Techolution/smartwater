package com.techolution.mauritius.smartwater.notification.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="notificationdetails")
public class NotificationDetails implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getMeter_id() {
		return meter_id;
	}

	public void setMeter_id(int meter_id) {
		this.meter_id = meter_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getProblem_date() {
		return problem_date;
	}

	public void setProblem_date(Date problem_date) {
		this.problem_date = problem_date;
	}

	public Date getResolved_date() {
		return resolved_date;
	}

	public void setResolved_date(Date resolved_date) {
		this.resolved_date = resolved_date;
	}

	public String getProblem_title() {
		return problem_title;
	}

	public void setProblem_title(String problem_title) {
		this.problem_title = problem_title;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	
	public String getIssuestatus() {
		return issuestatus;
	}

	public void setIssuestatus(String issuestatus) {
		this.issuestatus = issuestatus;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	@Id
	private long id;
	private int meter_id;
	private String status;
	
	private Date problem_date;
	
	private Date resolved_date;
	
	private String problem_title;
	
	private String priority;
	
	private String issuestatus;
	
	private String information;
	
	
	/*@ManyToOne(fetch = FetchType.EAGER)
	@JoinTable(name="connection_details",joinColumns= @JoinColumn(name="meter_id", referencedColumnName = "house_id"))
	private MeterConnection connection ;*/

}
