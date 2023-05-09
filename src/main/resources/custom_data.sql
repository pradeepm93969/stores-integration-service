INSERT INTO PERMISSION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,NAME,PERMISSION_GROUP) VALUES
('ROLE_READ_STORES',now(),'admin','admin',now(),'Read','Store Management'),
('ROLE_MANAGE_STORES',now(),'admin','admin',now(),'Manage','Store Management'),
('ROLE_READ_PRODUCTS',now(),'admin','admin',now(),'Read','Products Management'),
('ROLE_MANAGE_PRODUCTS',now(),'admin','admin',now(),'Manage','Products Management'),
('ROLE_READ_PRODUCT_LINKS',now(),'admin','admin',now(),'Read','Product Links Management'),
('ROLE_MANAGE_PRODUCT_LINKS',now(),'admin','admin',now(),'Manage','Product Links Management');


INSERT INTO SIDE_BAR_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,ENABLED, HEADER, ROOT, NAME,
ICON, PARENT_ID, LINK, AUTHORITIES, SEQUENCE) 
VALUES 
('STORE_INTEGRATION_SETTINGS',now(),'admin','admin', now(),1,1,1,'Store Integration','', '', '', '',999),
('STORE_MANAGEMENT',now(),'admin','admin', now(),1,0,1,'Store Management','fa-store', '', '/custom/storeManagement', 
'ROLE_ADMIN,ROLE_USER',1000),
('PRODUCT_MANAGEMENT',now(),'admin','admin', now(),1,0,1,'Product Management','fa-table-list', '', '/custom/productManagement', 
'ROLE_ADMIN,ROLE_USER',1100),
('PRODUCT_LINKING',now(),'admin','admin', now(),1,0,1,'Product Link','fa-link', '', '/custom/productLink', 
'ROLE_ADMIN,ROLE_USER',1200);

INSERT INTO SCHEDULER_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,ENABLED, STATUS, 
THREAD_COUNT, RETRY_COUNT, BATCH_SIZE, RESTART_SECONDS, EXPONENTIAL_FACTOR, NEXT_RUN_SECONDS, DEBUG) 
VALUES 
('LUMENSOFT_INBOUND_PRICE_SCHEDULER',now(),'admin','admin', now(),1,'STOPPED',10,0,200,120,1,60,0),
('LUMENSOFT_INBOUND_INVENTORY_SCHEDULER',now(),'admin','admin', now(),1,'STOPPED',10,0,200,120,1,60,0),
('MAGENTO_OUTBOUND_PRICE_INVENTORY_SCHEDULER',now(),'admin','admin', now(),1,'STOPPED',10,5,250,120,1,60,0);

INSERT INTO API_CONFIGURATION (ID,CREATED_AT,CREATED_BY,UPDATED_BY,UPDATED_AT,
URL,METHOD,USERNAME,PASSWORD,CUSTOM_VALUE,AUTHENTICATION_TYPE,CUSTOM_HEADER,TIMEOUT_IN_SECONDS,SYSTEM_NAME) 
VALUES ('MAGENTO_LOGIN',now(),'admin','admin', now(),
'https://dev-137.goli.pk/rest/all/V1/integration/admin/token','POST','integrations','Goli@2022-1',
NULL,'BASIC',NULL,2,'MAGENTO'),
('MAGENTO_VENDOR_PRODUCT_UPDATE',now(),'admin','admin', now(),
'https://dev-137.goli.pk/rest/all/V1/b2b/catalog-save','POST',NULL,NULL,
NULL,'BEARER',NULL,2,'MAGENTO');


