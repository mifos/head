-- data for Reports Mini Portal
INSERT INTO mis_bank VALUES (1,'BANK1');
INSERT INTO mis_bank VALUES (2,'BANK2');
INSERT INTO mis_bank VALUES (3,'BANK3');

INSERT INTO mis_bankbranch VALUES (1,1,'BRANCHB1',4);
INSERT INTO mis_bankbranch VALUES (2,2,'BRANCHB2',5);
INSERT INTO mis_bankbranch VALUES (3,3,'BRANCHB3',6);
INSERT INTO mis_bankbranch VALUES (4,1,'BRANCHB11',7);
INSERT INTO mis_bankbranch VALUES (5,2,'BRANCHB21',8);
INSERT INTO mis_bankbranch VALUES (6,3,'BRANCHB31',9);

INSERT INTO mis_geographicalarea VALUES (1,'A1',1,NULL);
INSERT INTO mis_geographicalarea VALUES (2,'A2',1,NULL);
INSERT INTO mis_geographicalarea VALUES (3,'A3',1,NULL);
INSERT INTO mis_geographicalarea VALUES (4,'B1',2,1);
INSERT INTO mis_geographicalarea VALUES (5,'B2',2,2);
INSERT INTO mis_geographicalarea VALUES (6,'B3',2,3);
INSERT INTO mis_geographicalarea VALUES (7,'B11',2,1);
INSERT INTO mis_geographicalarea VALUES (8,'B21',2,2);
INSERT INTO mis_geographicalarea VALUES (9,'B31',2,3);

INSERT INTO mis_geographicalareatype VALUES (1,'A');
INSERT INTO mis_geographicalareatype VALUES (2,'B');

INSERT INTO mis_shgmemberprofile VALUES (1,1,'MEMBER1','YES',50,'YES');
INSERT INTO mis_shgmemberprofile VALUES (1,2,'MEMBER2','YES',50,'YES');
INSERT INTO mis_shgmemberprofile VALUES (1,3,'MEMBER3','NO',0,'YES');
INSERT INTO mis_shgmemberprofile VALUES (1,4,'MEMBER4','YES',100,'NO');

INSERT INTO mis_shgprofile VALUES 
(1,'G1',10,4,null,'LEADER 1','LEADER 2',1);
INSERT INTO mis_shgprofile VALUES 
(2,'G2',10,4,null,'LEADER1','LEADER2',1);
INSERT INTO mis_shgprofile VALUES 
(3,'G3',8,5,null,'LAEDER1','LEADER2',2);
INSERT INTO mis_shgprofile VALUES 
(4,'G4',12,4,null,'LEADER1','LEADER2',1);
INSERT INTO mis_shgprofile VALUES 
(5,'G5',15,5,null,'LEADER1','LEADER2',2);
INSERT INTO mis_shgprofile VALUES 
(6,'G6',11,7,null,'LEADER1','LEADER2',1);

INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(1,1,'Client Summary & History Report','report_designer',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(2,1,'Client Product Wise History Report','product_history',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(3,1,'Clients Settlement info Report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(4,1,'Client Loan repayment schedule',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(5,1,'Client Fees, Charges and Penalties report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(6,1,'Clients pending approval report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(7,1,'Members without Saving Accouts',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(8,2,'Branch Performance Status Report','branch_performance',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(9,2,'Area Performance Status Report','area_performance',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(10,2,'Division Performance Status Report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(11,2,'Region Performance Status Report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(12,2,'Grameen Koota Performance Status Report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(13,2,'Staff Performance Report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(14,2,'Outreach Report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(15,2,'Kendra Summary Report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(16,3,'Collection sheet','collection_sheet',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(17,4,'Loan Product Distribution - Activity Wise, Size Wize, Product Wise, Sequency Wise','loan_distribution',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(18,4,'Branch Due disbursement Report','branch_disbursement',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(19,4,'Loans pending approval report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(20,4,'Loan Accounts reports',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(21,5,'Daily Cash Confirmation Report - Staff Wise','staffwise_report',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(22,5,'Daily Cash Flow Report - Branch','branchwise_report',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(23,5,'Fund Requirement Report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(24,5,'Daily Transaction Summary report',null,NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(25,6,'Daily Portfolio quality data report - Aging analysis','analysis',NULL);
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,
REPORT_IDENTIFIER, REPORT_JASPER) VALUES
(26,7,'Kendra Meeting Schedule','kendra_meeting',NULL);
