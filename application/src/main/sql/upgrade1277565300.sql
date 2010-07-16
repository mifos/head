update spouse_father_lookup 
set lookup_id=622
where spouse_father_id=4 and lookup_id=624;

update spouse_father_lookup 
set lookup_id=623
where spouse_father_id=5 and lookup_id=625;

update lookup_entity
set description='This entity is used to track whether the family member is living together with the client or not'
where entity_id=92 and entity_name='LivingStatus';

update lookup_value_locale
set lookup_id=956, lookup_id=625
where lookup_id=1006 and lookup_id=675;
 
update lookup_value
set lookup_id=626
where lookup_id=703 and lookup_name='RepaymentRule-RepaymentMoratorium';

delete from lookup_value where lookup_id=624 and lookup_name='Mother';
delete from lookup_value where lookup_id=625 and lookup_name='Child';

update lookup_value
set lookup_name='Mother', entity_id=52
where lookup_id=622 and lookup_name='Together';

update lookup_value
set lookup_name='Child', entity_id=52
where lookup_id=623 and lookup_name='Not Together';

insert into lookup_value values (620,92,'Together');
insert into lookup_value values (621,92,'NotTogether');


delete from supported_locale where locale_id=47 and locale_name='Hungarian-Hungary';
delete from language where lang_id=11 and lang_name='Hungarian';
delete from lookup_value where lookup_id=702 and lookup_name='Language-Hungarian';

insert into lookup_value (lookup_id,entity_id,lookup_name) values(624,74,'Language-Hungarian');
insert into language (lang_id,lang_name,lang_short_name,lookup_id) values (11,'Hungarian','hu',624);
insert into supported_locale (locale_id,country_id,lang_id,locale_name,default_locale) values(47,51,11,'Hungarian-Hungary',0);



