package com.app.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.exception.CustomApplicationException;
import com.app.modal.request.ChangePasswordRequest;
import com.app.modal.request.UpdateUserRequest;
import com.app.repository.entity.SideBarConfiguration;
import com.app.repository.entity.User;
import com.app.service.UserService;
import com.app.util.JwtUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/currentUser")
@Tag(name = "3. User Management")
public class CurrentUserController {
	
	@Autowired
	private UserService service;
	
	@Autowired
    private JwtUtil jwtUtil;
	
	@PutMapping("/updateProfile")
	public User updateProfile(@Valid @RequestBody UpdateUserRequest request) {
		return service.currentUserUpdateProfile(getCurrentUserId(), request);
	}
	
	@PutMapping("/updatePassword")
	public User updatePassword(@Valid @RequestBody ChangePasswordRequest request) {
		return service.currentUserUpdatePassword(getCurrentUserId(), request);
	}
	
	@GetMapping("")
	public User getUser() {
		return service.getById(getCurrentUserId());
	}
	
	@GetMapping("/sideBar")
	public List<SideBarConfiguration> getSideBar() {
		return service.getSideBar(getCurrentUserId());
	}
	
	private Long getCurrentUserId() {
		String token = jwtUtil.extractJwtFromRequest();
		if (StringUtils.isBlank(token)) {
			throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "Unauthorized", "Unuthrized access");
		} 
		return Long.parseLong(jwtUtil.getUserIdFromToken(token));
	}
}

