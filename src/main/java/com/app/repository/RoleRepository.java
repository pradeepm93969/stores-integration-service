package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.Role;

public interface RoleRepository extends JpaRepository<Role,String>, JpaSpecificationExecutor<Role> {
	
	Optional<Role> findByName(String name);

}
