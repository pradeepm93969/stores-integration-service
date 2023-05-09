package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.EmailConfiguration;

public interface EmailConfigurationRepository extends JpaRepository<EmailConfiguration, String>, JpaSpecificationExecutor<EmailConfiguration> {

}
