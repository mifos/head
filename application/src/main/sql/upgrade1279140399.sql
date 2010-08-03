ALTER TABLE survey ENGINE = InnoDB;
ALTER TABLE questions ENGINE = InnoDB;
ALTER TABLE ppi_survey ENGINE = InnoDB;
ALTER TABLE ppi_survey_instance ENGINE = InnoDB;
ALTER TABLE ppi_likelihoods ENGINE = InnoDB;
ALTER TABLE question_choices ENGINE = InnoDB;
ALTER TABLE survey_instance ENGINE = InnoDB;
ALTER TABLE survey_questions ENGINE = InnoDB;
ALTER TABLE survey_response ENGINE = InnoDB;


DROP PROCEDURE IF EXISTS addFk;

CREATE PROCEDURE addFk(IN tableName CHAR(64), IN columnName CHAR(64), IN referencedTableName CHAR(64),IN referencedColumnName CHAR(64))
BEGIN
    set @fkCount = 0

    ;SET @s = CONCAT(
            '
                select
                    COUNT(*) into @fkCount
                from
                    information_schema.table_constraints c
                    join information_schema.key_column_usage u
                        on (
                            c.CONSTRAINT_NAME = u.CONSTRAINT_NAME and
                            c.TABLE_SCHEMA = u.TABLE_SCHEMA and
                            c.CONSTRAINT_SCHEMA = u.CONSTRAINT_SCHEMA
                        )
                where
                    c.constraint_schema=SCHEMA() and
                    c.TABLE_SCHEMA=SCHEMA() and
                    c.constraint_type=''foreign key'' and
            ',
            ' c.table_name = '''               ,tableName,           ''' and ',
            ' u.column_name = '''              ,columnName,          ''' and ',
            ' u.referenced_table_name = '''    ,referencedTableName, ''' and ',
            ' u.referenced_column_name = '''   ,referencedColumnName,''''
        )
    ;PREPARE stmt FROM @s
    ;EXECUTE stmt

    ;IF @fkCount = 0
        THEN
            set foreign_key_checks = 0

            ;SET @s = CONCAT(
                    'alter table ' , tableName ,
                    ' add constraint foreign key( ' , columnName , ' ) references ',
                    referencedTableName, '( ', referencedColumnName,') on delete no action on update no action'
                )
            ;PREPARE stmt FROM @s
            ;EXECUTE stmt

            ;set foreign_key_checks = 1
    ;END IF
;END
;

call addFk('ppi_survey',        'survey_id',            'survey',               'survey_id');
call addFk('ppi_likelihoods',   'survey_id',            'survey',               'survey_id');
call addFk('question_choices',  'question_id',          'questions',            'question_id');
call addFk('survey_instance',   'survey_id',            'survey',               'survey_id');
call addFk('survey_instance',   'customer_id',          'customer',             'customer_id');
call addFk('survey_instance',   'officer_id',           'personnel',            'personnel_id');
call addFk('survey_instance',   'account_id',           'account',              'account_id');
call addFk('survey_instance',   'creating_officer_id',  'personnel',            'personnel_id');
call addFk('survey_questions',  'question_id',          'questions',            'question_id');
call addFk('survey_questions',  'survey_id',            'survey',               'survey_id');
call addFk('survey_response',   'survey_question_id',   'survey_questions',     'surveyquestion_id');
call addFk('survey_response',   'instance_id',          'survey_instance',      'instance_id');
call addFk('survey_response',   'choice_value',         'question_choices',     'choice_id');

DROP PROCEDURE IF EXISTS addFk;


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
