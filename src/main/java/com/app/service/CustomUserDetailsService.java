package com.app.service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.app.exception.CustomApplicationException;
import com.app.modal.request.AuthenticationRequest;
import com.app.modal.request.ResetPasswordRequest;
import com.app.modal.response.AuthenticationResponse;
import com.app.modal.response.TokenResponse;
import com.app.repository.UserRepository;
import com.app.repository.entity.Client;
import com.app.repository.entity.User;
import com.app.repository.entity.enums.GrantTypeEnum;
import com.app.repository.entity.enums.JwtTokenTypeEnum;
import com.app.util.ApplicationUtil;
import com.app.util.JwtUtil;

@Service
public class CustomUserDetailsService {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private CommonConfigurationService commonConfigurationService;

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ClientService clientService;
	
	@Autowired 
	private EmailService emailService;
	 
	/**@Autowired 
	private SmsService smsService;**/
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
    	AuthenticationResponse response = new AuthenticationResponse();
    	
    	//Validate if Client has Authorized Grant type
    	String[] credentials = ApplicationUtil.getBasicAuthCredentials();
    	Client client = clientService.getById(credentials[0]);
    	if (!credentials[1].equals(client.getClientSecret()) 
    			|| client.getAuthorizedGrantTypes() == null 
    			|| !client.getAuthorizedGrantTypes().contains(authenticationRequest.getGrantType())) {
    		throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access");
    	}
    	
    	if (authenticationRequest.getGrantType().equalsIgnoreCase(GrantTypeEnum.CLIENT_CREDENTIALS.toString())) {
    		response.setAccessToken(jwtUtil.generateToken(null, client, JwtTokenTypeEnum.CLIENT_CREDENTIALS));
    		
    	} else if (authenticationRequest.getGrantType().equalsIgnoreCase(GrantTypeEnum.REFRESH_TOKEN.toString())) {
    		
    		String jwtToken = jwtUtil.extractJwtFromRequest();
    		if (StringUtils.hasText(jwtToken) 
    				&& jwtUtil.getTypeFromToken(jwtToken).equalsIgnoreCase(JwtTokenTypeEnum.REFRESH_TOKEN.toString())) {
    			
    			User user = repository.findById(Long.parseLong(jwtUtil.getUserIdFromToken(jwtToken)))
    					.orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
    			response.setAccessToken(jwtUtil.generateToken(user, client, JwtTokenTypeEnum.ACCESS_TOKEN));
    			response.setRefreshToken(jwtUtil.generateToken(user, client, JwtTokenTypeEnum.REFRESH_TOKEN));
    		}
    		
    	} else if (authenticationRequest.getGrantType().equalsIgnoreCase(GrantTypeEnum.PASSWORD.toString())) {
    		
    		User user = repository.findByMobileNumberCountryCodeAndMobileNumberOrEmail(
    				authenticationRequest.getMobileNumberCountryCode(), authenticationRequest.getMobileNumber(), authenticationRequest.getEmail())
    				.orElseThrow(() -> new CustomApplicationException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
    		
    		if (!user.isEnabled()) {
    			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "USER_DISABLED", "User is disabled");
    		}
    		
    		if (user.getInvalidLoginAttempts() == Integer.parseInt(commonConfigurationService.getById(
    				"MAX_INVALID_LOGIN_ATTEMPTS").getValue()) 
    				&& OffsetDateTime.now().isBefore(user.getInvalidLoginAt().plusMinutes(
            				Integer.parseInt(commonConfigurationService.getById("ACCOUNT_COOLDOWN_TIME_IN_MIN").getValue())))) {
    			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "USER_LOCKED", "User is locked");
            }
    		
    		try {
    			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
    					user.getUsername(), authenticationRequest.getPassword()));
    			
    			user.setInvalidLoginAttempts(0);
    			user.setInvalidLoginAt(null);
    			repository.save(user);
    			
    			response.setAccessToken(jwtUtil.generateToken(user, client, JwtTokenTypeEnum.ACCESS_TOKEN));
    			response.setRefreshToken(jwtUtil.generateToken(user, client, JwtTokenTypeEnum.REFRESH_TOKEN));
    		} catch (BadCredentialsException e) {
    			userService.increaseInvalidLoginCount(user);
    			throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access");
    		}
    	}
    	
    	return response;
	}
    
	public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
		
		//Validate if Client has Authorized Grant type
		String[] credentials = ApplicationUtil.getBasicAuthCredentials();
    	Client client = clientService.getById(credentials[0]);
    	if (!credentials[1].equals(client.getClientSecret())) {
    		throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access");
    	}
		
		User user = repository.findByMobileNumberCountryCodeAndMobileNumberOrEmail(
				resetPasswordRequest.getMobileNumberCountryCode(), resetPasswordRequest.getMobileNumber(), resetPasswordRequest.getEmail())
                .orElseThrow(() -> new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access"));
        
        String newToken = jwtUtil.generateToken(user, client, JwtTokenTypeEnum.CHANGE_PASSWORD);
        
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("FIRST_NAME", user.getFirstName());
        bodyMap.put("ORIGIN", ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest().getHeader("Origin"));
        bodyMap.put("TOKEN", newToken);
        System.out.println(newToken);
        emailService.sendTemplateMail("PASSWORD_RESET", user.getEmail(), null, null, null, bodyMap);
        
        /**Map<String, String> messageMap = new HashMap<>();
        messageMap.put("FIRST_NAME", user.getFirstName());
        smsService.sendSms(user.getMobileNumberCountryCode(),
        		user.getMobileNumber(), "Your Password reset Link is XXX", Integer.toString(user.getMobileNumberCountryCode()) + 
        		user.getMobileNumber(), Long.toString(user.getId()), "PASSWORD_RESET_SMS");**/
        
	}

	public TokenResponse verifyResetPasswordToken(String token) {
		try { 
			jwtUtil.validateToken(token);
			if (!jwtUtil.getTypeFromToken(token).equalsIgnoreCase(
					JwtTokenTypeEnum.CHANGE_PASSWORD.toString())) {
				throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access"); 
			}
			return jwtUtil.getTokenResponse(token);
		} catch (Exception e) {
			throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access"); 
		}
	}
	
	public TokenResponse verifyAuthToken(String token) {
		try { 
			jwtUtil.validateToken(token);
			if (!jwtUtil.getTypeFromToken(token).equalsIgnoreCase(
					JwtTokenTypeEnum.ACCESS_TOKEN.toString())) {
				throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access"); 
			}
			return jwtUtil.getTokenResponse(token);
		} catch (Exception e) {
			throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access"); 
		} 
	}

	public void changePassword(@NotBlank String token, String newPassword) {
		verifyResetPasswordToken(token);
		User user = repository.findById(Long.parseLong(jwtUtil.getUserIdFromToken(token)))
				.orElseThrow(() -> new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthoried Access"));
		
		user.setPassword(passwordEncoder.encode(newPassword));
		repository.save(user);
	}
	
}