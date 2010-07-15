update spouse_father_lookup 
set lookup_id=622
where spouse_father_id=4 and lookup_id=624;

update spouse_father_lookup 
set lookup_id=623
where spouse_father_id=5 and lookup_id=625;

update lookup_entity
set description='This entity is used to track whether the family member is living together with the client or not'
where entity_id=92 and entity_name='LivingStatus';
 
update lookup_value
set lookup_id=625 
where lookup_id=675 and lookup_name='Permissions-CanShutdownMifos';

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

update repayment_rule
set repayment_rule_lookup_id=626
where repayment_rule_id=4 and repayment_rule_lookup_id=703;

update activity
set activity_name_lookup_id=625, description_lookup_id=625
where activity_id=234 and parent_id=227 and activity_name_lookup_id=675;

delete from supported_locale where locale_id=47 and locale_name='Hungarian-Hungary';
delete from language where lang_id=11 and lang_name='Hungarian';
delete from lookup_value where lookup_id=702 and lookup_name='Language-Hungarian';

insert into lookup_value (lookup_id,entity_id,lookup_name) values(624,74,'Language-Hungarian');
insert into language (lang_id,lang_name,lang_short_name,lookup_id) values (11,'Hungarian','hu',624);
insert into supported_locale (locale_id,country_id,lang_id,locale_name,default_locale) values(47,51,11,'Hungarian-Hungary',0);

insert into lookup_value (lookup_id) values (625);
insert into lookup_value (lookup_id) values (626);
insert into lookup_value (lookup_id) values (627);
insert into lookup_value (lookup_id) values (628);
insert into lookup_value (lookup_id) values (629);

update activity
set activity_name_lookup_id=625, description_lookup_id=625
where activity_id=234 and activity_name_lookup_id=703;

update activity
set activity_name_lookup_id=627, description_lookup_id=627
where activity_id=235 and activity_name_lookup_id=705;

update activity
set activity_name_lookup_id=628, description_lookup_id=628
where activity_id=236 and activity_name_lookup_id=706;

update activity
set activity_name_lookup_id=629, description_lookup_id=629
where activity_id=237 and activity_name_lookup_id=707;

update lookup_value_locale
set lookup_id=625
where lookup_value_id=956 and lookup_id=703;

update lookup_value_locale
set lookup_id=627
where lookup_value_id=957 and lookup_id=705;

update lookup_value_locale
set lookup_id=628
where lookup_value_id=958 and lookup_id=706;

update lookup_value_locale
set lookup_id=629
where lookup_value_id=959 and lookup_id=707;

update repayment_rule
set repayment_rule_lookup_id=626
where repayment_rule_id=4 and repayment_rule_lookup_id=704;

delete from lookup_value where lookup_id=703;
delete from lookup_value where lookup_id=704;
delete from lookup_value where lookup_id=705;
delete from lookup_value where lookup_id=706;
delete from lookup_value where lookup_id=707;

update lookup_value
set entity_id=87, lookup_name='Permissions-CanShutdownMifos'
where lookup_id=625;

update lookup_value
set entity_id=91, lookup_name='RepaymentRule-RepaymentMoratorium'
where lookup_id=626;

update lookup_value
set entity_id=87, lookup_name='Permissions-CanDefineHoliday'
where lookup_id=627;

update lookup_value
set entity_id=87, lookup_name='Permissions.CanViewDetailedAgingOfPortfolioAtRiskReport'
where lookup_id=628;

update lookup_value
set entity_id=87, lookup_name='Permissions.CanViewGeneralLedgerReport'
where lookup_id=629;