package com.techolution.mauritius.smartwater.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techolution.mauritius.smartwater.domain.MeterConnection;

@Repository

public interface ConnectionDetailsRepository extends  CrudRepository<MeterConnection,Long> {

}
