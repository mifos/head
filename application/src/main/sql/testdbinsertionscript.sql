/*
This file contains test data for the junit tests.
*/

/* Area office It belongs to the head office created by master scripts */
insert into office(office_id, parent_office_id, global_office_num, status_id, office_code_id, office_level_id,  search_id, office_short_name, max_child_count,local_remote_flag, display_name, created_by, created_date, updated_by, updated_date, version_no)
 		values(2,1,'0002',1,null,4,'1.1.1.','MIF2',2,1,'TestAreaOffice',null,null,null,null,1);
 		
insert into office_address (office_address_id, office_id, address_1, address_2, address_3, 
city, state, country, zip, telephone) 
values(2,2,'add2',null,null,null,null,null,null,null);

/* Branch office It belongs to the area office created by test script statements above*/
insert into office(office_id, parent_office_id, global_office_num, status_id, office_code_id, office_level_id,  search_id, office_short_name, max_child_count,local_remote_flag, display_name, created_by, created_date, updated_by, updated_date, version_no)
 		values(3,2,'0003',1,null,5,'1.1.1.1.','MIF3',2,1,'TestBranchOffice',null,null,null,null,1); 		

insert into office_address (office_address_id, office_id, address_1, address_2, address_3, 
city, state, country, zip, telephone) 
values(3,3,'add3',null,null,null,null,null,null,null);


 
 /*Non loan officer belonging to branch office with office id 3 */		
insert into personnel(personnel_id,level_id,global_personnel_num,office_id,
	title,personnel_status,preferred_locale,
	search_id,max_child_count,password,login_name,email_id,password_changed,
	display_name,created_by,created_date,updated_by,updated_date,
	no_of_tries,last_login,locked,version_no)
values(2,2,'2',3,
	1,1,1,
	null,1,null,'nonloanofficer',null,1,
	'non loan officer',1,null,1,null,
	4,null,0,0);

insert into personnel_details values(2,'testnon loan officer',null,null,'MFI','123','1979-12-12',null,50,null,null,null,'Bangalore',null,null,'Bangalore','Bangalore','Bangalore',null,null);	

insert into personnel_role (personnel_role_id, role_id, personnel_id) 
values(2,1,2);
/*Loan officer belonging to branch*/
insert into personnel(personnel_id,level_id,global_personnel_num,office_id,
	title,personnel_status,preferred_locale,
	search_id,max_child_count,password,login_name,email_id,password_changed,
	display_name,created_by,created_date,updated_by,updated_date,
	no_of_tries,last_login,locked,version_no) 
values(3,1,'3',3,
	1,1,1,
	null,1,null,'loanofficer',null,1,
	'loan officer',1,null,1,null,
	4,null,0,0);

insert into personnel_details values(3,'testloan officer',null,null,'MFI','123','1979-12-12',null,50,null,null,null,'Bangalore',null,null,'Bangalore','Bangalore','Bangalore',null,null);

insert into personnel_role (personnel_role_id, role_id, personnel_id) 
values(3,1,3);

insert into role(role_id,role_name,version_no,created_by,created_date,updated_by,updated_date)
values(2,'SavingTestPermisson',1,null,null,null,null);
insert into roles_activity(activity_id,role_id)
values(182,2);
insert into roles_activity(activity_id,role_id)
values(183,2);

update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'SecondLastName' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'SecondLastName' and entity_id = 17;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'GovernmentId' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'GovernmentId' and entity_id = 17;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'ExternalId' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'ExternalId' and entity_id = 12;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'ExternalId' and entity_id = 20;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'ExternalId' and entity_id = 22;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'Ethinicity' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'Citizenship' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'Handicapped' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'BusinessActivities' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'EducationLevel' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Photo' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'SpouseFatherSecondLastName' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Trained' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Trained' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'TrainedDate' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'TrainedDate' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address1' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'Address1' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address1' and entity_id = 20;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address2' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address2' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address2' and entity_id = 20;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address3' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address3' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address3' and entity_id = 20;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address3' and entity_id = 15;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Address3' and entity_id = 17;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'City' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'City' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'City' and entity_id = 20;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'State' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'State' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'State' and entity_id = 20;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Country' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'Country' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'Country' and entity_id = 20;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'PostalCode' and entity_id = 1;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'PostalCode' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 0
  where field_name = 'PostalCode' and entity_id = 20;  
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'PhoneNumber' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'PhoneNumber' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'PhoneNumber' and entity_id = 20;
update field_configuration set mandatory_flag = 1, hidden_flag = 1
  where field_name = 'PhoneNumber' and entity_id = 17;
update field_configuration set mandatory_flag = 1, hidden_flag = 0
  where field_name = 'PurposeOfLoan' and entity_id = 22;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'CollateralType' and entity_id = 22;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'CollateralNotes' and entity_id = 22;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptId' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptId' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptId' and entity_id = 20;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptId' and entity_id = 21;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptId' and entity_id = 22;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptId' and entity_id = 23;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptDate' and entity_id = 1;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptDate' and entity_id = 12;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptDate' and entity_id = 20;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptDate' and entity_id = 21;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptDate' and entity_id = 22;
update field_configuration set mandatory_flag = 0, hidden_flag = 1
  where field_name = 'ReceiptDate' and entity_id = 23;
update field_configuration set mandatory_flag = 1, hidden_flag = 0
  where field_name = 'SourceOfFund' and entity_id = 22;

/* make a listdatasource for test*/
insert into report_datasource (datasource_id,name,driver,url,username,password) values(1,'test','test','test
','test','test');

/* retain default custom fields for testing purposes */
insert into lookup_entity(entity_id,entity_name,description)values
(58,'ReplacementStatus','Custom Field ReplacementStatus for Client'),
(59,'GRTStaffId','Custom Field GRTStaffId for Group'),
(60,'MeetingTime','Custom Field Meeting Time for Center'),
(61,'DistanceFromBoToCenter','Custom Field Distance from BO To Center'),
(63,'NoOfClientsPerGroup','Custom Field  No. of Clients per Group'),
(64,'NoOfClientsPerCenter','Custom Field No. of Clients per Center'),
(65,'DistanceFromHoToBO','Custom Field Distance from HO To BO for office'),
(66,'ExternalLoanId','Custom Field ExternalID for office'),
(67,'ExternalSavingsId','Custom Field ExternalSavingsId');


insert into lookup_label(label_id,entity_id,locale_id,entity_name)values
(108,58,1,'Replacement Status'),
(110,59,1,'GRT Staff Id'),
(112,60,1,'Meeting Time for Center'),
(114,61,1,'Distance from BO to Center'),
(118,63,1,'Number of Clients per Group'),
(120,64,1,'Number of Clients per Center'),
(122,65,1,'Distance from HO to BO for office'),
(124,66,1,'External Loan Id'),
(126,67,1,'External Savings Id');


/* The table Custom Field Definition will contain the additional information fields that an MFI configure Mifosthat will be required to be shown for a client , group etc for the MFI - Configuration */
/* Client*/
insert into custom_field_definition(field_id,entity_id,field_type,entity_type,mandatory_flag,level_id,default_value) values 
(3,58,2,1,0,1,null),
/*Group*/
(4,59,2,12,0,2,null),
/*Center*/
(5,60,2,20,0,3,null),
(6,61,1,20,0,3,null),
/*Personnel*/
(9,62,2,17,0,1,null),
/*Office*/
(10,62,2,15,0,1,null),
(11,63,1,15,1,2,null),
(12,64,1,15,1,2,null),
(13,65,1,15,0,2,null),
/*Loan*/
(7,66,2,22,0,1,null),
/*Savings*/
(8,67,2,21,0,1,null);

/* The table Personnelcustom field contains the additional information of the default user  - Configuration */
insert into personnel_custom_field (field_id,personnel_id,field_value)
values(9,1,'');

insert into office_custom_field (office_id,field_id,field_value) values
(1,10,''),
(1,11,''),
(1,12,''),
(1,13,'');


