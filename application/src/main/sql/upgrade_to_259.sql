create table events (
    id integer auto_increment not null,
    event_name varchar(50) not null,
    primary key (id)
)engine=innodb character set utf8;

create table entity_events (
    id integer auto_increment not null,
    entity_type_id  smallint not null,
    event_id integer not null,
    description varchar(200) not null,
    primary key (id),
    foreign key (entity_type_id) references entity_master(entity_type_id),
    foreign key (event_id) references events(id)
)engine=innodb character set utf8;

create table question_group_entity_events(
    id integer auto_increment not null,
    question_group_id integer not null,
    entity_event_id integer not null,
    primary key (id),
    foreign key (question_group_id) references question_group(id),
    foreign key (entity_event_id) references entity_events(id)
)engine=innodb character set utf8;

update database_version set database_version = 259 where database_version = 258;
