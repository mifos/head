insert into lookup_value(lookup_id,entity_id,lookup_name)
    values((select max(lv.lookup_id)+1 from lookup_value lv),87,'Permissions-CanUseAccountingIntegration');
insert into lookup_value_locale(locale_id, lookup_id, lookup_value)
    values(1,(select lookup_id from lookup_value where entity_id =87 and lookup_name='Permissions-CanUseAccountingIntegration'),null);
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)values
    (246,196,
    (select lookup_id from lookup_value where entity_id =87 and lookup_name='Permissions-CanUseAccountingIntegration'),
    (select lookup_id from lookup_value where entity_id =87 and lookup_name='Permissions-CanUseAccountingIntegration'));
insert into roles_activity values (246,1);