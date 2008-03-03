/* The chart of accounts is now dynamically populated. 
   See story #122 for details.
   
   This script is only run if no existing Chart of Accounts data is
   found. See the Upgrade174 class for details. */

DELETE FROM coahierarchy;
DELETE FROM coa_idmapper;
DELETE FROM coa;
DELETE FROM gl_code;

ALTER TABLE coa ADD COLUMN CATEGORY_TYPE VARCHAR(20);

-- TODO: here are some ideas for futher/future improvements to the
-- chart of accounts tables...
--
-- -- drop coahierarchy & add "parent" column to "coa"
-- DROP TABLE coahierarchy;
-- ALTER TABLE coa ...
--
-- -- drop coa_idmapper
-- DROP TABLE coa_idmapper;
--
-- -- drop gl_code & add glcode_value to "coa" table
-- -- rename "coa" to "gl_account"
-- -- add "category_type" column to new "gl_account" table
-- --     enum: "ASSETS", "LIABILITIES", "INCOME", "EXPENDITURE"
--
-- -- drop old foreign keys and create new ones to "gl_account" table
-- ALTER TABLE coa DROP FOREIGN KEY glcode_id;
--
-- ALTER TABLE fees DROP FOREIGN KEY glcode_id;
-- ALTER TABLE financial_trxn DROP FOREIGN KEY glcode_id;
-- -- not sure about this next one. What's the constraint for?
-- ALTER TABLE loan_offering DROP CONSTRAINT loan_offering_penalty_glcode;
-- ALTER TABLE loan_offering DROP FOREIGN KEY principal_glcode_id;
-- ALTER TABLE loan_offering DROP FOREIGN KEY interest_glcode_id;
-- ALTER TABLE loan_offering DROP FOREIGN KEY penalties_glcode_id;
-- ALTER TABLE penalty DROP FOREIGN KEY glcode_id;
-- ALTER TABLE prd_offering DROP FOREIGN KEY glcode_id;
-- ALTER TABLE program DROP FOREIGN KEY glcode_id;
-- ALTER TABLE savings_offering DROP FOREIGN KEY deposit_glcode_id;
-- ALTER TABLE savings_offering DROP FOREIGN KEY interest_glcode_id;

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 176 WHERE DATABASE_VERSION = 175;
