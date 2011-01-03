create table question_group_instance(
    id integer auto_increment not null,
    question_group_id integer not null,
    entity_id integer not null,
    date_conducted date not null,
    completed_status smallint not null,
    created_by integer not null,
    version_id integer not null,
    primary key (id),
    foreign key (question_group_id) references question_group(id)
) engine=innodb character set utf8;

create table question_group_response(
    id integer auto_increment not null,
    question_group_instance_id integer not null,
    sections_questions_id integer not null,
    response varchar(200) not null,
    primary key (id),
    foreign key (sections_questions_id) references sections_questions(id),
    foreign key (question_group_instance_id) references question_group_instance(id)
) engine=innodb character set utf8;