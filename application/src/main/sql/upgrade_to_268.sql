insert into events (id, name) values (3, 'Approve'), (4, 'Close');
insert into event_sources (id, entity_type_id, event_id, description) values
    (4, (select entity_type_id from entity_master where entity_type = 'Group'), 1, 'Create Group');
insert into event_sources (id, entity_type_id, event_id, description) values
    (5, (select entity_type_id from entity_master where entity_type = 'Loan'), 3, 'Approve Loan');
insert into event_sources (id, entity_type_id, event_id, description) values
    (6, (select entity_type_id from entity_master where entity_type = 'Client'), 4, 'Close Client');

create table question_choice_tags(
    id integer auto_increment not null,
    choice_id integer not null,
    tag_text varchar(50) not null,
    primary key (id),
    foreign key (choice_id) references question_choices(choice_id),
    unique(tag_text)
) engine=innodb character set utf8;

alter table question_group add column is_ppi tinyint not null default 0;

update database_version set database_version = 268 where database_version = 267;