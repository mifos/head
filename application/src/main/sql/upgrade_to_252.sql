INSERT INTO office_holiday (holiday_id, office_id) SELECT t1.holidayId, t2.officeId FROM (SELECT holiday.holiday_id AS holidayId FROM holiday LEFT OUTER JOIN office_holiday ON office_holiday.holiday_id = holiday.holiday_id WHERE office_holiday.holiday_id IS NULL) t1, (SELECT office_id AS officeId FROM office) t2;

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 252 WHERE DATABASE_VERSION = 251;
