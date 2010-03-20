-- (Bug 1778) sets ACTIVITY_ID for the BIRT reports: Detailed Aging and Active Loans
-- It was not initialized because the column didn't exist when the reports were added, 
-- in upgrade 117 and 120
UPDATE REPORT SET ACTIVITY_ID=207 WHERE REPORT_ID=28;
UPDATE REPORT SET ACTIVITY_ID=212 WHERE REPORT_ID=29;
UPDATE DATABASE_VERSION SET DATABASE_VERSION=191 WHERE DATABASE_VERSION=190;