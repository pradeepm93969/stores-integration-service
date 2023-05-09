package com.app.service;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.exception.CustomApplicationException;
import com.app.repository.UserRepository;
import com.app.repository.entity.User;

@Service
public class SpringUserDetailsService implements UserDetailsService {
	
	@Autowired
	private CommonConfigurationService commonConfigurationService;

    @Autowired
    private UserRepository repository;
    
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    	User user = repository.getUserByUsername(name);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        
        if (user.getInvalidLoginAttempts() == Integer.parseInt(commonConfigurationService.getById(
        		"MAX_INVALID_LOGIN_ATTEMPTS").getValue()) 
        		&& OffsetDateTime.now().isBefore(user.getInvalidLoginAt().plusMinutes(
        				Integer.parseInt(commonConfigurationService.getById("ACCOUNT_COOLDOWN_TIME_IN_MIN").getValue())))) {
        	throw new CustomApplicationException(HttpStatus.UNPROCESSABLE_ENTITY, "USER_LOCKED", "User is locked");
        }
        return new CustomUserDetails(user);
    }

}