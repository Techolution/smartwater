package com.techolution.mauritius.smartwater.supply.repository;




import javax.management.Notification;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository

public interface ConnectionDetailsRepository extends  CrudRepository<Notification,Long> {

}
