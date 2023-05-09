package com.app.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.annotation.BasicAuth;
import com.app.annotation.PerformanceMonitoring;
import com.app.modal.request.AuthenticationRequest;
import com.app.modal.request.ChangePasswordRequest;
import com.app.modal.request.ResetPasswordRequest;
import com.app.modal.response.AuthenticationResponse;
import com.app.modal.response.GenericMessageResponse;
import com.app.modal.response.TokenResponse;
import com.app.service.CustomUserDetailsService;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/oauth2")
@Tag(name = "1. Registration And Login")
@SecurityRequirement(name = "basicAuth")
@Validated
public class AuthenticationController {
	
	@Autowired
	private CustomUserDetailsService userService;
	
	@PostMapping("/login")
	@Operation(summary = "Login to application")
	@BasicAuth(permissions = "login")
	@PerformanceMonitoring
	@Bulkhead(name = "authBulkhead", fallbackMethod = "loginFallBack")
	public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
		return ResponseEntity.status(HttpStatus.OK)
                .body(userService.login(authenticationRequest));
	}
	
	public ResponseEntity<AuthenticationResponse> loginFallBack(AuthenticationRequest authenticationRequest, 
			io.github.resilience4j.bulkhead.BulkheadFullException ex) {
        System.out.println("BulkHead applied no further calls are accepted");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10"); //retry after 10 seconds

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) //send retry header
                .body(null);
    }
	
	@PostMapping("/resetPassword")
	@BasicAuth(permissions = "reset_password")
	@PerformanceMonitoring
	@Bulkhead(name = "resetPasswordBulkhead", fallbackMethod = "resetPasswordFallBack")
	public ResponseEntity<GenericMessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
	    userService.resetPassword(resetPasswordRequest);
	    return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericMessageResponse("Success"));
	}
	
	public ResponseEntity<GenericMessageResponse> resetPasswordFallBack(ResetPasswordRequest resetPasswordRequest, 
			io.github.resilience4j.bulkhead.BulkheadFullException ex) {
        System.out.println("BulkHead applied no further calls are accepted");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10"); //retry after 10 seconds

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) //send retry header
                .body(null);
    }
	
	@PostMapping("/verifyResetPasswordToken")
	@BasicAuth(permissions = "reset_password")
	@PerformanceMonitoring
	@Bulkhead(name = "resetPasswordBulkhead", fallbackMethod = "verifyResetPasswordTokenFallBack")
	public ResponseEntity<TokenResponse> verifyResetPasswordToken(@NotBlank @RequestParam("token") String token) {
	    return ResponseEntity.status(HttpStatus.OK)
                .body(userService.verifyResetPasswordToken(token));
	}
	
	public ResponseEntity<String> verifyResetPasswordTokenFallBack(String token, 
			io.github.resilience4j.bulkhead.BulkheadFullException ex) {
        System.out.println("BulkHead applied no further calls are accepted");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10"); //retry after 10 seconds

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) //send retry header
                .body(null);
    }
	
	@PostMapping("/changePassword")
	@BasicAuth(permissions = "reset_password")
	@PerformanceMonitoring
	@Bulkhead(name = "resetPasswordBulkhead", fallbackMethod = "changePasswordFallBack")
	public ResponseEntity<GenericMessageResponse> changePassword(@NotBlank @RequestParam("token") String token, 
			@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
	    userService.changePassword(token, changePasswordRequest.getNewPassword());
	    return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericMessageResponse("Success"));
	}
	
	public ResponseEntity<String> changePasswordFallBack(String token, ChangePasswordRequest changePasswordRequest, io.github.resilience4j.bulkhead.BulkheadFullException ex) {
        System.out.println("BulkHead applied no further calls are accepted");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10"); //retry after 10 seconds

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) //send retry header
                .body(null);
    }
	
	@PostMapping("/verifyAuthToken")
	@BasicAuth(permissions = "verify_token")
	@PerformanceMonitoring
	@Bulkhead(name = "verifyAuthTokenBulkhead", fallbackMethod = "verifyAuthTokenFallBack")
	public ResponseEntity<TokenResponse> verifyAuthToken(@NotBlank @RequestParam("token") String token) {
	    return ResponseEntity.status(HttpStatus.OK)
                .body(userService.verifyAuthToken(token));
	}
	
	public ResponseEntity<String> verifyAuthTokenFallBack(String token, 
			io.github.resilience4j.bulkhead.BulkheadFullException ex) {
        System.out.println("BulkHead applied no further calls are accepted");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10"); //retry after 10 seconds

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) //send retry header
                .body(null);
    }
	
	
}
	
	