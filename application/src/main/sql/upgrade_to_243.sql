create unique index prd_offering_name_idx on prd_offering (prd_offering_name);

update database_version set database_version = 243 where database_version = 242;