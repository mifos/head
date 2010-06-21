create table customer_family_detail (
customer_family_id integer auto_increment not null,
customer_id integer,
customer_name_id integer,
relationship smallint,
gender smallint,
date_of_birth date,
living_status smallint,
primary key (customer_family_id),
foreign key (customer_id) references customer(customer_id) on delete no action on update no action,
foreign key (customer_name_id) references customer_name_detail(customer_name_id) on delete no action on update no action
)engine=innodb character set utf8;

update database_version set database_version = 222 where database_version = 221;



