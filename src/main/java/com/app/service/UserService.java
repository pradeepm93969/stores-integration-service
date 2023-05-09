package com.app.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.app.exception.CustomApplicationException;
import com.app.modal.request.ChangePasswordRequest;
import com.app.modal.request.UpdateUserRequest;
import com.app.repository.UserRepository;
import com.app.repository.entity.Permission;
import com.app.repository.entity.Role;
import com.app.repository.entity.SideBarConfiguration;
import com.app.repository.entity.User;
import com.app.repository.entity.enums.ErrorMessageConstants;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private CommonConfigurationService commonConfigurationService;
	
	@Autowired
	private SideBarConfigurationService sideBarConfigurationService;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	public Page<User> get(Specification<User> spec, Integer pageNo, Integer pageSize, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, 
        		Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
        				? Direction.ASC : Direction.DESC, sortBy));
 
        return repository.findAll(spec, pageable);
	}
	
	public List<User> get(Specification<User> spec, String sortBy, String sortDirection) {
		Sort sort = Sort.by((StringUtils.isBlank(sortDirection) || sortDirection.equalsIgnoreCase("asc")) 
				? Direction.ASC : Direction.DESC, sortBy);
        return repository.findAll(spec, sort);
	}

	@Cacheable(value = "User", key = "#id")
	public User getById(Long id) {
		return repository.findById(id).orElseThrow(() -> 
			new CustomApplicationException(HttpStatus.NOT_FOUND, 
					"USER_NOT_FOUND", "User not found"));
	}

	public User create(User request) {
		if (request.getRoleInput() != null && !request.getRoleInput().isEmpty() 
				&& request.getRoleInput().contains("ROLE_ADMIN"))
			throw new CustomApplicationException(HttpStatus.BAD_REQUEST, "CANNOT_ASSIGN_ROLE_ADMIN", "Cannot assign role admin");
		
		Optional<User> checkOptionalEntity = repository.findByMobileNumberCountryCodeAndMobileNumber(
				request.getMobileNumberCountryCode(), request.getMobileNumber());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(
					HttpStatus.BAD_REQUEST, "MOBILE_NUMBER_TAKEN", "Mobile number is already taken");
		}
		
		checkOptionalEntity = repository.findByEmail(request.getEmail());
		if (checkOptionalEntity.isPresent()) {
			throw new CustomApplicationException(
					HttpStatus.BAD_REQUEST, "EMAIL_TAKEN", "Email is alreday taken");
		}
		
		User user = new User();
		user.setUsername(request.getEmail());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(commonConfigurationService.getById("DEFAULT_PASSWORD").getValue()));
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setMobileNumberCountryCode(request.getMobileNumberCountryCode());
		user.setMobileNumber(request.getMobileNumber());
		user.setAvatarImage(request.getAvatarImage());
		user.setEnabled(true);
		if (request.getRoleInput() != null && !request.getRoleInput().isEmpty())
			user.setRoles(request.getRoleInput().stream().map(s -> roleService.getById(s))
                    .collect(Collectors.toSet()));
		return repository.save(user);
	}

	@CachePut(value = "User", key = "#id")
	public User update(Long id, User request) {
		if (id == 1 || (request.getRoleInput() != null && !request.getRoleInput().isEmpty() && 
				request.getRoleInput().contains("ROLE_ADMIN")))
			throw new CustomApplicationException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Unauthorized access");
		
		User user = getById(id);
		
		Optional<User> checkOptionalEntity = repository.findByMobileNumberCountryCodeAndMobileNumber(
				request.getMobileNumberCountryCode(), request.getMobileNumber());
		if (checkOptionalEntity.isPresent() && checkOptionalEntity.get().getId() != id) {
			throw new CustomApplicationException(
					HttpStatus.BAD_REQUEST, "MOBILE_NUMBER_TAKEN", "Mobile number is already taken");
		}
		
		checkOptionalEntity = repository.findByEmail(request.getEmail());
		if (checkOptionalEntity.isPresent() && checkOptionalEntity.get().getId() != id) {
			throw new CustomApplicationException(
					HttpStatus.BAD_REQUEST, "EMAIL_TAKEN", "Email is alreday taken");
		}
		
		user.setUsername(request.getEmail());
		user.setEmail(request.getEmail());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setMobileNumberCountryCode(request.getMobileNumberCountryCode());
		user.setMobileNumber(request.getMobileNumber());
		user.setAvatarImage(request.getAvatarImage());
		user.setEnabled(user.isEnabled());
		if (request.getRoleInput() != null && !request.getRoleInput().isEmpty())
			user.setRoles(request.getRoleInput().stream().map(s -> roleService.getById(s))
                    .collect(Collectors.toSet()));
			
		return repository.save(user);
	}

	@CacheEvict(value = "User", key = "#id")
	public void delete(Long id) {
		getById(id);
		if (id == 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
					ErrorMessageConstants.DEFAULT_ADMIN_DELETE);
		}
		repository.deleteById(id);
	}

	@CachePut(value = "User", key = "#id")
	public User currentUserUpdateProfile(Long id, @Valid UpdateUserRequest request) {
		User user = getById(id);
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setAvatarImage(request.getAvatarImage());
		return repository.save(user);
	}

	@CachePut(value = "User", key = "#id")
	public User currentUserUpdatePassword(Long id, ChangePasswordRequest request) {
		User user = getById(id);
		user.setPassword(passwordEncoder.encode(request.getConfirmPassword()));
		return repository.save(user);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseInvalidLoginCount(User user) {
    	user.setInvalidLoginAttempts(
    			(user.getInvalidLoginAttempts() + 1 < Integer.parseInt(
    					commonConfigurationService.getById("MAX_INVALID_LOGIN_ATTEMPTS").getValue())) 
				? user.getInvalidLoginAttempts() + 1 
				: Integer.parseInt(commonConfigurationService.getById("MAX_INVALID_LOGIN_ATTEMPTS").getValue()));
		user.setInvalidLoginAt(OffsetDateTime.now());
		repository.save(user);
    }

	@Cacheable(value = "UserSideBar", key = "#id")
	public List<SideBarConfiguration> getSideBar(Long id) {
		User user = getById(id);
		
		//Get User Permissions
		List<String> userPermissions = new ArrayList<>();
		Set<Role> roles = user.getRoles();
		Set<Permission> permissions = null;
		if (roles != null && roles.size() > 0) {
			for (Role role : roles) {
				userPermissions.add(role.getId());
				permissions = role.getPermissions();
				if (permissions != null && permissions.size() > 0) {
					for (Permission permission : permissions) {
						userPermissions.add(permission.getId());
					}
				}
			}
		}
		
		//Get Side Bar
		List<SideBarConfiguration> output = new ArrayList<SideBarConfiguration>();
		List<SideBarConfiguration> allSideBar = sideBarConfigurationService.getAll();
		Map<String, SideBarConfiguration> sideBarConfigurationMap = new HashMap<>();
		boolean grant = false;
		for (SideBarConfiguration ec : allSideBar) {
			grant = false;
			if (StringUtils.isBlank(ec.getAuthorities())) {
				grant = true;
			} else {
				List<String> requiredPermissions = new ArrayList<String>(Arrays.asList(ec.getAuthorities().split(",")));
				requiredPermissions.retainAll(userPermissions);
				if (requiredPermissions.size() > 0) {
					grant = true;
				}
			}
			
			if (grant && ec.isEnabled()) {
				if (ec.isRoot()) {
					output.add(ec);
					sideBarConfigurationMap.put(ec.getId(), ec);
				} else {
					sideBarConfigurationMap.get(ec.getParentId()).getChilds().add(ec);
					sideBarConfigurationMap.put(ec.getId(), ec);
				}
			}
		}
		return output;
	}
	
}