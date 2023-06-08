DROP VIEW IF EXISTS registered_athletes;
CREATE OR REPLACE VIEW registered_athletes AS
	SELECT a.*, ss.name as sport_school_name, gs.name as group_name, fs.code as family_code, h.date as last_register_date, last_unregister_date,
		athlete_schedules.num_days as num_days, athlete_schedules.days as days, athlete_schedules.schedule_ids as schedule_ids,
	    row_number() over (partition by fs.id order by h.date asc) as familiar_number
	FROM athletes a
	inner join sport_schools ss on ss.id = a.sport_school_id
	inner join families fs on fs.id = a.family_id
	inner join (select athlete_id, type, date, row_number() over (partition by athlete_id order by id desc) rn
		from historic
		where date <= now() or (date > now() and activation_date <= now())) h on a.id = h.athlete_id
	left join (select athlete_id, string_agg(s."day", ',') as days, string_agg(s."id"::text, ',') as schedule_ids, count(schedule_id) as num_days
		from athletes_schedules ass
		inner join schedules s on ass.schedule_id = s.id group by 1) athlete_schedules on athlete_schedules.athlete_id = a.id
	left join groups gs on gs.id = a.group_id
	left join (select athlete_id, max(date) as last_unregister_date
		from historic
		where date <= now() and type = 'UNREGISTER'
		group by 1) h_unregister on h_unregister.athlete_id = a.id
	where rn = 1 and type = 'REGISTER';
commit;


DROP VIEW IF EXISTS unregistered_athletes;
CREATE OR REPLACE VIEW unregistered_athletes AS
	SELECT a.*, ss.name as sport_school_name, gs.name as group_name, fs.code as family_code, last_register_date, h.date as last_unregister_date,
		athlete_schedules.num_days as num_days, athlete_schedules.days as days
	FROM athletes a
	inner join sport_schools ss on ss.id = a.sport_school_id
	inner join families fs on fs.id = a.family_id
	left join (select athlete_id, type, date, row_number() over (partition by athlete_id order by id desc) rn
		from historic
		where date <= now()) h on a.id = h.athlete_id
	left join (select athlete_id, string_agg(s."day", ',') as days, count(schedule_id) as num_days
		from athletes_schedules ass
		inner join schedules s on ass.schedule_id = s.id group by 1) athlete_schedules on athlete_schedules.athlete_id = a.id
	left join groups gs on gs.id = a.group_id
	left join (select athlete_id, min(date) as last_register_date
		from historic
		where date <= now() and type = 'REGISTER'
		group by 1) h_register on h_register.athlete_id = a.id
	where ((rn is null AND type is null) OR (rn = 1 AND type = 'UNREGISTER'));
	