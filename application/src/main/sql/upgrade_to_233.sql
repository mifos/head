update office set search_id = concat(search_id,'.') where search_id not like '%.';

update database_version set database_version = 233 where database_version = 232;