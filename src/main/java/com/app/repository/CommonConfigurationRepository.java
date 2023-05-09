package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.CommonConfiguration;

public interface CommonConfigurationRepository extends JpaRepository<CommonConfiguration, String>, JpaSpecificationExecutor<CommonConfiguration> {

}
