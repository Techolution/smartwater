package com.techolution.mauritius.smartwater.notification.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techolution.mauritius.smartwater.notification.domain.MeterConnection;


@Repository

public interface ConnectionDetailsRepository extends  CrudRepository<MeterConnection,Long> {

}
