insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(84,'SpouseFatherInformation',1,1,0),
(85,'FamilyDetails',1,1,0);

update database_version set database_version = 257 where database_version = 256;