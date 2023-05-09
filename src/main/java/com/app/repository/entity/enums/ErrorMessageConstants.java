package com.app.repository.entity.enums;

public class ErrorMessageConstants {
	
	/**
	 * Error messages related to Database opertaion
	 */
	public static final String ENTITY_ALREADY_IN_USE ="{0} is already taken!";
	public static final String ENTITY_NOT_FOUND ="{0} Not Found in Database";

	/**
	 * Error messages related to Role entity
	 */
	public static final String ROLE_LINKED_WITH_USERS ="ROLE_LINKED_WITH_USERS|Role is linked with users, Kindly remove this role from all the users";
	public static final String DEFAULT_CLIENT_DELETE ="DEFAULT_CLIENT_DELETE|default client cannot be deleted";
	public static final String DEFAULT_ADMIN_DELETE = "DEFAULT_ADMIN_DELETE|First Admin cannot be deleted";
	
	
}
