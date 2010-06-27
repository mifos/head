/* Added for jira issue 2730 */
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(81,'MaritalStatus',1,0,0),
(82,'NumberOfChildren',1,0,0);

update database_version set database_version = 241 where database_version = 240;
