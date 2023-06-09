package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.repository.entity.Client;


public interface ClientRepository  extends JpaRepository<Client, String>, JpaSpecificationExecutor<Client> {

}
