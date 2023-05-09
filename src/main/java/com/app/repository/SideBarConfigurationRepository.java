package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.SideBarConfiguration;

public interface SideBarConfigurationRepository extends JpaRepository<SideBarConfiguration, String>, JpaSpecificationExecutor<SideBarConfiguration> {
	
	List<SideBarConfiguration> findAllByOrderBySequenceAsc();

}
