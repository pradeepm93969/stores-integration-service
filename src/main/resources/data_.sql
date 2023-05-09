INSERT INTO OAUTH_CLIENT_DETAILS (
	ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,ACCESS_TOKEN_VALIDITY,AUTHORITIES,AUTHORIZED_GRANT_TYPES,CLIENT_SECRET,
	REFRESH_TOKEN_VALIDITY,RESOURCE_IDS,SCOPE,IS_DEFAULT)
	VALUES
	('FRONT_END_UI',now(),'admin','admin',now(),3600,'reset_password,login,verify_token','password,refresh_token',
    'FRONT_END_UI',3600,NULL,NULL,1),
	('THIRD_PARTY',now(),'admin','admin',now(),600,'','client_credentials',
    'THIRD_PARTY',3600,NULL,NULL,0);
    
	
INSERT INTO PERMISSION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,NAME,PERMISSION_GROUP) VALUES
	 ('MANAGE_CLIENTS',now(),'admin','admin',now(),'Create-Update-Delete','Client Management'),
	 ('READ_CLIENTS',now(),'admin','admin',now(),'Read','Client Management'),
	 ('MANAGE_ROLES',now(),'admin','admin',now(),'Create-Update-Delete','Role Management'),
	 ('READ_ROLES',now(),'admin','admin',now(),'Read','Role Management'),
	 ('MANAGE_INTEGRATIONS',now(),'admin','admin',now(),'Create-Update-Delete','Integrations Management'),
	 ('READ_INTEGRATIONS',now(),'admin','admin',now(),'Read','Integrations Management'),
	 ('MANAGE_USERS',now(),'admin','admin',now(),'Create-Update-Delete','User Management'),
	 ('READ_USERS',now(),'admin','admin',now(),'Read','User Management'),
	 ('MANGE_CONFIGURATIONS',now(),'admin','admin',now(),'Create-Update-Delete','Configuration'),
	 ('READ_CONFIGURATIONS',now(),'admin','admin',now(),'Read','Configuration'),
	 ('MANGE_SIDEBARS',now(),'admin','admin',now(),'Create-Update-Delete','Side Bar Configuration'),
	 ('READ_SIDEBARS',now(),'admin','admin',now(),'Read','Side Bar Configuration');

INSERT INTO ROLE (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,NAME) VALUES
	 ('ROLE_ADMIN',now(),'admin','admin',now(),'Admin User'),
	 ('ROLE_USER',now(),'admin','admin',now(),'Normal User');

INSERT INTO ROLE_PERMISSION (ROLE_ID, PERMISSION_ID) VALUES
    ('ROLE_USER','READ_CLIENTS'),
    ('ROLE_USER','MANAGE_CLIENTS');

INSERT INTO USER (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,
USERNAME,PASSWORD,EMAIL,FIRST_NAME,LAST_NAME,AVATAR_IMAGE,MOBILE_NUMBER,MOBILE_NUMBER_COUNTRY_CODE,
ENABLED,INVALID_LOGIN_ATTEMPTS) 
VALUES (1,now(),'admin','admin',now(),
'pradeepm93969@gmail.com', '{bcrypt}$2a$10$/DA7fAreo3CbKOdTIb0fq.fCDSTnZuYxZXF7Mf6EeGK1Kpa7LHewS', 'pradeepm93969@gmail.com','admin','admin','Male-9.jpg',543306145,971,
1,0),
(2,now(),'admin','admin',now(),
'user@user.com', '{bcrypt}$2a$10$/DA7fAreo3CbKOdTIb0fq.fCDSTnZuYxZXF7Mf6EeGK1Kpa7LHewS', 'user@user.com','user','user','Male-9.jpg',9999999999,91,
1,0);

INSERT INTO USER_ROLE (USER_ID, ROLE_ID)
    VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER');
    
INSERT INTO EMAIL_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,ALLOWED_DOMAIN,BODY,ENABLED,SUBJECT) 
VALUES ('PASSWORD_RESET',now(),'admin','admin', now(),'',
'Hi ${FIRST_NAME} <br><br> Please navigate to below page to reset your password: 
<br><br> <a href=\"${ORIGIN}#/public/changePassword?token=${TOKEN}\">Reset Your Password</a><br><br> 
This is an automatically generated e-mail. Please do not reply.',1,'Your Password Reset Email');

INSERT INTO SMS_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,ENABLED,MESSAGE,WHITELISTED,WHITELISTED_NUMBERS) 
VALUES ('PASSWORD_RESET',now(),'admin','admin', now(),1,
'Hi ${FIRST_NAME} You have requested for password reset',1,'971543306145');

INSERT INTO API_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,
URL,METHOD,USERNAME,PASSWORD,CUSTOM_VALUE,AUTHENTICATION_TYPE,CUSTOM_HEADER,TIMEOUT_IN_SECONDS,SYSTEM_NAME) 
VALUES ('CNS_SMS',now(),'admin','admin', now(),
'https://notification.thepetstore.com:9453/cns/message','POST','last-mile-service','1c186ba9-21ee-48fe-9797-7e9387296e4c',
NULL,'BASIC',NULL,2,'CNS');

INSERT INTO COMMON_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,VALUE) VALUES 
('ALERT_EMAILS',now(),'admin','admin', now(),'pradeep.manjunath@goli.pk'),
('EMAIL_FROM_ADDRESS',now(),'admin','admin', now(),'no-reply@goli.pk'),
('DEFAULT_PASSWORD',now(),'admin','admin', now(),'Password@123'),
('ACCOUNT_COOLDOWN_TIME_IN_MIN',now(),'admin','admin', now(),'60'),
('MAX_INVALID_LOGIN_ATTEMPTS',now(),'admin','admin', now(),'10');

INSERT INTO SCHEDULER_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,ENABLED, STATUS, 
THREAD_COUNT, RETRY_COUNT, BATCH_SIZE, RESTART_SECONDS, EXPONENTIAL_FACTOR, NEXT_RUN_SECONDS, DEBUG) 
VALUES ('SMS_SCHEDULER',now(),'admin','admin', now(),1,'STOPPED',1,5,250,120,1,60,0);

INSERT INTO SIDE_BAR_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,ENABLED, HEADER, ROOT, NAME,
ICON, PARENT_ID, LINK, AUTHORITIES, SEQUENCE) 
VALUES 
('DASHBOARD',now(),'admin','admin', now(),1,0,1,'Dashboard','fa-home', '', '/user-management/dashboard', '',0),
('ADMIN_SETTINGS',now(),'admin','admin', now(),1,1,1,'Admin Settings','', '', '', '',9999),
('SIDE_BAR_MANAGEMENT',now(),'admin','admin', now(),1,0,1,'Side Bar management','fa-door-open', '', '/admin/sideBarManagement', 'ROLE_ADMIN',10000),
('CLIENT_MANAGEMENT',now(),'admin','admin', now(),1,0,1,'Client management','fa-handshake', '', '/admin/clientManagement', 'ROLE_ADMIN',10100),
('INTEGRATION_MANAGEMENT',now(),'admin','admin', now(),1,0,1,'Integrations','fa-exchange-alt', '', '', 'ROLE_ADMIN',10300),
('INTEGRATION_MANAGEMENT_API_CONFIGURATIONS',now(),'admin','admin', now(),1,0,0,'API Configurations','fa-cog', 'INTEGRATION_MANAGEMENT', '/admin/apiConfigurations', 'ROLE_ADMIN',10310),
('INTEGRATION_MANAGEMENT_INTEGRATION_LOG',now(),'admin','admin', now(),1,0,0,'Integration Log','fa-binoculars', 'INTEGRATION_MANAGEMENT', '/admin/integrationLog', 'ROLE_ADMIN',10320),
('OTHER_CONFIGURATIONS',now(),'admin','admin', now(),1,0,1,'Other Configurations','fa-cogs', '', '', 'ROLE_ADMIN',10400),
('OTHER_CONFIGURATIONS_COMMON_CONFIGURATIONS',now(),'admin','admin', now(),1,0,0,'Common Configurations','fa-cog', 'OTHER_CONFIGURATIONS', '/admin/commonConfigurations', 'ROLE_ADMIN',10410),
('OTHER_CONFIGURATIONS_EMAIL_CONFIGURATIONS',now(),'admin','admin', now(),1,0,0,'Email Configurations','fa-envelope', 'OTHER_CONFIGURATIONS', '/admin/emailConfigurations', 'ROLE_ADMIN',10420),
('OTHER_CONFIGURATIONS_SMS_CONFIGURATIONS',now(),'admin','admin', now(),1,0,0,'SMS Configurations','fa-mobile-alt', 'OTHER_CONFIGURATIONS', '/admin/smsConfigurations', 'ROLE_ADMIN',10430),
('OTHER_CONFIGURATIONS_SCHEDULER_CONFIGURATIONS',now(),'admin','admin', now(),1,0,0,'Scheduler Configurations','fa-clock', 'OTHER_CONFIGURATIONS', '/admin/schedulerConfigurations', 'ROLE_ADMIN',10440),
('USER_SETTINGS',now(),'admin','admin', now(),1,1,1,'User Settings','', '','','',10999),
('USER_MANAGEMENT',now(),'admin','admin', now(),1,0,1,'User Management','fa-users-cog', 'USER_SETTINGS','/admin/userManagement','ROLE_ADMIN,ROLE_USER',11000),
('ROLE_MANAGEMENT',now(),'admin','admin', now(),1,0,1,'Role Management','fa-atom', 'USER_SETTINGS', '/admin/roleManagement', 'ROLE_ADMIN',12000);