package com.repetovskyi.websecurity.repository;

/*
  @author   Repetovskyi V.
  @project   web-security
  @class  DeviceRepository
  @version  1.0.0 
  @since 9/27/2026 - 20.56
*/

import com.repetovskyi.websecurity.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
}
