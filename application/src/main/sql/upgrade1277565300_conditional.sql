update lookup_entity
set description='This entity is used to track whether the family member is living together with the client or not'
where entity_name='LivingStatus';

update lookup_value
set lookup_name='NotTogether'
where lookup_name='Not Together';

