-- This script represents the latest schema.

-- Applying it should be the same as
-- Start with the latest-schema.sql from version foo of mifos
-- apply all upgrade_to_*.sql upgrades between foo and now

-- Applying this file also should also be the same as 
-- upgrading from the start:
-- Start with mifosdbcreationscript.sql
-- merge rmpdbcreationscript.sql
-- merge Iteration13-DBScripts25092006.sql
-- merge Iteration14-DDL-DBScripts10102006.sql
-- merge Iteration15-DDL-DBScripts24102006.sql
-- merge add-version.sql
-- merge Index.sql
-- merge all upgrade_to_*.sql files to date

create table currency (
  currency_id smallint auto_increment not null,
  currency_name varchar(50),
  display_symbol varchar(50),
  rounding_mode smallint,
  rounding_amount decimal(6,3),
  default_currency smallint,
  default_digits_after_decimal smallint not null ,
  currency_code varchar(3),
  primary key(currency_id)
)
engine=innodb character set utf8;



create table lookup_entity (
  entity_id smallint auto_increment not null,
  entity_name varchar(100) not null,

  -- Is this used for anything?  It seems like it should just be removed.
  description varchar(200),
  primary key(entity_id)
)
engine=innodb character set utf8;

create table country (
  country_id smallint not null,
  country_name varchar(100),
  country_short_name varchar(10),
  primary key(country_id)
)
engine=innodb character set utf8;

-- TODO: drop this table and move data to "coa" table
create table gl_code (
  glcode_id smallint auto_increment not null,
  glcode_value varchar(50) not null,
  primary key(glcode_id)
)
engine=innodb character set utf8;
-- enforce uniqueness for GLCODE_VALUE
create unique index glcode_value_idx on gl_code (glcode_value);

create table lookup_value (
  lookup_id integer auto_increment not null,
  entity_id smallint,
  lookup_name varchar(100), 
  primary key(lookup_id),
  foreign key(entity_id)
    references lookup_entity(entity_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index lookup_value_idx on lookup_value (entity_id);
create unique index lookup_name_idx on lookup_value (lookup_name);

create table activity (
  activity_id smallint auto_increment not null,
  parent_id smallint,
  activity_name_lookup_id integer not null,
  description_lookup_id integer not null,
  primary key(activity_id),
  foreign key(parent_id)
    references activity (activity_id)
      on delete no action
      on update no action,
  foreign key(activity_name_lookup_id)
   references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(description_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table recurrence_type (
  recurrence_id smallint auto_increment not null,
  recurrence_name varchar(50),
  description varchar(200) not null,
  primary key(recurrence_id)
)
engine=innodb character set utf8;

create table meeting_type (
  meeting_type_id smallint auto_increment not null,
  meeting_purpose varchar(50),
  description varchar(200) not null,
  primary key(meeting_type_id)
)
engine=innodb character set utf8;

create table language (
  lang_id smallint not null,
  lang_name varchar(100),
  lang_short_name varchar(10),
  lookup_id integer not null,
  primary key(lang_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
  	  on delete no action
      on update no action
)
engine=innodb character set utf8;

create table fee_type (
  fee_type_id smallint not null,
  fee_lookup_id smallint,
  flat_or_rate smallint,
  formula varchar(100),
  primary key(fee_type_id),
  foreign key(fee_lookup_id)
    references lookup_entity(entity_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table freq_of_deposits (
  freq_of_deposits_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(freq_of_deposits_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table fee_payment (
  fee_payment_id smallint auto_increment not null,
  fee_payment_lookup_id integer,
  primary key  (fee_payment_id),
  foreign key (fee_payment_lookup_id)
    references lookup_value (lookup_id)
	  on delete no action
      on update no action
) engine=innodb character set utf8;

create table fund_code (
  fundcode_id smallint auto_increment not null,
  fundcode_value varchar(50) not null,
  primary key(fundcode_id)
)
engine=innodb character set utf8;

create table fund (
  fund_id smallint auto_increment not null,
  fund_name varchar(100),
  version_no integer,
  fundcode_id smallint not null,
  primary key(fund_id),
  foreign key(fundcode_id) 
	references fund_code(fundcode_id)
	  on delete no action
	  on update no action
)
engine=innodb character set utf8;

create table interest_calculation_types (
  interest_calculation_type_id smallint auto_increment not null,
  interest_calculation_lookup_id integer not null,
  description varchar(100),
  primary key(interest_calculation_type_id),
  foreign key(interest_calculation_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table grace_period_type (
  grace_period_type_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(grace_period_type_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table prd_type (
  prd_type_id smallint auto_increment not null,
  prd_type_lookup_id integer not null,
  lateness_days smallint,
  dormancy_days smallint,
  version_no integer,
  primary key(prd_type_id),
  foreign key(prd_type_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table account_type (
  account_type_id smallint auto_increment not null,
  lookup_id integer not null,
  description varchar(50),
  primary key(account_type_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table recommended_amnt_unit (
  recommended_amnt_unit_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(recommended_amnt_unit_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table prd_category_status (
  prd_category_status_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(prd_category_status_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table prd_state (
  prd_state_id smallint auto_increment not null,
  prd_state_lookup_id integer,
  primary key(prd_state_id),
  foreign key(prd_state_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table account_action (
  account_action_id smallint not null,
  lookup_id integer not null,
  primary key(account_action_id),
  foreign key(lookup_id)
     references lookup_value(lookup_id)
       on delete no action
       on update no action
)
engine=innodb character set utf8;


create table yes_no_master (
  yes_no_master_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(yes_no_master_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table week_days_master(
 week_days_master_id smallint auto_increment not null,
 lookup_id integer not null,
 working_day smallint not null,
 start_of_fiscal_week smallint not null,
  primary key(week_days_master_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;



create table rank_days_master(
 rank_days_master_id smallint auto_increment not null,
 lookup_id integer not null,
  primary key(rank_days_master_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
 


create table savings_type (
  savings_type_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(savings_type_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table personnel_level (
  level_id smallint not null,
  level_name_id integer not null,
  parent_level_id smallint,
  interaction_flag smallint,
  primary key(level_id),
  foreign key(parent_level_id)
    references personnel_level(level_id)
      on delete no action
      on update no action,
  foreign key(level_name_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table office_level (
  level_id smallint not null,
  parent_level_id smallint,
  level_name_id smallint,
  interaction_flag smallint,
  configured smallint not null,
  version_no integer,
  primary key(level_id),
  foreign key(parent_level_id)
    references office_level(level_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table payment_type (
  payment_type_id smallint auto_increment not null,
  payment_type_lookup_id integer,
  primary key(payment_type_id),
  foreign key(payment_type_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table prd_applicable_master (
  prd_applicable_master_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(prd_applicable_master_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table prd_category (
  prd_category_id smallint auto_increment not null,
  prd_type_id smallint not null,
  global_prd_offering_num varchar(50) not null,
  prd_category_name varchar(100) not null,
  created_date date,
  created_by integer,
  office_id smallint,
  updated_by integer,
  udpated_date date,
  state smallint not null,
  description varchar(500),
  version_no integer,
  primary key(prd_category_id),
  foreign key(prd_type_id)
    references prd_type(prd_type_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table position (
  position_id integer auto_increment not null,
  lookup_id integer not null,
  primary key(position_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table category_type (
  category_id smallint not null,
  category_lookup_id integer not null,
  primary key(category_id),
  foreign key(category_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_level (
  level_id smallint not null,
  parent_level_id smallint,
  level_name_id smallint not null,
  interaction_flag smallint,
  max_child_count smallint not null,
  max_instance_count smallint not null,
  primary key(level_id),
  foreign key(parent_level_id)
    references customer_level(level_id)
      on delete no action
      on update no action,
  foreign key(level_name_id)
    references lookup_entity(entity_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table account_state (
  account_state_id smallint auto_increment not null,
  lookup_id integer not null,
  prd_type_id smallint not null,
  currently_in_use smallint not null,
  status_description varchar(200),
  primary key(account_state_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table prd_status (
  offering_status_id smallint auto_increment not null,
  prd_state_id smallint not null,
  prd_type_id smallint not null,
  currently_in_use smallint not null,
  version_no integer not null,
  primary key(offering_status_id),
  foreign key(prd_type_id)
    references prd_type(prd_type_id)
      on delete no action
      on update no action,
  foreign key(prd_state_id)
    references prd_state(prd_state_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table interest_types (
  interest_type_id smallint auto_increment not null,
  lookup_id integer not null,
  category_id smallint not null,
  descripton varchar(50),
  primary key(interest_type_id),
  foreign key(category_id)
    references prd_type(prd_type_id)
      on delete no action
      on update no action,
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_state (
  status_id smallint auto_increment not null,
  status_lookup_id integer not null,
  level_id smallint not null,
  description varchar(200),
  currently_in_use smallint not null,
  primary key(status_id),
  foreign key(level_id)
    references customer_level(level_id)
      on delete no action
      on update no action,
  foreign key(status_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table supported_locale (
  locale_id smallint not null,
  country_id smallint,
  lang_id smallint,
  locale_name varchar(50),
  default_locale smallint,
  primary key(locale_id),
  foreign key(country_id)
    references country(country_id)
      on delete no action
      on update no action,
  foreign key(lang_id)
    references language(lang_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table fee_payments_categories_type (
  fee_payments_category_type_id smallint not null,
  fee_payment_id smallint,
  category_id smallint,
  fee_type_id smallint,
  primary key(fee_payments_category_type_id),
  foreign key(category_id)
    references category_type(category_id)
      on delete no action
      on update no action,
  foreign key(fee_payment_id)
    references fee_payment(fee_payment_id)
      on delete no action
      on update no action,
  foreign key(fee_type_id)
    references fee_type(fee_type_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table interest_calc_rule (
  interest_calc_rule_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(interest_calc_rule_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table office_status(
  status_id smallint auto_increment not null,
  lookup_id int not null,
  primary key  (status_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action

)
engine=innodb character set utf8;

create table office_code(
  code_id smallint auto_increment not null,
  lookup_id int not null,
  primary key  (code_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action

)
engine=innodb character set utf8;


create table office (
  office_id smallint auto_increment not null,
  global_office_num varchar(100) not null,
  office_level_id smallint not null,
  search_id varchar(100) not null,
  max_child_count integer not null,
  local_remote_flag smallint not null,
  display_name varchar(200) not null,
  created_by smallint,
  created_date date,
  updated_by smallint,
  updated_date date,
  office_short_name varchar(4) not null,
  parent_office_id smallint default null,
  status_id smallint not null,
  version_no integer not null,
  office_code_id smallint default null,
  primary key(office_id),
  unique(global_office_num),
  foreign key(office_level_id)
    references office_level(level_id)
      on delete no action
      on update no action,
  foreign key(parent_office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(status_id)
    references office_status(status_id)
      on delete no action
      on update no action,
  foreign key(office_code_id)
    references office_code(code_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create unique index office_global_idx on office (global_office_num);

create table office_address (
  office_address_id smallint auto_increment not null,
  office_id smallint not null,
  address_1 varchar(200) default null,
  address_2 varchar(200) default null,
  address_3 varchar(200) default null,
  city varchar(100) default null,
  state varchar(100) default null,
  country varchar(100) default null,
  zip varchar(20) default null,
  telephone varchar(20) default null,
  primary key  (office_address_id),
 foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table office_custom_field (
  office_custom_field_id integer auto_increment not null,
  office_id smallint not null,
  field_id smallint not null,
  field_value varchar(200) default null,
   primary key(office_custom_field_id),
 foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action
) 
engine=innodb character set utf8;


create table mfi_attribute (
  attribute_id smallint not null,
  office_id smallint not null,
  attribute_name varchar(100) not null,
  attribute_value varchar(200) not null,
  primary key(attribute_id),
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table repayment_rule (
  repayment_rule_id smallint auto_increment not null,
  repayment_rule_lookup_id integer,
  primary key(repayment_rule_id),
  foreign key(repayment_rule_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table holiday (
  office_id smallint not null,
  holiday_from_date date not null,
  holiday_thru_date date,
  holiday_name varchar(100),
  repayment_rule_id smallint not null,
  holiday_changes_applied_flag smallint default 1,

  primary key(office_id, holiday_from_date),
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(repayment_rule_id)
    references repayment_rule(repayment_rule_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table branch_ho_update (
  office_id smallint not null,
  last_updated_date date,
  primary key(office_id),
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table lookup_value_locale (
  lookup_value_id integer auto_increment not null,
  locale_id smallint not null,
  lookup_id integer not null,
  lookup_value varchar(300),
  primary key(lookup_value_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(locale_id)
    references supported_locale(locale_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table lookup_label (
  label_id integer auto_increment not null,
  entity_id smallint,
  locale_id smallint,
  entity_name varchar(200),
  primary key(label_id),
  foreign key(entity_id)
    references lookup_entity(entity_id)
      on delete no action
      on update no action,
  foreign key(locale_id)
    references supported_locale(locale_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_state_flag (
  flag_id smallint not null,
  flag_lookup_id integer not null,
  status_id smallint not null,
  flag_description varchar(200) not null,
  isblacklisted smallint,
  primary key(flag_id),
  foreign key(status_id)
    references customer_state(status_id)
      on delete no action
      on update no action,
  foreign key(flag_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table program (
  program_id integer auto_increment not null,
  office_id smallint not null,
  lookup_id integer not null,
  glcode_id smallint,
  program_name varchar(100),
  start_date date not null,
  end_date date,
  confidentiality smallint,
  primary key(program_id),
  foreign key(glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action,
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table personnel (
  personnel_id smallint auto_increment not null,
  level_id smallint not null,
  global_personnel_num varchar(100),
  office_id smallint,
  title integer,
  personnel_status smallint,
  preferred_locale smallint,
  search_id varchar(100),
  max_child_count integer,
  password blob(250),
  login_name varchar(200),
  email_id varchar(255),
  password_changed smallint not null,
  display_name varchar(200),
  created_by smallint not null,
  created_date date,
  updated_by smallint,
  updated_date date,
  last_login date,
  locked smallint not null,
  no_of_tries smallint not null,
  version_no integer not null,
  primary key(personnel_id),
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(created_by)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(level_id)
    references personnel_level(level_id)
      on delete no action
      on update no action,
  foreign key(preferred_locale)
    references supported_locale(locale_id)
      on delete no action
      on update no action,
  foreign key(title)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(updated_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create unique index personnel_global_idx on personnel (global_personnel_num);
create unique index personnel_search_idx on personnel (search_id);
create index personnel_office_idx on personnel (office_id);
create unique index personnel_login_idx on personnel (login_name);


-- Seems not to be used by anything
create table  fee_update_type (
fee_update_type_id smallint auto_increment not null ,
lookup_id integer not null,
primary key  (fee_update_type_id),
foreign key (lookup_id) references lookup_value(lookup_id)
on delete no action 
on update no action
)engine=innodb character set utf8;



create table fee_formula_master (
  formulaid smallint auto_increment not null,
  forumla_lookup_id integer not null ,
  primary key (formulaid),
  foreign key (forumla_lookup_id) references lookup_value(lookup_id)
    on delete no action
    on update no action
) engine=innodb character set utf8;

create table fee_status (
  status_id smallint auto_increment not null,
  status_lookup_id integer not null ,
  primary key  (status_id),
  foreign key (status_lookup_id)
    references lookup_value (lookup_id)
    on delete no action
    on update no action
) engine=innodb character set utf8;


create table fees (
  fee_id smallint auto_increment not null,
  global_fee_num varchar(50),
  fee_name varchar(50) not  null,
  fee_payments_category_type_id smallint,
  office_id smallint not null,
  glcode_id smallint  not null,
  status smallint not null,
  category_id smallint not null,
  rate_or_amount decimal(16,5),
  rate_or_amount_currency_id smallint,
  rate_flat_falg smallint,
  created_date date  not null,
  created_by smallint  not null,
  updated_date date,
  updated_by smallint,
  update_flag  smallint,
  formula_id smallint,
  default_admin_fee varchar(10),
  fee_amount decimal(10,3) ,
  fee_amount_currency_id smallint,
  rate decimal(16,5),
  version_no integer not null,
  discriminator varchar(20),
  primary key  (fee_id),
  foreign key (glcode_id) 
    references gl_code (glcode_id)
    on delete no action
    on update no action,
  foreign key (category_id) 
    references category_type (category_id)
    on delete no action
    on update no action,
  foreign key (status) 
    references fee_status (status_id)
    on delete no action
    on update no action,
  foreign key (office_id)
    references office (office_id)
    on delete no action
    on update no action,
  foreign key (created_by)
    references personnel (personnel_id)
    on delete no action
    on update no action,
  foreign key (updated_by)
    references personnel (personnel_id)
    on delete no action
    on update no action,
  foreign key (formula_id)
    references fee_formula_master (formulaid)
    on delete no action
    on update no action,
  foreign key(rate_or_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(fee_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;
create unique index fee_global_idx on fees (global_fee_num);
create index fee_pmnt_catg_idx on fees (fee_payments_category_type_id);
create index fee_office_idx on fees (office_id);

create table feelevel (
  feelevel_id smallint auto_increment not null,
  fee_id smallint not null,
  level_id smallint not null,
  primary key(feelevel_id),
  foreign key(fee_id)
    references fees(fee_id)
      on delete no action
      on update no action
) engine=innodb character set utf8;

create table fee_frequency_type (
  fee_frequency_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key  (fee_frequency_id),
  foreign key (lookup_id) references lookup_value(lookup_id)
    on delete no action
    on update no action
) engine=innodb character set utf8;


create table personnel_hierarchy (
  hierarchy_id integer not null,
  parent_id smallint not null,
  personnel_id smallint,
  status smallint,
  start_date date,
  end_date date not null,
  updated_by smallint,
  updated_date date,
  primary key(hierarchy_id),
  foreign key(parent_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(personnel_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(updated_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index personnel_hierarchy_idx on personnel_hierarchy (personnel_id, status);

create table role (
  role_id smallint not null,
  role_name varchar(50) not null,
  version_no integer  not null,
  created_by smallint,
  created_date date,
  updated_by smallint,
  updated_date date,
  primary key(role_id),
  foreign key(created_by)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(updated_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table penalty (
  penalty_id smallint not null,
  global_penalty_num varchar(100),
  penalty_type varchar(100),
  office_id smallint,
  category_id smallint,
  glcode_id smallint not null,
  lookup_id integer,
  rate decimal(13, 10) not null,
  formula varchar(100),
  primary key(penalty_id),
  foreign key(category_id)
    references category_type(category_id)
      on delete no action
      on update no action,
  foreign key(glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action,
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table office_action_payment_type (
  office_id smallint,
  prd_type_id smallint,
  account_action_id smallint not null,
  payment_type_id smallint,
  foreign key(account_action_id)
    references account_action(account_action_id)
      on delete no action
      on update no action,
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(payment_type_id)
    references payment_type(payment_type_id)
      on delete no action
      on update no action,
  foreign key(prd_type_id)
    references prd_type(prd_type_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer (
  customer_id integer auto_increment not null,
  customer_level_id smallint not null,
  global_cust_num varchar(100),
  loan_officer_id smallint,
  customer_formedby_id smallint,
  status_id smallint,
  branch_id smallint,
  display_name varchar(200),
  first_name   varchar(200),
  last_name   varchar(200),
  second_last_name   varchar(200),
  display_address varchar(500),
  external_id varchar(50),
  date_of_birth date,
  group_flag smallint,
  trained smallint,
  trained_date date,
  parent_customer_id integer,
  created_date date,
  updated_date date,
  
  /* We could have a UNIQUE constraint on the combination
     of SEARCH_ID and BRANCH_ID if we want (search ID's are
     only unique within a branch). */
  search_id varchar(100),

  max_child_count integer,
  ho_updated smallint,
  client_confidential smallint,
  mfi_joining_date date,
  government_id	varchar(50),
  customer_activation_date date,
  created_by smallint,
  updated_by smallint,
  blacklisted smallint,
  discriminator varchar(20),
  version_no integer not null,
  primary key(customer_id),
  foreign key(customer_level_id)
    references customer_level(level_id)
      on delete no action
      on update no action,
  foreign key(status_id)
    references customer_state(status_id)
      on delete no action
      on update no action,
  foreign key(branch_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(loan_officer_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(customer_formedby_id)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create unique index cust_global_idx on customer (global_cust_num);
create index cust_search_idx on customer (search_id);
create index cust_lo_idx on customer (loan_officer_id, branch_id);
create index customer_lo_name_idx 
  on customer (loan_officer_id, customer_level_id,display_name(15),
    first_name(15),last_name(15),second_last_name(15));
create index customer_name_idx 
  on customer (customer_level_id,
    first_name(15),last_name(15),second_last_name(15));

create table cust_perf_history (
  customer_id integer not null,
  loan_cycle_counter smallint,
  last_loan_amnt decimal(10,3),
  active_loans_count smallint,
  total_savings_amnt decimal(10,3),
  delinquint_portfolio decimal(10,3),
  primary key(customer_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_detail (
  customer_id integer not null,
  ethinicity integer,
  citizenship integer,
  handicapped integer,
  business_activities integer,
  marital_status integer,
  education_level integer,
  num_children smallint,
  gender smallint,
  date_started date,
  handicapped_details varchar(200),
  poverty_status integer,
  poverty_lhood_pct decimal(10,3),
  primary key(customer_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(citizenship)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(education_level)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(ethinicity)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(handicapped)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(marital_status)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(poverty_status) 
  	references lookup_value(lookup_id)
  	  on delete no action
  	  on update no action      
)
engine=innodb character set utf8;

create table prd_offering (
  prd_offering_id smallint auto_increment not null,
  prd_applicable_master_id smallint not null,
  global_prd_offering_num varchar(50) not null,
  prd_category_id smallint  not null,
  prd_type_id smallint,
  office_id smallint,
  start_date date  not null,
  end_date date,
  glcode_id smallint,
  prd_offering_name varchar(50) not null,
  prd_offering_short_name varchar(50) not null,
  offering_status_id smallint,
  description varchar(200),
  created_date date not null,
  created_by integer not null,
  updated_date date,
  updated_by integer,
  version_no integer,
  prd_mix_flag smallint,
  primary key(prd_offering_id),
  foreign key(glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action,
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(prd_category_id)
    references prd_category(prd_category_id)
      on delete no action
      on update no action,
  foreign key(offering_status_id)
    references prd_status(offering_status_id)
      on delete no action
      on update no action,
  foreign key(prd_type_id)
    references prd_type(prd_type_id)
      on delete no action
      on update no action,
   foreign key(prd_applicable_master_id)
    references prd_applicable_master(prd_applicable_master_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create unique index prd_offering_global_idx on prd_offering (global_prd_offering_num);
create index prd_offering_office_idx on prd_offering (office_id);
create index prd_type_idx on prd_offering (prd_type_id);

create table loan_offering (
  prd_offering_id smallint not null,
  interest_type_id smallint not null,
  interest_calc_rule_id smallint,
  penalty_id smallint,
  min_loan_amount decimal(10, 3) default null,
  min_loan_amount_currency_id smallint,
  max_loan_amnt decimal(10, 3) default null,
  max_loan_amnt_currency_id smallint,
  default_loan_amount decimal(10, 3) default null,
  default_loan_amount_currency_id smallint,
  graceperiod_type_id smallint,
  max_interest_rate decimal(13, 10) not null,
  min_interest_rate decimal(13, 10) not null,
  def_interest_rate decimal(13, 10) not null,
  max_no_installments smallint default null,
  min_no_installments smallint default null,
  def_no_installments smallint default null,
  penalty_grace smallint,
  loan_counter_flag smallint,
  int_ded_disbursement_flag smallint not null,
  prin_due_last_inst_flag smallint not null,
  penalty_rate decimal(13,10),
  grace_period_duration smallint,
  principal_glcode_id smallint not null,
  interest_glcode_id smallint not null,
  penalties_glcode_id smallint,
  primary key(prd_offering_id),
  foreign key(principal_glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action,
  foreign key(interest_glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action,    
  constraint loan_offering_penalty_glcode foreign key(penalties_glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action,    
  foreign key(graceperiod_type_id)
    references grace_period_type(grace_period_type_id)
      on delete no action
      on update no action,
  foreign key(prd_offering_id)
    references prd_offering(prd_offering_id)
      on delete no action
      on update no action,
   constraint loan_offering_penalty foreign key(penalty_id)
    references penalty(penalty_id)
      on delete no action
      on update no action,
  constraint loan_offering_interest_calc_rule foreign key(interest_calc_rule_id)
    references interest_calc_rule(interest_calc_rule_id)
      on delete no action
      on update no action,
  foreign key(interest_type_id)
    references interest_types(interest_type_id)
      on delete no action
      on update no action,
  foreign key(min_loan_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(max_loan_amnt_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(default_loan_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action     
)
engine=innodb character set utf8;

create table savings_offering (
  prd_offering_id smallint not null,
  interest_calculation_type_id smallint not null,
  savings_type_id smallint not null,
  recommended_amnt_unit_id smallint,
  recommended_amount decimal(10, 3),
  recommended_amount_currency_id smallint,
  interest_rate decimal(13, 10) not null,
  max_amnt_withdrawl decimal(10,3),
  max_amnt_withdrawl_currency_id smallint,
  min_amnt_for_int decimal(10,3),
  min_amnt_for_int_currency_id smallint,
  deposit_glcode_id smallint not null,
  interest_glcode_id smallint not null,
  primary key(prd_offering_id),
  foreign key(prd_offering_id)
    references prd_offering(prd_offering_id)
      on delete no action
      on update no action,
   foreign key(recommended_amnt_unit_id)
    references recommended_amnt_unit(recommended_amnt_unit_id)
      on delete no action
      on update no action,
  foreign key(savings_type_id)
    references savings_type(savings_type_id)
      on delete no action
      on update no action,
   foreign key(interest_calculation_type_id)
    references interest_calculation_types(interest_calculation_type_id)
      on delete no action
      on update no action,
  foreign key(recommended_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(max_amnt_withdrawl_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(min_amnt_for_int_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(deposit_glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action,
  foreign key(interest_glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table product_offering_mandatory_savings (
  product_offering_mandatory_savings_id smallint not null, 
  product_offering_mandatory_savings_type smallint, 
  prd_offering_id smallint, 
  product_offering_mandatory_savings_value smallint, 
  product_offering_mandatory_savings_range smallint, 
  primary key (product_offering_mandatory_savings_id), 
  foreign key (prd_offering_id) references prd_offering(prd_offering_id)
    on delete no action
    on update no action
) 
engine=innodb character set utf8;

create table insurance_offering (
  prd_offering_id smallint not null,
  primary key(prd_offering_id),
  foreign key(prd_offering_id)
    references prd_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table personnel_details (
  personnel_id smallint not null ,
  first_name varchar(100) not null,
  middle_name varchar(100),
  second_last_name varchar(100),
  last_name varchar(100),
  government_id_number varchar(50),
  dob date not null,
  marital_status integer,
  gender integer  not null,
  date_of_joining_mfi date,
  date_of_joining_branch date,
  date_of_leaving_branch date,
  address_1 varchar(200),
  address_2 varchar(200),
  address_3 varchar(200),
  city varchar(100),
  state varchar(100),
  country varchar(100),
  postal_code varchar(100),
  telephone varchar(20),
  foreign key(personnel_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(gender)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(marital_status)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table personnel_movement (
  personnel_movement_id smallint auto_increment not null,
  personnel_id smallint,
  office_id smallint not null,
  status smallint,
  start_date date,
  end_date date,
  updated_by smallint,
  updated_date date ,
  primary key(personnel_movement_id),
  foreign key(personnel_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(updated_by)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index personnel_movement_idx on personnel_movement (personnel_id);

create table personnel_notes (
  comment_id integer auto_increment not null,
  personnel_id smallint not null,
  comment_date date not null,
  comments varchar(500) not null,
  officer_id smallint,
  primary key(comment_id),
  foreign key(personnel_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(officer_id)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table personnel_status (
  personnel_status_id smallint auto_increment not null,
  lookup_id integer not null,
  primary key(personnel_status_id),
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table entity_master (
   entity_type_id smallint auto_increment not null,
   entity_type varchar(100) not null,
   primary key (entity_type_id)
)engine=innodb character set utf8;

create table custom_field_definition (
  field_id smallint auto_increment not null,
  entity_id smallint not null,
  level_id smallint,
  field_type smallint,
  entity_type smallint not null,
  mandatory_flag smallint not null,
  default_value varchar(200),
  primary key(field_id),
  foreign key(level_id)
    references customer_level(level_id)
      on delete no action
      on update no action,
  foreign key(entity_id)
    references lookup_entity(entity_id)
      on delete no action
      on update no action,
  foreign key(entity_type)
    references entity_master(entity_type_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table change_log (
  change_log_id integer auto_increment not null,
  changed_by smallint not null,
  modifier_name varchar(50) not null,
  entity_id integer default null,
  entity_type smallint default null,
  changed_date date default null,
  fields_changed varchar(250) default null,
  primary key  (change_log_id),
  foreign key(changed_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
) engine=innodb character set utf8;
create index change_log_idx
  on change_log (entity_type, entity_id, changed_date);

create table customer_picture (
  picture_id integer auto_increment not null,
  customer_id integer not null,
  picture blob,
  primary key(picture_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_historical_data (
  historical_id smallint auto_increment not null,
  customer_id integer not null,
  product_name varchar(100),
  loan_amount decimal(10,3),
  loan_amount_currency_id smallint,
  total_amount_paid decimal(10,3),
  total_amount_paid_currency_id smallint,  
  interest_paid decimal(10,3),
  interest_paid_currency_id smallint,
  missed_payments_count integer,
  total_payments_count integer,
  notes varchar(500),
  loan_cycle_number integer,
  created_by smallint,
  updated_by smallint,
  created_date date,
  updated_date date,
  version_no integer,
  primary key(historical_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_address_detail (
  customer_address_id integer auto_increment not null,
  customer_id integer,
  locale_id smallint,
  address_name varchar(100),
  line_1 varchar(200),
  line_2 varchar(200),
  line_3 varchar(200),
  city varchar(100),
  state varchar(100),
  country varchar(100),
  zip varchar(20),
  address_status smallint,
  phone_number varchar(20),
  primary key(customer_address_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(locale_id)
    references supported_locale(locale_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index cust_address_idx on customer_address_detail (customer_id);

create table customer_custom_field (
  customer_customfield_id integer auto_increment not null,
  customer_id integer not null,
  field_id smallint not null,
  field_value varchar(200),
  primary key(customer_customfield_id),
  foreign key(field_id)
    references custom_field_definition(field_id)
      on delete no action
      on update no action,
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table offering_fund (
  offering_fund_id smallint not null,
  fund_id smallint,
  prd_offering_id smallint,
  primary key(offering_fund_id),
  foreign key(fund_id)
    references fund(fund_id)
      on delete no action
      on update no action,
  foreign key(prd_offering_id)
    references prd_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table prd_offering_fees (
  prd_offering_fee_id smallint auto_increment not null,
  fee_id smallint,
  prd_offering_id smallint,
  primary key(prd_offering_fee_id),
  foreign key(fee_id)
    references fees(fee_id)
      on delete no action
      on update no action,
  foreign key(prd_offering_id)
    references prd_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index prd_offering_fee_idx 
  on prd_offering_fees (prd_offering_id, fee_id);

create table program_fund (
  program_fund_id smallint not null,
  fund_id smallint,
  program_id integer,
  primary key(program_fund_id),
  foreign key(fund_id)
    references fund(fund_id)
      on delete no action
      on update no action,
  foreign key(program_id)
    references program(program_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table personnel_custom_field (
  personnel_custom_field_id integer auto_increment not null,
  field_id smallint not null,
  personnel_id smallint not null,
  field_value varchar(100),
  primary key(personnel_custom_field_id),
  foreign key(personnel_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(field_id)
    references custom_field_definition(field_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table roles_activity (
  activity_id smallint not null,
  role_id smallint not null,
  primary key(activity_id, role_id),
  foreign key(activity_id)
    references activity(activity_id)
      on delete no action
      on update no action,
  foreign key(role_id)
    references role(role_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table personnel_role (
  personnel_role_id integer auto_increment not null,
  role_id smallint not null,
  personnel_id smallint not null,
  primary key(personnel_role_id),
  foreign key(personnel_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(role_id)
    references role(role_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table meeting (
 meeting_id integer auto_increment not null,
 meeting_type_id smallint not null,
 meeting_place varchar(200),
 start_date date,
 end_date date,
 start_time date,
 end_time date,
 version_no integer,
 primary key(meeting_id),
   foreign key(meeting_type_id)
    references meeting_type(meeting_type_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8; 


create table fee_frequency (
  fee_frequency_id smallint auto_increment not null,
  fee_id smallint not null ,
  fee_frequencytype_id smallint not null ,
  frequency_payment_id smallint,
  frequency_meeting_id integer,
  primary key  (fee_frequency_id),
  foreign key (fee_id)
    references fees (fee_id)
    on delete no action
      on update no action,
  foreign key (fee_frequencytype_id)
    references fee_frequency_type (fee_frequency_id)
    on delete no action
     on update no action,
  foreign key (frequency_payment_id)
    references fee_payment (fee_payment_id)
    on delete no action
      on update no action,
  foreign key (frequency_meeting_id)
    references meeting (meeting_id)
    on delete no action
      on update no action
) engine=innodb character set utf8;
   
create table recurrence_detail (
  details_id integer auto_increment not null,
  meeting_id integer not null, 
  recurrence_id smallint,
  recur_after smallint not null,
  version_no integer,
  primary key(details_id),
  foreign key(recurrence_id)
    references recurrence_type(recurrence_id)
      on delete no action
      on update no action,
  foreign key(meeting_id)
    references meeting(meeting_id)
      on delete no action
      on update no action      
)
engine=innodb character set utf8; 

create table recur_on_day (
  recur_on_day_id integer  auto_increment not null,
  details_id integer not null,
  days  smallint,
  rank_of_days smallint,
  day_number smallint,
  version_no integer,
    primary key(recur_on_day_id),
    foreign key(details_id)
    references recurrence_detail(details_id)
      on delete no action
      on update no action,
  foreign key(days)
    references week_days_master(week_days_master_id)
      on delete no action
      on update no action,
  foreign key(rank_of_days)
    references rank_days_master(rank_days_master_id)
      on delete no action
      on update no action            
)
engine=innodb character set utf8;


create table customer_meeting (
  customer_meeting_id integer auto_increment not null,
  meeting_id integer not null,
  customer_id integer not null,
  updated_flag smallint not null,
  updated_meeting_id integer,
  primary key(customer_meeting_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(meeting_id)
    references meeting(meeting_id)
      on delete no action
      on update no action,
  foreign key (updated_meeting_id) 
    references meeting(meeting_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index customer_meeting_idx on customer_meeting (customer_id);
create index cust_inherited_meeting_idx on customer_meeting (customer_id);

create table inherited_meeting (
  meeting_id integer,
  customer_id integer,
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(meeting_id)
    references customer_meeting(customer_meeting_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_meeting_detail (
  meeting_id integer not null,
  details_id integer not null,
  primary key(meeting_id, details_id),
  foreign key(meeting_id)
    references customer_meeting(customer_meeting_id)
      on delete no action
      on update no action,
  foreign key(details_id)
    references recurrence_detail(details_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_program (
  program_id integer not null,
  customer_id integer not null,
  version_no integer not null,
  primary key(program_id, customer_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(program_id)
    references program(program_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table loan_offering_fund (
  loan_offering_fund_id smallint  auto_increment not null,
  fund_id smallint not null,
  prd_offering_id smallint not null,
  primary key(loan_offering_fund_id),
  foreign key(prd_offering_id)
    references loan_offering(prd_offering_id)
      on delete no action
      on update no action,
  foreign key(fund_id)
    references fund(fund_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_note (
  comment_id integer auto_increment not null,
  customer_id integer not null,
  field_officer_id smallint not null,
  comment_date date not null,
  comment varchar(500) not null,
  primary key(comment_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(field_officer_id)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index cust_note_idx on customer_note (customer_id);

create table customer_position (
  customer_position_id smallint auto_increment not null,
  position_id integer  not null,
  customer_id integer,
  parent_customer_id integer,
  version_no integer,
  primary key(customer_position_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action

)
engine=innodb character set utf8;
create unique index cust_position_idx 
  on customer_position (customer_id, position_id);

create table prd_fee_frequency (
  prdoffering_fee_id smallint not null,
  fee_id smallint,
  frequency_id smallint not null,
  primary key(prdoffering_fee_id),
  foreign key(fee_id)
    references fees(fee_id)
      on delete no action
      on update no action,
  foreign key(prdoffering_fee_id)
    references prd_offering_fees(prd_offering_fee_id)
      on delete no action
      on update no action,
  foreign key(frequency_id)
    references recurrence_type(recurrence_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table office_hierarchy (
  hierarchy_id integer auto_increment not null,
  parent_id smallint not null,
  office_id smallint,
  status smallint,
  start_date date,
  end_date date,
  updated_by smallint,
  updated_date date,
  primary key(hierarchy_id),
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(parent_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(updated_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index office_hierarchy_idx on office_hierarchy (office_id,status);

create table customer_hierarchy (
  hierarchy_id integer auto_increment not null,
  parent_id integer not null,
  customer_id integer,
  status smallint,
  start_date date,
  end_date date,
  updated_by smallint,
  updated_date date,
  primary key(hierarchy_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(parent_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(updated_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index cust_hierarchy_idx on customer_hierarchy (customer_id, status);

create table customer_flag_detail (
  customer_flag_id integer auto_increment not null,
  customer_id integer not null,
  flag_id smallint not null,
  created_by smallint,
  created_date date,
  version_no integer,
  primary key(customer_flag_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(flag_id)
    references customer_state_flag(flag_id)
      on delete no action
      on update no action,
  foreign key(created_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_name_detail (
  customer_name_id integer auto_increment not null,
  customer_id integer,
  name_type smallint,
  locale_id smallint,
  salutation integer,
  first_name varchar(100) not null,
  middle_name varchar(100),
  last_name varchar(100) not null,
  second_last_name varchar(100),
  second_middle_name varchar(100),
  display_name varchar(200),
  primary key(customer_name_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(salutation)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
  foreign key(locale_id)
    references supported_locale(locale_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index cust_name_idx on customer_name_detail (customer_id);

create table checklist (
  checklist_id smallint auto_increment not null,
  checklist_name varchar(100),
  checklist_status smallint  default 1  not null,
  locale_id smallint not null,
  created_by smallint,
  created_date date,
  updated_by smallint,
  updated_date date,
  primary key(checklist_id),
  foreign key(locale_id)
    references supported_locale(locale_id)
      on delete no action
      on update no action,
  foreign key(created_by)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(updated_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table account (
  account_id integer auto_increment not null,
  global_account_num varchar(100),
  customer_id integer,
  account_state_id smallint,
  account_type_id smallint not null,
  office_id smallint,
  personnel_id smallint,
  created_by smallint not null,
  created_date date not null,
  updated_by smallint,
  updated_date date,
  closed_date date,
  version_no integer,
  offsetting_allowable smallint not null,
  primary key(account_id),
  foreign key(account_state_id)
    references account_state(account_state_id)
      on delete no action
      on update no action,
  foreign key(account_type_id)
    references account_type(account_type_id)
      on delete no action
      on update no action,
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(personnel_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action      
)
engine=innodb character set utf8;
create unique index account_global_idx on account (global_account_num);
create index customer_id_account_idx on account (customer_id);

create table account_activity (
  activity_id integer auto_increment not null,
  account_id integer not null,
  personnel_id smallint not null,
  activity_name varchar(50) not null,
  principal decimal(10, 3),
  principal_currency_id smallint,  
  principal_outstanding decimal(10, 3),
  principal_outstanding_currency_id smallint,
  interest decimal(13, 10),
  interest_currency_id smallint,
  interest_outstanding decimal(13, 10),
  interest_outstanding_currency_id smallint,  
  fee decimal(13, 2),
  fee_currency_id smallint,  
  fee_outstanding decimal(13, 2),
  fee_outstanding_currency_id smallint,  
  penalty decimal(13, 10),
  penalty_currency_id smallint,  
  penalty_outstanding decimal(13, 10), 	
  penalty_outstanding_currency_id smallint, 	  
  primary key(activity_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
 foreign key(principal_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
 foreign key(principal_outstanding_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
 foreign key(interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
 foreign key(interest_outstanding_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
 foreign key(fee_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
 foreign key(fee_outstanding_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
 foreign key(penalty_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
 foreign key(penalty_outstanding_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;

create table account_notes (
  account_notes_id integer  auto_increment not null,
  account_id integer not null,
  note varchar(500) not null,
  comment_date date not null,
  officer_id smallint not null,
  primary key(account_notes_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(officer_id)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table customer_movement (
  customer_movement_id integer auto_increment not null,
  customer_id integer,
  personnel_id smallint,
  office_id smallint not null,
  status smallint,
  start_date date,
  end_date date,
  updated_by smallint,
  updated_date date,
  primary key(customer_movement_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(personnel_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(updated_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index cust_movement_idx on customer_movement (customer_id, status);

create table loan_account (
  account_id integer not null,
  business_activities_id integer,
  collateral_type_id integer,
  grace_period_type_id smallint not null,
  group_flag smallint,
  loan_amount decimal(10, 3),
  loan_amount_currency_id smallint,
  loan_balance decimal(10, 3),
  loan_balance_currency_id smallint,
  interest_type_id smallint,
  interest_rate decimal(13, 10),
  fund_id smallint,
  meeting_id integer,
  currency_id smallint,
  no_of_installments smallint not null,
  disbursement_date date,
  collateral_note text,
  grace_period_duration smallint,
  interest_at_disb smallint,
  grace_period_penalty smallint,
  prd_offering_id smallint,
  redone smallint not null,
  parent_account_id integer,
  month_rank smallint,
  month_week smallint,
  recur_month smallint,
  
  primary key(account_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(loan_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(loan_balance_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(fund_id)
    references fund(fund_id)
      on delete no action
      on update no action,
  foreign key(grace_period_type_id)
    references grace_period_type(grace_period_type_id)
      on delete no action
      on update no action,
  foreign key(interest_type_id)
    references interest_types(interest_type_id)
      on delete no action
      on update no action,
  foreign key(meeting_id)
    references meeting(meeting_id)
      on delete no action
      on update no action,
 constraint fk_loan_col_type_id foreign key(collateral_type_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
 constraint fk_loan_bus_act_id foreign key(business_activities_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action,
 constraint fk_loan_prd_off_id foreign key(prd_offering_id)
    references prd_offering(prd_offering_id)
      on delete no action
      on update no action,
 constraint fk_loan_acc_id foreign key(parent_account_id)
    references account(account_id)
      on delete no action
      on update no action,     
  constraint fk_loan_account foreign key(parent_account_id)
    references loan_account(account_id)
      on delete no action
      on update no action,
  constraint fk_loan_rankday foreign key (month_rank)
    references rank_days_master(rank_days_master_id)
     on delete no action
     on update no action,
  constraint fk_loan_monthweek foreign key (month_week)
  references week_days_master(week_days_master_id)
    on delete no action 
    on update no action
 
     
)
engine=innodb character set utf8;


create table loan_activity_details (
  id integer auto_increment not null,
  created_by smallint not null,
  account_id integer not null,	
  created_date timestamp not null,
  comments varchar(100) not null,
  principal_amount decimal(10, 3),
  principal_amount_currency_id smallint,
  interest_amount decimal(10, 3),
  interest_amount_currency_id smallint,
  penalty_amount decimal(10, 3),
  penalty_amount_currency_id smallint,
  fee_amount decimal(10, 3),
  fee_amount_currency_id smallint,
  balance_principal_amount decimal(10, 3),
  balance_principal_amount_currency_id smallint,
  balance_interest_amount decimal(10, 3),
  balance_interest_amount_currency_id smallint,
  balance_penalty_amount decimal(10, 3),
  balance_penalty_amount_currency_id smallint,
  balance_fee_amount decimal(10, 3),
  balance_fee_amount_currency_id smallint,		
  primary key(id),
  foreign key(created_by)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(principal_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(interest_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(fee_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(penalty_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(balance_principal_amount_currency_id)
  references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(balance_interest_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(balance_penalty_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(balance_fee_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action        
	
)
engine=innodb character set utf8;


create table waive_off_history(
waive_off_id    integer auto_increment not null,
account_id	integer not null,
waive_off_date	date not null,
waive_off_type	varchar(20) not null,
primary key  (waive_off_id),
foreign key(account_id)
    references loan_account(account_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;


create table loan_summary(
 account_id integer not null,
 orig_principal decimal(10,3),
 orig_principal_currency_id smallint,
 
 orig_interest decimal(10,3),
 orig_interest_currency_id smallint,
 
 orig_fees decimal(10,3),
 orig_fees_currency_id smallint,
 
 orig_penalty decimal(10,3),
 orig_penalty_currency_id smallint,
 
 principal_paid decimal(10,3),
 principal_paid_currency_id smallint,
 
 interest_paid decimal(10,3),
 interest_paid_currency_id smallint,
 
 fees_paid decimal(10,3),
 fees_paid_currency_id smallint,
 
 penalty_paid decimal(10,3),
 penalty_paid_currency_id smallint,
 
 raw_amount_total decimal(10,3),
 raw_amount_total_currency_id smallint,
 
 primary key(account_id),
 foreign key(orig_principal_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(orig_interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(orig_fees_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(orig_penalty_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(principal_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(interest_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(fees_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(penalty_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(account_id)
    references loan_account(account_id)
      on delete no action
      on update no action,
  constraint fk_loan_summary_raw_amount_total
  foreign key(raw_amount_total_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table change_log_detail (
  sequence_num integer auto_increment not null,
  change_log_id integer not null ,
  field_name varchar(100) default null,
  old_value varchar(200)  ,
  new_value varchar(200)  ,
  primary key  (sequence_num),
  foreign key(change_log_id)
    references change_log(change_log_id)
      on delete no action
      on update no action
) engine=innodb character set utf8;

create table customer_account (
  account_id integer not null,
  primary key(account_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table customer_account_activity (
  customer_account_activity_id integer auto_increment not null,
  account_id integer not null,
  description varchar(200) not null,
  amount decimal(10,3),
  fee_amount_currency_id smallint,
  created_date date not null,
  created_by smallint,
  primary key(customer_account_activity_id),
  foreign key(account_id)
      references account(account_id)
      on delete no action
      on update no action,
  foreign key(fee_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(created_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)      
engine=innodb character set utf8;

create table attendance (
  meeting_id integer not null,
  meeting_date date not null,
  attendance smallint,
  notes varchar(200) not null,
  primary key(meeting_id, meeting_date),
  foreign key(meeting_id)
    references customer_meeting(customer_meeting_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table checklist_detail (
  detail_id integer auto_increment not null,
  checklist_id smallint,
  locale_id smallint,
  detail_text varchar(250),
  answer_type smallint not null,
  primary key(detail_id),
  foreign key(checklist_id)
    references checklist(checklist_id)
      on delete no action
      on update no action,
  foreign key(locale_id)
    references supported_locale(locale_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index chk_detail_idx on checklist_detail (checklist_id,locale_id);

create table loan_penalty (
  loan_penalty_id integer auto_increment not null,
  account_id integer,
  penalty_id smallint not null,
  start_date date,
  end_date date,
  penalty_type varchar(200),
  penalty_rate decimal(13,10),
  primary key(loan_penalty_id),
  foreign key(account_id)
    references loan_account(account_id)
      on delete no action
      on update no action,
  foreign key(penalty_id)
    references penalty(penalty_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table prd_checklist (
  checklist_id smallint not null,
  prd_type_id smallint,
  account_status smallint not null,
  primary key (checklist_id),
  foreign key(account_status)
    references account_state(account_state_id)
      on delete no action
      on update no action,
  foreign key(checklist_id)
    references checklist(checklist_id)
      on delete no action
      on update no action,
  foreign key(prd_type_id)
    references prd_type(prd_type_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table account_payment (
  payment_id integer auto_increment not null,
  account_id integer not null,
  payment_type_id smallint not null,
  currency_id smallint,
  amount decimal(10, 3) not null,
  receipt_number varchar(25),
  voucher_number varchar(50),
  check_number varchar(50),
  payment_date date not null,
  receipt_date date,
  bank_name varchar(50),
  primary key(payment_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(payment_type_id)
    references payment_type(payment_type_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index account_id_account_payment_idx on account_payment (account_id);


create table customer_schedule (
  id integer auto_increment not null,
  account_id integer not null,
  customer_id integer not null,
  currency_id smallint,
  action_date date,
    misc_fees decimal(10,3),
  misc_fees_currency_id smallint,
  
  misc_fees_paid decimal(10,3),
  misc_fees_paid_currency_id smallint,
  
  misc_penalty decimal(10,3),
  misc_penalty_currency_id smallint,
  
  misc_penalty_paid decimal(10,3),
  misc_penalty_paid_currency_id smallint,
  
  payment_status smallint not null,
  installment_id smallint not null,
  payment_date date,
  parent_flag smallint,
  version_no integer not null,
  primary key(id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index customer_schedule_action_date_idx 
  on customer_schedule (customer_id,action_date,payment_status);

create table loan_schedule (
  id integer auto_increment not null,
  account_id integer not null,
  customer_id integer not null,
  currency_id smallint,
  action_date date,
  
  principal decimal(10, 3) not null,
  principal_currency_id smallint,
  
  interest decimal(10, 3) not null,
  interest_currency_id smallint,
  
  penalty decimal(10, 3) not null,
  penalty_currency_id smallint,
  
  misc_fees decimal(10,3),
  misc_fees_currency_id smallint,
  
  misc_fees_paid decimal(10,3),
  misc_fees_paid_currency_id smallint,
  
  misc_penalty decimal(10,3),
  misc_penalty_currency_id smallint,
  
  misc_penalty_paid decimal(10,3),
  misc_penalty_paid_currency_id smallint,
  
  principal_paid decimal(10,3),
  principal_paid_currency_id smallint,
  
  interest_paid decimal(10,3),
  interest_paid_currency_id smallint,
  
  penalty_paid decimal(10,3),
  penalty_paid_currency_id smallint,
  
  payment_status smallint not null,
  installment_id smallint not null,
  payment_date date,
  parent_flag smallint,
  version_no integer not null,
  primary key(id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
 foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
       
  foreign key(principal_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(penalty_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(misc_fees_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(misc_fees_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(misc_penalty_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(principal_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(interest_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(penalty_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,

  foreign key(misc_penalty_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table saving_schedule (
  id integer auto_increment not null,
  account_id integer not null,
  customer_id integer not null,
  currency_id smallint,
  action_date date,
  deposit decimal(10, 3) not null,
  deposit_currency_id smallint,
  deposit_paid decimal(10,3),
  deposit_paid_currency_id smallint,
  payment_status smallint not null,
  installment_id smallint not null,
  payment_date date,
  parent_flag smallint,
  version_no integer not null,
  primary key(id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
 foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(deposit_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(deposit_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_checklist (
  checklist_id smallint not null,
  level_id smallint not null,
  customer_status_id smallint not null,
  primary key  (checklist_id),
  foreign key(checklist_id)
    references checklist(checklist_id)
      on delete no action
      on update no action,
  foreign key(level_id)
    references customer_level(level_id)
      on delete no action
      on update no action,
  foreign key(customer_status_id)
    references customer_state(status_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table account_fees (
  account_fee_id integer auto_increment not null,
  account_id integer not null,
  fee_id smallint not null,
  fee_frequency integer,
  status smallint,
  inherited_flag smallint,
  start_date date,
  end_date date,
  account_fee_amnt decimal(10,3) not null,
  account_fee_amnt_currency_id smallint,
  fee_amnt  decimal(10,3) not null,
  fee_status smallint,
  status_change_date date,
  version_no integer not null,
  last_applied_date date,
  primary key(account_fee_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(fee_id)
    references fees(fee_id)
      on delete no action
      on update no action,
  foreign key(account_fee_amnt_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(fee_frequency)
    references recurrence_detail(details_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index account_fees_id_idx on account_fees (account_id,fee_id);

create table savings_account (
  account_id integer not null,
  activation_date date,
  savings_balance decimal(10, 3),
  savings_balance_currency_id smallint,
  recommended_amount decimal(10, 3),
  recommended_amount_currency_id smallint,
  recommended_amnt_unit_id smallint,
  savings_type_id smallint not null,
  int_to_be_posted decimal(10, 3),
  int_to_be_posted_currency_id smallint,  
  last_int_calc_date date,
  last_int_post_date date,
  next_int_calc_date date,
  next_int_post_date date,
  inter_int_calc_date date,
  prd_offering_id smallint not null,
  interest_rate decimal(13, 10) not null,
  interest_calculation_type_id smallint not null,
  time_per_for_int_calc integer,
  min_amnt_for_int decimal(10,3),
  min_amnt_for_int_currency_id smallint,
  primary key(account_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(savings_balance_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(recommended_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(int_to_be_posted_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(recommended_amnt_unit_id)
    references recommended_amnt_unit(recommended_amnt_unit_id)
      on delete no action
      on update no action,
  foreign key(savings_type_id)
    references savings_type(savings_type_id)
      on delete no action
      on update no action,
  foreign key(prd_offering_id)
    references prd_offering(prd_offering_id)
      on delete no action
      on update no action,
  foreign key(interest_calculation_type_id)
    references interest_calculation_types(interest_calculation_type_id)
      on delete no action
      on update no action,
  foreign key(time_per_for_int_calc)
     references meeting (meeting_id)
      on delete no action
      on update no action,
  foreign key(min_amnt_for_int_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table savings_activity_details (
  id integer auto_increment not null,
  created_by smallint,
  account_id integer not null,	
  created_date timestamp not null,
  account_action_id smallint not null,
  amount decimal(10, 3) not null,
  amount_currency_id smallint not null,
  balance_amount decimal(10, 3) not null,
  balance_amount_currency_id smallint not null,
  primary key(id),
  foreign key(created_by)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(account_action_id)
    references account_action(account_action_id)
      on delete no action
      on update no action,
  foreign key(amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(balance_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action	
)
engine=innodb character set utf8;

create table savings_performance(
  id integer auto_increment not null,
  account_id integer not null,
  total_deposits decimal(10,3),
  total_deposits_currency_id smallint,
  total_withdrawals decimal(10,3),
  total_withdrawals_currency_id smallint,
  total_interest_earned decimal(10,3),
  total_interest_earned_currency_id smallint,
  missed_deposits smallint,
  primary key(id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(total_deposits_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(total_withdrawals_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(total_interest_earned_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action     
)
engine=innodb character set utf8;

create table account_custom_field (
  account_custom_field_id integer auto_increment not null,
  account_id integer not null,
  field_id smallint not null,
  field_value varchar(200),
  primary key(account_custom_field_id),
  foreign key(field_id)
    references custom_field_definition(field_id)
      on delete no action
      on update no action,
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table account_trxn (
  account_trxn_id integer auto_increment not null,
  account_id integer not null,
  payment_id integer not null,
  personnel_id integer,
  account_action_id smallint not null,
  currency_id smallint,
  amount_currency_id smallint,
  amount decimal(10, 3) not null,
  due_date date,
  comments varchar(200),
  action_date date not null,
  created_date timestamp not null,
  customer_id integer,
  installment_id smallint,
  related_trxn_id integer,
  primary key(account_trxn_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(account_action_id)
    references account_action(account_action_id)
      on delete no action
      on update no action,
  foreign key(payment_id)
    references account_payment(payment_id)
      on delete no action
      on update no action,
  foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action      
)
engine=innodb character set utf8;
create index account_id_account_trxn_idx on account_trxn (account_id);

create table fee_trxn_detail (
  fee_trxn_detail_id integer auto_increment not null,
  account_trxn_id integer not null,
  account_fee_id integer,
  fee_amount_currency_id smallint,
  fee_amount decimal(10, 3) not null,
  primary key(fee_trxn_detail_id),
  foreign key(account_fee_id)
    references account_fees(account_fee_id)
      on delete no action
      on update no action,
  foreign key(fee_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(account_trxn_id)
    references account_trxn(account_trxn_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index fee_account_trxn_idx on fee_trxn_detail (account_trxn_id);


create table loan_fee_schedule (
  account_fees_detail_id integer auto_increment not null,
  id integer not null,
  installment_id integer not null,
  fee_id smallint not null,
  account_fee_id integer not null,  
  amount decimal(10, 3),
  amount_currency_id smallint,
  amount_paid decimal(10,3),
  amount_paid_currency_id smallint, 
  version_no integer not null,
  primary key(account_fees_detail_id),
  foreign key(id)
    references loan_schedule(id)
      on delete no action
      on update no action,
  foreign key(amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(amount_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(fee_id)
    references fees(fee_id),
  foreign key(account_fee_id)
    references account_fees(account_fee_id)
)
engine=innodb character set utf8;

create table customer_fee_schedule (
  account_fees_detail_id integer auto_increment not null,
  id integer not null,
  installment_id integer not null,
  fee_id smallint not null,
  account_fee_id integer not null,  
  amount decimal(10, 3),
  amount_currency_id smallint,
  amount_paid decimal(10,3),
  amount_paid_currency_id smallint, 
  version_no integer not null,
  primary key(account_fees_detail_id ),
  foreign key(id)
    references customer_schedule(id)
      on delete no action
      on update no action,
  foreign key(amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(amount_paid_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(fee_id)
    references fees(fee_id),
  foreign key(account_fee_id)
    references account_fees(account_fee_id)

 
)
engine=innodb character set utf8;



create table savings_trxn_detail (
  account_trxn_id integer not null,
  deposit_amount decimal(10, 3) ,
  deposit_amount_currency_id smallint,
  withdrawal_amount decimal(10, 3) ,
  withdrawal_amount_currency_id smallint,
  interest_amount decimal(10, 3) ,
  interest_amount_currency_id smallint,
  balance decimal(10, 3), 
  balance_currency_id  smallint not null,
  primary key(account_trxn_id),
  foreign key(account_trxn_id)
    references account_trxn(account_trxn_id)
      on delete no action
      on update no action,
  foreign key(deposit_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(withdrawal_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(interest_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,      
  foreign key(balance_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table loan_trxn_detail (
  account_trxn_id integer not null,
  principal_amount decimal(10, 3),
  principal_amount_currency_id smallint,
  interest_amount decimal(10, 3),
  interest_amount_currency_id smallint,
  penalty_amount decimal(10, 3),
  penalty_amount_currency_id smallint,
  misc_fee_amount decimal(10, 3),
  misc_fee_amount_currency_id smallint,
  misc_penalty_amount decimal(10, 3),
  misc_penalty_amount_currency_id smallint,
  primary key(account_trxn_id),
  foreign key(principal_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(interest_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(penalty_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(misc_penalty_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(misc_fee_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,     
  foreign key(account_trxn_id)
    references account_trxn(account_trxn_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create index loan_account_trxn_idx on loan_trxn_detail (account_trxn_id);

create table customer_trxn_detail (
  account_trxn_id integer not null,
  total_amount decimal(10, 3),
  total_amount_currency_id smallint,
  misc_fee_amount decimal(10, 3),
  misc_fee_amount_currency_id smallint,
  misc_penalty_amount decimal(10, 3),
  misc_penalty_amount_currency_id smallint,
  primary key(account_trxn_id),
  foreign key(total_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(misc_penalty_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(misc_fee_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(account_trxn_id)
    references account_trxn(account_trxn_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table customer_loan_account_detail (
  account_trxn_id integer not null,
  account_id integer not null,
  currency_id smallint,
  installment_number smallint not null,
  due_date date not null,
  principal decimal(10, 3) not null,
  principal_currency_id smallint not null,
  interest decimal(10, 3) not null,
  interest_currency_id smallint not null,
  penalty decimal(10, 3) not null,
  penalty_currency_id smallint not null,
  foreign key(account_trxn_id)
    references account_trxn(account_trxn_id)
      on delete no action
      on update no action,
foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(principal_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(penalty_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(account_id)
    references loan_account(account_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;
create table spouse_father_lookup (
  spouse_father_id integer not null ,
  lookup_id integer not null ,
  primary key  (spouse_father_id),
  foreign key (lookup_id) references lookup_value (lookup_id)
  on delete no action
      on update no action
) 
engine=innodb character set utf8;

create table prd_offering_meeting (
  prd_offering_meeting_id smallint auto_increment not null,
  prd_offering_id smallint not null ,
  prd_meeting_id integer ,
  prd_offering_meeting_type_id smallint not null ,
  primary key  (prd_offering_meeting_id),
  foreign key (prd_offering_id) references prd_offering (prd_offering_id),
  foreign key (prd_meeting_id) references meeting (meeting_id),
  foreign key (prd_offering_meeting_type_id) references meeting_type (meeting_type_id)
) engine=innodb;

create table account_state_flag (
  flag_id smallint auto_increment not null,
  lookup_id integer not null,
  status_id smallint not null,
  flag_description varchar(200),
  retain_flag smallint not null,
  primary key(flag_id),
  foreign key(status_id)
    references account_state(account_state_id)
      on delete no action
      on update no action,
  foreign key(lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table account_flag_detail (
  account_flag_id integer auto_increment not null,
  account_id integer not null,
  flag_id smallint not null,
  created_by smallint not null,
  created_date date not null,
  primary key(account_flag_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(flag_id)
    references account_state_flag(flag_id)
      on delete no action
      on update no action,
  foreign key(created_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table account_status_change_history (
  account_status_change_id integer auto_increment not null,
  account_id integer not null,
  old_status smallint,
  new_status smallint not null,
  changed_by smallint not null,
  changed_date date not null,
  primary key(account_status_change_id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(old_status)
    references account_state(account_state_id)
      on delete no action
      on update no action,
  foreign key(new_status)
    references account_state(account_state_id)
      on delete no action
      on update no action,
  foreign key(changed_by)
    references personnel(personnel_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table scheduled_tasks (
    taskid integer auto_increment,	
    taskname varchar(200),
    description varchar(500),
    starttime timestamp,
    endtime timestamp,
    status smallint,
    primary key(taskid)
)engine=innodb character set utf8;
    
create table temp_id (
  id smallint auto_increment,
  tempid smallint,
  primary key(id)
)engine=innodb character set utf8;

create table config_key_value_integer (
  configuration_id integer auto_increment not null,
  configuration_key varchar(100) not null,
  configuration_value integer not null,
  primary key(configuration_id),
  unique (configuration_key)
)
engine = innodb character set utf8;

create table field_configuration (
  field_config_id integer auto_increment not null,
  field_name varchar(100) not null,
  entity_id smallint not null,
  mandatory_flag smallint not null,
  hidden_flag smallint not null,
  parent_field_config_id integer,
  primary key(field_config_id),
  foreign key(entity_id)
    references entity_master(entity_type_id)
      on delete no action
      on update no action,
  foreign key(parent_field_config_id)
    references field_configuration(field_config_id)
      on delete no action
      on update no action  
)engine=innodb character set utf8;

create table customer_attendance_types(
   attendance_id smallint auto_increment not null,
   attendance_lookup_id integer not null,
   description varchar(50),
   primary key(attendance_id),
   foreign key(attendance_lookup_id)
    references lookup_value(lookup_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


-- TODO: a better name for this table would be "gl_account" 
create table coa (
  coa_id smallint auto_increment not null,
  coa_name varchar(150) not null,
  glcode_id smallint not null,
  primary key(coa_id),
  category_type varchar(20),
  foreign key(glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;

-- TODO: drop this table, it isn't being used
create table coa_idmapper(
  constant_id smallint not null ,
  coa_id smallint not null,
  description varchar(50),
  primary key(constant_id),
  foreign key(coa_id)
    references coa(coa_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;

create table financial_action (
   fin_action_id smallint not null,
   lookup_id integer not null,
   primary key(fin_action_id),
   foreign key(lookup_id)
    references lookup_value_locale(lookup_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;

-- TODO: drop this table, just move "parent_coaid" column to "coa" table
create table coahierarchy (
  coa_id smallint not null ,
  parent_coaid smallint,
  foreign key(coa_id)
    references coa(coa_id)
      on delete no action
      on update no action,
   foreign key(parent_coaid)
     references coa(coa_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;

create table financial_trxn (
   trxn_id integer auto_increment not null,
   account_trxn_id integer not null,
   related_fin_trxn integer,
 currency_id smallint,
   fin_action_id smallint,
   glcode_id smallint not null, 
   posted_amount decimal(10, 3) not null,
   posted_amount_currency_id smallint,
   balance_amount decimal(10, 3) not null,
   balance_amount_currency_id smallint,
   action_date date not null,
   posted_date date not null,
   posted_by smallint,
   accounting_updated smallint,
   notes varchar(200),
   debit_credit_flag smallint not null,
   primary key(trxn_id),
   foreign key(account_trxn_id)
     references account_trxn(account_trxn_id)
      on delete no action
      on update no action,
foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(posted_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(balance_amount_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
	foreign key(currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(related_fin_trxn)
    references financial_trxn(trxn_id)
      on delete no action
      on update no action,
  foreign key(fin_action_id)
    references financial_action(fin_action_id)
      on delete no action
      on update no action,
   foreign key(posted_by)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(glcode_id)
    references gl_code(glcode_id)
      on delete no action
      on update no action  
)engine=innodb character set utf8;

create table customer_attendance(
	id integer auto_increment not null,
	meeting_date date not null,
	customer_id integer not null,
	attendance smallint,
	primary key(id),
	foreign key(customer_id)
	    references customer(customer_id)
	      on delete no action
	      on update no action
)
engine=innodb character set utf8;


create table coll_sheet (
  coll_sheet_id integer auto_increment not null,
  coll_sheet_date date not null,
  status_flag smallint not null,
  run_date date not null,
  primary key(coll_sheet_id)
)engine=innodb character set utf8;

create table coll_sheet_customer (
  coll_sheet_cust_id bigint auto_increment not null,
  coll_sheet_id integer not null,
  cust_id integer not null,
  cust_display_name varchar(200) not null,
  total_due_savings_loan decimal(10,3) ,
  total_due_savings_loan_currency 	smallint ,
  cust_accnt_fee decimal(10,3),
  cust_accnt_fee_currency smallint,
  cust__accnt_penalty decimal(10,3) ,
  cust__accnt_penalty_currency smallint,
  cust_level smallint not null,
  cust_accnt_id integer,
  cust_office_id smallint not null,
  search_id varchar(100) not null,
  parent_customer_id integer ,
  collective_ln_amnt_due decimal(10,3),
  collective_ln_amnt_due_currency smallint,
  collective_ln_disbursal decimal(10,3),
  collective_ln_disbursal_currency smallint,
  collective_savings_due decimal(10,3),
  collective_savings_due_currency smallint,
  collective_accnt_charges decimal(10,3),
  collective_accnt_charges_currency smallint,
  collective_total_charges decimal(10,3),
  collective_total_charges_currency smallint,
  collective_net_cash_in decimal(10,3),
  collective_net_cash_in_currency smallint,
  loan_officer_id smallint,
  primary key(coll_sheet_cust_id),    
  foreign key(total_due_savings_loan_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(cust_accnt_fee_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(cust__accnt_penalty_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(collective_ln_amnt_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(collective_ln_disbursal_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(collective_savings_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(collective_accnt_charges_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(collective_total_charges_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(collective_net_cash_in_currency)
    references currency(currency_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;

create table coll_sheet_loan_details (
  loan_details_id bigint auto_increment not null,
  coll_sheet_cust_id bigint not null,
  accnt_id integer not null,
  total_prin_due decimal(10,3) not null,
  total_prin_due_currency smallint,
  orig_loan_amnt decimal(10,3) not null,
  orig_loan_amnt_currency smallint,
  amnt_to_close_loan decimal(10,3) not null,
  amnt_to_close_loan_currency smallint,
  total_no_of_installments smallint not null,
  current_installment_no smallint,
  principal_due decimal(10,3),
  principal_due_currency smallint,
  interest_due decimal(10,3),
  interest_due_currency smallint,
  fees_due decimal(10,3),
  fees_due_currency smallint,
  penalty_due decimal(10,3),
  penalty_due_currency smallint,
  total_scheduled_amnt_due decimal(10,3),
  total_scheduled_amnt_due_currency smallint,
  principal_overdue decimal(10,3),
  principal_overdue_currency smallint,
  interest_overdue decimal(10,3),
  interest_overdue_currency smallint,
  fees_overdue decimal(10,3),
  fees_overdue_currency smallint,
  penalty_overdue decimal(10,3),
  penalty_overdue_currency smallint,
  total_amnt_overdue decimal(10,3),
  total_amnt_overdue_currency smallint,
  total_amnt_due decimal(10,3) ,
  total_amnt_due_currency smallint,
  amnt_tobe_disbursed decimal(10,3),
  amnt_tobe_disbursed_currency smallint,
  primary key(loan_details_id),    
  foreign key(orig_loan_amnt_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(amnt_to_close_loan_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(principal_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(interest_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(fees_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(penalty_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(total_scheduled_amnt_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(principal_overdue_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(interest_overdue_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(fees_overdue_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(penalty_overdue_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(total_amnt_overdue_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(total_amnt_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(amnt_tobe_disbursed_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(total_prin_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;

create table coll_sheet_savings_details (
  savings_details_id bigint auto_increment not null,
  coll_sheet_cust_id integer not null,
  accnt_id integer not null,
  accnt_balance decimal(10,3),
  accnt_balance_currency smallint,
  recommended_amnt_due decimal(10,3),
  recommended_amnt_due_currency smallint,
  amnt_overdue decimal(10,3),
  amnt_overdue_currency smallint,
  installment_id smallint not null,
  total_savings_amnt_due decimal(10,3),
  total_savings_amnt_due_currency smallint,
  primary key(savings_details_id),    
  foreign key(accnt_balance_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(recommended_amnt_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(amnt_overdue_currency)
    references currency(currency_id)
      on delete no action
      on update no action,    
  foreign key(total_savings_amnt_due_currency)
    references currency(currency_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;


create table report_category (
  report_category_id smallint auto_increment not null,
  report_category_value varchar(100),  
  primary key(report_category_id),
  activity_id smallint,
  foreign key(activity_id)
    references activity(activity_id)
      on delete no action
      on update no action
  
)engine=innodb character set utf8;

create table report (
  report_id smallint auto_increment not null,
  report_category_id smallint ,
  report_name varchar(100),
  report_identifier varchar(100),
  activity_id smallint,
  report_active smallint,
  primary key(report_id),
  foreign key(report_category_id)
    references report_category(report_category_id)
      on delete no action
      on update no action,
  foreign key(activity_id)
    references activity(activity_id)
      on delete no action
      on update no action
  
)engine=innodb character set utf8;

create table transaction_type
(
 transaction_id smallint not null,
 transaction_name varchar(100) not null,
 primary key (transaction_id)
)
engine=innodb character set utf8;

create table accepted_payment_type
(
 accepted_payment_type_id smallint auto_increment not null,
 transaction_id smallint not null,
 payment_type_id smallint not null,
 primary key(accepted_payment_type_id),
 foreign key(transaction_id)
   references transaction_type(transaction_id)
      on delete no action
      on update no action,
  foreign key(payment_type_id)
    references payment_type(payment_type_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table client_perf_history (
  id integer auto_increment not null,
  customer_id integer not null,
  last_loan_amnt decimal(10,3),
  last_loan_amnt_currency_id smallint,
  total_active_loans smallint,
  total_savings_amnt decimal(10,3),
  total_savings_amnt_currency_id smallint,
  delinquint_portfolio decimal(10,3),
  delinquint_portfolio_currency_id smallint,
  primary key(id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table group_perf_history (
  id integer auto_increment not null,
  customer_id integer not null,
  no_of_clients smallint,
  last_group_loan_amnt_disb decimal(10,3),
  last_group_loan_amnt_disb_currency_id smallint,
  avg_loan_size decimal(10,3),
  avg_loan_size_currency_id smallint,
  total_outstand_loan_amnt decimal(10,3),
  total_outstand_loan_amnt_currency_id smallint,
  portfolio_at_risk decimal(10,3),
  portfolio_at_risk_currency_id smallint,
  total_savings_amnt decimal(10,3),
  total_savings_amnt_currency_id smallint,
  primary key(id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table loan_perf_history (
  id integer auto_increment not null,
  account_id integer not null,
  no_of_payments smallint,
  no_of_missed_payments smallint,
  days_in_arrears smallint,
  loan_maturity_date date,
  primary key(id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table client_initial_savings_offering (
  client_offering_id integer auto_increment not null,
  customer_id integer not null,
  prd_offering_id smallint not null,
  primary key(client_offering_id),
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(prd_offering_id)
    references prd_offering(prd_offering_id)
      on delete no action
      on update no action
)engine=innodb character set utf8;

create table loan_arrears_aging (
  id integer auto_increment not null,
  account_id integer not null,
  customer_id integer not null,
  customer_name varchar(200),  
  parent_customer_id integer,
  office_id smallint not null,
  days_in_arrears smallint not null,
  overdue_principal decimal(10,3),
  overdue_principal_currency_id smallint,
  overdue_interest decimal(10,3),
  overdue_interest_currency_id smallint,
  overdue_balance decimal(10,3),  
  overdue_balance_currency_id smallint,  
  unpaid_principal decimal(10,3),
  unpaid_principal_currency_id smallint,
  unpaid_interest decimal(10,3),
  unpaid_interest_currency_id smallint,
  unpaid_balance decimal(10,3),
  unpaid_balance_currency_id smallint,
  primary key(id),
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(parent_customer_id)
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(office_id)
    references office(office_id)
      on delete no action
      on update no action,
  foreign key(overdue_principal_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(overdue_interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(overdue_balance_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(unpaid_principal_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
  foreign key(unpaid_interest_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action,
      foreign key(unpaid_balance_currency_id)
    references currency(currency_id)
      on delete no action
      on update no action            
)engine=innodb character set utf8;

create table loan_counter(
 loan_counter_id integer auto_increment not null,
 client_perf_id integer not null,
 loan_offering_id smallint not null,
 loan_cycle_counter smallint,
 
 primary key(loan_counter_id)
)
engine=innodb character set utf8;

create table database_version (
    database_version integer
)
engine=innodb character set utf8;

-- Begin tables for the Reports Mini Portal

create table mis_bank (
  bankid int default '0' not null,
  bankname varchar(50) default '' not null,
  primary key  (bankid)
) engine=myisam character set latin1;

create table mis_bankbranch (
  bankbranchid int default '0' not null,
  bankid int default '0' not null,
  branchname varchar(50) default '' not null,
  areaid int default '0' not null,
  primary key  (bankbranchid),
  index fk_mis_bankbranch_mis_bank (bankid)
) engine=myisam character set latin1;

create table mis_geographicalarea (
  areaid int default '0' not null,
  areaname varchar(50) default '' not null,
  areatypeid int default '0' not null,
  parentareaid int default null,
  primary key  (areaid)
) engine=myisam character set latin1;

create table mis_geographicalareatype (
  areatypeid int default '0' not null,
  areatypename varchar(20) default '' not null,
  primary key  (areatypeid)
) engine=myisam character set latin1;

create table mis_shgmemberprofile (
  groupid int default '0' not null,
  memberid int default '0' not null,
  membername varchar(100) default '' not null,
  attendence varchar(100) default '0' not null,
  savings decimal(10,0) default '0' not null,
  mstatus varchar(100) default '0' not null,
  primary key  (memberid,groupid)
) engine=myisam character set latin1;

create table mis_shgprofile (
  groupid int default '0' not null,
  groupname varchar(50) default '' not null,
  nummembers int default '0' not null,
  areaid int default '0' not null,
  formationdate timestamp,
  groupleader1 varchar(50) default null,
  groupleader2 varchar(50) default null,
  bankbranchid int default null,
  primary key  (groupid)
) engine=myisam character set latin1;

create table report_datasource (
  datasource_id int auto_increment not null,
  name varchar(255) default '' not null,
  driver varchar(255) default null,
  url varchar(255) default '' not null,
  username varchar(255) default null,
  password varchar(255) default null,
  max_idle int default null,
  max_active int default null,
  max_wait bigint default null,
  validation_query varchar(255) default null,
  jndi tinyint default null,
  primary key  (datasource_id),
  unique (name),
  unique (name)
) engine=innodb character set latin1; 

create table report_jasper_map (
  report_id smallint auto_increment not null,
  report_category_id smallint default null,
  report_name varchar(100) default null,
  report_identifier varchar(100) default null,
  report_jasper varchar(100) default null,
  primary key  (report_id),
  index report_category_id (report_category_id),
  constraint report_jasper_map_ibfk_1 foreign key (report_category_id) 
      references report_category (report_category_id) 
      on delete no action on update no action
) engine=innodb character set utf8; 

create table report_parameter (
  parameter_id int auto_increment not null,
  name varchar(255) default '' not null,
  type varchar(255) default '' not null,
  classname varchar(255) default '' not null,
  data text,
  datasource_id int default null,
  description varchar(255) default null,
  primary key  (parameter_id),
  unique (name),
  unique (name),
  index datasource_id (datasource_id)
) engine=myisam character set latin1; 

create table report_parameter_map (
  report_id int default '0' not null,
  parameter_id int default null,
  required tinyint default null,
  sort_order int default null,
  step int default null,
  map_id int auto_increment not null,
  primary key  (map_id),
  index report_id (report_id),
  index parameter_id (parameter_id)
) engine=myisam character set latin1; 
-- end tables for the Reports Mini Portal

-- start tables for surveys module
create table survey (
  survey_id integer auto_increment not null,
  survey_name varchar(200) not null,
  survey_applies_to varchar(200) not null,
  date_of_creation date not null,
  state integer not null,
  primary key(survey_id)
);

create table ppi_survey (
  country_id integer not null,
  survey_id integer not null,
  very_poor_min integer not null,
  very_poor_max integer not null,
  poor_min integer not null,
  poor_max integer not null,
  at_risk_min integer not null,
  at_risk_max integer not null,
  non_poor_min integer not null,
  non_poor_max integer not null,
  primary key(country_id),
  foreign key(survey_id)
    references survey(survey_id)
      on delete no action
      on update no action
);

create table survey_instance (
  instance_id integer auto_increment not null,
  survey_id integer not null,
  customer_id integer,
  officer_id smallint,
  date_conducted date not null,
  completed_status integer not null,
  account_id integer,
  creating_officer_id smallint not null,
  primary key(instance_id),
  foreign key(survey_id)
    references survey(survey_id)
      on delete no action
      on update no action,
  foreign key(customer_id) 
    references customer(customer_id)
      on delete no action
      on update no action,
  foreign key(officer_id)
    references personnel(personnel_id)
      on delete no action
      on update no action,
  foreign key(account_id)
    references account(account_id)
      on delete no action
      on update no action,
  foreign key(creating_officer_id)
    references personnel(personnel_id)
      on delete no action
      on update no action
);

create table ppi_survey_instance (
    instance_id integer not null,
    bottom_half_below decimal(10, 3),
    top_half_below decimal(10, 3),
    primary key(instance_id)
); 

create table questions (
  question_id integer auto_increment not null,
  answer_type integer not null,
  question_state integer not null,
  question_text varchar(1000) not null,
  numeric_min integer,
  numeric_max integer,
  short_name varchar(50) not null,
  primary key(question_id),
  unique(short_name)
);


create table survey_questions (
  surveyquestion_id integer auto_increment not null,
  survey_id integer not null,
  question_id integer not null,
  question_order integer not null,
  mandatory smallint  default 1  not null,
  primary key(surveyquestion_id),
  foreign key(question_id)
    references questions(question_id)
      on delete no action
      on update no action,
  foreign key(survey_id)
    references survey(survey_id)
      on delete no action
      on update no action
);

create table ppi_likelihoods (
  likelihood_id integer auto_increment not null,
  survey_id integer not null,
  score_from integer not null,
  score_to integer not null,
  bottom_half_below decimal(10,3) not null,
  top_half_below decimal(10,3) not null,
  likelihood_order integer not null,
  primary key(likelihood_id),
  foreign key(survey_id)
    references survey(survey_id)
      on delete no action
      on update no action
);

create table question_choices (
  choice_id integer auto_increment not null,
  question_id integer not null,
  choice_text varchar(200) not null,
  choice_order integer not null,
  ppi varchar(1) not null,
  ppi_points integer,
  primary key(choice_id),
  foreign key(question_id)
    references questions(question_id)
      on delete no action
      on update no action
);


create table survey_response (
  response_id integer auto_increment not null,
  instance_id integer not null,
  survey_question_id integer not null,

  freetext_value text,
  choice_value integer,
  date_value date,
  number_value decimal(16,5),
  multi_select_value text,

  unique(instance_id, survey_question_id),
  primary key(response_id),
  foreign key(survey_question_id)
    references survey_questions(surveyquestion_id)
      on delete no action
      on update no action,
  foreign key(instance_id)
    references survey_instance(instance_id)
      on delete no action
      on update no action,
  foreign key(choice_value)
    references question_choices(choice_id)
      on delete no action
      on update no action
);


-- end tables for surveys module

-- defining table for products mix 

create table prd_offering_mix (
  prd_offering_mix_id  integer auto_increment not null,
  prd_offering_id smallint not null ,
  prd_offering_not_allowed_id smallint not null ,
  created_by smallint ,
  created_date date ,
  updated_by smallint,
  updated_date date ,
  version_no integer,
  primary key  (prd_offering_mix_id),
  constraint prd_offering_mix_prd_offering_id_1
  foreign key (prd_offering_id) 
  references prd_offering (prd_offering_id) 
  on delete no action 
  on update no action,
  constraint prd_offering_mix_prd_offering_id_2 
  foreign key (prd_offering_not_allowed_id) 
  references prd_offering (prd_offering_id) 
  on delete no action 
  on update no action
) engine=innodb character set utf8;


-- tables for loan defaults based on last loan

create table loan_amount_from_last_loan (
loan_amount_from_last_loan_id  smallint auto_increment not null,
  prd_offering_id  smallint not null,
start_range decimal(10, 3) not null, 
end_range decimal(10, 3)  not null,
  min_loan_amount  decimal(10, 3) not null,
  max_loan_amnt  decimal(10, 3) not null,
  default_loan_amount  decimal(10, 3) not null,
 primary key(loan_amount_from_last_loan_id),  
foreign key(prd_offering_id)
    references  loan_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table  no_of_install_from_last_loan (
 no_of_install_from_last_loan_id  smallint auto_increment not null,
  prd_offering_id  smallint not null,
start_range decimal(10, 3) not null, 
end_range decimal(10, 3)  not null,
  min_no_install  decimal(10, 3) not null,
  max_no_install  decimal(10, 3) not null,
  default_no_install  decimal(10, 3) not null,
 primary key(no_of_install_from_last_loan_id ),  
foreign key(prd_offering_id)
    references  loan_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table  loan_amount_from_loan_cycle(
 loan_amount_from_loan_cycle_id  smallint auto_increment not null,
  prd_offering_id  smallint not null,
  min_loan_amount  decimal(10, 3) not null,
  max_loan_amnt  decimal(10, 3) not null,
  default_loan_amount  decimal(10, 3) not null,
 range_index smallint not null,
 primary key(loan_amount_from_loan_cycle_id),  
foreign key(prd_offering_id)
    references  loan_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table  no_of_install_from_loan_cycle (
 no_of_install_from_loan_cycle_id  smallint auto_increment not null,
  prd_offering_id  smallint not null,
  min_no_install  decimal(10, 3) not null,
  max_no_install  decimal(10, 3) not null,
  default_no_install  decimal(10, 3) not null,
range_index decimal(10, 3) not null,
 primary key(no_of_install_from_loan_cycle_id),  
foreign key(prd_offering_id)
    references  loan_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table  loan_amount_same_for_all_loan(
loan_amount_same_for_all_loan_id  smallint auto_increment not null,
  prd_offering_id  smallint not null,
  min_loan_amount  decimal(10, 3) not null,
  max_loan_amnt  decimal(10, 3) not null,
  default_loan_amount  decimal(10, 3) not null,
 primary key(loan_amount_same_for_all_loan_id),  
foreign key(prd_offering_id)
    references  loan_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table  no_of_install_same_for_all_loan(
  no_of_install_same_for_all_loan_id  smallint auto_increment not null,
  prd_offering_id  smallint not null,
  min_no_install  decimal(10, 3) not null,
  max_no_install  decimal(10, 3) not null,
  default_no_install  decimal(10, 3) not null,
 primary key(no_of_install_same_for_all_loan_id),  
foreign key(prd_offering_id)
    references  loan_offering(prd_offering_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;



create table  max_min_loan_amount(
account_id integer   auto_increment not null,
  min_loan_amount  decimal(10, 3) not null,
  max_loan_amount  decimal(10, 3) not null,
  primary key(account_id),  
foreign key(account_id)
    references  loan_account(account_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;


create table max_min_no_of_install(
account_id integer   auto_increment not null,
 min_no_install  decimal(10, 3) not null,
  max_no_install  decimal(10, 3) not null,
  primary key(account_id),  
foreign key(account_id)
    references  loan_account(account_id)
      on delete no action
      on update no action
)
engine=innodb character set utf8;

create table group_loan_counter(
 group_loan_counter_id integer auto_increment not null,
 group_perf_id integer not null,
 loan_offering_id smallint not null,
 loan_cycle_counter smallint,
 primary key(group_loan_counter_id)
)
engine=innodb character set utf8;

create table admin_document (
  admin_document_id integer  auto_increment not null,
  admin_document_name varchar(200) ,
  admin_document_identifier varchar(100) ,
  admin_document_active smallint ,
  created_by smallint ,
  created_date date ,
  updated_by smallint ,
  updated_date date ,
  version_no integer ,
  primary key  (admin_document_id)
)
engine=innodb character set utf8;

create table admin_document_acc_state_mix (
  admin_doc_acc_state_mix_id integer  auto_increment,
  account_state_id smallint not null,
  admin_document_id integer not null,
  created_by smallint ,
  created_date date ,
  updated_by smallint ,
  updated_date date ,
  version_no integer ,
  primary key  (admin_doc_acc_state_mix_id),
  constraint admin_document_acc_state_mix_fk foreign key (account_state_id) references account_state (account_state_id) on delete no action on update no action,
  constraint admin_document_acc_state_mix_fk1 foreign key (admin_document_id) references admin_document (admin_document_id) on delete no action on update no action
)
engine=innodb character set utf8;


create table batch_branch_report(
  branch_report_id integer auto_increment not null,
  branch_id smallint not null,
  run_date date not null,
  primary key(branch_report_id)
)engine=innodb character set utf8;

create table batch_client_summary(
  client_summary_id integer auto_increment not null primary key,
  branch_report_id integer not null,
  field_name varchar(50) not null,
  total varchar(50),
  vpoor_total varchar(50),
  foreign key(branch_report_id) references batch_branch_report(branch_report_id)
)engine=innodb character set utf8;

create table batch_loan_arrears_aging (
  loan_arrears_aging_id integer auto_increment not null,
  aging_period_id integer not null,
  branch_report_id integer not null,
  clients_aging integer not null,
  loans_aging integer not null,
  amount_aging decimal(20,3) not null,
  amount_aging_currency_id smallint not null,
  amount_outstanding_aging decimal(20,3) not null,
  amount_outstanding_aging_currency_id smallint not null,
  interest_aging decimal(20,3) not null,
  interest_aging_currency_id smallint not null,
  primary key (loan_arrears_aging_id),
  foreign key (branch_report_id)
    references batch_branch_report (branch_report_id)
) engine = innodb character set utf8;

create table batch_staff_summary(
  staff_summary_id integer auto_increment not null primary key,
  branch_report_id integer not null,
  personnel_id smallint not null,
  personnel_name varchar(50) not null,
  joining_date date,
  active_borrowers integer not null,
  active_loans integer not null,
  center_count integer not null,
  client_count integer not null,
  loan_amount_outstanding decimal(20,3) not null,
  loan_amount_outstanding_currency_id smallint not null,
  interest_fees_outstanding decimal(20,3) not null,
  interest_fees_outstanding_currency_id smallint not null,
  portfolio_at_risk decimal(20,3) not null,
  total_clients_enrolled integer not null,
  clients_enrolled_this_month integer not null,  
  loan_arrears_amount decimal(20, 3) not null,
  loan_arrears_amount_currency_id smallint not null,
  foreign key(branch_report_id) references batch_branch_report(branch_report_id)
)engine=innodb character set utf8;

create table batch_staffing_level_summary(
 staffing_level_summary_id integer auto_increment not null primary key,
 branch_report_id integer not null,
 role_id integer not null,
 role_name varchar(50) not null,
 personnel_count integer not null,
 foreign key(branch_report_id) references batch_branch_report(branch_report_id)
)engine=innodb character set utf8;

create table batch_loan_details(
 loan_details_id integer auto_increment not null primary key,
 branch_report_id integer not null,
 product_name varchar(50) not null,
 number_of_loans_issued integer not null,
 loan_amount_issued decimal(20,3) not null,
 loan_amount_issued_currency_id smallint not null,
 loan_interest_issued decimal(20,3) not null,
 loan_interest_issued_currency_id smallint not null,
 number_of_loans_outstanding integer not null,
 loan_outstanding_amount decimal(20,3) not null,
 loan_outstanding_amount_currency_id smallint not null,
 loan_outstanding_interest decimal(20,3) not null,
 loan_outstanding_interest_currency_id smallint not null,
 foreign key(branch_report_id) references batch_branch_report(branch_report_id)
)engine=innodb character set utf8;

create table batch_loan_arrears_profile(
 loan_arrears_profile_id integer auto_increment not null primary key,
 branch_report_id integer not null,
 loans_in_arrears integer not null,
 clients_in_arrears integer not null,
 overdue_balance decimal(20,3) not null,
 overdue_balance_currency_id smallint not null,
 unpaid_balance decimal(20,3) not null,
 unpaid_balance_currency_id smallint not null,
 loans_at_risk integer not null,
 outstanding_amount_at_risk decimal(20,3) not null,
 outstanding_amount_at_risk_currency_id smallint not null,
 overdue_amount_at_risk decimal(20,3) not null,
 overdue_amount_at_risk_currency_id smallint not null,
 clients_at_risk integer not null,
 foreign key(branch_report_id) references batch_branch_report(branch_report_id)
)engine=innodb character set utf8;

create table batch_branch_cash_confirmation_report(
  branch_cash_confirmation_report_id integer auto_increment not null,
  branch_id smallint not null,
  run_date date not null,
  primary key(branch_cash_confirmation_report_id)
)engine=innodb character set utf8;

create table batch_branch_confirmation_recovery(
  recovery_id integer auto_increment not null primary key,
  branch_cash_confirmation_report_id integer not null,
  product_name varchar(50) not null,
  due decimal(20,3) not null,
  due_currency_id smallint not null,
  actual decimal(20,3) not null,
  actual_currency_id smallint not null,
  arrears decimal(20,3) not null,
  arrears_currency_id smallint not null,
  foreign key(branch_cash_confirmation_report_id) 
  references batch_branch_cash_confirmation_report(branch_cash_confirmation_report_id)
)engine=innodb character set utf8;

create table batch_branch_confirmation_issue(
  id integer auto_increment not null primary key,
  branch_cash_confirmation_report_id integer not null,
  product_name varchar(50) not null,  
  actual decimal(20,3) not null,
  actual_currency_id smallint not null,  
  foreign key(branch_cash_confirmation_report_id) 
  references batch_branch_cash_confirmation_report(branch_cash_confirmation_report_id)
)engine=innodb character set utf8;

create table batch_branch_confirmation_disbursement(
  id integer auto_increment not null primary key,
  branch_cash_confirmation_report_id integer not null,
  product_name varchar(50) not null,  
  actual decimal(20,3) not null,
  actual_currency_id smallint not null,  
  foreign key(branch_cash_confirmation_report_id) 
  references batch_branch_cash_confirmation_report(branch_cash_confirmation_report_id)
)engine=innodb character set utf8;

