/* MIFOS-2982 Remove Regenerateschedule batch job */
alter table customer_meeting drop index cust_inherited_meeting_idx;
alter table customer_meeting drop foreign key customer_meeting_ibfk_3;
alter table customer_meeting drop column updated_meeting_id;
alter table customer_meeting drop column updated_flag;

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 253 WHERE DATABASE_VERSION = 252;
