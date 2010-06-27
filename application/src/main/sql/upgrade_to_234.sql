create index cust_government_idx on customer (government_id);

update database_version set database_version = 234 where database_version = 233;