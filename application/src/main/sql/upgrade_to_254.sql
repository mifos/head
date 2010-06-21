alter table loan_account change collateral_note collateral_note varchar(500);

update database_version set database_version = 254 where database_version = 253;