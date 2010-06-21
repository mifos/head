update prd_category_status set prd_category_status_id = 2 where prd_category_status_id = 0;
update prd_category set state = 2 where state = 0;

update database_version set database_version = 215 where database_version = 214;
