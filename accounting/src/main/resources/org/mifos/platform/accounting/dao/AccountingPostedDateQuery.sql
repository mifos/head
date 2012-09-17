select posted_date from financial_trxn fintrxn
where posted_date between date(?) and date(?)
group by posted_date order by posted_date DESC limit 10 offset ?;