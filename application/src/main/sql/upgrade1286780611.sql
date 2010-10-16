create table prd_offering_question_group(
    prd_offering_id smallint not null,
    question_group_id integer not null,
    foreign key (prd_offering_id) references prd_offering(prd_offering_id),
    foreign key (question_group_id) references question_group(id)
) engine=innodb character set utf8;