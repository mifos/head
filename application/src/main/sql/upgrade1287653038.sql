select max(lookup_id)+1 into @next_lookup_id from lookup_value;
insert into lookup_value(lookup_id,entity_id,lookup_name)
    values(@next_lookup_id, 37, 'InterestTypes-DecliningPrincipalBalance');
insert into interest_types (interest_type_id, lookup_id, category_id, descripton)
    values(5,@next_lookup_id,1,'InterestTypes-DecliningPrincipalBalance');