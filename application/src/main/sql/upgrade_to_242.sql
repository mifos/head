create unique index prd_offering_short_name_idx on prd_offering (prd_offering_short_name);

update database_version set database_version = 242 where database_version = 241;