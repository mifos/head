update office set search_id = concat(search_id,'.') where search_id not like '%.';
