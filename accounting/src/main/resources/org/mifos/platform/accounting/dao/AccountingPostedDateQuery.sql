select posted_date from financial_trxn fintrxn
inner join gl_code gl on gl.glcode_id = fintrxn.glcode_id 
inner join coa on coa.glcode_id = gl.glcode_id 
inner join account_trxn atrxn on atrxn.account_trxn_id = fintrxn.account_trxn_id
inner join account a on a.account_id = atrxn.account_id
inner join office o on o.office_id = a.office_id
inner join loan_account l on l.account_id = atrxn.account_id
where posted_date between date(?) and date(?)
group by posted_date order by posted_date DESC limit 10 offset ?;