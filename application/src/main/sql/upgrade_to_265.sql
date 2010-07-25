insert into event_sources (id, entity_type_id, event_id, description) values
    (2, (select entity_type_id from entity_master where entity_type = 'Loan'), 1, 'Create Loan');

update database_version set database_version = 265 where database_version = 264;