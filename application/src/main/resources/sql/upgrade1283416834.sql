insert into event_sources (id, entity_type_id, event_id, description) values
    (7, (select entity_type_id from entity_master where entity_type = 'Loan'), 2, 'View Loan');
insert into event_sources (id, entity_type_id, event_id, description) values
    (8, (select entity_type_id from entity_master where entity_type = 'Group'), 2, 'View Group');
insert into event_sources (id, entity_type_id, event_id, description) values
    (9, (select entity_type_id from entity_master where entity_type = 'Center'), 1, 'Create Center');
insert into event_sources (id, entity_type_id, event_id, description) values
    (10, (select entity_type_id from entity_master where entity_type = 'Center'), 2, 'View Center');

insert into events (id, name) values (5, 'Disburse');
insert into event_sources (id, entity_type_id, event_id, description) values
    (11, (select entity_type_id from entity_master where entity_type = 'Loan'), 5, 'Disburse Loan');
insert into event_sources (id, entity_type_id, event_id, description) values
    (12, (select entity_type_id from entity_master where entity_type = 'Savings'), 1, 'Create Savings');
insert into event_sources (id, entity_type_id, event_id, description) values
    (13, (select entity_type_id from entity_master where entity_type = 'Savings'), 2, 'View Savings');