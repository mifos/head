drop table if exists question_group_entity_events;
drop table if exists entity_events;
drop table if exists events;

create table events (
    id integer not null,
    name varchar(50) not null,
    primary key (id)
);

create table event_sources (
    id integer not null,
    entity_type_id  smallint not null,
    event_id integer not null,
    description varchar(200) not null,
    primary key (id),
    foreign key (entity_type_id) references entity_master(entity_type_id),
    foreign key (event_id) references events(id)
);

create table question_group_event_sources(
    id integer auto_increment not null,
    question_group_id integer not null,
    event_source_id integer not null,
    primary key (id),
    foreign key (question_group_id) references question_group(id),
    foreign key (event_source_id) references event_sources(id)
);

create table sections(
    id integer auto_increment not null,
    question_group_id integer not null,
    name varchar(50) not null,
    sequence_number integer not null,
    primary key (id),
    foreign key (question_group_id) references question_group(id)
);

create table sections_questions(
    id integer auto_increment not null,
    section_id integer not null,
    question_id integer not null,
    is_required tinyint default 0,
    primary key (id),
    foreign key (section_id) references sections(id),
    foreign key (question_id) references questions(question_id)
);

insert into events (id, name) values (1, 'Create'), (2, 'View');
insert into event_sources (id, entity_type_id, event_id, description) values
    (1, (select entity_type_id from entity_master where entity_type = 'Client'), 1, 'Create Client');
insert into event_sources (id, entity_type_id, event_id, description) values
    (2, (select entity_type_id from entity_master where entity_type = 'Client'), 2, 'View Client');

update database_version set database_version = 260 where database_version = 259;