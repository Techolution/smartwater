package com.techolution.mauritius.smartwater.notification.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techolution.mauritius.smartwater.notification.domain.NotificationDetails;





@Repository

public interface NotificationDetailsRepository extends  CrudRepository<NotificationDetails,Long> {
	
	
	 //@Query("SELECT nt.meter_id,nt.status,nt.problem_date,nt.problem_title,nt.priority,nt.information FROM Notificationdetails nt join meter_connection mc on mc.house_id=nt.meter_id where nt.issuestatus = :issuestatus") 
	//public List<NotificationDetails> findByIssuestatus(@Param("issuestatus") String issuestatus);
	public List<NotificationDetails> findByIssuestatus( String issuestatus);
	
	
	//public List<NotificationDetails> getAlertStatusByCount( String issuestatus);

}
