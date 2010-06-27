/*link all holidays to the head office*/
INSERT INTO office_holiday (holiday_id, office_id) 
SELECT h.holiday_id, o.office_id FROM holiday h, office o
where o.office_level_id = 1;