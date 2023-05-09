package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.SmsConfiguration;

public interface SmsConfigurationRepository extends JpaRepository<SmsConfiguration, String>, JpaSpecificationExecutor<SmsConfiguration> {

}
