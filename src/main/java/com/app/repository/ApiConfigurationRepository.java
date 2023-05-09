package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.ApiConfiguration;

public interface ApiConfigurationRepository extends JpaRepository<ApiConfiguration, String>, JpaSpecificationExecutor<ApiConfiguration> {

}
