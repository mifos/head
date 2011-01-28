insert into lookup_value(lookup_id,entity_id,lookup_name)
    values((select max(lv.lookup_id)+1 from lookup_value lv), 37, 'InterestTypes-DecliningPrincipalBalance');
insert into interest_types (interest_type_id, lookup_id, category_id, descripton)
    values(5,
            (select lookup_id from lookup_value
                where entity_id =37 and lookup_name='InterestTypes-DecliningPrincipalBalance'),
           1,'InterestTypes-DecliningPrincipalBalance');