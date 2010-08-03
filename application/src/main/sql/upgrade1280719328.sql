alter table questions change short_name short_name varchar(200) not null;

alter table question_group add column is_editable tinyint not null default 0;
