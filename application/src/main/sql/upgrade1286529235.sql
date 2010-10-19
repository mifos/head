alter table question_group_instance add event_source_id integer not null;

update question_group_instance set event_source_id = (select event_source_id from question_group_event_sources as events where events.question_group_id = question_group_id limit 1);