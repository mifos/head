create table qrtz_JOB_DETAILS(
    job_name  varchar(200) not null,
    job_group varchar(200) not null,
    description varchar(250) null,
    job_class_name   varchar(250) not null,
    is_durable varchar(1) not null,
    is_volatile varchar(1) not null,
    is_stateful varchar(1) not null,
    requests_recovery varchar(1) not null,
    job_data blob null,
    primary key (job_name,job_group)
) engine=innodb character set utf8;

create table qrtz_JOB_LISTENERS(
    job_name  varchar(200) not null,
    job_group varchar(200) not null,
    job_listener varchar(200) not null,
    primary key (job_name,job_group,job_listener),
    foreign key (job_name,job_group)
        references qrtz_JOB_DETAILS(job_name,job_group)
) engine=innodb character set utf8;

create table qrtz_TRIGGERS(
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    job_name  varchar(200) not null,
    job_group varchar(200) not null,
    is_volatile varchar(1) not null,
    description varchar(250) null,
    next_fire_time bigint(13) null,
    prev_fire_time bigint(13) null,
    priority integer null,
    trigger_state varchar(16) not null,
    trigger_type varchar(8) not null,
    start_time bigint(13) not null,
    end_time bigint(13) null,
    calendar_name varchar(200) null,
    misfire_instr smallint(2) null,
    job_data blob null,
    primary key (trigger_name,trigger_group),
    foreign key (job_name,job_group)
        references qrtz_JOB_DETAILS(job_name,job_group)
) engine=innodb character set utf8;

create table qrtz_SIMPLE_TRIGGERS(
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    repeat_count bigint(7) not null,
    repeat_interval bigint(12) not null,
    times_triggered bigint(10) not null,
    primary key (trigger_name,trigger_group),
    foreign key (trigger_name,trigger_group)
        references qrtz_TRIGGERS(trigger_name,trigger_group)
) engine=innodb character set utf8;

create table qrtz_CRON_TRIGGERS(
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    cron_expression varchar(200) not null,
    time_zone_id varchar(80),
    primary key (trigger_name,trigger_group),
    foreign key (trigger_name,trigger_group)
        references qrtz_TRIGGERS(trigger_name,trigger_group)
) engine=innodb character set utf8;

create table qrtz_BLOB_TRIGGERS(
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    blob_data blob null,
    primary key (trigger_name,trigger_group),
    foreign key (trigger_name,trigger_group)
        references qrtz_TRIGGERS(trigger_name,trigger_group)
) engine=innodb character set utf8;

create table qrtz_TRIGGER_LISTENERS(
    trigger_name  varchar(200) not null,
    trigger_group varchar(200) not null,
    trigger_listener varchar(200) not null,
    primary key (trigger_name,trigger_group,trigger_listener),
    foreign key (trigger_name,trigger_group)
        references qrtz_TRIGGERS(trigger_name,trigger_group)
) engine=innodb character set utf8;

create table qrtz_CALENDARS(
    calendar_name  varchar(200) not null,
    calendar blob not null,
    primary key (calendar_name)
) engine=innodb character set utf8;

create table qrtz_PAUSED_TRIGGER_GRPS(
    trigger_group  varchar(200) not null,
    primary key (trigger_group)
);

create table qrtz_FIRED_TRIGGERS(
    entry_id varchar(95) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    is_volatile varchar(1) not null,
    instance_name varchar(200) not null,
    fired_time bigint(13) not null,
    priority integer not null,
    state varchar(16) not null,
    job_name varchar(200) null,
    job_group varchar(200) null,
    is_stateful varchar(1) null,
    requests_recovery varchar(1) null,
    primary key (entry_id)
) engine=innodb character set utf8;

create table qrtz_SCHEDULER_STATE(
    instance_name varchar(200) not null,
    last_checkin_time bigint(13) not null,
    checkin_interval bigint(13) not null,
    primary key (instance_name)
) engine=innodb character set utf8;

create table qrtz_LOCKS(
    lock_name  varchar(40) not null,
    primary key (lock_name)
) engine=innodb character set utf8;

insert into qrtz_LOCKS values('TRIGGER_ACCESS');
insert into qrtz_LOCKS values('JOB_ACCESS');
insert into qrtz_LOCKS values('CALENDAR_ACCESS');
insert into qrtz_LOCKS values('STATE_ACCESS');
insert into qrtz_LOCKS values('MISFIRE_ACCESS');

drop table scheduled_tasks;
