select tally.branchname, tally.voucherdate, tally.vouchertype, tally.glcode, tally.glname, tally.debit, tally.credit

from
(
/* Note: the query is has been made into a sub-select because some output columns were not being picked up on the ubuntu platform */
select  o.display_name as "branchname",
DATE_FORMAT(fintrxn.action_date, '%Y-%m-%d') as "voucherdate",

(CASE fintrxn.fin_action_id

WHEN 1 then /*PRINCIPALPOSTING*/
    (CASE atrxn.account_action_id
        WHEN 1 then 'RECEIPT' /*LoanRepayment*/
        WHEN 9 then 'JOURNAL' /*Adjustment (undoes LoanRepayment)*/
        WHEN 18 then 'JOURNAL' /*LoanReversal (reverses fees & payments parts)*/
        ELSE concat('Unknown Account Action:', atrxn.account_action_id, ' for Finanical Action ', fintrxn.fin_action_id)
    END)
WHEN 2 then /*INTERESTPOSTING*/
    (CASE atrxn.account_action_id
        WHEN 1 then 'RECEIPT' /*LoanRepayment*/
        WHEN 9 then 'JOURNAL' /*Adjustment (undoes LoanRepayment)*/
        WHEN 18 then 'JOURNAL' /*LoanReversal (reverses fees & payments parts)*/
        ELSE concat('Unknown Account Action:', atrxn.account_action_id, ' for Finanical Action ', fintrxn.fin_action_id)
    END)
WHEN 4 then /*MISCFEEPOSTING*/
    (CASE atrxn.account_action_id
        WHEN 1 then 'RECEIPT' /*LoanRepayment*/
        WHEN 9 then 'JOURNAL' /*Adjustment (undoes LoanRepayment)*/
        WHEN 18 then 'JOURNAL' /*LoanReversal (reverses fees & payments parts)*/
        ELSE concat('Unknown Account Action:', atrxn.account_action_id, ' for Finanical Action ', fintrxn.fin_action_id)
    END)
WHEN 6 then /*MISCPENALTYPOSTING*/
    (CASE atrxn.account_action_id
        WHEN 1 then 'RECEIPT' /*LoanRepayment*/
        WHEN 9 then 'JOURNAL' /*Adjustment (undoes LoanRepayment)*/
        WHEN 12 then 'RECEIPT' /*CustomerAccountRepayment*/
        WHEN 18 then 'JOURNAL' /*LoanReversal (reverses fees & payments parts)*/
        ELSE concat('Unknown Account Action:', atrxn.account_action_id, ' for Finanical Action ', fintrxn.fin_action_id)
    END)
WHEN 3 then  /*FEEPOSTING*/
    (CASE atrxn.account_action_id
        WHEN 1 then 'RECEIPT' /*Fee*/
        WHEN 4 then 'RECEIPT' /*Fee*/
        WHEN 9 then 'JOURNAL' /* Adjustment (undoes Fee) */
        WHEN 18 then 'JOURNAL' /*LoanReversal (reverses fees & payments parts)*/
        WHEN 12 then 'RECEIPT' /*Customer Fee*/
        WHEN 13 THEN 'JOURNAL' /*CustomerAdjustment*/
        ELSE concat('Unknown Account Action:', atrxn.account_action_id, ' for Finanical Action ', fintrxn.fin_action_id)
    END)
WHEN 7 then  /*DISBURSAL*/
    (CASE atrxn.account_action_id
        WHEN 10 then 'PAYMENT' /*Disbursal*/
        WHEN 19 then 'JOURNAL' /*DisrbursalAmountReversal (undoes Disbursal part)*/
        ELSE concat('Unknown Account Action:', atrxn.account_action_id, ' for Finanical Action ', fintrxn.fin_action_id)
    END)


WHEN 5 then 'RECEIPT' /*PENALTYPOSTING - However, this is not used by mifos*/
WHEN 9 then 'RECEIPT' /*MANDATORYDEPOSIT*/
WHEN 10 then 'RECEIPT' /*VOLUNTARYDEPOSIT*/
WHEN 16 then 'RECEIPT' /*CUSTOMERACCOUNTMISCFEESPOSTING*/

WHEN 11 then 'PAYMENT' /*MANDATORYWITHDRAWAL*/
WHEN 12 then 'PAYMENT' /*VOLUNTARYWITHDRAWAL*/

WHEN 8 then 'JOURNAL' /*ROUNDING*/
WHEN 14 then 'JOURNAL' /*SAVINGS_INTERESTPOSTING*/
WHEN 18 then 'JOURNAL' /*MANDATORYDEPOSIT_ADJUSTMENT*/
WHEN 19 then 'JOURNAL' /*VOLUNTARYDEPOSIT_ADJUSTMENT*/
WHEN 20 then 'JOURNAL' /*MANDATORYWITHDRAWAL_ADJUSTMENT*/
WHEN 21 then 'JOURNAL' /*VOLUNTARYWITHDRAWAL_ADJUSTMENT*/
WHEN 22 then 'JOURNAL' /*WRITEOFF*/
WHEN 23 then 'JOURNAL' /*RESCHEDULE*/

ELSE concat('Unknown Financial Action:', fintrxn.fin_action_id)  /*shouldnt ever get this */
END) as "vouchertype",

gl.glcode_value as "glcode", coa.coa_name as "glname",
sum( case when fintrxn.debit_credit_flag = 0 then abs(fintrxn.posted_amount) else 0 end ) as "debit",
sum( case when fintrxn.debit_credit_flag = 1 then abs(fintrxn.posted_amount) else 0 end ) as "credit"

from financial_trxn fintrxn
inner join gl_code gl on gl.glcode_id = fintrxn.glcode_id
inner join coa on coa.glcode_id = gl.glcode_id
inner join account_trxn atrxn on atrxn.account_trxn_id = fintrxn.account_trxn_id
inner join account a on a.account_id = atrxn.account_id
inner join office o on o.office_id = a.office_id
inner join loan_account l on l.account_id = atrxn.account_id 
where fintrxn.posted_date between date(?) and date(?) and l.parent_account_id is null
group by branchname, voucherdate, vouchertype, glcode, glname
order by branchname, voucherdate, vouchertype, glcode, glname) tally;