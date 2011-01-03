alter table question_group_response modify column response varchar(500) NOT NULL;
alter table question_choices modify column choice_text varchar(500) NOT NULL;
