package com.app.zcustom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.zcustom.repository.entity.Store;

public interface StoreDetailRepository extends JpaRepository<Store, String>, JpaSpecificationExecutor<Store> {

}
