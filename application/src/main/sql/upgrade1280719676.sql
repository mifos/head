insert into event_sources (id, entity_type_id, event_id, description) values
    (3, (select entity_type_id from entity_master where entity_type = 'Client'), 2, 'View Client');
