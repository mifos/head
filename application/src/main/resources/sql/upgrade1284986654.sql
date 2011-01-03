insert into event_sources (id, entity_type_id, event_id, description) values
    (14, (select entity_type_id from entity_master where entity_type = 'Office'), 1, 'Create Office');
insert into event_sources (id, entity_type_id, event_id, description) values
    (15, (select entity_type_id from entity_master where entity_type = 'Personnel'), 1, 'Create Personnel');