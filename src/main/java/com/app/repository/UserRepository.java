package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.app.repository.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
	
	@Query("SELECT u FROM User u WHERE u.username = :username")
    public User getUserByUsername(String username);

    Optional<User> findByUsername(String name);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByMobileNumberCountryCodeAndMobileNumberOrEmail(int countryCode, String mobileNo, String email);
    
    Optional<User> findByMobileNumberCountryCodeAndMobileNumber(int countryCode, String mobileNo);

}
