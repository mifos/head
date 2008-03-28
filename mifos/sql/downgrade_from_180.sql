-- Copyright (c) 2005-2008 Grameen Foundation USA
-- 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
-- All rights reserved.
-- 
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--     http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- permissions and limitations under the License.
-- 
-- See also http://www.apache.org/licenses/LICENSE-2.0.html for an
-- explanation of the license and how it is applied.
DROP TABLE IF EXISTS BATCH_CLIENT_SUMMARY;
DROP TABLE IF EXISTS BATCH_STAFF_SUMMARY;
DROP TABLE IF EXISTS BATCH_LOAN_ARREARS_AGING;
DROP TABLE IF EXISTS BATCH_STAFFING_LEVEL_SUMMARY;
DROP TABLE IF EXISTS BATCH_LOAN_DETAILS;
DROP TABLE IF EXISTS BATCH_LOAN_ARREARS_PROFILE;
DROP TABLE IF EXISTS BATCH_BRANCH_REPORT;

DROP TABLE IF EXISTS BATCH_BRANCH_CONFIRMATION_RECOVERY;
DROP TABLE IF EXISTS BATCH_BRANCH_CONFIRMATION_ISSUE;
DROP TABLE IF EXISTS BATCH_BRANCH_CONFIRMATION_DISBURSEMENT;
DROP TABLE IF EXISTS BATCH_BRANCH_CASH_CONFIRMATION_REPORT;

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 179 WHERE DATABASE_VERSION = 180;
