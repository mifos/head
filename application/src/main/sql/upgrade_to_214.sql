drop table if exists mis_bank;
drop table if exists mis_bankbranch;
drop table if exists mis_geographicalarea;
drop table if exists mis_geographicalareatype;
drop table if exists mis_shgmemberprofile;
drop table if exists mis_shgprofile;

update database_version set database_version = 214 where database_version = 213;
