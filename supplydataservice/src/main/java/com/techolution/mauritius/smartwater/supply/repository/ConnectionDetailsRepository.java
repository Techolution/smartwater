package com.techolution.mauritius.smartwater.supply.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techolution.mauritius.smartwater.supply.domain.MeterConnection;



@Repository

public interface ConnectionDetailsRepository extends  CrudRepository<MeterConnection,Long> {

}
