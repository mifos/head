ALTER TABLE survey ENGINE = InnoDB;
ALTER TABLE questions ENGINE = InnoDB;
ALTER TABLE ppi_survey ENGINE = InnoDB;
ALTER TABLE ppi_survey_instance ENGINE = InnoDB;
ALTER TABLE ppi_likelihoods ENGINE = InnoDB;
ALTER TABLE question_choices ENGINE = InnoDB;
ALTER TABLE survey_instance ENGINE = InnoDB;
ALTER TABLE survey_questions ENGINE = InnoDB;
ALTER TABLE survey_response ENGINE = InnoDB;

drop table if exists sections_questions;
drop table if exists sections;
drop table if exists question_group_event_sources;
drop table if exists event_sources;
drop table if exists events;
drop table if exists question_group;

create table question_group(
  id integer auto_increment not null,
  title varchar(200) not null,
  date_of_creation date not null,
  state integer not null,
  primary key (id)
) engine=innodb character set utf8;

create table events (
    id integer not null,
    name varchar(50) not null,
    primary key (id)
) engine=innodb character set utf8;

create table event_sources (
    id integer not null,
    entity_type_id  smallint not null,
    event_id integer not null,
    description varchar(200) not null,
    primary key (id),
    foreign key (entity_type_id) references entity_master(entity_type_id),
    foreign key (event_id) references events(id)
) engine=innodb character set utf8;

create table question_group_event_sources(
    id integer auto_increment not null,
    question_group_id integer not null,
    event_source_id integer not null,
    primary key (id),
    foreign key (question_group_id) references question_group(id),
    foreign key (event_source_id) references event_sources(id)
) engine=innodb character set utf8;

create table sections(
    id integer auto_increment not null,
    question_group_id integer not null,
    name varchar(50) not null,
    sequence_number integer not null,
    primary key (id),
    foreign key (question_group_id) references question_group(id)
) engine=innodb character set utf8;

create table sections_questions(
    id integer auto_increment not null,
    section_id integer not null,
    question_id integer not null,
    is_required tinyint default 0,
    sequence_number integer not null,
    primary key (id),
    foreign key (section_id) references sections(id),
    foreign key (question_id) references questions(question_id)
) engine=innodb character set utf8;

insert into events (id, name) values (1, 'Create'), (2, 'View');
insert into event_sources (id, entity_type_id, event_id, description) values
    (1, (select entity_type_id from entity_master where entity_type = 'Client'), 1, 'Create Client');

update database_version set database_version = 262 where database_version = 261;