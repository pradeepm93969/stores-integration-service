package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.SchedulerConfiguration;

public interface SchedulerConfigurationRepository extends JpaRepository<SchedulerConfiguration, String>, JpaSpecificationExecutor<SchedulerConfiguration> {

}
