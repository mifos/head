alter table holiday
    drop foreign key holiday_ibfk_1;

alter table holiday
    drop primary key;

alter table holiday
    add column holiday_id int not null auto_increment primary key;
    
create table office_holiday (
  office_id smallint,
  holiday_id int,
  primary key(office_id, holiday_id),
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(holiday_id)
    references holiday(holiday_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

update office_holiday, office, holiday 
    set office_holiday.office_id = office.office_id, 
        office_holiday.holiday_id = holiday.office_id 
    where office.office_id = holiday.office_id;

alter table holiday
    drop column office_id;
    
update database_version set database_version = 246 where database_version = 245;
