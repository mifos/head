
/*!40000 ALTER TABLE `mis_bank` DISABLE KEYS */;
LOCK TABLES `mis_bank` WRITE;
INSERT INTO `mis_bank` VALUES (1,'BANK1'),(2,'BANK2'),(3,'BANK3');
UNLOCK TABLES;
/*!40000 ALTER TABLE `mis_bank` ENABLE KEYS */;

/*!40000 ALTER TABLE `mis_bankbranch` DISABLE KEYS */;
LOCK TABLES `mis_bankbranch` WRITE;
INSERT INTO `mis_bankbranch` VALUES (1,1,'BRANCHB1',4),(2,2,'BRANCHB2',5),(3,3,'BRANCHB3',6),(4,1,'BRANCHB11',7),(5,2,'BRANCHB21',8),(6,3,'BRANCHB31',9);
UNLOCK TABLES;
/*!40000 ALTER TABLE `mis_bankbranch` ENABLE KEYS */;


/*!40000 ALTER TABLE `mis_geographicalarea` DISABLE KEYS */;
LOCK TABLES `mis_geographicalarea` WRITE;
INSERT INTO `mis_geographicalarea` VALUES (1,'A1',1,NULL),(2,'A2',1,NULL),(3,'A3',1,NULL),(4,'B1',2,1),(5,'B2',2,2),(6,'B3',2,3),(7,'B11',2,1),(8,'B21',2,2),(9,'B31',2,3);
UNLOCK TABLES;
/*!40000 ALTER TABLE `mis_geographicalarea` ENABLE KEYS */;


/*!40000 ALTER TABLE `mis_geographicalareatype` DISABLE KEYS */;
LOCK TABLES `mis_geographicalareatype` WRITE;
INSERT INTO `mis_geographicalareatype` VALUES (1,'A'),(2,'B');
UNLOCK TABLES;
/*!40000 ALTER TABLE `mis_geographicalareatype` ENABLE KEYS */;


/*!40000 ALTER TABLE `mis_shgmemberprofile` DISABLE KEYS */;
LOCK TABLES `mis_shgmemberprofile` WRITE;
INSERT INTO `mis_shgmemberprofile` VALUES (1,1,'MEMBER1','YES','50','YES'),(1,2,'MEMBER2','YES','50','YES'),(1,3,'MEMBER3','NO','0','YES'),(1,4,'MEMBER4','YES','100','NO');
UNLOCK TABLES;
/*!40000 ALTER TABLE `mis_shgmemberprofile` ENABLE KEYS */;


/*!40000 ALTER TABLE `mis_shgprofile` DISABLE KEYS */;
LOCK TABLES `mis_shgprofile` WRITE;
INSERT INTO `mis_shgprofile` VALUES (1,'G1',10,4,'0000-00-00 00:00:00','LEADER 1','LEADER 2',1),(2,'G2',10,4,'0000-00-00 00:00:00','LEADER1','LEADER2',1),(3,'G3',8,5,'0000-00-00 00:00:00','LAEDER1','LEADER2',2),(4,'G4',12,4,'0000-00-00 00:00:00','LEADER1','LEADER2',1),(5,'G5',15,5,'0000-00-00 00:00:00','LEADER1','LEADER2',2),(6,'G6',11,7,'0000-00-00 00:00:00','LEADER1','LEADER2',1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `mis_shgprofile` ENABLE KEYS */;


LOCK TABLES `report_jasper_map` WRITE;
INSERT INTO report_jasper_map(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,REPORT_IDENTIFIER, REPORT_JASPER) VALUES(1,1,'Client Summary & History Report','report_designer',NULL),(2,1,'Client Product Wise History Report','product_history',NULL),(3,1,'Clients Settlement info Report',null,NULL),(4,1,'Client Loan repayment schedule',null,NULL),(5,1,'Client Fees, Charges and Penalties report',null,NULL),(6,1,'Clients pending approval report',null,NULL),(7,1,'Members without Saving Accouts',null,NULL),(8,2,'Branch Performance Status Report','branch_performance',NULL),(9,2,'Area Performance Status Report','area_performance',NULL),(10,2,'Division Performance Status Report',null,NULL),(11,2,'Region Performance Status Report',null,NULL),(12,2,'Grameen Koota Performance Status Report',null,NULL),(13,2,'Staff Performance Report',null,NULL),(14,2,'Outreach Report',null,NULL),(15,2,'Kendra Summary Report',null,NULL),(16,3,'Collection sheet','collection_sheet',NULL),(17,4,'Loan Product Distribution - Activity Wise, Size Wize, Product Wise, Sequency Wise','loan_distribution',NULL),(18,4,'Branch Due disbursement Report','branch_disbursement',NULL),(19,4,'Loans pending approval report',null,NULL),(20,4,'Loan Accounts reports',null,NULL),(21,5,'Daily Cash Confirmation Report - Staff Wise','staffwise_report',NULL),(22,5,'Daily Cash Flow Report - Branch','branchwise_report',NULL),(23,5,'Fund Requirement Report',null,NULL),(24,5,'Daily Transaction Summary report',null,NULL),(25,6,'Daily Portfolio quality data report - Aging analysis','analysis',NULL),(26,7,'Kendra Meeting Schedule','kendra_meeting',NULL);
UNLOCK TABLES;

