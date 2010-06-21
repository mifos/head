-- This script represents the latest master data (data which is shipped
-- with MIFOS and is more about the operation of MIFOS than business data).

/* The point of having this file is to see the latest master data in one place,
   rather than having to look through a bunch of upgrade scripts.  So we
   should group things logically (by table or functionality, for example)
   rather than historically.
   We shouldn't have an INSERT followed by an UPDATE to the same row (unless
   there is some special reason like foreign keys). */

-- Applying it should be the same as:
-- Start with mifosmasterdata.sql
-- apply rmpmasterdata.sql
-- apply Iteration14-DML-DBScripts10102006.sql
-- apply Iteration15-DBScripts20061012.sql
-- apply add-version.sql
-- apply Index.sql
-- apply all upgrades to date

insert into database_version(database_version) values(257);

/* The table Currency holds configuration related items for a currency like
 * display symbol, rounding mode etc which is to be applied on a currency.
 * Currency names and codes are from ISO 4217.
 * http://www.iso.org/iso/support/faqs/faqs_widely_used_standards/widely_used_standards_other/currency_codes/currency_codes_list-1.htm
 * http://en.wikipedia.org/wiki/ISO_4217
 *
 * Some columns don't need meaningful values. See "More ISO currencies" below
 * for exactly what columns need meaningful values.
 *
 * Consult the application-wide install-time configuration file for
 * changing currency configuration.
 * http://mifos.org/developers/wiki/ConfiguringMifos#application-wide-install-time-settings
 */
insert into currency (currency_id, currency_code, rounding_amount, currency_name) values 
(1, 'USD', 1, 'US Dollar'),
(2, 'INR', 1, 'Indian Rupee'),
(3, 'EUR', 1, 'Euro' ),
(4, 'GBP', 1, 'Pound Sterling'),

/* More ISO currencies.
 *
 */
(5  , 'AED', 1, 'United Arab Emirates dirham'),
(6  , 'AFN', 1, 'Afghani'),
(7  , 'ALL', 1, 'Lek'),
(8  , 'AMD', 1, 'Armenian dram'),
(9  , 'ANG', 1, 'Netherlands Antillean guilder'),
(10 , 'AOA', 1, 'Kwanza'),
(11 , 'ARS', 1, 'Argentine peso'),
(12 , 'AUD', 1, 'Australian dollar'),
(13 , 'AWG', 1, 'Aruban guilder'),
(14 , 'AZN', 1, 'Azerbaijanian manat'),
(15 , 'BAM', 1, 'Convertible marks'),
(16 , 'BBD', 1, 'Barbados dollar'),
(17 , 'BDT', 1, 'Bangladeshi taka'),
(18 , 'BGN', 1, 'Bulgarian lev'),
(19 , 'BHD', 1, 'Bahraini dinar'),
(20 , 'BIF', 1, 'Burundian franc'),
(21 , 'BMD', 1, 'Bermudian dollar'),
(22 , 'BND', 1, 'Brunei dollar'),
(23 , 'BOB', 1, 'Boliviano'),
(24 , 'BOV', 1, 'Bolivian Mvdol'),
(25 , 'BRL', 1, 'Brazilian real'),
(26 , 'BSD', 1, 'Bahamian dollar'),
(27 , 'BTN', 1, 'Ngultrum'),
(28 , 'BWP', 1, 'Pula'),
(29 , 'BYR', 1, 'Belarussian ruble'),
(30 , 'BZD', 1, 'Belize dollar'),
(31 , 'CAD', 1, 'Canadian dollar'),
(32 , 'CDF', 1, 'Franc Congolais'),
(33 , 'CHE', 1, 'WIR euro'),
(34 , 'CHF', 1, 'Swiss franc'),
(35 , 'CHW', 1, 'WIR franc'),
(36 , 'CLF', 1, 'Unidad de Fomento'),
(37 , 'CLP', 1, 'Chilean peso'),
(38 , 'CNY', 1, 'Renminbi'),
(39 , 'COP', 1, 'Colombian peso'),
(40 , 'COU', 1, 'Unidad de Valor Real'),
(41 , 'CRC', 1, 'Costa Rican colon'),
(42 , 'CUP', 1, 'Cuban peso'),
(43 , 'CVE', 1, 'Cape Verde escudo'),
(44 , 'CZK', 1, 'Czech koruna'),
(45 , 'DJF', 1, 'Djibouti franc'),
(46 , 'DKK', 1, 'Danish krone'),
(47 , 'DOP', 1, 'Dominican peso'),
(48 , 'DZD', 1, 'Algerian dinar'),
(49 , 'EEK', 1, 'Kroon'),
(50 , 'EGP', 1, 'Egyptian pound'),
(51 , 'ERN', 1, 'Nakfa'),
(52 , 'ETB', 1, 'Ethiopian birr'),
(53 , 'FJD', 1, 'Fiji dollar'),
(54 , 'FKP', 1, 'Falkland Islands pound'),
(55 , 'GEL', 1, 'Lari'),
(56 , 'GHS', 1, 'Cedi'),
(57 , 'GIP', 1, 'Gibraltar pound'),
(58 , 'GMD', 1, 'Dalasi'),
(59 , 'GNF', 1, 'Guinea franc'),
(60 , 'GTQ', 1, 'Quetzal'),
(61 , 'GYD', 1, 'Guyana dollar'),
(62 , 'HKD', 1, 'Hong Kong dollar'),
(63 , 'HNL', 1, 'Lempira'),
(64 , 'HRK', 1, 'Croatian kuna'),
(65 , 'HTG', 1, 'Haiti gourde'),
(66 , 'HUF', 1, 'Forint'),
(67 , 'IDR', 1, 'Rupiah'),
(68 , 'ILS', 1, 'Israeli new sheqel'),
(69 , 'IQD', 1, 'Iraqi dinar'),
(70 , 'IRR', 1, 'Iranian rial'),
(71 , 'ISK', 1, 'Iceland krona'),
(72 , 'JMD', 1, 'Jamaican dollar'),
(73 , 'JOD', 1, 'Jordanian dinar'),
(74 , 'JPY', 1, 'Japanese yen'),
(75 , 'KES', 1, 'Kenyan shilling'),
(76 , 'KGS', 1, 'Som'),
(77 , 'KHR', 1, 'Riel'),
(78 , 'KMF', 1, 'Comoro franc'),
(79 , 'KPW', 1, 'North Korean won'),
(80 , 'KRW', 1, 'South Korean won'),
(81 , 'KWD', 1, 'Kuwaiti dinar'),
(82 , 'KYD', 1, 'Cayman Islands dollar'),
(83 , 'KZT', 1, 'Tenge'),
(84 , 'LAK', 1, 'Kip'),
(85 , 'LBP', 1, 'Lebanese pound'),
(86 , 'LKR', 1, 'Sri Lanka rupee'),
(87 , 'LRD', 1, 'Liberian dollar'),
(88 , 'LSL', 1, 'Loti'),
(89 , 'LTL', 1, 'Lithuanian litas'),
(90 , 'LVL', 1, 'Latvian lats'),
(91 , 'LYD', 1, 'Libyan dinar'),
(92 , 'MAD', 1, 'Moroccan dirham'),
(93 , 'MDL', 1, 'Moldovan leu'),
(94 , 'MGA', 1, 'Malagasy ariary'),
(95 , 'MKD', 1, 'Denar'),
(96 , 'MMK', 1, 'Kyat'),
(97 , 'MNT', 1, 'Tugrik'),
(98 , 'MOP', 1, 'Pataca'),
(99 , 'MRO', 1, 'Ouguiya'),
(100, 'MUR', 1, 'Mauritius rupee'),
(101, 'MVR', 1, 'Rufiyaa'),
(102, 'MWK', 1, 'Kwacha'),
(103, 'MXN', 1, 'Mexican peso'),
(104, 'MXV', 1, 'Mexican Unidad de Inversion'),
(105, 'MYR', 1, 'Malaysian ringgit'),
(106, 'MZN', 1, 'Metical'),
(107, 'NAD', 1, 'Namibian dollar'),
(108, 'NGN', 1, 'Naira'),
(109, 'NIO', 1, 'Cordoba oro'),
(110, 'NOK', 1, 'Norwegian krone'),
(111, 'NPR', 1, 'Nepalese rupee'),
(112, 'NZD', 1, 'New Zealand dollar'),
(113, 'OMR', 1, 'Rial Omani'),
(114, 'PAB', 1, 'Balboa'),
(115, 'PEN', 1, 'Nuevo sol'),
(116, 'PGK', 1, 'Kina'),
(117, 'PHP', 1, 'Philippine peso'),
(118, 'PKR', 1, 'Pakistan rupee'),
(119, 'PLN', 1, 'Zloty'),
(120, 'PYG', 1, 'Guarani'),
(121, 'QAR', 1, 'Qatari rial'),
(122, 'RON', 1, 'Romanian new leu'),
(123, 'RSD', 1, 'Serbian dinar'),
(124, 'RUB', 1, 'Russian rouble'),
(125, 'RWF', 1, 'Rwanda franc'),
(126, 'SAR', 1, 'Saudi riyal'),
(127, 'SBD', 1, 'Solomon Islands dollar'),
(128, 'SCR', 1, 'Seychelles rupee'),
(129, 'SDG', 1, 'Sudanese pound'),
(130, 'SEK', 1, 'Swedish krona'),
(131, 'SGD', 1, 'Singapore dollar'),
(132, 'SHP', 1, 'Saint Helena pound'),
(133, 'SKK', 1, 'Slovak koruna'),
(134, 'SLL', 1, 'Leone'),
(135, 'SOS', 1, 'Somali shilling'),
(136, 'SRD', 1, 'Surinam dollar'),
(137, 'STD', 1, 'Dobra'),
(138, 'SYP', 1, 'Syrian pound'),
(139, 'SZL', 1, 'Lilangeni'),
(140, 'THB', 1, 'Baht'),
(141, 'TJS', 1, 'Somoni'),
(142, 'TMM', 1, 'Manat'),
(143, 'TND', 1, 'Tunisian dinar'),
(144, 'TOP', 1, 'Pa''anga'),
(145, 'TRY', 1, 'New Turkish lira'),
(146, 'TTD', 1, 'Trinidad and Tobago dollar'),
(147, 'TWD', 1, 'New Taiwan dollar'),
(148, 'TZS', 1, 'Tanzanian shilling'),
(149, 'UAH', 1, 'Hryvnia'),
(150, 'UGX', 1, 'Uganda shilling'),
(151, 'USN', 1, 'US dollar (next day)'),
(152, 'USS', 1, 'US dollar (same day)'),
(153, 'UYU', 1, 'Peso Uruguayo'),
(154, 'UZS', 1, 'Uzbekistan som'),
/* the following two rows are supposed to contain UTF-8 characters, but this
 * breaks the unit tests. See
 * http://article.gmane.org/gmane.comp.finance.mifos.devel/4991 */
(155, 'VEF', 1, 'Venezuelan Bolivares Fuertes'),
(156, 'VND', 1, 'Vietnamese Dong'),
(157, 'VUV', 1, 'Vatu'),
(158, 'WST', 1, 'Samoan tala'),
(159, 'XAF', 1, 'CFA franc BEAC'),
(160, 'XAG', 1, 'Silver'),
(161, 'XAU', 1, 'Gold'),
(162, 'XBA', 1, 'European Composite Unit'),
(163, 'XBB', 1, 'European Monetary Unit'),
(164, 'XBC', 1, 'European Unit of Account 9'),
(165, 'XBD', 1, 'European Unit of Account 17'),
(166, 'XCD', 1, 'East Caribbean dollar'),
(167, 'XDR', 1, 'Special Drawing Rights'),
(168, 'XFU', 1, 'UIC franc'),
(169, 'XOF', 1, 'CFA Franc BCEAO'),
(170, 'XPD', 1, 'Palladium'),
(171, 'XPF', 1, 'CFP franc'),
(172, 'XPT', 1, 'Platinum'),
(173, 'XTS', 1, 'Code reserved for testing purposes'),
(174, 'XXX', 1, 'No currency'),
(175, 'YER', 1, 'Yemeni rial'),
(176, 'ZAR', 1, 'South African rand'),
(177, 'ZMK', 1, 'Kwacha'),
(178, 'ZWD', 1, 'Zimbabwe dollar');

/* Updated lookup_entity */
/* The table LOOKUP_ENTITY will contain the entry for entities which
need to be configured like center to be called kendra by mfi,
ethnicity to be called something by the MFI,
salutation to be called something by the MFI etc - Configuration */
insert into lookup_entity(entity_id,entity_name,description) values
(1,'ClientStatus','Client Status'),
(2,'GroupStatus','Group Status'),
(3,'CenterStatus','Center Status'),
(4,'OfficeStatus','Office Status'),
(5,'AccountState','Account States'),

/* duplicate that appears to be unused */
(6,'PersonnelStatusUnused','Personnel Status (Unused)'),
(7,'GroupFlag','Group Flag'),
(8,'FeeType','Fee Type'),
(9,'Titles','Customer Position'),
(10,'PovertyStatus','Poverty Status For Client'),
(11,'Center','Center VALUES'),
(12,'Group','Group VALUES'),
(13,'Client','Client VALUES'),
(14,'Office','Office'),
(15,'Salutation','Mr/Mrs'),
(16,'Gender','Male/Female'),
(17,'MaritalStatus','Married/UnMarried'),
(18,'Citizenship','Citizenship'),
(19,'Ethinicity','Ethnicity'),
(20,'EducationLevel','EducationLevel'),
(21,'BusinessActivities','BusinessActivities'),
(22,'Handicapped','Handicaped'),
(23,'ClientFormedBy','CustomField ClientFormedBy for client'),
(24,'PostalCode','ZipCode'),
(25,'ProductState','Product State'),
(26,'Loan','Loan'),
(27,'Savings','Savings'),

(29,'PersonnelTitles','CFO/Accountant'),
(30,'PersonnelLevels','LoanOfficer/NonLoanOfficer'),
(34,'OfficeLevels','Head Office/Regional Office/Sub Regional Office/Area Office/BranchOffice'),
(35,'PrdApplicableMaster','Ceratin product categories applicable to certain types of clients'),
(36,'WeekDays','Week Days List'),
(37,'InterestTypes','Interest Types for PrdOfferings and Accounts'),
(38,'CategoryType','This is mainly used in fees to show the categories where this fee is applicable'),
(39,'InterestCalcRule','Interest calculation rule for loan prd offerings'),
(41,'GracePeriodTypes','Grace Period Types for loan products'),
(42,'DayRank','Day Rank'),
(43,'CollateralTypes','Collateral Types for loan accounts'),
(44,'OfficeCode','Office Code'),
(45,'ProductCategoryStatus','ProductCategoryStatus'),
(46,'ProductStatus','ProductStatus'),
(47,'SavingsType','SavingsType'),
(48,'RecommendedAmtUnit','RecommendedAmtUnit'),
(49,'IntCalTypes','IntCalTypes'),
(50,'YESNO','YESNO'),
(51,'AccountType','AccountType'),
(52,'SpouseFather','SpouseFather'),
(53,'CustomerStatus','CustomerStatus'),
(54,'FeePayment','FeePayment'),
(55,'FeeFormulaMaster','FeeFormulaMaster'),
(56,'PersonnelStatus','PersonnelStatus'),
(57,'Personnel','Personnel'),

/* 2007-12-08 Default custom field entities 58-67 were removed */
/* except for 62, which is also used for non-custom fields */
(62,'ExternalId','External ID'),
(68,'FeeStatus','FeeStatus'),
(69,'AccountAction','AccountAction'),
(70,'AccountFlags','AccountFlags'),
(71,'PaymentType','PaymentType'),
(72,'SavingsStatus','Saving Status'),
(73,'Position','Position'),
(74,'Language','Language'),
(75,'CustomerAttendanceType','CustomerAttendanceType'),
(76,'FinancialAction','Financial Action'),
(77,'BulkEntry','BulkEntry'),
(78,'SavingsAccountFlag','SavingsAccountFlag'),
(79,'Address3','Address3'),
(80,'City','City'),
(81,'Interest','Interest'),
(82,'LoanPurposes','Loan Purposes'),
(83,'State','State'),
(84,'Address1','Address1'),
(85,'Address2','Address2'),
(86,'GovernmentId','GovernmentId'),
(87,'Permissions','Permissions'),
(88,'ServiceCharge','Interest'),
(89,'feeUpdationType',' fee updation can to applied to existing accounts or future accounts'),
(90,'FeeFrequency','Fee Frequency'),
(91,'RepaymentRule','Repayment Rule Types'),
(92,'LivingStatus','This entity is used to track whether the family member is living together with the client or not');

/* fee_type */
insert into fee_type(fee_type_id,fee_lookup_id,flat_or_rate,formula) values
(1,1,null,null),
(2,1,null,null),
(3,2,null,null),
(4,3,null,null),
(5,3,null,null);

/* MEETING_TYPE */
/* The table MEETING_TYPE indicates to what the meeting is associated against like loan,customer etc - System */
insert into meeting_type(meeting_type_id,meeting_purpose,description) values
(1,'LOANFREQUENCYOFINSTALLMENTS','Loan Frequency of istalments'),
(2,'SAVINGSTIMEPERFORINTCALC','Savings Time Period for Interest Calculation'),
(3,'SAVINGSFRQINTPOSTACC','Savings Frequency of Interest Posting to Accounts'),
(4,'CUSTOMERMEETING','Customer Meeting'),
(5,'FEEMEETING','Fees Meetings');

/*recurrence_type*/
/* The table RECURRENCE_TYPE indicates the type of meeting like daily,weekly,monthly - System */
insert into recurrence_type(recurrence_id,recurrence_name,description) values
(1,'Week(s)','Weekly Recurrence'),
(2,'Month(s)','Monthly Recurrence'),
(3,'Day(s)','Daily Recurrence');

/* The table COUNTRY will contain information related to the country like country name etc - Configuration */
insert into country(country_id,country_name,country_short_name) values
(1,'America','US'),
(2,'India','IN'),
(3,'Spain','ES'),
(4,'England','ENG'),
(5,'South Africa','SA'),
(6,'United Kingdom','GB');

/* The table Office_Level contains the different levels supported by the MFI like HeadOffice,RegionalOffice etc  */
/* Office Levels can be configured in the UI, so there is no need to make changes in this section when configuring Mifos  */
insert into office_level(level_id,parent_level_id,level_name_id,interaction_flag,configured) values
(1,null,104,0,1),
(2,1,105,0,1),
(3,2,106,0,1),
(4,3,107,0,1),
(5,4,108,1,1);

insert into lookup_value(lookup_id,entity_id,lookup_name) values(189, 74, 'Language-English');

/* The table Language will contain the language specific attributes like language name etc--Configuration */
insert into language(lang_id,lang_name,lang_short_name,lookup_id)
values(1,'English','EN',189);

/* The table Supported Locale will contain the locales supported by the MFI like English , french etc - Configuration */
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale)
values(1,6,1,'EN',1);

-- Entity: ClientStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(1, 1, 'ClientStatus-PartialApplication');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(1, 1, 1, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(2, 1, 'ClientStatus-ApplicationPendingApproval');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(3, 1, 2, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(3, 1, 'ClientStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(5, 1, 3, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(4, 1, 'ClientStatus-OnHold');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(7, 1, 4, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(5, 1, 'ClientStatus-Cancelled');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(9, 1, 5, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(6, 1, 'ClientStatus-Closed');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(11, 1, 6, null);
-- Entity: GroupStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(7, 2, 'GroupStatus-PartialApplication');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(13, 1, 7, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(8, 2, 'GroupStatus-ApplicationPendingApproval');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(15, 1, 8, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(9, 2, 'GroupStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(17, 1, 9, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(10, 2, 'GroupStatus-OnHold');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(19, 1, 10, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(11, 2, 'GroupStatus-Cancelled');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(21, 1, 11, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(12, 2, 'GroupStatus-Closed');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(23, 1, 12, null);
-- Entity: CenterStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(13, 3, 'CenterStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(25, 1, 13, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(14, 3, 'CenterStatus-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(27, 1, 14, null);
-- Entity: OfficeStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(15, 4, 'OfficeStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(29, 1, 15, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(16, 4, 'OfficeStatus-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(31, 1, 16, null);
-- Entity: AccountState
insert into lookup_value(lookup_id,entity_id,lookup_name) values(17, 5, 'AccountState-PartialApplication');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(33, 1, 17, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(18, 5, 'AccountState-ApplicationPendingApproval');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(35, 1, 18, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(19, 5, 'AccountState-ApplicationApproved');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(37, 1, 19, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(20, 5, 'AccountState-DisbursedToLo');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(39, 1, 20, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(21, 5, 'AccountState-ActiveInGoodStanding');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(41, 1, 21, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(22, 5, 'AccountState-ClosedObligationMet');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(43, 1, 22, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(23, 5, 'AccountState-ClosedWrittenOff');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(45, 1, 23, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(24, 5, 'AccountState-ClosedRescheduled');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(47, 1, 24, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(25, 5, 'AccountState-ActiveInBadStanding');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(49, 1, 25, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(141, 5, 'AccountState-Cancel');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(261, 1, 141, null);

/* duplicate that appears to be unused */
-- Entity: PersonnelStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(26, 6, 'PersonnelStatusUnused-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(51, 1, 26, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(27, 6, 'PersonnelStatusUnused-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(53, 1, 27, null);

-- Entity: GroupFlag
insert into lookup_value(lookup_id,entity_id,lookup_name) values(28, 7, 'GroupFlag-Withdraw');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(55, 1, 28, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(29, 7, 'GroupFlag-Rejected');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(57, 1, 29, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(30, 7, 'GroupFlag-Blacklisted');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(59, 1, 30, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(31, 7, 'GroupFlag-Duplicate');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(61, 1, 31, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(32, 7, 'GroupFlag-Transferred');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(63, 1, 32, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(33, 7, 'GroupFlag-LeftProgram');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(65, 1, 33, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(34, 7, 'GroupFlag-Other');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(67, 1, 34, null);
-- Entity: FeeType
insert into lookup_value(lookup_id,entity_id,lookup_name) values(35, 8, 'FeeType-MaintenanceFee');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(69, 1, 35, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(36, 8, 'FeeType-ConsultancyFee');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(71, 1, 36, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(37, 8, 'FeeType-TrainingFee');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(73, 1, 37, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(38, 8, 'FeeType-MeetingCharges');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(75, 1, 38, null);
-- Entity: Titles
-- Entity: PovertyStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(41, 10, 'PovertyStatus-VeryPoor');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(81, 1, 41, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(42, 10, 'PovertyStatus-Poor');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(83, 1, 42, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(43, 10, 'PovertyStatus-NonPoor');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(85, 1, 43, null);
-- Entity: Center
-- Entity: Group
-- Entity: Client
-- Entity: Office
-- Entity: Salutation
-- Entity: Gender
insert into lookup_value(lookup_id,entity_id,lookup_name) values(49, 16, 'Gender-Male');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(97, 1, 49, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(50, 16, 'Gender-Female');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(99, 1, 50, null);
-- Entity: MaritalStatus
-- Entity: Citizenship
-- Entity: Ethinicity
-- Entity: EducationLevel
-- these values are removed in version 198, will be completely removed later on
-- Entity: BusinessActivities
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(136, 21, 'BusinessActivities-DailyLabour');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(251, 1, 136, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(137, 21, 'BusinessActivities-Agriculture');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(253, 1, 137, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(144, 21, 'BusinessActivities-AnimalHusbandry');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(265, 1, 144, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(145, 21, 'BusinessActivities-MicroEnterprise');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(266, 1, 145, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(222, 21, 'BusinessActivities-Production');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(430, 1, 222, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(225, 21, 'BusinessActivities-Trading');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(432, 1, 225, NULL);
-- Entity: Handicapped
-- Entity: ClientFormedBy
-- Entity: PostalCode
-- Entity: ProductState
insert into lookup_value(lookup_id,entity_id,lookup_name) values(51, 25, 'ProductState-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(101, 1, 51, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(52, 25, 'ProductState-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(103, 1, 52, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(53, 25, 'ProductState-Close');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(105, 1, 53, null);
-- Entity: Loan
insert into lookup_value(lookup_id,entity_id,lookup_name) values(54, 26, 'Loan-Loan');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(107, 1, 54, null);
-- Entity: Savings
insert into lookup_value(lookup_id,entity_id,lookup_name) values(55, 27, 'Savings-Savings');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(109, 1, 55, null);
-- Entity: PersonnelTitles
-- Entity: PersonnelLevels
insert into lookup_value(lookup_id,entity_id,lookup_name) values(60, 30, 'PersonnelLevels-LoanOfficer');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(119, 1, 60, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(61, 30, 'PersonnelLevels-NonLoanOfficer');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(120, 1, 61, null);
-- Entity: OfficeLevels
insert into lookup_value(lookup_id,entity_id,lookup_name) values(104, 34, 'OfficeLevels-HeadOffice');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(189, 1, 104, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(105, 34, 'OfficeLevels-RegionalOffice');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(191, 1, 105, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(106, 34, 'OfficeLevels-DivisionalOffice');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(193, 1, 106, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(107, 34, 'OfficeLevels-AreaOffice');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(195, 1, 107, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(108, 34, 'OfficeLevels-BranchOffice');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(197, 1, 108, null);
-- Entity: PrdApplicableMaster
insert into lookup_value(lookup_id,entity_id,lookup_name) values(68, 35, 'PrdApplicableMaster-Clients');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(135, 1, 68, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(69, 35, 'PrdApplicableMaster-Groups');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(136, 1, 69, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(70, 35, 'PrdApplicableMaster-Centers');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(137, 1, 70, null);
-- Entity: InterestTypes
insert into lookup_value(lookup_id,entity_id,lookup_name) values(79, 37, 'InterestTypes-Flat');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(157, 1, 79, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(80, 37, 'InterestTypes-DecliningBalance');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(158, 1, 80, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(604,37, 'InterestTypes-DecliningBalance-EqualPrincipalInstallment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(946, 1, 604, null);
-- Entity: CategoryType
insert into lookup_value(lookup_id,entity_id,lookup_name) values(81, 38, 'CategoryType-AllCustomers');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(161, 1, 81, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(82, 38, 'CategoryType-Client');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(162, 1, 82, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(83, 38, 'CategoryType-Group');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(163, 1, 83, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(84, 38, 'CategoryType-Center');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(164, 1, 84, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(85, 38, 'CategoryType-AllProductTypes');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(165, 1, 85, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(86, 38, 'CategoryType-Loans');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(166, 1, 86, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(87, 38, 'CategoryType-Savings');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(167, 1, 87, null);
-- Entity: InterestCalcRule
insert into lookup_value(lookup_id,entity_id,lookup_name) values(88, 39, 'InterestCalcRule-AlwaysRecalculate');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(168, 1, 88, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(89, 39, 'InterestCalcRule-NeverRecalculate');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(169, 1, 89, null);
-- Entity: GracePeriodTypes
insert into lookup_value(lookup_id,entity_id,lookup_name) values(96, 41, 'GracePeriodTypes-None');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(176, 1, 96, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(97, 41, 'GracePeriodTypes-GraceOnAllRepayments');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(177, 1, 97, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(98, 41, 'GracePeriodTypes-PrincipalOnlyGrace');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(178, 1, 98, null);
-- these values are removed in version 198, will be completely removed later on
-- Entity: CollateralTypes
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(109, 43, 'CollateralTypes-Type1');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(199, 1, 109, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(110, 43, 'CollateralTypes-Type2');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(200, 1, 110, NULL);
-- Entity: OfficeCode
insert into lookup_value(lookup_id,entity_id,lookup_name) values(111, 44, 'OfficeCode-Code1');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(201, 1, 111, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(112, 44, 'OfficeCode-Code2');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(203, 1, 112, null);
-- Entity: ProductCategoryStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(113, 45, 'ProductCategoryStatus-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(205, 1, 113, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(114, 45, 'ProductCategoryStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(207, 1, 114, null);
-- Entity: ProductStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(115, 46, 'ProductStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(209, 1, 115, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(116, 46, 'ProductStatus-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(211, 1, 116, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(117, 46, 'ProductStatus-Closed');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(213, 1, 117, null);
-- Entity: SavingsType
insert into lookup_value(lookup_id,entity_id,lookup_name) values(118, 47, 'SavingsType-Mandatory');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(215, 1, 118, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(119, 47, 'SavingsType-Voluntary');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(217, 1, 119, null);
-- Entity: RecommendedAmtUnit
insert into lookup_value(lookup_id,entity_id,lookup_name) values(120, 48, 'RecommendedAmtUnit-PerIndividual');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(219, 1, 120, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(121, 48, 'RecommendedAmtUnit-CompleteGroup');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(221, 1, 121, null);
-- Entity: IntCalTypes
insert into lookup_value(lookup_id,entity_id,lookup_name) values(122, 49, 'IntCalTypes-MinimumBalance');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(223, 1, 122, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(123, 49, 'IntCalTypes-AverageBalance');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(225, 1, 123, null);
-- Entity: YESNO
insert into lookup_value(lookup_id,entity_id,lookup_name) values(124, 50, 'YESNO-Yes');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(227, 1, 124, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(125, 50, 'YESNO-No');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(229, 1, 125, null);
-- Entity: AccountType
insert into lookup_value(lookup_id,entity_id,lookup_name) values(126, 51, 'AccountType-Loan');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(231, 1, 126, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(127, 51, 'AccountType-Saving');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(233, 1, 127, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(140, 51, 'AccountType-Customer');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(259, 1, 140, null);
-- Entity: SpouseFather
insert into lookup_value(lookup_id,entity_id,lookup_name) values(128, 52, 'SpouseFather-Spouse');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(235, 1, 128, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(129, 52, 'SpouseFather-Father');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(237, 1, 129, null);
-- Entity: CustomerStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(142, 53, 'CustomerStatus-CustomerAccountActive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(263, 1, 142, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(143, 53, 'CustomerStatus-CustomerAccountInactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(264, 1, 143, null);
-- Entity: FeePayment
insert into lookup_value(lookup_id,entity_id,lookup_name) values(146, 54, 'FeePayment-Upfront');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(267, 1, 146, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(147, 54, 'FeePayment-TimeOfDisburstment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(269, 1, 147, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(148, 54, 'FeePayment-TimeOfFirstLoanRepayment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(271, 1, 148, null);
-- Entity: FeeFormulaMaster
insert into lookup_value(lookup_id,entity_id,lookup_name) values(149, 55, 'FeeFormulaMaster-LoanAmount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(273, 1, 149, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(150, 55, 'FeeFormulaMaster-LoanAmountInterest');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(275, 1, 150, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(151, 55, 'FeeFormulaMaster-Interest');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(277, 1, 151, null);
-- Entity: PersonnelStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(152, 56, 'PersonnelStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(279, 1, 152, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(153, 56, 'PersonnelStatus-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(281, 1, 153, null);
-- Entity: Personnel
insert into lookup_value(lookup_id,entity_id,lookup_name) values(154, 57, 'Personnel-Personnel');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(283, 1, 154, null);
-- Entity: ExternalId
-- Entity: FeeStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(165, 68, 'FeeStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(305, 1, 165, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(166, 68, 'FeeStatus-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(307, 1, 166, null);
-- Entity: AccountAction
insert into lookup_value(lookup_id,entity_id,lookup_name) values(167, 69, 'AccountAction-LoanRepayment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(309, 1, 167, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(168, 69, 'AccountAction-Penalty');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(311, 1, 168, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(169, 69, 'AccountAction-MiscellenousPenalty');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(313, 1, 169, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(170, 69, 'AccountAction-Fee');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(315, 1, 170, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(171, 69, 'AccountAction-MiscellenousFee');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(317, 1, 171, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(172, 69, 'AccountAction-Deposit');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(319, 1, 172, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(173, 69, 'AccountAction-Withdrawal');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(321, 1, 173, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(191, 69, 'AccountAction-Payment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(380, 1, 191, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(192, 69, 'AccountAction-Adjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(382, 1, 192, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(193, 69, 'AccountAction-Disbursal');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(384, 1, 193, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(214, 69, 'AccountAction-Interest_posting');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(415, 1, 214, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(362, 69, 'AccountAction-CustomerAccountRepayment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(700, 1, 362, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(364, 69, 'AccountAction-CustomerAdjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(702, 1, 364, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(366, 69, 'AccountAction-SavingsAdjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(704, 1, 366, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(547, 69, 'AccountAction-LoanWrittenOff');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(885, 1, 547, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(548, 69, 'AccountAction-WaiveOffDue');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(886, 1, 548, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(549, 69, 'AccountAction-WaiveOffOverDue');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(887, 1, 549, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(572, 69, 'AccountAction-LoanReversal');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(917, 1, 572, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(573, 69, 'AccountAction-DisrbursalAmountReversal');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(918, 1, 573, null);
-- Entity: AccountFlags
insert into lookup_value(lookup_id,entity_id,lookup_name) values(174, 70, 'AccountFlags-Withdraw');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(323, 1, 174, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(175, 70, 'AccountFlags-Rejected');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(325, 1, 175, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(176, 70, 'AccountFlags-Other');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(327, 1, 176, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(571, 70, 'AccountFlags-LoanReversal');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(916, 1, 571, null);
-- Entity: PaymentType
insert into lookup_value(lookup_id,entity_id,lookup_name) values(177, 71, 'PaymentType-Cash');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(329, 1, 177, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(179, 71, 'PaymentType-Voucher');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(912, 1, 179, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(180, 71, 'PaymentType-Cheque');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(913, 1, 180, null);
-- Entity: SavingsStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values(181, 72, 'SavingsStatus-PartialApplication');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(337, 1, 181, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(182, 72, 'SavingsStatus-ApplicationPendingApproval');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(339, 1, 182, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(183, 72, 'SavingsStatus-Cancelled');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(341, 1, 183, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(184, 72, 'SavingsStatus-Active');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(343, 1, 184, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(185, 72, 'SavingsStatus-Closed');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(345, 1, 185, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(210, 72, 'SavingsStatus-Inactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(407, 1, 210, null);
-- Entity: Position
insert into lookup_value(lookup_id,entity_id,lookup_name) values(186, 73, 'Position-CenterLeader');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(347, 1, 186, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(187, 73, 'Position-CenterSecretary');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(349, 1, 187, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(188, 73, 'Position-GroupLeader');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(351, 1, 188, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(216, 73, 'Position-GroupSecretary');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(418, 1, 216, null);
-- Entity: Language
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(189, 74, 'Language-English');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(376, 1, 189, null);
-- Entity: CustomerAttendance
insert into lookup_value(lookup_id,entity_id,lookup_name) values(194, 75, 'CustomerAttendance-P');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(386, 1, 194, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(195, 75, 'CustomerAttendance-Ab');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(388, 1, 195, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(196, 75, 'CustomerAttendance-Al');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(390, 1, 196, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(197, 75, 'CustomerAttendance-L');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(392, 1, 197, null);
-- Entity: FinancialAction
insert into lookup_value(lookup_id,entity_id,lookup_name) values(198, 76, 'FinancialAction-Principal');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(394, 1, 198, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(199, 76, 'FinancialAction-Interest');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(395, 1, 199, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(200, 76, 'FinancialAction-Fees');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(396, 1, 200, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(201, 76, 'FinancialAction-Penalty');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(397, 1, 201, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(202, 76, 'FinancialAction-RoundingAdjustments');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(398, 1, 202, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(203, 76, 'FinancialAction-MandatoryDeposit');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(399, 1, 203, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(204, 76, 'FinancialAction-VoluntoryDeposit');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(400, 1, 204, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(205, 76, 'FinancialAction-MandatoryWithdrawal');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(401, 1, 205, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(206, 76, 'FinancialAction-VoluntoryWithdrawal');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(402, 1, 206, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(207, 76, 'FinancialAction-ReversalAdjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(403, 1, 207, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(208, 76, 'FinancialAction-SavingsInterestPosting');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(404, 1, 208, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(209, 76, 'FinancialAction-Interest_posting');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(405, 1, 209, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(215, 76, 'FinancialAction-LoanDisbursement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(417, 1, 215, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(229, 76, 'FinancialAction-MiscFee');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(440, 1, 229, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(361, 76, 'FinancialAction-MiscPenalty');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(699, 1, 361, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(363, 76, 'FinancialAction-CustomerAccountFeesPosting');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(701, 1, 363, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(365, 76, 'FinancialAction-CustomerAdjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(703, 1, 365, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(367, 76, 'FinancialAction-MandatoryDepositAdjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(705, 1, 367, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(368, 76, 'FinancialAction-VoluntoryDepositAdjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(706, 1, 368, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(369, 76, 'FinancialAction-MandatoryWithdrawalAdjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(707, 1, 369, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(370, 76, 'FinancialAction-VoluntoryWithdrawalAdjustment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(708, 1, 370, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(550, 76, 'FinancialAction-LoanWrittenOff');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(888, 1, 550, null);
-- Entity: BulkEntry
-- Entity: SavingsAccountFlag
insert into lookup_value(lookup_id,entity_id,lookup_name) values(211, 78, 'SavingsAccountFlag-Withdraw');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(409, 1, 211, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(212, 78, 'SavingsAccountFlag-Rejected');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(411, 1, 212, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(213, 78, 'SavingsAccountFlag-Blacklisted');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(413, 1, 213, null);
-- Entity: Address3
-- Entity: City
-- Entity: Interest
-- Entity: LoanPurposes
-- Entity: State
-- Entity: Address1
-- Entity: Address2
-- Entity: GovernmentId
-- Entity: Permissions
insert into lookup_value(lookup_id,entity_id,lookup_name) values(371, 87, 'Permissions-OrganizationManagement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(709, 1, 371, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(372, 87, 'Permissions-Funds');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(710, 1, 372, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(373, 87, 'Permissions-CanCreateFunds');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(711, 1, 373, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(374, 87, 'Permissions-CanModifyFunds');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(712, 1, 374, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(375, 87, 'Permissions-Fees');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(713, 1, 375, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(376, 87, 'Permissions-CanDefineNewFeeType');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(714, 1, 376, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(377, 87, 'Permissions-CanModifyFeeInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(715, 1, 377, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(378, 87, 'Permissions-Checklists');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(716, 1, 378, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(379, 87, 'Permissions-CanDefineNewChecklistType');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(717, 1, 379, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(380, 87, 'Permissions-CanModifyChecklistInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(718, 1, 380, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(381, 87, 'Permissions-OfficeManagement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(719, 1, 381, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(382, 87, 'Permissions-Offices');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(720, 1, 382, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(383, 87, 'Permissions-CanCreateNewOffice');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(721, 1, 383, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(384, 87, 'Permissions-CanModifyOfficeInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(722, 1, 384, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(385, 87, 'Permissions-UserManagement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(723, 1, 385, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(386, 87, 'Permissions-Personnel');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(724, 1, 386, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(387, 87, 'Permissions-CanCreateNewSystemUsers');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(725, 1, 387, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(388, 87, 'Permissions-CanModifyUserInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(726, 1, 388, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(389, 87, 'Permissions-CanUnlockAUser');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(727, 1, 389, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(390, 87, 'Permissions-Roles');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(728, 1, 390, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(391, 87, 'Permissions-CanCreateNewRole');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(729, 1, 391, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(392, 87, 'Permissions-CanModifyARole');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(730, 1, 392, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(393, 87, 'Permissions-CanDeleteARole');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(731, 1, 393, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(394, 87, 'Permissions-ClientManagement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(732, 1, 394, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(395, 87, 'Permissions-Clients');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(733, 1, 395, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(396, 87, 'Permissions-Clients-CanCreateNewClientInSaveForLaterState');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(734, 1, 396, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(397, 87, 'Permissions-Clients-CanCreateNewClientInSubmitForApprovalState');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(735, 1, 397, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(398, 87, 'Permissions-Clients-CanChangeStateToPartialApplication');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(736, 1, 398, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(399, 87, 'Permissions-Clients-CanChangeStateToActive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(737, 1, 399, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(400, 87, 'Permissions-Clients-CanChangeStateToCancelled');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(738, 1, 400, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(401, 87, 'Permissions-Clients-CanChangeStateToOnHold');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(739, 1, 401, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(402, 87, 'Permissions-Clients-CanChangeStateToClosed');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(740, 1, 402, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(403, 87, 'Permissions-Clients-CanChangeStateToApplicationPendingApproval');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(741, 1, 403, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(404, 87, 'Permissions-Clients-CanMakePaymentsToClientAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(742, 1, 404, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(405, 87, 'Permissions-Clients-CanMakeAdjustmentEntriesToClientAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(743, 1, 405, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(407, 87, 'Permissions-Clients-CanWaiveADueAmount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(745, 1, 407, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(408, 87, 'Permissions-Clients-CanRemoveFeeTypesFromClientAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(746, 1, 408, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(409, 87, 'Permissions-Clients-CanAddNotesToClient');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(747, 1, 409, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(410, 87, 'Permissions-Clients-CanEditMfiInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(748, 1, 410, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(411, 87, 'Permissions-Clients-CanEditGroupMembership');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(749, 1, 411, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(412, 87, 'Permissions-Clients-CanEditOfficeMembership');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(750, 1, 412, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(413, 87, 'Permissions-Clients-CanEditMeetingSchedule');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(751, 1, 413, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(414, 87, 'Permissions-Clients-CanAddEditHistoricalData');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(752, 1, 414, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(415, 87, 'Permissions-Clients-CanEditFeeAmountAttachedToTheAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(753, 1, 415, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(416, 87, 'Permissions-Clients-CanBlacklistAClient');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(754, 1, 416, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(417, 87, 'Permissions-Groups');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(755, 1, 417, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(418, 87, 'Permissions-Groups-CanCreateNewGroupInSaveForLaterState');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(756, 1, 418, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(419, 87, 'Permissions-Groups-CanCreateNewGroupInSubmitForApprovalState');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(757, 1, 419, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(420, 87, 'Permissions-Groups-CanChangeStateToPartialApplication');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(758, 1, 420, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(421, 87, 'Permissions-Groups-CanChangeStateToActive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(759, 1, 421, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(422, 87, 'Permissions-Groups-CanChangeStateToCancelled');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(760, 1, 422, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(423, 87, 'Permissions-Groups-CanChangeStateToOnHold');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(761, 1, 423, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(424, 87, 'Permissions-Groups-CanChangeStateToClosed');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(762, 1, 424, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(425, 87, 'Permissions-Groups-CanChangeStateToApplicationPendingApproval');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(763, 1, 425, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(426, 87, 'Permissions-Groups-CanMakePaymentsToGroupAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(764, 1, 426, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(427, 87, 'Permissions-Groups-CanMakeAdjustmentEntriesToGroupAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(765, 1, 427, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(429, 87, 'Permissions-Groups-CanWaiveADueAmount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(767, 1, 429, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(430, 87, 'Permissions-Groups-CanRemoveFeeTypesFromGroupAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(768, 1, 430, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(431, 87, 'Permissions-Groups-CanAddNotesToGroup');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(769, 1, 431, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(432, 87, 'Permissions-Groups-CanEditGroupInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(770, 1, 432, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(433, 87, 'Permissions-Groups-CanEditCenterClientship');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(771, 1, 433, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(434, 87, 'Permissions-Groups-CanEditOfficeMembership');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(772, 1, 434, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(435, 87, 'Permissions-Groups-CanEditMeetingSchedule');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(773, 1, 435, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(436, 87, 'Permissions-Groups-CanAddEditHistoricalData');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(774, 1, 436, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(437, 87, 'Permissions-Groups-CanEditFeeAmountAttachedToTheAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(775, 1, 437, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(438, 87, 'Permissions-Groups-CanBlacklistAGroup');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(776, 1, 438, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(439, 87, 'Permissions-Centers');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(777, 1, 439, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(440, 87, 'Permissions-Centers-CanCreateNewCenter');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(778, 1, 440, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(441, 87, 'Permissions-Centers-CanModifyCenterInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(779, 1, 441, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(442, 87, 'Permissions-Centers-CanEditCenterStatus');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(780, 1, 442, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(443, 87, 'Permissions-Centers-CanMakePaymentsToCenterAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(781, 1, 443, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(444, 87, 'Permissions-Centers-CanMakeAdjustmentEntriesToCenterAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(782, 1, 444, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(446, 87, 'Permissions-Centers-CanWaiveADueAmount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(784, 1, 446, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(447, 87, 'Permissions-Centers-CanRemoveFeeTypesFromCenterAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(785, 1, 447, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(448, 87, 'Permissions-Centers-CanAddNotesToCenterRecords');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(786, 1, 448, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(449, 87, 'Permissions-Centers-CanEditFeeAmountAttachedToTheAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(787, 1, 449, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(450, 87, 'Permissions-ProductDefinition');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(788, 1, 450, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(451, 87, 'Permissions-ProductCategories');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(789, 1, 451, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(452, 87, 'Permissions-CanDefineNewProductCategories');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(790, 1, 452, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(453, 87, 'Permissions-CanEditProductCategoryInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(791, 1, 453, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(454, 87, 'Permissions-LoanProducts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(792, 1, 454, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(455, 87, 'Permissions-CanDefineNewLoanProductInstance');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(793, 1, 455, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(456, 87, 'Permissions-CanEditLoanProductInstances');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(794, 1, 456, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(457, 87, 'Permissions-SavingsProducts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(795, 1, 457, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(458, 87, 'Permissions-CanDefineNewSavingsProductInstance');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(796, 1, 458, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(459, 87, 'Permissions-CanEditSavingsProductInstances');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(797, 1, 459, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(460, 87, 'Permissions-LoanManagement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(798, 1, 460, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(461, 87, 'Permissions-LoanProcessing');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(799, 1, 461, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(462, 87, 'Permissions-CanCreateNewLoanAccountInSaveForLaterState');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(800, 1, 462, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(463, 87, 'Permissions-CanCreateNewLoanAccountInSubmitForApprovalState');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(801, 1, 463, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(464, 87, 'Permissions-LoanProcessing-CanChangeStateToPartialApplication');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(802, 1, 464, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(465, 87, 'Permissions-LoanProcessing-CanChangeStateToApproved');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(803, 1, 465, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(466, 87, 'Permissions-LoanProcessing-CanChangeStateToCancelled');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(804, 1, 466, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(467, 87, 'Permissions-LoanProcessing-CanChangeStateToDisbursedToLo');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(805, 1, 467, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(469, 87, 'Permissions-LoanProcessing-CanChangeStateToPendingApproval');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(807, 1, 469, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(470, 87, 'Permissions-LoanProcessing-CanChangeStateToClosedWrittenOff');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(808, 1, 470, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(471, 87, 'Permissions-LoanProcessing-CanChangeStateToClosedRescheduled');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(809, 1, 471, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(474, 87, 'Permissions-LoanTransactions');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(812, 1, 474, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(475, 87, 'Permissions-CanMakePaymentToTheAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(813, 1, 475, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(476, 87, 'Permissions-CanMakeAdjustmentEntryToTheAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(814, 1, 476, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(478, 87, 'Permissions-CanWaivePenalty');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(816, 1, 478, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(479, 87, 'Permissions-CanWaiveAFeeInstallment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(817, 1, 479, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(480, 87, 'Permissions-CanRemoveFeeTypesAttachedToTheAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(818, 1, 480, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(481, 87, 'Permissions-Clients-CanSpecifyMeetingSchedule');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(819, 1, 481, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(482, 87, 'Permissions-Groups-CanSpecifyMeetingSchedule');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(820, 1, 482, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(483, 87, 'Permissions-Clients-CanEditPersonalInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(821, 1, 483, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(484, 87, 'Permissions-Centers-CanEditMeetingSchedule');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(822, 1, 484, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(485, 87, 'Permissions-Centers-CanSpecifyMeetingSchedule');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(823, 1, 485, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(486, 87, 'Permissions-CanEditLoanAccountInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(824, 1, 486, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(487, 87, 'Permissions-CanApplyChargesToLoans');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(825, 1, 487, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(488, 87, 'Permissions-CanEditSelfInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(826, 1, 488, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(489, 87, 'Permissions-SavingsManagement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(827, 1, 489, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(490, 87, 'Permissions-CanCreateNewSavingsAccountInSaveForLaterState');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(828, 1, 490, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(491, 87, 'Permissions-CanUpdateSavingsAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(829, 1, 491, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(492, 87, 'Permissions-CanCloseSavingsAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(830, 1, 492, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(493, 87, 'Permissions-SavingsManagement-CanChangeStateToPartialApplication');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(831, 1, 493, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(494, 87, 'Permissions-ReportsManagement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(832, 1, 494, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(495, 87, 'Permissions-CanAdministerReports');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(833, 1, 495, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(496, 87, 'Permissions-CanPreviewReports');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(834, 1, 496, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(497, 87, 'Permissions-CanUploadNewReports');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(835, 1, 497, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(498, 87, 'Permissions-ClientDetail');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(836, 1, 498, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(499, 87, 'Permissions-Center');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(837, 1, 499, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(500, 87, 'Permissions-Status');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(838, 1, 500, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(501, 87, 'Permissions-Performance');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(839, 1, 501, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(502, 87, 'Permissions-LoanProductDetail');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(840, 1, 502, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(503, 87, 'Permissions-Analysis');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(841, 1, 503, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(504, 87, 'Permissions-Miscellaneous');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(842, 1, 504, null);

-- commenting  lookup values for Jasper reports that no longer ship by default - pbiligi
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(505, 87, 'Permissions-CanViewClientSummaryHistoryReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(843, 1, 505, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(506, 87, 'Permissions-CanViewClientProductWiseHistoryReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(844, 1, 506, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(507, 87, 'Permissions-CanViewClientSettlementInfoReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(845, 1, 507, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(508, 87, 'Permissions-CanViewClientLoanRepaymentSchedule');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(846, 1, 508, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(509, 87, 'Permissions-CanViewClientFeesChargesAndPenaltiesReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(847, 1, 509, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(510, 87, 'Permissions-CanViewClientPendingApprovalReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(848, 1, 510, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(511, 87, 'Permissions-CanViewClientsWithoutSavingsAccount');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(849, 1, 511, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(512, 87, 'Permissions-CanViewCollectionSheet');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(850, 1, 512, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(513, 87, 'Permissions-CanViewDailyCashConfirmationReportStaffWise');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(851, 1, 513, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(514, 87, 'Permissions-CanViewDailyCashFlowReportBranch');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(852, 1, 514, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(515, 87, 'Permissions-CanViewFundRequirementReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(853, 1, 515, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(516, 87, 'Permissions-CanViewDailyTransactionSummaryReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(854, 1, 516, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(517, 87, 'Permissions-CanViewBranchPerformanceStatusReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(855, 1, 517, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(518, 87, 'Permissions-CanViewAreaPerformanceStatusReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(856, 1, 518, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(519, 87, 'Permissions-CanViewDivisionPerformanceStatusReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(857, 1, 519, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(520, 87, 'Permissions-CanViewRegionPerformanceStatusReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(858, 1, 520, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(521, 87, 'Permissions-CanViewGrameenKootaPerformanceStatusReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(859, 1, 521, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(522, 87, 'Permissions-CanViewStaffPerformanceReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(860, 1, 522, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(523, 87, 'Permissions-CanViewOutreachReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(861, 1, 523, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(524, 87, 'Permissions-CanViewCenterSummaryReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(862, 1, 524, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(525, 87, 'Permissions-CanViewLoanProductDistributionActivityWiseSizeWizeProductWiseSequencyWise');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(863, 1, 525, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(526, 87, 'Permissions-CanViewBranchDueDisbursementReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(864, 1, 526, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(527, 87, 'Permissions-CanViewLoansPendingApprovalReport');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(865, 1, 527, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(528, 87, 'Permissions-CanViewLoanAccountsReports');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(866, 1, 528, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(529, 87, 'Permissions-CanViewDailyPortfolioQualityDataReportAgingAnalysis');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(867, 1, 529, NULL);
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(530, 87, 'Permissions-CanViewCenterMeetingSchedule');
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(868, 1, 530, NULL);

insert into lookup_value(lookup_id,entity_id,lookup_name) values(531, 87, 'Permissions-CanRepayLoan');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(869, 1, 531, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(532, 87, 'Permissions-CanAddNotesToLoanAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(870, 1, 532, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(533, 87, 'Permissions-SavingsManagement-CanChangeStateToPendingApproval');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(871, 1, 533, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(534, 87, 'Permissions-SavingsManagement-CanChangeStateToCancel');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(872, 1, 534, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(535, 87, 'Permissions-SavingsManagement-CanChangeStateToActive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(873, 1, 535, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(536, 87, 'Permissions-SavingsManagement-CanChangeStateToInactive');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(874, 1, 536, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(537, 87, 'Permissions-CanBlacklistSavingsAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(875, 1, 537, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(538, 87, 'Permissions-CanCreateNewSavingsAccountInSubmitForApprovalState');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(876, 1, 538, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(539, 87, 'Permissions-NotImplemented');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(877, 1, 539, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(546, 87, 'Permissions-CanDoAdjustmentsForSavingsAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(884, 1, 546, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(551, 87, 'Permissions-CanWaiveDueDepositsForSavingsAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(889, 1, 551, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(552, 87, 'Permissions-CanWaiveOverDueDepositsForSavingsAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(890, 1, 552, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(553, 87, 'Permissions-CanDisburseLoan');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(891, 1, 553, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(554, 87, 'Permissions-CanMakeDepositWithdrawalToSavingsAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(892, 1, 554, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(555, 87, 'Permissions-CanAddNotesToSavingsAccount');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(893, 1, 555, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(560, 87, 'Permissions-CanApproveLoansInBulk');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(898, 1, 560, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(561, 87, 'Permissions-CanModifyLatenessDormancyDefinition');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(899, 1, 561, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(562, 87, 'Permissions-CanModifyOfficeHierarchy');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(900, 1, 562, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(563, 87, 'Permissions-CanAddNotesToPersonnel');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(901, 1, 563, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(564, 87, 'Permissions-Bulk');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(902, 1, 564, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(565, 87, 'Permissions-CanEnterCollectionSheetData');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(903, 1, 565, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(566, 87, 'Permissions-Clients-CanApplyChargesToClientAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(904, 1, 566, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(567, 87, 'Permissions-Groups-CanApplyChargesToGroupAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(905, 1, 567, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(568, 87, 'Permissions-Centers-CanApplyChargesToCenterAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(906, 1, 568, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(569, 87, 'Permissions-CanCreateMultipleLoanAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(914, 1, 569, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(570, 87, 'Permissions-CanReverseLoanDisbursals');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(915, 1, 570, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(574, 87, 'Permissions-ConfigurationManagement');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(919, 1, 574, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(575, 87, 'Permissions-CanDefineLabels');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(920, 1, 575, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(579, 87, 'Permissions-CanDefineHiddenMandatoryFields');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(924, 1, 579, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(580, 87, 'Permissions-Clients-CanRemoveClientsFromGroups');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(925, 1, 580, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(581, 87, 'Permissions-CanViewDetailedAgingOfPortfolioAtRisk');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(926, 1, 581, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(582, 87, 'Permissions-Clients-CanAddAnExistingClientToAGroup');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(927, 1, 582, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(583, 87, 'Permissions-ProductMix');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(928, 1, 583, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(584, 87, 'Permissions-CanDefineProductMix');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(929, 1, 584, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(585, 87, 'Permissions-CanEditProductMix');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(930, 1, 585, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(586, 87, 'Permissions-CanViewActiveLoansByLoanOfficer');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(931, 1, 586, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(587, 87, 'Permissions-CanDefineLookupValues');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(932, 1, 587, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(588, 87, 'Permissions-CanUploadReportTemplate');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(933, 1, 588, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(589, 87, 'Permissions-CanViewReports');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(934, 1, 589, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(590, 87, 'Permissions-CanEditReportInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(935, 1, 590, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(591, 87, 'Permissions-CanAdjustPaymentWhenAccountStatusIsClosedObligationMet');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(936, 1, 591, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(592, 87, 'Permissions-CanRedoLoanDisbursals');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(937, 1, 592, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(593, 87, 'Permissions-CanDefineAcceptedPaymentType');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(938, 1, 593, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(594, 87, 'Permissions-CanDefineNewReportCategory');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(939, 1, 594, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(595, 87, 'Permissions-CanViewReportCategory');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(940, 1, 595, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(596, 87, 'Permissions-CanDeleteReportCategory');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(941, 1, 596, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(597, 87, 'Permissions-CanDownloadReportTemplate');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(942, 1, 597, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(598, 87, 'Permissions-CanDefineCustomFields');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(943, 1, 598, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(602, 87, 'Permissions-CanUploadAdminDocuments');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(944, 1, 602, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(603, 87, 'Permissions-CanViewAdminDocuments');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(945, 1, 603, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(605, 87, 'Permissions-SystemInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(947, 1, 605, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(606, 87, 'Permissions-CanViewSystemInformation');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(948, 1, 606, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(607, 87, 'Permissions-CanViewCollectionSheetReport');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value) values(949, 1, 607, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(608, 87, 'Permissions-CanViewOrganizationSettings');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(950, 1, 608, null);
insert into lookup_value (lookup_id, entity_id, lookup_name) values(619, 87, 'Permissions-CanImportTransactions');
insert into lookup_value_locale(lookup_value_id, locale_id, lookup_id, lookup_value) values(955, 1, 619, null);
insert into lookup_value (lookup_id, entity_id, lookup_name) values(625, 87, 'Permissions-CanShutdownMifos');
insert into lookup_value_locale(lookup_value_id, locale_id, lookup_id, lookup_value) values(956, 1, 625, null);
insert into lookup_value (lookup_id, entity_id, lookup_name) values(627, 87, 'Permissions-CanDefineHoliday');
insert into lookup_value_locale(lookup_value_id, locale_id, lookup_id, lookup_value) values(957, 1, 627, null);

-- Entity: ServiceCharge
-- Entity: feeUpdationType
insert into lookup_value(lookup_id,entity_id,lookup_name) values(556, 89, 'feeUpdationType-AppliesToExistingFutureAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(894, 1, 556, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(557, 89, 'feeUpdationType-AppliesToFutureAccounts');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(895, 1, 557, null);
-- Entity: FeeFrequency
insert into lookup_value(lookup_id,entity_id,lookup_name) values(558, 90, 'FeeFrequency-Periodic');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(896, 1, 558, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(559, 90, 'FeeFrequency-OneTime');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(897, 1, 559, null);
-- Entity: RepaymentRule
insert into lookup_value(lookup_id,entity_id,lookup_name) values(576, 91, 'RepaymentRule-SameDay');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(921, 1, 576, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(577, 91, 'RepaymentRule-NextMeetingRepayment');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(922, 1, 577, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(578, 91, 'RepaymentRule-NextWorkingDay');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(923, 1, 578, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(626, 91, 'RepaymentRule-RepaymentMoratorium');
-- This was not added in Upgrade238, testing might missed it because of bug MIFOS-2875
-- Not sure if it's going to be a severe problem  
-- INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE)
-- VALUES(957, 1, 626, NULL);



insert into lookup_value(lookup_id,entity_id,lookup_name) values
(620, 92, 'Together'),
(621, 92, 'NotTogether'),
(622, 52, 'Mother'),
(623, 52, 'Child');
-- Potentially unused or orphaned lookup_values
-- Keep them here for a while and if everything is ok, they will be removed
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES
-- (65, 34, 'DBUpgrade.OfficeLevels.Unsued'),
-- (71, 35, 'DBUpgrade.PrdApplicableMaster.Unused'),
-- (90, 39, 'DBUpgrade.InterestCalcRule.Unused'),
-- (223, 79, 'DBUpgrade.Address3.Unused'),
-- (224, 80, 'DBUpgrade.City.Unused'),
-- (259, 82, 'DBUpgrade.LoanPurposes1.Unused'),
-- (263, 82, 'DBUpgrade.LoanPurposes2.Unused');


/*Personnel Level*/
/* The table personnel level will contain the personnel hierarchy supported by the MFI(This feature is currently not supported) - Configuration */
insert into personnel_level(level_id, parent_level_id, level_name_id, interaction_flag) values
(1,1,60,0),
(2,1,61,0);


/* The table LOOKUP_LABEL will contain the configuration information
for the LOOKUP_ENTITY defined,like salutation entity could have
mr,mrs etc - Configuration */
/* Client Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name) values
(1,1,1,null),
/* Group Status */
(3,2,1,null),
/* Center Status */
(5,3,1,null),
/* Office Status */
(7,4,1,null),
/* Loan Status */
(9,5,1,null),
/* Personnel Status */
(11,6,1,null),
/* Group Flag */
(13,7,1,null),
/* Fee Type */
(15,8,1,null),
/* Titles */
(17,9,1,null),
/* Poverty Status */
(19,10,1,null),
/* Center */
(21,11,1,null),
/* Group */
(23,12,1,null),
/* 'Client' */
(25,13,1,null),
/* Office */
(27,14,1,null),
/* Salutation */
(29,15,1,null),
/* Gender */
(31,16,1,null),
/* MartialStatus */
(33,17,1,null),
/* Citizenship */
(35,18,1,null),
/* Ethnicity */
(37,19,1,null),
/* EducationLevel */
(39,20,1,null),
/* Occupation */
(41,21,1,null),
/* Handicapped */
(43,22,1,null),
/* Postal Code */
(47,24,1,null),
/* Product State */
(49,25,1,null),
/* Loan */
(51,26,1,null),
/* Savings */
(53,27,1,null),
/* User Title */
(57,29,1,null),
/* User Hierarchy */
(59,30,1,null),
/* OfficeLevel */
(67,34,1,null),
/* PrdApplicableMaster */
(69,35,1,null),
/* Week Days */
(71,36,1,null),
/* Days Rank */
(73,42,1,null),
/* InterestTypes */
(75,37,1,null),
/* CategoryTypes */
(76,38,1,null),
/* InterestCalcRule */
(77,39,1,null),
/* GracePeriodTypes */
(79,41,1,null),
/* CollateralTypes */
(80,43,1,null),
/* Office Code */
(81,44,1,null),
/* Product Category Status */
(83,45,1,null),
/* Product Status */
(85,46,1,null),
/* SAVINGS TYPE */
(87,47,1,null),
/* REC AMNT UNIT */
(89,48,1,null),
/* INT CALC TYPES */
(91,49,1,null),
/* YES/NO */
(93,50,1,null),
/* Account Type */
(95,51,1,null),
/* Spouse/Father */
(97,52,1,null),
/* Customer Status */
(99,53,1,null),
/* Fee Payment */
(100,54,1,null),
/* Fee Formula Master */
(102,55,1,null),
/* Personnel Status */
(104,56,1,null),
/* Personnel */
(106,57,1,null),
/* 2007-12-18 Custom field labels 108-126 removed */
/* except for 116 which is also used by non-custom fields */
/* External Id */
(116,62,1,null),
/* Fee Status */
(128,68,1,null),
/* Account Action */
(130,69,1,null),
/* AccountFlags */
(132,70,1,null),
/* PaymentType */
(134,71,1,null),
/* Savings Status */
(136,72,1,null),

/*language*/
/* Language */

(151,74,1,null),
/* CustomerAttendance */
(154,75,1,null),
/* Financial Action */
(156,76,1,null),
/* Bulk entry */
(158,77,1,null),
/* Address 3 */
(160,79,1,null),
/* City/District */
(162,80,1,null),
/* Interest */
(164,81,1,null),
/* Loan Purposes */
(166,82,1,null),
/* State */
(167,83,1,null),
/* Address1 */
(168,84,1,null),
/* Address2 */
(169,85,1,null),
/* Government ID */
(170,86,1,null),
/* Permissions */
(171,87,1,null),
/* Interest */
(172,88,1,null);

/*Category Types*/
/*The table CATEGORY_TYPE is Used for defining applicablity of the fees--System */
insert into category_type(category_id,category_lookup_id)values
(1,81),
(2,82),
(3,83),
(4,84),
(5,86);

/* The table PRD_TYPE will contain the configuration of lateness days, dormancy days associated to loan/saving product of the MFI - Configuration */
insert into prd_type(prd_type_id,prd_type_lookup_id,lateness_days,dormancy_days,version_no)values
(1,54,10,1,1),
(2,55,12,30,1);

/* The Table PRD_CATEGORY contains the differnt product categories like loan , savings supported by the system - System */
insert into prd_category(prd_category_id,prd_type_id,global_prd_offering_num,prd_category_name ,created_date,created_by,office_id,updated_by,udpated_date,state,description,version_no)values
(1,1,'1-1','Other',null,null,null,null,null,1,null,1),
(2,2,'1-2','Other',null,null,null,null,null,1,null,1);


insert into office_code(code_id,lookup_id) values
(1,111),
(2,112);

/* The PRD_CATEGORY_STATUS table contains the different product category status supported by the product category like active(1),inactive(2) etc - System */
insert into prd_category_status(prd_category_status_id,lookup_id) values
(1,114),
(2,113);

/* The PRD_STATE table contains the different states supported for the product types like active,inactive etc - System */
insert into prd_state(prd_state_id,prd_state_lookup_id)values
(1,115),
(2,116);

/* The table Savings type contains information like Mandatory, Voluntary related to savings - System */
insert into savings_type(savings_type_id ,lookup_id) values
(1,118),
(2,119);

/* The table Recommended Amount which contains information used in group savings account holding values like complete group , per individual - System */
insert into recommended_amnt_unit(recommended_amnt_unit_id,lookup_id)values
(1,120),
(2,121);

/* The table Interest Calculation Types contains calculation types for savings like minimum balance , average balance - System */
insert into interest_calculation_types(interest_calculation_type_id,interest_calculation_lookup_id,description) values
(1,122,null),
(2,123,null);

insert into yes_no_master(yes_no_master_id,lookup_id ) values
(1,124),
(2,125);

/* The table account type contains the account types supported by the system like Loan , savings, customer - System */
insert into account_type(account_type_id,lookup_id,description) values
(1,126,'Loan Account'),
(2,127,'Savings Account'),
(3,140,'Customer Account'),
-- Add an Individual Loan Account Type (A virtual Loan Account)
(4,126,'Individual Loan Account');

/* The table SPOUSE_FATHER_LOOKUP contains the spouse father data which is used in display - System */
insert into spouse_father_lookup(spouse_father_id,lookup_id) values
(1,128),
(2,129),
(4,622),
(5,623);
/* The table Fee Payment Type contains the applicability of fee like time of disburesement,time of first installment etc - System */
insert into fee_payment(fee_payment_id,fee_payment_lookup_id)values
(1,146),
(2,147),
(3,148);

/* The table Fee Status contains the fee status supported by the system like active, inactive etc - System */
insert into fee_status(status_id,status_lookup_id)values
(1,165),
(2,166);

insert into fee_payments_categories_type(fee_payments_category_type_id,fee_payment_id,category_id,fee_type_id)values
(1,1,1,1),
(2,1,1,1),
(3,1,1,1);

/* The table Fee Formula master contains the formula for calculating fee amount like % of Interest, %of Principal etc - System */
insert into fee_formula_master(formulaid,forumla_lookup_id)values
(1,149),
(2,150),
(3,151);


/* The table PERSONNEL_STATUS contains the different status that will be associated to a personnel like active, inactive etc. - System */
insert into personnel_status(personnel_status_id,lookup_id)values
(1,152),
(2,153);



/* The table Customer Addendence Types will contain the different attendence types supported by the MFI like present,absent etc - Configuration */
insert into customer_attendance_types values
(1,194,'Present'),
(2,195,'Absent'),
(3,196,'Approved leave'),
(4,197,'Late');

/* The table Customer level will contain the different levels supported by the system like center,group etc - System */
/* Level for Centers */
insert into customer_level(level_id,parent_level_id,level_name_id,interaction_flag,max_child_count,max_instance_count)values
(3,null,11,null,4,10),
/* Level for Groups */
(2,3,12,null,2,12),
/* Level for Clients */
(1,2,13,null,1,30);

/* The table Customer State contains the different states supported at each level of customer, like Center could have Partial, Pending state etc - System */
/* States for Customers */
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)values
(1,1,1,'Customer Was Partial',1),
(2,1,2,'Customer Was Pending',1),
(3,1,3,'Customer Was Active',1),
(4,1,4,'Customer Was Hold',1),
(5,1,5,'Customer Was Cancel',1),
(6,1,6,'Customer Was Close',1),
/* States for Groups */
(7,2,7,'Customer Was Partial',1),
(8,2,8,'Customer Was Pending',1),
(9,2,9,'Customer Was Active',1),
(10,2,10,'Customer Was Hold',1),
(11,2,11,'Customer Was Cancel',1),
(12,2,12,'Customer Was Close',1),
/* States for Centers */
(13,3,13,'Customer Was Active',1),
(14,3,14,'Customer Was Inactive',1);


/* The table Customer State Flag contains the different flags associated at the various customer states - System */
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)values
(1,28,5,'Withdraw',0),
(2,29,5,'Rejected',0),
(3,30,5,'Blacklisted',1),
(4,31,5,'Duplicate',0),
(5,34,5,'Other',0),
(6,32,6,'Transferred',0),
(7,31,6,'Duplicate',0),
(8,30,6,'Blacklisted',1),
(9,33,6,'Left program',0),
(10,34,6,'Other',0),
(11,28,11,'Withdraw',0),
(12,29,11,'Rejected',0),
(13,30,11,'Blacklisted',1),
(14,31,11,'Duplicate',0),
(15,34,11,'Other',0),
(16,32,12,'Transferred',0),
(17,31,12,'Duplicate',0),
(18,30,12,'Blacklisted',1),
(19,33,12,'Left program',0),
(20,34,12,'Other',0);


/* The table PRD_STATUS contains the different status that would be associated to a product like loan active, savings active etc - System */
insert into prd_status(offering_status_id,prd_state_id,prd_type_id,currently_in_use,version_no)values
(1,1,1,1,1),
(2,1,2,1,1),
(4,2,1,1,1),
(5,2,2,1,1);

/* The table PRD_APPLICABLE_MASTER indicates to which type a product is being associated like client, group , center etc - System */
insert into prd_applicable_master(prd_applicable_master_id,lookup_id)values
(1,68),
(2,69),
(3,70);


/* The table Account State contains the different states supported at each type of account, like Loan could have Partial, Pending state etc - System */
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)values
(1,17,1,1,'Partial application'),
(2,18,1,1,'Application pending approval'),
(3,19,1,1,'Application approved'),
(4,20,1,0,'Disbursed to loan officer'),
(5,21,1,1,'Active in good standing'),
(6,22,1,1,'Closed - obligation met'),
(7,23,1,1,'Closed - written off'),
(8,24,1,1,'Closed - rescheduled'),
(9,25,1,1,'Active in bad standing'),
(10,141,1,1,'Canceled'),
(11,142,1,1,'Customer Account Active'),
(12,143,1,1,'Customer Account Inactive'),
(13,181,2,1,'Partial application'),
(14,182,2,1,'Application pending approval'),
(15,183,2,1,'Canceled'),
(16,184,2,1,'Active'),
(17,185,2,1,'Closed'),
(18,210,2,1,'Inactive');

/* The table Account State Flag contains the different flags associated at the various account states - System */
insert into account_state_flag(flag_id,lookup_id,status_id,flag_description,retain_flag)values
(1,174,10,'Withdraw',0),
(2,175,10,'Rejected',0),
(3,176,10,'Other',0),
/*Savings account flags*/
(4,211,15,'Withdraw',0),
(5,212,15,'Rejected',0),
(6,213,15,'Blacklisted',1);

/* The table PAYMENT_TYPE contains the different payment types associated like cash,etc - System */
insert into payment_type(payment_type_id,payment_type_lookup_id)values
(1,177),
(2,179),
(3,180);

/* The table ACCOUNT_ACTION contains the different account actions that can be performed like deposit,withdraw,etc - System */
insert into account_action(account_action_id,lookup_id)values
(1,167),
(2,168),
(3,169),
(4,170),
(5,171),
(6,172),
(7,173),
(8,191),
(9,192),
(10,193),
(11,214),
(12,362),
(13,364),
(14,366),
(15,547),
(16,548),
(17,549);
/*Postion Table */

/* The table Position contains the group and center position values that can be associated to a client like Group President, Group Secretary, etc. Note that Groups and Centers share the same set of position titles- Configuration */
insert into position (position_id,lookup_id)values
(1,186),
(2,187),
(3,188),
(4,216);


/* STILL NEEDS COMMENTS */
insert into office_action_payment_type(office_id,prd_type_id,account_action_id,payment_type_id)values
(null,null,1,null),
(null,null,2,null),
(null,null,3,null),
(null,null,4,null),
(null,null,5,null);

/* The table Office Status contains the status that can be associated to a office like active,inactive etc - System */
insert into office_status(status_id,lookup_id)values
(1,15),
(2,16);

/* The table Office will contain the default Head office information of the MFI like officename etc */
/* This information can be configured through the UI, so does not need to be configured in the script */
insert into office( parent_office_id, global_office_num, status_id,
  office_code_id, office_level_id,  search_id, office_short_name,
  max_child_count,local_remote_flag, display_name,
  created_by, created_date, updated_by, updated_date, version_no)
values(null,'0001',1,
  null,1,'1.1.','MIF1',
  2,1,'Mifos HO ',
  null,null,null,null,1);


/* STILL NEEDS COMMENTS */
insert into mfi_attribute(attribute_id, office_id, attribute_name, attribute_value)values
(1,1,'CENTER','GROUP'),
(2,1,'CENTER','GROUP'),
(3,1,'CENTER','GROUP'),
(4,1,'CENTER','GROUP'),
(5,1,'CENTER','GROUP');


/* DONT EDIT THE LOGIN_NAME COLUMN VALUE IN THE FOLLOWING ROW BECAUSE IT WOULD BE USED AS DEFAULT USER
   PASSWORD FOR THE SAME IS BEING GENERATED BY INITIALIZER PLUGIN*/

/* The table Personnel contains the default user of the system with
   information like login name etc - Configuration */
 insert into personnel(personnel_id,level_id,global_personnel_num,office_id,
 title,personnel_status,preferred_locale,search_id,max_child_count,password,
 login_name,email_id,password_changed,display_name,created_by,
created_date,updated_by,updated_date,no_of_tries,last_login,locked,version_no)
values(1,2,'1',1,
1,1,1,null,1,null,
'mifos',null,1,'mifos',1,
null,1,null,0,null,0,0);

/* The table Personnel Details contains the address information of the default user  - Configuration */
insert into personnel_details
values(1,'Mifos',null,null,'MFI','123','1979-12-12',null,50,null,null,null,'Bangalore',null,null,'Bangalore','Bangalore','Bangalore',null,null);

/* the table ENTITY_MASTER is used to specify entities that can be configured and are refered to in later sections of the script. */
/* Examples of how these entities might be configured later in the script: adding "Custom fields", or hidding/renaming fields that are associated with an entity (ie, hiding the field "middle name" in clients */
/* No configuration is needed in this section */
insert into entity_master(entity_type_id,entity_type)values
(1,'Client'),
(2,'LoanProduct'),
(3,'SavingsProduct'),
(4,'ProductCategory'),
(5,'ProductConfiguration'),
(6,'Fees'),
(7,'Accounts'),
(8,'Admin'),
(9,'Checklist'),
(10,'Configuration'),
(11,'Customer'),
(12,'Group'),
(13,'Login'),
(14,'Meeting'),
(15,'Office'),
(16,'Penalty'),
(17,'Personnel'),
(19,'Roleandpermission'),
(20,'Center'),
(21,'Savings'),
(22,'Loan'),
(23,'BulkEntry');



/* The table Activity will contain all the activities supported by the system - System */
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values
(1,null,371,371 ),
(2,1,372,372 ),
(3,2,373,373 ),
(4,2,374,374 ),
(5,1,375,375 ),
(6,5,376,376 ),
(7,5,377,377 ),
(8,1,378,378 ),
(9,8,379,379 ),
(10,8,380,380 ),
(13,null,381,381 ),
(14,13,382,382 ),
(15,14,383,383 ),
(16,14,384,384 ),
(17,null,385,385 ),
(18,17,386,386 ),
(19,18,387,387 ),
(20,18,388,388 ),
(21,18,389,389 ),
(22,17,390,390 ),
(23,22,391,391 ),
(24,22,392,392 ),
(25,22,393,393 ),
(33,null,394,394 ),
(34,33,395,395 ),
(35,34,396,396 ),
(36,34,397,397 ),
(37,34,398,398),
(38,34,399,399 ),
(39,34,400,400 ),
(40,34,401,401 ),
(41,34,402,402 ),
(42,34,403,403 ),
(43,34,404,404 ),
(44,34,405,405 ),
(46,34,407,407 ),
(47,34,408,408 ),
(48,34,409,409 ),
(49,34,410,410 ),
(50,34,411,411),
(51,34,412,412 ),
(52,34,413,413 ),
(53,34,414,414 ),
(54,34,415,415 ),
(55,34,416,416 ),
(56,33,417,417 ),
(57,56,418,418 ),
(58,56,419,419 ),
(59,56,420,420 ),
(60,56,421,421 ),
(61,56,422,422 ),
(62,56,423,423 ),
(63,56,424,424 ),
(64,56,425,425 ),
(65,56,426,426 ),
(66,56,427,427 ),
(68,56,429,429 ),
(69,56,430,430 ),
(70,56,431,431 ),
(71,56,432,432 ),
(72,56,433,433 ),
(73,56,434,434 ),
(74,56,435,435 ),
(75,56,436,436 ),
(76,56,437,437 ),
(77,56,438,438 ),
(78,33,439,439 ),
(79,78,440,440 ),
(80,78,441,441 ),
(81,78,442,442 ),
(82,78,443,443 ),
(83,78,444,444 ),
(85,78,446,446 ),
(86,78,447,447 ),
(87,78,448,438 ),
(88,78,449,449 ),
(89,null,450,450 ),
(90,89,451,451 ),
(91,90,452,452 ),
(92,90,453,453 ),
(93,89,454,454 ),
(94,93,455,455 ),
(95,93,456,456 ),
(96,89,457,457 ),
(97,96,458,458 ),
(98,96,459,459 ),
(99,null,460,460 ),
(100,99,461,461 ),
(101,100,462,462 ),
(102,100,463,463 ),
(103,100,464,464 ),
(104,100,465,465 ),
(105,100,466,466 ),
(106,100,467,467 ),
(108,100,469,469 ),
(109,100,470,470 ),
(110,100,471,471 ),
(113,99,474,474 ),
(115,113,475,475 ),
(116,113,476,476 ),
(118,113,478,478 ),
(119,113,479,479 ),
(120,113,480,480 ),
(121,34,481,481 ),
(122,56,482,482 ),
/* (123,33,709,709 ), */
/* (124,123,709,709 ), */
/* (125,34,709,709 ), */
(126,34,483,483 ),
(127,78,484,484 ),
(128,78,485,485 ),
(129,100,486,486 ),
/*  (130,100,709,709 ),*/
(131,113,487,487 ),
/* (133,123,709,709 ),*/
/* (134,123,709,709 ),*/
(135,18,488,488 ),
(136,null,489,489 ),
(137,136,490,490 ),
(138,136,491,491 ),
(139,136,492,492 ),
(140,136,493,493 ),
(141,null,494,494 ),
(145,141,498,498 ),
(146,141,499,499 ),
(147,141,500,500 ),
(148,141,501,501 ),
(149,141,502,502 ),
(150,141,503,503 ),
(151,141,504,504 ),

-- commenting  activities for Jasper reports that no longer ship by default - pbiligi
-- (152,145,505,505 ),
-- (153,145,506,506 ),
-- (154,145,507,507 ),
-- (155,145,508,508 ),
-- (156,145,509,509 ),
-- (157,145,510,510 ),
-- (158,145,511,511 ),
-- (159,146,512,512 ),
-- (160,147,513,513 ),
-- (161,147,514,514 ),
-- (162,147,515,515 ),
-- (163,147,516,516 ),
-- (164,148,517,517 ),
-- (165,148,518,518 ),
-- (166,148,519,519 ),
-- (167,148,520,520 ),
-- (168,148,521,521 ),
-- (169,148,522,522 ),
-- (170,148,523,523 ),
-- (171,148,524,524 ),
-- (172,149,525,525 ),
-- (173,149,526,526 ),
-- (174,149,527,527 ),
-- (175,149,528,528 ),
-- (176,150,529,529 ),
-- (177,151,530,530 ),

(178,113,531,531 ),
(179,100,532,532 ),

/* remaning saving states mappings */
(180,136,533,533 ),
(181,136,534,534 ),
(182,136,535,535 ),
(183,136,536,536 ),
(184,136,537,537 ),
(185,136,538,538 ),
(186,136,546,546 ),
(187,136,551,551 ),
(188,136,552,552 ),
(189,113,553,553 ),
(190,136,554,554 ),
(191,136,555,555 ),
(193,13,562,562 ),
(194,18,563,563 ),
(195,90,561,561),
(196,null,564,564 ),
(192,196,560,560 ),
(197,196,565,565 ),
(198,34,566,566 ),
(199,56,567,567 ),
(200,78,568,568 ),
(201,196,569,569);


/* The table Role will contain the default admin role required for the MFI - System */
insert into role(role_id,role_name,version_no,created_by,created_date,
updated_by,updated_date)
values(1,'Admin',1,null,null,null,null);

/* The table Role Activity will contain the mapping between the role created for the mfi admin against the activities he can perform which could typically be all - System */
insert into roles_activity(activity_id,role_id)values
(3,1),
(4,1),
(6,1),
(7,1),
(9,1),
(10,1),
(15,1),
(16,1),
(19,1),
(20,1),
(21,1),
(23,1),
(24,1),
(25,1),
(35,1),
(36,1),
(37,1),
(38,1),
(39,1),
(40,1),
(41,1),
(42,1),
(43,1),
(44,1),
(46,1),
(47,1),
(48,1),
(49,1),
(50,1),
(51,1),
(52,1),
(53,1),
(54,1),
(55,1),
(57,1),
(58,1),
(59,1),
(60,1),
(61,1),
(62,1),
(63,1),
(64,1),
(65,1),
(66,1),
(68,1),
(69,1),
(70,1),
(71,1),
(72,1),
(73,1),
(74,1),
(75,1),
(76,1),
(77,1),
(79,1),
(80,1),
(81,1),
(82,1),
(83,1),
(85,1),
(86,1),
(87,1),
(88,1),
(91,1),
(92,1),
(94,1),
(95,1),
(97,1),
(98,1),
(101,1),
(102,1),
(103,1),
(104,1),
(105,1),
(106,1),
(108,1),
(109,1),
(110,1),
(115,1),
(116,1),
(118,1),
(119,1),
(120,1),
(121,1),
(122,1),
/*(123,1),*/
/*(124,1),*/
/* (125,1),*/
(126,1),
(127,1),
(128,1),
(129,1),
/* (130,1), */
(131,1),
/* (133,1),*/
/*(134,1),*/
(135,1),
(136,1),
(137,1),
(138,1),
(139,1),
(140,1),
(141,1),
(145,1),
(146,1),
(147,1),
(148,1),
(149,1),
(150,1),
(151,1),
-- commenting Admin activities related  to jasper reports which are not shipped by default - pbiligi
-- (152,1),
-- (153,1),
-- (154,1),
-- (155,1),
-- (156,1),
-- (157,1),
-- (158,1),
-- (159,1),
-- (160,1),
-- (161,1),
-- (162,1),
-- (163,1),
-- (164,1),
-- (165,1),
-- (166,1),
-- (167,1),
-- (168,1),
-- (169,1),
-- (170,1),
-- (171,1),
-- (172,1),
-- (173,1),
-- (174,1),
-- (175,1),
-- (176,1),
-- (177,1),

(178,1),
(179,1),
(180,1),
(181,1),
(182,1),
(183,1),
(184,1),
(185,1),
(186,1),
(187,1),
(188,1),
(189,1),
(190,1),
(191,1),
(192,1),
(193,1),
(194,1),
(195,1),
(197,1),
(198,1),
(199,1),
(200,1),
(201,1);


/* inserting VALUES in interest_type table*/
/* The table Interest Types contains the different interest types supported by the system like Flat etc - System */
insert into interest_types (interest_type_id, lookup_id, category_id, descripton)values
(1,79,1,'Flat'),
(2,80,1,'Declining'),
(4,604,1,'Declining Balance-Equal Principal Installment');

insert into interest_calc_rule (interest_calc_rule_id, lookup_id)values
(1,88),
(2,89);

/* Grace period types*/
/* The table GRACE_PERIOD_TYPE contains the different grace types supported by the system like principal only grace,interest only grace etc - System */
insert into grace_period_type (grace_period_type_id, lookup_id)values
(1,96),
(2,97),
(3,98);

/* master data for table fee_frequency_type*/
/* The table Fee Frequency contains type of fees like periodic, one time - System */
insert into fee_frequency_type(fee_frequency_id,lookup_id) values
(1,558),
(2,559);

/*master data for table FIELD_CONFIGURATION */
/* The table Field Configuration will contain the MFI configuration related to fields being mandatory , hidden etc - Configuration */
/* ENTITY_ID is defined in the ENTITY_MASTER table and refers to entities such as client, personnel, groups, etc against which various data field can be marked as hidden and/or mandatory */

/*Middle Name for client and personnel*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(1,'MiddleName',1,0,0),
(2,'MiddleName',17,0,0),
/*Second Last Name for client and personnel*/
(3,'SecondLastName',1,0,0),
(4,'SecondLastName',17,0,0),
/*Government id*/
(5,'GovernmentId',1,0,0),
(6,'GovernmentId',17,0,0),
/*External id for cleint group center personnel*/
(7,'ExternalId',1,0,0),
(8,'ExternalId',12,0,0),
(9,'ExternalId',20,0,0),
/*Ethnicity, Citizenship, Handicapped, business Activities, Education Level, Photo*/
(10,'Ethinicity',1,0,0),
(11,'Citizenship',1,0,0),
(12,'Handicapped',1,0,0),
(13,'BusinessActivities',1,0,0),
(14,'EducationLevel',1,0,0),
(15,'Photo',1,0,0),
/*SpouseFatharMiddleName for Client*/
(16,'SpouseFatherMiddleName',1,0,0),
/*SpouseFatharSecondLastName for Client*/
(17,'SpouseFatherSecondLastName',1,0,0),
/*Trained and trained date*/
(18,'Trained',1,0,0),
(19,'Trained',12,0,0),
(20,'TrainedDate',1,0,0),
(21,'TrainedDate',12,0,0),
/*Entire Address for client*/
(22,'Address',1,0,0),
(23,'Address',12,0,0),
(24,'Address',20,0,0);
/**Address 1 for client group and center*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)values
(25,'Address1',1,0,0,22),
(26,'Address1',12,0,0,23),
(27,'Address1',20,0,0,24),
/**Address 2 for client group and center*/
(28,'Address2',1,0,0,22),
(29,'Address2',12,0,0,23),
(30,'Address2',20,0,0,24),
/**Address 3*/
(31,'Address3',1,0,0,22),
(32,'Address3',12,0,0,23),
(33,'Address3',20,0,0,24);

insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(34,'Address3',15,0,0),
(35,'Address3',17,0,0);
/*City*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)values
(36,'City',1,0,0,22),
(37,'City',12,0,0,23),
(38,'City',20,0,0,24);
/*State*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)values
(39,'State',1,0,0,22),
(40,'State',12,0,0,23),
(41,'State',20,0,0,24);

insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(42,'State',15,0,0),
(43,'State',17,0,0);
/*Country*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)values
(44,'Country',1,0,0,22),
(45,'Country',12,0,0,23),
(46,'Country',20,0,0,24);

insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(47,'Country',15,0,0),
(48,'Country',17,0,0);

/*Postal Code*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)values
(49,'PostalCode',1,0,0,22),
(50,'PostalCode',12,0,0,23),
(51,'PostalCode',20,0,0,24);

insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(52,'PostalCode',15,0,0),
(53,'PostalCode',17,0,0);

/**Phone number*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(54,'PhoneNumber',1,0,0),
(55,'PhoneNumber',12,0,0),
(56,'PhoneNumber',20,0,0),
(57,'PhoneNumber',17,0,0),
/*Purpose Of Loan*/
(58,'PurposeOfLoan',22,0,0),
/*Collateral Type and Notes For Loan*/
(59,'CollateralType',22,0,0),
(60,'CollateralNotes',22,0,0),
/*Receipt id and date*/
(61,'ReceiptId',1,0,0),
(62,'ReceiptId',12,0,0),
(63,'ReceiptId',20,0,0),
(64,'ReceiptId',21,0,0),
(65,'ReceiptId',22,0,0),
(66,'ReceiptId',23,0,0),
(67,'ReceiptDate',1,0,0),
(68,'ReceiptDate',12,0,0),
(69,'ReceiptDate',20,0,0),
(70,'ReceiptDate',21,0,0),
(71,'ReceiptDate',22,0,0),
(72,'ReceiptDate',23,0,0),
(73,'PovertyStatus',1,1,0),
(75,'Address2',15,0,0),
(76,'Address2',17,0,0),
(77,'Address1',15,0,0),
(78,'Address1',17,0,0),
(79,'City',15,0,0),
(80,'SourceOfFund',22,0,0);

/* Added for jira issue 2730 */
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(81,'MaritalStatus',1,0,0),
(82,'NumberOfChildren',1,0,0);

/* Added for jira issues 2814 and 2930 */
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(83,'ExternalId',22,0,0);

/* Added for jira issue 2929 */
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)values
(84,'SpouseFatherInformation',1,1,0),
(85,'FamilyDetails',1,1,0);

/* The table CONFIG_KEY_VALUE_INTEGER holds generic key-value pair configuration data for integer values */
-- this row was CollectionSheetHelper.daysInAdvance
-- (value now stored in applicationConfiguration.default.properties)
-- Adam [ Thu Dec 20 22:28:57 PST 2007 ]
insert into config_key_value_integer(configuration_key, configuration_value) values 
('x',0),
-- this row was sessionTimeout (see web.xml for configured session timeout)
-- Adam [ Thu Dec 20 22:28:58 PST 2007 ]
(' ',0),
('jasperReportIsHidden',1),
('loanIndividualMonitoringIsEnabled',0),
('repaymentSchedulesIndependentOfMeetingIsEnabled',0),
/* Client Rules */
('CenterHierarchyExists',1),
('ClientCanExistOutsideGroup',0),
('GroupCanApplyLoans',0),
('minDaysBetweenDisbursalAndFirstRepaymentDay',1),
('maxDaysBetweenDisbursalAndFirstRepaymentDay',365),
('AdministrativeDocumentsIsEnabled',1);

/*Added Table Personnel_Role */
/* The table Personnel role will contain the role associated to the Admin user of the MFI - Configuration */
insert into personnel_role(personnel_role_id, role_id, personnel_id)values(1,1,1);

/* The table Financial Action will contain the different financial actions
   supported by the system like Payment , Adjutment etc - System. Constants in
   FinancialActionConstants map to IDs in this table. */
insert into financial_action values
(1,198),
(2,199),
(3,200),
(4,229),
(5,201),
(6,361),
(7,215),
(8,202),
(9,203),
(10,204),
(11,205),
(12,206),
(13,207),
(14,208),
/* TODO: delete this one (and associated lookup values), it doesn't appear in
   FinancialActionConstants and can therefore never be used. */
(15,209),
(16,363),
/* TODO: delete this one (and associated lookup values), it doesn't appear in
   FinancialActionConstants and can therefore never be used. */
(17,365),
(18,367),
(19,368),
(20,369),
(21,370),
(22,550);


/* The table Transaction Type defines the transaction types for which the MFI can assign acceptable payment methods.  - System.*/
insert into transaction_type (transaction_id,transaction_name) values
(1,'Loan Disbursement'),
(2,'Loan Repayment'),
(3,'Savings Deposit'),
(4,'Savings Withdrawals'),
(5,'Client Fees/penalty payments');

/* The table Accepted Payment Type will contain the different payment modes supported by the system - System*/
insert into accepted_payment_type (accepted_payment_type_id,transaction_id,payment_type_id) values 
(1,1,1),
(2,2,1),
(3,3,1),
(4,4,1),
(5,5,1);

/* The table FEE_UPDATE_TYPE indicates what in fee is changed like amount changed, status changed etc - System */
insert into fee_update_type(lookup_id) values
(556),
(557);

insert into fund_code(fundcode_id,fundcode_value) values
(1,'00'),
(2,'01'),
(3,'02'),
(4,'03'),
(5,'04');

insert into fund(fund_id,fundcode_id,fund_name,version_no)values
(1,1,'Non Donor',0),
(2,1,'Funding Org A',0),
(3,1,'Funding Org B',0),
(4,1,'Funding Org C',0),
(5,1,'Funding Org D',0);


-- Begin data for Reports Mini Portal

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(915,1,570,'Can reverse Loan disbursals');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(202,99,570,570);
insert into roles_activity values (202,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(916,1,571,'Loan reversal');
insert into account_state_flag(
  flag_id,lookup_id,status_id,flag_description,retain_flag)
  values(7,571,10,'Loan reversal',0);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(917,1,572,'Loan Reversal');
insert into account_action(account_action_id,lookup_id) values(18,572);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(918,1,573,'Disrbursal amount Reversal');
insert into account_action(account_action_id,lookup_id) values(19,573);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(919,1,574,'Configuration Management');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(203,null,574,574);
insert into roles_activity values (203,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(920,1,575,'Can define labels');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(204,203,575,575);
insert into roles_activity values (204,1);


insert into repayment_rule(repayment_rule_id, repayment_rule_lookup_id) values 
 (1,576)
,(2,577)
,(3,578)
,(4,626)
;

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(924,1,579,'Can define hidden/mandatory fields');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(205,203,579,579);
insert into roles_activity values (205,1);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(74,'AssignClients',1,0,0);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(925,1,580,'Can remove clients from groups');
insert into activity(activity_id,parent_id,
  activity_name_lookup_id,description_lookup_id)
  values(206,34,580,580);
insert into roles_activity values (206,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(926,1,581,'Can view Detailed Aging of Portfolio at Risk');
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,
--  ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
--  VALUES(207,150,581,581);
-- INSERT INTO ROLES_ACTIVITY VALUES (207,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE
--  VALUES(927,1,582,'Can add an existing client to a group');
insert into activity(activity_id,parent_id,
  activity_name_lookup_id,description_lookup_id)
  values(208,34,582,582);
insert into roles_activity values (208,1);

insert into activity(activity_id,parent_id,
  activity_name_lookup_id,description_lookup_id)
  values(229,145,607,607);
insert into roles_activity values (229,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(928,1,583,'Product Mix');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(209,89,583,583);

insert into roles_activity values (209,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(929,1,584,'Can define product mix');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(210,209,584,584);
insert into roles_activity values (210,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(930,1,585,'Can edit product mix');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(211,209,585,585);
insert into roles_activity values (211,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(931,1,586,'Can view Active Loans By Loan Officer');
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,
--   ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
--  VALUES(212,150,586,586);
-- INSERT INTO ROLES_ACTIVITY VALUES (212,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(932,1,587,'Can Define Lookup Values');
insert into activity(activity_id,parent_id,activity_name_lookup_id,
  description_lookup_id) values(213,203,587,587);
insert into roles_activity values (213,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(933,1,588,'Can upload report template');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(214,141,588,588);
insert into roles_activity values (214,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(934,1,589,'Can view reports');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(215,141,589,589);
insert into roles_activity values (215,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(935,1,590,'Can edit report information');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(216,141,590,590);
insert into roles_activity values (216,1);

insert into lookup_value(lookup_id,entity_id,lookup_name) values(611, 87, 'Permissions-CanViewBranchCashConfirmationReport');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value) values(953, 1, 611, null);

insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(231,150,611,611);
insert into roles_activity values (231,1);

insert into lookup_value(lookup_id,entity_id,lookup_name) values(612, 87, 'Permissions-CanViewBranchProgressReport');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value) values(954, 1, 612, null);

insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(232,150,612,612);
insert into roles_activity values (232,1);

/* The table Report Category will contain the different report category
   supported by the MFI like Performance, Client Detail etc - Configuration */
insert into report_category(report_category_id,report_category_value)values
(1,'Client Detail'),
(2,'Performance'),
(3,'Center'),
(4,'Loan Product Detail'),
(5,'Status'),
(6,'Analysis'),
(7,'Miscellaneous');

/* The table Report will contain the different reports supported by the MFI
   like Client Summary etc. The report will be linked to the category
   under which it belongs - Configuration */
insert into report(report_category_id,report_name,report_identifier, activity_id, report_active)values
(1,'Collection Sheet Report','collection_sheet_report',229,1),
(6,'Branch Cash Confirmation Report','branch_cash_confirmation_report',231,1),
(6,'Branch Progress Report','branch_progress_report',232,1);

insert into report_jasper_map (report_category_id,report_name,report_identifier,report_jasper) values 
(1,'Collection Sheet Report','collection_sheet_report','CollectionSheetReport.rptdesign'),
(6,'Branch Cash Confirmation Report','branch_cash_confirmation_report','BranchCashConfirmationReport.rptdesign'),
(6,'Branch Progress Report','branch_progress_report','ProgressReport.rptdesign');

update report_category set activity_id=145 where report_category_id=1;
update report_category set activity_id=148 where report_category_id=2;
update report_category set activity_id=146 where report_category_id=3;
update report_category set activity_id=149 where report_category_id=4;
update report_category set activity_id=147 where report_category_id=5;
update report_category set activity_id=150 where report_category_id=6;
update report_category set activity_id=151 where report_category_id=7;

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(936,1,591,'Can adjust payment when account status is "closed-obligation met"');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(217,113,591,591);
insert into roles_activity values (217,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(937,1,592,'Can redo Loan disbursals');
insert into activity(activity_id,parent_id,activity_name_lookup_id,
  description_lookup_id)
  values(218,99,592,592);
insert into roles_activity values (218,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(938,1,593,'Can define Accepted Payment Type');
insert into activity(activity_id,parent_id,activity_name_lookup_id,
  description_lookup_id)
  values(219,1,593,593);
insert into roles_activity values (219,1);


update report set report_active = 1;

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(939,1,594,'Can define new report category');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(220,141,594,594);
insert into roles_activity values (220,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(940,1,595,'Can view report category');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(221,141,595,595);
insert into roles_activity values (221,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(941,1,596,'Can delete report category');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(222,141,596,596);
insert into roles_activity values (222,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(942,1,597,'Can download report template');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) values(223,141,597,597);
insert into roles_activity values (223,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(943,1,598,'Can define custom fields');
insert into activity(activity_id,parent_id,activity_name_lookup_id,
  description_lookup_id) values(224,203,598,598);
insert into roles_activity values (224,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(944,1,602,'Can upload admin documents');
insert into activity(activity_id,parent_id,activity_name_lookup_id,
  description_lookup_id) values(225,141,602,602);
insert into roles_activity values (225,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(945,1,603,'Can view admin documents');
insert into activity(activity_id,parent_id,activity_name_lookup_id,
  description_lookup_id) values(226,141,603,603);
insert into roles_activity values (226,1);

-- INSERT INTO LOOKUP_VALUE_LOCALE VALUES(947,1,605,'Can view system information');
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(227,null,605,605);
insert into roles_activity values (227,1);

insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(228,227,606,606);
insert into roles_activity values (228,1);

insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(230,203,608,608);
insert into roles_activity values (230,1);

-- For permission "Can import transaction" under Bulk catagory
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(233,196,619,619);
insert into roles_activity values (233,1);

-- For permission "Can shut down Mifos" under System Information category
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(234,227,625,625);
insert into roles_activity values (234,1);

-- For permission "Can define holiday" under Organization Management category
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(235,1,627,627);
insert into roles_activity values (235,1);

insert into lookup_value (lookup_id, entity_id, lookup_name) values(609, 76, 'FinancialAction-LoanRescheduled');
insert into lookup_value_locale(lookup_value_id, locale_id, lookup_id, lookup_value) values(951, 1, 609, null);
insert into financial_action(fin_action_id, lookup_id) values(23, 609);

insert into lookup_value (lookup_id, entity_id, lookup_name) values(610, 69, 'AccountAction-LoanRescheduled');
insert into lookup_value_locale(lookup_value_id, locale_id, lookup_id, lookup_value) values(952, 1, 610, null);
insert into account_action(account_action_id, lookup_id) values(20, 610);

/* This is for I18n in this order */
/* Iceland is inserted into country table */
insert into country(country_id,country_name,country_short_name) values(7,'Iceland','IS');
/*language*/
insert into lookup_value(lookup_id,entity_id,lookup_name) values(599,74,'Language-Icelandic');
/* Icelandic is inserted into the language table */
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(2,'Icelandic','is',599);
/* and the Iceland locale is inserted */
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(2,7,2,'Icelandic',0);

/* Spanish locale support */
insert into country(country_id,country_name,country_short_name) values(8,'Spain','ES');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(600,74,'Language-Spanish');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(3,'Spanish','es',600);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(3,8,3,'Spanish',0);

/* French locale support */
insert into country(country_id,country_name,country_short_name) values(9,'France','FR');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(601,74,'Language-French');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(4,'French','fr',601);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(4,9,4,'French',0);

/* Chinese locale support */
insert into country(country_id,country_name,country_short_name) values(10,'China','CN');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(613,74,'Language-Chinese');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(5,'Chinese','zh',613);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(5,10,5,'Chinese-China',0);

/* Swahili locale support */
insert into country(country_id,country_name,country_short_name) values
(11,'Kenya','KE'),
(12,'Tanzania','TZ'),
(13,'Uganda','UG');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(614,74,'Language-Swahili');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(6,'Swahili','sw',614);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values
(6,11,6,'Swahili-Kenya',0),
(7,12,6,'Swahili-Tanzania',0),
(8,13,6,'Swahili-Uganda',0);

/* Arabic locale support */
insert into country(country_id,country_name,country_short_name) values
(14,'Algeria','DZ'),
(15,'Bahrain','BH'),
(16,'Comoros','KM'),
(17,'Chad','TD'),
(18,'Djibouti','DJ'),
(19,'Egypt','EG'),
(20,'Eritrea','ER'),
(21,'Iraq','IQ'),
(22,'Israel','IL'),
(23,'Jordan','JO'),
(24,'Kuwait','KW'),
(25,'Lebanon','LB'),
(26,'Libyan Arab Rebublic','LY'),
(27,'Mauritania','MR'),
(28,'Morocco','MA'),
(29,'Oman','OM'),
(30,'Qatar','QA'),
(31,'Saudi Arabia','SA'),
(32,'Somalia','SO'),
(33,'Sudan','SD'),
(34,'Syrian Arab Republic','SY'),
(35,'Tunisia','TN'),
(36,'United Arab Emirates','AE'),
(37,'Yemen','YE'),
(38,'Palestinian Territory, Occupied','PS'),
(39,'Western Sahara','EH');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(615,74,'Language-Arabic');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(7,'Arabic','ar',615);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values
(9,14,7,'Arabic-Algeria',0),
(10,15,7,'Arabic-Bahrain',0),
(11,16,7,'Arabic-Comoros',0),
(12,17,7,'Arabic-Chad',0),
(13,18,7,'Arabic-Djibouti',0),
(14,19,7,'Arabic-Egypt',0),
(15,20,7,'Arabic-Eritrea',0),
(16,21,7,'Arabic-Iraq',0),
(17,22,7,'Arabic-Israel',0),
(18,23,7,'Arabic-Jordan',0),
(19,24,7,'Arabic-Kuwait',0),
(20,25,7,'Arabic-Lebanon',0),
(21,26,7,'Arabic-Libyan Arab Rebublic',0),
(22,27,7,'Arabic-Mauritania',0),
(23,28,7,'Arabic-Morocco',0),
(24,29,7,'Arabic-Oman',0),
(25,30,7,'Arabic-Qatar',0),
(26,31,7,'Arabic-Saudi Arabia',0),
(27,32,7,'Arabic-Somalia',0),
(28,33,7,'Arabic-Sudan',0),
(29,34,7,'Arabic-Syrian Arab Republic',0),
(30,35,7,'Arabic-Tunisia',0),
(31,36,7,'Arabic-United Arab Emirates',0),
(32,37,7,'Arabic-Yemen',0),
(33,38,7,'Arabic-Palestinian Territory, Occupied',0),
(34,39,7,'Arabic-Western Sahara',0);

/* Portuguese locale support */
insert into country(country_id,country_name,country_short_name) values
(40,'Angola','AO'),
(41,'Brazil','BR'),
(42,'Cape Verde','CV'),
(43,'Guinea-Bissau','GW'),
(44,'Equatorial Guinea','GQ'),
(45,'Macau','MO'),
(46,'Mozambique','MZ'),
(47,'Portugal','PT'),
(48,'Sao Tome and Principe','ST');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(616,74,'Language-Portuguese');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(8,'Portuguese','pt',616);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values
(35,40,8,'Portuguese-Angola',0),
(36,41,8,'Portuguese-Brazil',0),
(37,42,8,'Portuguese-Cape Verde',0),
(38,43,8,'Portuguese-Guinea-Bissau',0),
(39,44,8,'Portuguese-Equatorial Guinea',0),
(40,45,8,'Portuguese-Macau',0),
(41,46,8,'Portuguese-Mozambique',0),
(42,47,8,'Portuguese-Portugal',0),
(43,48,8,'Portuguese-Sao Tome and Principe',0);

/* Khmer locale support */
insert into country(country_id,country_name,country_short_name) values(49,'Cambodia','KH');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(617,74,'Language-Khmer');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(9,'Khmer','km',617);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(44,49,9,'Khmer-Cambodia',0);

/* Lao locale support */
insert into country(country_id,country_name,country_short_name) values(50,'Laos','LA');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(618,74,'Language-Lao');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(10,'Lao','lo',618);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(45,50,10,'Lao-Laos',0);

/* Al Majmoua custom English strings */
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(46,25,1,'English-Lebanon-AlMajmoua',0);

/* Hungarian/Hungary locale support */
insert into country(country_id,country_name,country_short_name) values(51,'Hungary','HU');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(624,74,'Language-Hungarian');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(11,'Hungarian','hu',624);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(47,51,11,'Hungarian-Hungary',0);

/* Upgrade 255,256 START*/
insert into lookup_value (lookup_id, entity_id, lookup_name) values 
(628,87,'Permissions.CanViewDetailedAgingOfPortfolioAtRiskReport'),
(629,87,'Permissions.CanViewGeneralLedgerReport');
insert into lookup_value_locale (lookup_value_id, locale_id, lookup_id, lookup_value) values
(958,1,628,'Can View Detailed Aging Of Portfolio At Risk Report'),
(959,1,629,'Can View General Ledger Report');
insert into activity (activity_id, parent_id, activity_name_lookup_id, description_lookup_id) values
(236,150,628,628),
(237,150,629,629);
insert into roles_activity (activity_id, role_id) values
(236,1),
(237,1);
insert into report (report_id, report_category_id, report_name, report_identifier, activity_id, report_active) values 
(4,6,'Detailed Aging Of Portfolio At Risk Report','detailed_aging_of_portfolio_at_risk_report',236,1),
(5,6,'General Ledger Report','general_ledger_report',237,1);
insert into report_jasper_map (report_id, report_category_id, report_name, report_identifier, report_jasper) values 
(4,6,'Detailed Aging Of Portfolio At Risk Report','detailed_aging_of_portfolio_at_risk_report','DetailedAgingPortfolioAtRiskReport.rptdesign'),
(5,6,'General Ledger Report','general_ledger_report','GeneralLedgerReport.rptdesign');
/* Upgrade 255,256 END*/