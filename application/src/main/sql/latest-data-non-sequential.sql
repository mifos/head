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

insert into database_version(database_version) values(212);

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
insert into currency(currency_id,currency_name,display_symbol,rounding_mode,rounding_amount,default_currency,default_digits_after_decimal,currency_code)
values(1,'US Dollar','$',1,.5,0,1,'USD');
insert into currency(currency_id,currency_name,display_symbol,rounding_mode,rounding_amount,default_currency,default_digits_after_decimal,currency_code)	
values(2,'Indian Rupee','Rs',1,1,1,1,'INR');
insert into currency(currency_id,currency_name,display_symbol,rounding_mode,rounding_amount,default_currency,default_digits_after_decimal,currency_code)
values(3,'Euro','',2,.5,0,1,'EUR');
insert into currency(currency_id,currency_name,display_symbol,rounding_mode,rounding_amount,default_currency,default_digits_after_decimal,currency_code)
values(4,'Pound Sterling','',1,.5,0,1,'GBP');

/* More ISO currencies.
 * 
 * For the following rows, DEFAULT_DIGITS_AFTER_DECIMAL values are just guesses
 * and might very well be incorrect. */ 
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (5  , '', 0, 'AED', 2, 'United Arab Emirates dirham');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (6  , '', 0, 'AFN', 2, 'Afghani');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (7  , '', 0, 'ALL', 2, 'Lek');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (8  , '', 0, 'AMD', 2, 'Armenian dram');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (9  , '', 0, 'ANG', 2, 'Netherlands Antillean guilder');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (10 , '', 0, 'AOA', 2, 'Kwanza');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (11 , '', 0, 'ARS', 2, 'Argentine peso');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (12 , '', 0, 'AUD', 2, 'Australian dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (13 , '', 0, 'AWG', 2, 'Aruban guilder');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (14 , '', 0, 'AZN', 2, 'Azerbaijanian manat');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (15 , '', 0, 'BAM', 2, 'Convertible marks');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (16 , '', 0, 'BBD', 2, 'Barbados dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (17 , '', 0, 'BDT', 2, 'Bangladeshi taka');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (18 , '', 0, 'BGN', 2, 'Bulgarian lev');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (19 , '', 0, 'BHD', 3, 'Bahraini dinar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (20 , '', 0, 'BIF', 0, 'Burundian franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (21 , '', 0, 'BMD', 2, 'Bermudian dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (22 , '', 0, 'BND', 2, 'Brunei dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (23 , '', 0, 'BOB', 2, 'Boliviano');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (24 , '', 0, 'BOV', 2, 'Bolivian Mvdol');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (25 , '', 0, 'BRL', 2, 'Brazilian real');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (26 , '', 0, 'BSD', 2, 'Bahamian dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (27 , '', 0, 'BTN', 2, 'Ngultrum');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (28 , '', 0, 'BWP', 2, 'Pula');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (29 , '', 0, 'BYR', 0, 'Belarussian ruble');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (30 , '', 0, 'BZD', 2, 'Belize dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (31 , '', 0, 'CAD', 2, 'Canadian dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (32 , '', 0, 'CDF', 2, 'Franc Congolais');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (33 , '', 0, 'CHE', 2, 'WIR euro');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (34 , '', 0, 'CHF', 2, 'Swiss franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (35 , '', 0, 'CHW', 2, 'WIR franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (36 , '', 0, 'CLF', 0, 'Unidad de Fomento');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (37 , '', 0, 'CLP', 0, 'Chilean peso');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (38 , '', 0, 'CNY', 2, 'Renminbi');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (39 , '', 0, 'COP', 2, 'Colombian peso');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (40 , '', 0, 'COU', 2, 'Unidad de Valor Real');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (41 , '', 0, 'CRC', 2, 'Costa Rican colon');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (42 , '', 0, 'CUP', 2, 'Cuban peso');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (43 , '', 0, 'CVE', 2, 'Cape Verde escudo');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (44 , '', 0, 'CZK', 2, 'Czech koruna');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (45 , '', 0, 'DJF', 0, 'Djibouti franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (46 , '', 0, 'DKK', 2, 'Danish krone');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (47 , '', 0, 'DOP', 2, 'Dominican peso');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (48 , '', 0, 'DZD', 2, 'Algerian dinar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (49 , '', 0, 'EEK', 2, 'Kroon');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (50 , '', 0, 'EGP', 2, 'Egyptian pound');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (51 , '', 0, 'ERN', 2, 'Nakfa');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (52 , '', 0, 'ETB', 2, 'Ethiopian birr');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (53 , '', 0, 'FJD', 2, 'Fiji dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (54 , '', 0, 'FKP', 2, 'Falkland Islands pound');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (55 , '', 0, 'GEL', 2, 'Lari');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (56 , '', 0, 'GHS', 2, 'Cedi');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (57 , '', 0, 'GIP', 2, 'Gibraltar pound');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (58 , '', 0, 'GMD', 2, 'Dalasi');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (59 , '', 0, 'GNF', 0, 'Guinea franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (60 , '', 0, 'GTQ', 2, 'Quetzal');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (61 , '', 0, 'GYD', 2, 'Guyana dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (62 , '', 0, 'HKD', 2, 'Hong Kong dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (63 , '', 0, 'HNL', 2, 'Lempira');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (64 , '', 0, 'HRK', 2, 'Croatian kuna');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (65 , '', 0, 'HTG', 2, 'Haiti gourde');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (66 , '', 0, 'HUF', 2, 'Forint');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (67 , '', 0, 'IDR', 2, 'Rupiah');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (68 , '', 0, 'ILS', 2, 'Israeli new sheqel');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (69 , '', 0, 'IQD', 3, 'Iraqi dinar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (70 , '', 0, 'IRR', 2, 'Iranian rial');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (71 , '', 0, 'ISK', 0, 'Iceland krona');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (72 , '', 0, 'JMD', 2, 'Jamaican dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (73 , '', 0, 'JOD', 3, 'Jordanian dinar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (74 , '', 0, 'JPY', 0, 'Japanese yen');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (75 , '', 0, 'KES', 2, 'Kenyan shilling');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (76 , '', 0, 'KGS', 2, 'Som');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (77 , '', 0, 'KHR', 2, 'Riel');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (78 , '', 0, 'KMF', 0, 'Comoro franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (79 , '', 0, 'KPW', 2, 'North Korean won');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (80 , '', 0, 'KRW', 0, 'South Korean won');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (81 , '', 0, 'KWD', 3, 'Kuwaiti dinar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (82 , '', 0, 'KYD', 2, 'Cayman Islands dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (83 , '', 0, 'KZT', 2, 'Tenge');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (84 , '', 0, 'LAK', 2, 'Kip');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (85 , '', 0, 'LBP', 2, 'Lebanese pound');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (86 , '', 0, 'LKR', 2, 'Sri Lanka rupee');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (87 , '', 0, 'LRD', 2, 'Liberian dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (88 , '', 0, 'LSL', 2, 'Loti');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (89 , '', 0, 'LTL', 2, 'Lithuanian litas');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (90 , '', 0, 'LVL', 2, 'Latvian lats');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (91 , '', 0, 'LYD', 3, 'Libyan dinar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (92 , '', 0, 'MAD', 2, 'Moroccan dirham');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (93 , '', 0, 'MDL', 2, 'Moldovan leu');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (94 , '', 0, 'MGA', 0, 'Malagasy ariary');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (95 , '', 0, 'MKD', 2, 'Denar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (96 , '', 0, 'MMK', 2, 'Kyat');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (97 , '', 0, 'MNT', 2, 'Tugrik');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (98 , '', 0, 'MOP', 2, 'Pataca');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (99 , '', 0, 'MRO', 0, 'Ouguiya');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (100, '', 0, 'MUR', 2, 'Mauritius rupee');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (101, '', 0, 'MVR', 2, 'Rufiyaa');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (102, '', 0, 'MWK', 2, 'Kwacha');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (103, '', 0, 'MXN', 2, 'Mexican peso');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (104, '', 0, 'MXV', 2, 'Mexican Unidad de Inversion');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (105, '', 0, 'MYR', 2, 'Malaysian ringgit');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (106, '', 0, 'MZN', 2, 'Metical');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (107, '', 0, 'NAD', 2, 'Namibian dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (108, '', 0, 'NGN', 2, 'Naira');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (109, '', 0, 'NIO', 2, 'Cordoba oro');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (110, '', 0, 'NOK', 2, 'Norwegian krone');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (111, '', 0, 'NPR', 2, 'Nepalese rupee');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (112, '', 0, 'NZD', 2, 'New Zealand dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (113, '', 0, 'OMR', 3, 'Rial Omani');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (114, '', 0, 'PAB', 2, 'Balboa');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (115, '', 0, 'PEN', 2, 'Nuevo sol');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (116, '', 0, 'PGK', 2, 'Kina');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (117, '', 0, 'PHP', 2, 'Philippine peso');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (118, '', 0, 'PKR', 2, 'Pakistan rupee');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (119, '', 0, 'PLN', 2, 'Zloty');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (120, '', 0, 'PYG', 0, 'Guarani');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (121, '', 0, 'QAR', 2, 'Qatari rial');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (122, '', 0, 'RON', 2, 'Romanian new leu');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (123, '', 0, 'RSD', 2, 'Serbian dinar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (124, '', 0, 'RUB', 2, 'Russian rouble');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (125, '', 0, 'RWF', 0, 'Rwanda franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (126, '', 0, 'SAR', 2, 'Saudi riyal');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (127, '', 0, 'SBD', 2, 'Solomon Islands dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (128, '', 0, 'SCR', 2, 'Seychelles rupee');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (129, '', 0, 'SDG', 2, 'Sudanese pound');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (130, '', 0, 'SEK', 2, 'Swedish krona');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (131, '', 0, 'SGD', 2, 'Singapore dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (132, '', 0, 'SHP', 2, 'Saint Helena pound');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (133, '', 0, 'SKK', 2, 'Slovak koruna');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (134, '', 0, 'SLL', 2, 'Leone');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (135, '', 0, 'SOS', 2, 'Somali shilling');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (136, '', 0, 'SRD', 2, 'Surinam dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (137, '', 0, 'STD', 2, 'Dobra');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (138, '', 0, 'SYP', 2, 'Syrian pound');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (139, '', 0, 'SZL', 2, 'Lilangeni');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (140, '', 0, 'THB', 2, 'Baht');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (141, '', 0, 'TJS', 2, 'Somoni');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (142, '', 0, 'TMM', 2, 'Manat');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (143, '', 0, 'TND', 3, 'Tunisian dinar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (144, '', 0, 'TOP', 2, 'Pa''anga');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (145, '', 0, 'TRY', 2, 'New Turkish lira');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (146, '', 0, 'TTD', 2, 'Trinidad and Tobago dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (147, '', 0, 'TWD', 2, 'New Taiwan dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (148, '', 0, 'TZS', 2, 'Tanzanian shilling');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (149, '', 0, 'UAH', 2, 'Hryvnia');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (150, '', 0, 'UGX', 2, 'Uganda shilling');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (151, '', 0, 'USN', 2, 'US dollar (next day)');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (152, '', 0, 'USS', 2, 'US dollar (same day)');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (153, '', 0, 'UYU', 2, 'Peso Uruguayo');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (154, '', 0, 'UZS', 2, 'Uzbekistan som');
/* the following two rows are supposed to contain UTF-8 characters, but this
 * breaks the unit tests. See
 * http://article.gmane.org/gmane.comp.finance.mifos.devel/4991 */
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (155, '', 0, 'VEF', 2, 'Venezuelan Bolivares Fuertes');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (156, '', 0, 'VND', 2, 'Vietnamese Dong');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (157, '', 0, 'VUV', 0, 'Vatu');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (158, '', 0, 'WST', 2, 'Samoan tala');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (159, '', 0, 'XAF', 0, 'CFA franc BEAC');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (160, '', 0, 'XAG', 0, 'Silver');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (161, '', 0, 'XAU', 0, 'Gold');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (162, '', 0, 'XBA', 0, 'European Composite Unit');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (163, '', 0, 'XBB', 0, 'European Monetary Unit');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (164, '', 0, 'XBC', 0, 'European Unit of Account 9');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (165, '', 0, 'XBD', 0, 'European Unit of Account 17');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (166, '', 0, 'XCD', 2, 'East Caribbean dollar');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (167, '', 0, 'XDR', 0, 'Special Drawing Rights');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (168, '', 0, 'XFU', 0, 'UIC franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (169, '', 0, 'XOF', 0, 'CFA Franc BCEAO');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (170, '', 0, 'XPD', 0, 'Palladium');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (171, '', 0, 'XPF', 0, 'CFP franc');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (172, '', 0, 'XPT', 0, 'Platinum');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (173, '', 0, 'XTS', 0, 'Code reserved for testing purposes');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (174, '', 0, 'XXX', 0, 'No currency');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (175, '', 0, 'YER', 2, 'Yemeni rial');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (176, '', 0, 'ZAR', 2, 'South African rand');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (177, '', 0, 'ZMK', 2, 'Kwacha');
insert into currency (currency_id, display_symbol, default_currency, currency_code, default_digits_after_decimal, currency_name) values (178, '', 0, 'ZWD', 2, 'Zimbabwe dollar');

/* Updated lookup_entity */
/* The table LOOKUP_ENTITY will contain the entry for entities which 
need to be configured like center to be called kendra by mfi, 
ethnicity to be called something by the MFI, 
salutation to be called something by the MFI etc - Configuration */
insert into lookup_entity(entity_id,entity_name,description)
values(1,'ClientStatus','Client Status');
insert into lookup_entity(entity_id,entity_name,description)
values(2,'GroupStatus','Group Status');
insert into lookup_entity(entity_id,entity_name,description)
values(3,'CenterStatus','Center Status');
insert into lookup_entity(entity_id,entity_name,description)
values(4,'OfficeStatus','Office Status');
insert into lookup_entity(entity_id,entity_name,description)
values(5,'AccountState','Account States');
/* duplicate that appears to be unused */
insert into lookup_entity(entity_id,entity_name,description)
values(6,'PersonnelStatusUnused','Personnel Status (Unused)');

insert into lookup_entity(entity_id,entity_name,description)
values(7,'GroupFlag','Group Flag');
insert into lookup_entity(entity_id,entity_name,description)
values(8,'FeeType','Fee Type');
insert into lookup_entity(entity_id,entity_name,description)
values(9,'Titles','Customer Position');
insert into lookup_entity(entity_id,entity_name,description)
values(10,'PovertyStatus','Poverty Status For Client');
insert into lookup_entity(entity_id,entity_name,description)
values(11,'Center','Center VALUES');
insert into lookup_entity(entity_id,entity_name,description)
values(12,'Group','Group VALUES');
insert into lookup_entity(entity_id,entity_name,description)
values(13,'Client','Client VALUES');
insert into lookup_entity(entity_id,entity_name,description)
values(14,'Office','Office');
insert into lookup_entity(entity_id,entity_name,description)
values(15,'Salutation','Mr/Mrs');
insert into lookup_entity(entity_id,entity_name,description)
values(16,'Gender','Male/Female');
insert into lookup_entity(entity_id,entity_name,description)
values(17,'MaritalStatus','Married/UnMarried');
insert into lookup_entity(entity_id,entity_name,description)
values(18,'Citizenship','Citizenship');
insert into lookup_entity(entity_id,entity_name,description)
values(19,'Ethinicity','Ethnicity');
insert into lookup_entity(entity_id,entity_name,description)
values(20,'EducationLevel','EducationLevel');
insert into lookup_entity(entity_id,entity_name,description)
values(21,'BusinessActivities','BusinessActivities');
insert into lookup_entity(entity_id,entity_name,description)
values(22,'Handicapped','Handicaped');
insert into lookup_entity(entity_id,entity_name,description)
values(23,'ClientFormedBy','CustomField ClientFormedBy for client');
insert into lookup_entity(entity_id,entity_name,description)
values(24,'PostalCode','ZipCode');
insert into lookup_entity(entity_id,entity_name,description)
values(25,'ProductState','Product State');
insert into lookup_entity(entity_id,entity_name,description)
values(26,'Loan','Loan');
insert into lookup_entity(entity_id,entity_name,description)
values(27,'Savings','Savings');

insert into lookup_entity(entity_id,entity_name,description)
values(29,'PersonnelTitles','CFO/Accountant');
insert into lookup_entity(entity_id,entity_name,description)
values(30,'PersonnelLevels','LoanOfficer/NonLoanOfficer');

insert into lookup_entity(entity_id,entity_name,description)
values(34,'OfficeLevels','Head Office/Regional Office/Sub Regional Office/Area Office/BranchOffice');
insert into lookup_entity(entity_id,entity_name,description)
values(35,'PrdApplicableMaster','Ceratin product categories applicable to certain types of clients');
insert into lookup_entity(entity_id,entity_name,description)
values(36,'WeekDays','Week Days List');
insert into lookup_entity(entity_id,entity_name,description)
values(37,'InterestTypes','Interest Types for PrdOfferings and Accounts');
insert into lookup_entity(entity_id,entity_name,description)
values(38,'CategoryType','This is mainly used in fees to show the categories where this fee is applicable');
insert into lookup_entity(entity_id,entity_name,description)
values(39,'InterestCalcRule','Interest calculation rule for loan prd offerings');

insert into lookup_entity(entity_id,entity_name,description)
values(41,'GracePeriodTypes','Grace Period Types for loan products');
insert into lookup_entity(entity_id,entity_name,description)
values(42,'DayRank','Day Rank');
insert into lookup_entity(entity_id,entity_name,description)
values(43,'CollateralTypes','Collateral Types for loan accounts');
insert into lookup_entity(entity_id,entity_name,description)
values(44,'OfficeCode','Office Code');
insert into lookup_entity(entity_id,entity_name,description)
values(45,'ProductCategoryStatus','ProductCategoryStatus');
insert into lookup_entity(entity_id,entity_name,description)
values(46,'ProductStatus','ProductStatus');
insert into lookup_entity(entity_id,entity_name,description)
values(47,'SavingsType','SavingsType');
insert into lookup_entity(entity_id,entity_name,description)
values(48,'RecommendedAmtUnit','RecommendedAmtUnit');
insert into lookup_entity(entity_id,entity_name,description)
values(49,'IntCalTypes','IntCalTypes');
insert into lookup_entity(entity_id,entity_name,description)
values(50,'YESNO','YESNO');
insert into lookup_entity(entity_id,entity_name,description)
values(51,'AccountType','AccountType');
insert into lookup_entity(entity_id,entity_name,description)
values(52,'SpouseFather','SpouseFather');
insert into lookup_entity(entity_id,entity_name,description)
values(53,'CustomerStatus','CustomerStatus');
insert into lookup_entity(entity_id,entity_name,description)
values(54,'FeePayment','FeePayment');
insert into lookup_entity(entity_id,entity_name,description)
values(55,'FeeFormulaMaster','FeeFormulaMaster');
insert into lookup_entity(entity_id,entity_name,description)
values(56,'PersonnelStatus','PersonnelStatus');
insert into lookup_entity(entity_id,entity_name,description)
values(57,'Personnel','Personnel');

/* 2007-12-08 Default custom field entities 58-67 were removed */ 
/* except for 62, which is also used for non-custom fields */  

insert into lookup_entity(entity_id,entity_name,description)
values(62,'ExternalId','External ID');

insert into lookup_entity(entity_id,entity_name,description)
values(68,'FeeStatus','FeeStatus');
insert into lookup_entity(entity_id,entity_name,description)
values(69,'AccountAction','AccountAction');
insert into lookup_entity(entity_id,entity_name,description)
values(70,'AccountFlags','AccountFlags');
insert into lookup_entity(entity_id,entity_name,description)
values(71,'PaymentType','PaymentType');
insert into lookup_entity(entity_id,entity_name,description)
values(72,'SavingsStatus','Saving Status');
insert into lookup_entity(entity_id,entity_name,description)
values(73,'Position','Position');
insert into lookup_entity(entity_id,entity_name,description)
values(74,'Language','Language');
insert into lookup_entity 
values(75,'CustomerAttendanceType','CustomerAttendanceType');
insert into lookup_entity 
values(76,'FinancialAction','Financial Action');
insert into lookup_entity 
values(77,'BulkEntry','BulkEntry');
insert into lookup_entity 
values(78,'SavingsAccountFlag','SavingsAccountFlag');
insert into lookup_entity 
values(79,'Address3','Address3');
insert into lookup_entity 
values(80,'City','City');
insert into lookup_entity 
values(81,'Interest','Interest');
insert into lookup_entity 
values(82,'LoanPurposes','Loan Purposes');
insert into lookup_entity 
values(83,'State','State');
insert into lookup_entity 
values(84,'Address1','Address1');
insert into lookup_entity 
values(85,'Address2','Address2');
insert into lookup_entity 
values(86,'GovernmentId','GovernmentId');
insert into lookup_entity 
values(87,'Permissions','Permissions');
insert into lookup_entity(entity_id,entity_name,description)
values(88,'ServiceCharge','Interest');

insert into lookup_entity 
values(89,'feeUpdationType',' fee updation can to applied to existing accounts or future accounts');
insert into lookup_entity(entity_id,entity_name,description)
values(90,'FeeFrequency','Fee Frequency');

insert into lookup_entity(entity_id,entity_name,description)
values(91,'RepaymentRule','Repayment Rule Types');


/* fee_type */
insert into fee_type(fee_type_id,fee_lookup_id,flat_or_rate,formula)
values(1,1,null,null);
insert into fee_type(fee_type_id,fee_lookup_id,flat_or_rate,formula)
values(2,1,null,null);
insert into fee_type(fee_type_id,fee_lookup_id,flat_or_rate,formula)
values(3,2,null,null);
insert into fee_type(fee_type_id,fee_lookup_id,flat_or_rate,formula)
values(4,3,null,null);
insert into fee_type(fee_type_id,fee_lookup_id,flat_or_rate,formula)
values(5,3,null,null);

/* MEETING_TYPE */
/* The table MEETING_TYPE indicates to what the meeting is associated against like loan,customer etc - System */
insert into meeting_type(meeting_type_id,meeting_purpose,description)
values(1,'LOANFREQUENCYOFINSTALLMENTS','Loan Frequency of istalments');
insert into meeting_type(meeting_type_id,meeting_purpose,description) 
values(2,'SAVINGSTIMEPERFORINTCALC','Savings Time Period for Interest Calculation');
insert into meeting_type(meeting_type_id,meeting_purpose,description) 
values(3,'SAVINGSFRQINTPOSTACC','Savings Frequency of Interest Posting to Accounts');
insert into meeting_type(meeting_type_id,meeting_purpose,description) 
values(4,'CUSTOMERMEETING','Customer Meeting');
insert into meeting_type(meeting_type_id,meeting_purpose,description) 
values(5,'FEEMEETING','Fees Meetings');

/*recurrence_type*/
/* The table RECURRENCE_TYPE indicates the type of meeting like daily,weekly,monthly - System */
insert into recurrence_type(recurrence_id,recurrence_name,description)
values(1,'Week(s)','Weekly Recurrence');
insert into recurrence_type(recurrence_id,recurrence_name,description)
values(2,'Month(s)','Monthly Recurrence');
insert into recurrence_type(recurrence_id,recurrence_name,description)
values(3,'Day(s)','Daily Recurrence');

/* The table COUNTRY will contain information related to the country like country name etc - Configuration */
insert into country(country_id,country_name,country_short_name)
values(1,'America','US');
insert into country(country_id,country_name,country_short_name)
values(2,'India','IN');
insert into country(country_id,country_name,country_short_name)
values(3,'Spain','ES');
insert into country(country_id,country_name,country_short_name)
values(4,'England','ENG');
insert into country(country_id,country_name,country_short_name)
values(5,'South Africa','SA');
insert into country(country_id,country_name,country_short_name)
values(6,'United Kingdom','GB');

/* The table Office_Level contains the different levels supported by the MFI like HeadOffice,RegionalOffice etc  */
/* Office Levels can be configured in the UI, so there is no need to make changes in this section when configuring Mifos  */
insert into office_level(level_id,parent_level_id,level_name_id,interaction_flag,configured)
values(1,null,104,0,1);
insert into office_level(level_id,parent_level_id,level_name_id,interaction_flag,configured)
values(2,1,105,0,1);
insert into office_level(level_id,parent_level_id,level_name_id,interaction_flag,configured)
values(3,2,106,0,1);
insert into office_level(level_id,parent_level_id,level_name_id,interaction_flag,configured)
values(4,3,107,0,1);
insert into office_level(level_id,parent_level_id,level_name_id,interaction_flag,configured)
values(5,4,108,1,1);

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
-- Entity: WeekDays
insert into lookup_value(lookup_id,entity_id,lookup_name) values(72, 36, 'WeekDays-Sunday');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(143, 1, 72, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(73, 36, 'WeekDays-Monday');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(145, 1, 73, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(74, 36, 'WeekDays-Tuesday');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(147, 1, 74, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(75, 36, 'WeekDays-Wednesday');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(149, 1, 75, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(76, 36, 'WeekDays-Thursday');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(151, 1, 76, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(77, 36, 'WeekDays-Friday');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(153, 1, 77, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(78, 36, 'WeekDays-Saturday');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(155, 1, 78, null);
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
-- Entity: DayRank
insert into lookup_value(lookup_id,entity_id,lookup_name) values(99, 42, 'DayRank-First');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(179, 1, 99, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(100, 42, 'DayRank-Second');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(181, 1, 100, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(101, 42, 'DayRank-Third');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(183, 1, 101, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(102, 42, 'DayRank-Fourth');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(185, 1, 102, null);
insert into lookup_value(lookup_id,entity_id,lookup_name) values(103, 42, 'DayRank-Last');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
values(187, 1, 103, null);
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

-- Potentially unused or orphaned lookup_values
-- Keep them here for a while and if everything is ok, they will be removed
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(65, 34, 'DBUpgrade.OfficeLevels.Unsued');
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(71, 35, 'DBUpgrade.PrdApplicableMaster.Unused');
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(90, 39, 'DBUpgrade.InterestCalcRule.Unused');
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(223, 79, 'DBUpgrade.Address3.Unused');
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(224, 80, 'DBUpgrade.City.Unused');
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(259, 82, 'DBUpgrade.LoanPurposes1.Unused');
-- INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) VALUES(263, 82, 'DBUpgrade.LoanPurposes2.Unused');


/*Personnel Level*/
/* The table personnel level will contain the personnel hierarchy supported by the MFI(This feature is currently not supported) - Configuration */
insert into personnel_level(level_id, parent_level_id, level_name_id, interaction_flag)
values(1,1,60,0);
insert into personnel_level(level_id, parent_level_id, level_name_id, interaction_flag)
values(2,1,61,0);


/* The table LOOKUP_LABEL will contain the configuration information 
for the LOOKUP_ENTITY defined,like salutation entity could have 
mr,mrs etc - Configuration */
/* Client Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(1,1,1,null);
/* Group Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(3,2,1,null);
/* Center Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(5,3,1,null);
/* Office Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(7,4,1,null);
/* Loan Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(9,5,1,null);
/* Personnel Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(11,6,1,null);
/* Group Flag */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(13,7,1,null);
/* Fee Type */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(15,8,1,null);
/* Titles */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(17,9,1,null);
/* Poverty Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(19,10,1,null);
/* Center */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(21,11,1,null);
/* Group */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(23,12,1,null)
/* 'Client' */;
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(25,13,1,null); 
/* Office */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(27,14,1,null);
/* Salutation */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(29,15,1,null);
/* Gender */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(31,16,1,null);
/* MartialStatus */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(33,17,1,null);
/* Citizenship */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(35,18,1,null);
/* Ethnicity */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(37,19,1,null);
/* EducationLevel */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(39,20,1,null);
/* Occupation */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(41,21,1,null);
/* Handicapped */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(43,22,1,null);
/* Postal Code */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(47,24,1,null);
/* Product State */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(49,25,1,null);
/* Loan */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(51,26,1,null);
/* Savings */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(53,27,1,null);
/* User Title */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(57,29,1,null);
/* User Hierarchy */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(59,30,1,null);
/* OfficeLevel */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(67,34,1,null);
/* PrdApplicableMaster */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(69,35,1,null);
/* Week Days */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(71,36,1,null);
/* Days Rank */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(73,42,1,null);
/* InterestTypes */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(75,37,1,null);
/* CategoryTypes */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(76,38,1,null);
/* InterestCalcRule */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(77,39,1,null);

/* GracePeriodTypes */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(79,41,1,null);
/* CollateralTypes */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(80,43,1,null);
/* Office Code */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(81,44,1,null);
/* Product Category Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(83,45,1,null);
/* Product Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(85,46,1,null);
/* SAVINGS TYPE */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(87,47,1,null);
/* REC AMNT UNIT */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(89,48,1,null);
/* INT CALC TYPES */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(91,49,1,null);
/* YES/NO */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(93,50,1,null);
/* Account Type */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(95,51,1,null);
/* Spouse/Father */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(97,52,1,null);
/* Customer Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(99,53,1,null);
/* Fee Payment */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(100,54,1,null);
/* Fee Formula Master */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(102,55,1,null);
/* Personnel Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(104,56,1,null);
/* Personnel */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(106,57,1,null);

/* 2007-12-18 Custom field labels 108-126 removed */
/* except for 116 which is also used by non-custom fields */

/* External Id */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(116,62,1,null);

/* Fee Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(128,68,1,null);
/* Account Action */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(130,69,1,null);
/* AccountFlags */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(132,70,1,null);
/* PaymentType */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(134,71,1,null);

/* Savings Status */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(136,72,1,null);


/*language*/
/* Language */
insert into lookup_label(label_id,entity_id,locale_id,entity_name)
values(151,74,1,null);
/* CustomerAttendance */
insert into lookup_label 
values(154,75,1,null);
/* Financial Action */
insert into lookup_label 
values(156,76,1,null);
/* Bulk entry */
insert into lookup_label 
values(158,77,1,null);
/* Address 3 */
insert into lookup_label 
values(160,79,1,null);
/* City/District */
insert into lookup_label 
values(162,80,1,null);
/* Interest */
insert into lookup_label 
values(164,81,1,null);
/* Loan Purposes */
insert into lookup_label 
values(166,82,1,null);
/* State */
insert into lookup_label 
values(167,83,1,null);
/* Address1 */
insert into lookup_label 
values(168,84,1,null);
/* Address2 */
insert into lookup_label 
values(169,85,1,null);
/* Government ID */
insert into lookup_label 
values(170,86,1,null);
/* Permissions */
insert into lookup_label 
values(171,87,1,null);
/* Interest */
insert into lookup_label
values(172,88,1,null);


/*Category Types*/
/*The table CATEGORY_TYPE is Used for defining applicablity of the fees--System */
insert into category_type(category_id,category_lookup_id)
values(1,81);
insert into category_type(category_id,category_lookup_id)
values(2,82);
insert into category_type(category_id,category_lookup_id)
values(3,83);
insert into category_type(category_id,category_lookup_id)
values(4,84);
insert into category_type(category_id,category_lookup_id)
values(5,86);

/* The table PRD_TYPE will contain the configuration of lateness days, dormancy days associated to loan/saving product of the MFI - Configuration */
insert into prd_type(prd_type_id,prd_type_lookup_id,lateness_days,dormancy_days,version_no)
values(1,54,10,1,1);
insert into prd_type(prd_type_id,prd_type_lookup_id,lateness_days,dormancy_days,version_no)
values(2,55,12,30,1);

/* The Table PRD_CATEGORY contains the differnt product categories like loan , savings supported by the system - System */
insert into prd_category(prd_category_id,prd_type_id,global_prd_offering_num,prd_category_name ,created_date,created_by,office_id,updated_by,udpated_date,state,description,version_no)
values(1,1,'1-1','Other',null,null,null,null,null,1,null,1);
insert into prd_category(prd_category_id,prd_type_id,global_prd_offering_num,prd_category_name ,created_date,created_by,office_id,updated_by,udpated_date,state,description,version_no)
values(2,2,'1-2','Other',null,null,null,null,null,1,null,1);


insert into office_code(code_id,lookup_id)
values(1,111);
insert into office_code(code_id,lookup_id)
values(2,112);

/* The PRD_CATEGORY_STATUS table contains the different product category status supported by the product category like active,inactive etc - System */
insert into prd_category_status(prd_category_status_id,lookup_id) 
values(0,113);

-- At first glance, this appears to just be updating a row to what it already
-- is.  But what does it really do?  Something about auto-increments or
-- something?  It is needed to make MySQL load the file.
update prd_category_status set prd_category_status_id=0 where lookup_id=113;

insert into prd_category_status(prd_category_status_id,lookup_id)
values(1,114);

/* The PRD_STATE table contains the different states supported for the product types like active,inactive etc - System */
insert into prd_state(prd_state_id,prd_state_lookup_id)
values(1,115);
insert into prd_state(prd_state_id,prd_state_lookup_id)
values(2,116);

/* The table Savings type contains information like Mandatory, Voluntary related to savings - System */
insert into savings_type(savings_type_id ,lookup_id)
values(1,118);
insert into savings_type(savings_type_id ,lookup_id)
values(2,119);

/* The table Recommended Amount which contains information used in group savings account holding values like complete group , per individual - System */
insert into recommended_amnt_unit(recommended_amnt_unit_id,lookup_id)
values(1,120); 
insert into recommended_amnt_unit(recommended_amnt_unit_id,lookup_id)
values(2,121);

/* The table Interest Calculation Types contains calculation types for savings like minimum balance , average balance - System */
insert into interest_calculation_types(interest_calculation_type_id,interest_calculation_lookup_id,description)
values(1,122,null);
insert into interest_calculation_types(interest_calculation_type_id,interest_calculation_lookup_id,description)
values(2,123,null); 

insert into yes_no_master(yes_no_master_id,lookup_id )
values(1,124); 
insert into yes_no_master(yes_no_master_id,lookup_id )
values(2,125); 

/* The table account type contains the account types supported by the system like Loan , savings, customer - System */
insert into account_type(account_type_id,lookup_id,description)
values(1,126,'Loan Account');
insert into account_type(account_type_id,lookup_id,description)
values(2,127,'Savings Account');
insert into account_type(account_type_id,lookup_id,description)
values(3,140,'Customer Account');
-- Add an Individual Loan Account Type (A virtual Loan Account)
insert into account_type(account_type_id,lookup_id,description)
values(4,126,'Individual Loan Account');

/* The table SPOUSE_FATHER_LOOKUP contains the spouse father data which is used in display - System */ 
insert into spouse_father_lookup(spouse_father_id,lookup_id)
values(1,128);
insert into spouse_father_lookup(spouse_father_id,lookup_id)
values(2,129);

/* The table Fee Payment Type contains the applicability of fee like time of disburesement,time of first installment etc - System */
insert into fee_payment(fee_payment_id,fee_payment_lookup_id)
values(1,146);
insert into fee_payment(fee_payment_id,fee_payment_lookup_id)
values(2,147);
insert into fee_payment(fee_payment_id,fee_payment_lookup_id)
values(3,148);

/* The table Fee Status contains the fee status supported by the system like active, inactive etc - System */
insert into fee_status(status_id,status_lookup_id)
values(1,165);
insert into fee_status(status_id,status_lookup_id)
values(2,166);

insert into fee_payments_categories_type(fee_payments_category_type_id,fee_payment_id,category_id,fee_type_id) 
values(1,1,1,1);
insert into fee_payments_categories_type(fee_payments_category_type_id,fee_payment_id,category_id,fee_type_id) 
values(2,1,1,1);
insert into fee_payments_categories_type(fee_payments_category_type_id,fee_payment_id,category_id,fee_type_id) 
values(3,1,1,1);

/* The table Fee Formula master contains the formula for calculating fee amount like % of Interest, %of Principal etc - System */
insert into fee_formula_master(formulaid,forumla_lookup_id)
values(1,149);
insert into fee_formula_master(formulaid,forumla_lookup_id)
values(2,150);
insert into fee_formula_master(formulaid,forumla_lookup_id)
values(3,151);


/* The table PERSONNEL_STATUS contains the different status that will be associated to a personnel like active, inactive etc. - System */ 
insert into personnel_status(personnel_status_id,lookup_id)
values(1,152);
insert into personnel_status(personnel_status_id,lookup_id)
values(2,153);



/* The table Customer Addendence Types will contain the different attendence types supported by the MFI like present,absent etc - Configuration */
insert into customer_attendance_types 
values(1,194,'Present');
insert into customer_attendance_types 
values(2,195,'Absent');
insert into customer_attendance_types 
values(3,196,'Approved leave');
insert into customer_attendance_types 
values(4,197,'Late');

/* The table Customer level will contain the different levels supported by the system like center,group etc - System */
/* Level for Centers */
insert into customer_level(level_id,parent_level_id,level_name_id,interaction_flag,max_child_count,max_instance_count)
values(3,null,11,null,4,10);
/* Level for Groups */
insert into customer_level(level_id,parent_level_id,level_name_id,interaction_flag,max_child_count,max_instance_count)
values(2,3,12,null,2,12);
/* Level for Clients */
insert into customer_level(level_id,parent_level_id,level_name_id,interaction_flag,max_child_count,max_instance_count)
values(1,2,13,null,1,30);

/* The table Customer State contains the different states supported at each level of customer, like Center could have Partial, Pending state etc - System */
/* States for Customers */
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(1,1,1,'Customer Was Partial',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(2,1,2,'Customer Was Pending',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(3,1,3,'Customer Was Active',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(4,1,4,'Customer Was Hold',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(5,1,5,'Customer Was Cancel',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(6,1,6,'Customer Was Close',1);
/* States for Groups */
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(7,2,7,'Customer Was Partial',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(8,2,8,'Customer Was Pending',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(9,2,9,'Customer Was Active',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(10,2,10,'Customer Was Hold',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(11,2,11,'Customer Was Cancel',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(12,2,12,'Customer Was Close',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
/* States for Centers */
values(13,3,13,'Customer Was Active',1);
insert into customer_state(status_id,level_id,status_lookup_id,description,currently_in_use)
values(14,3,14,'Customer Was Inactive',1);


/* The table Customer State Flag contains the different flags associated at the various customer states - System */
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(1,28,5,'Withdraw',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(2,29,5,'Rejected',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(3,30,5,'Blacklisted',1);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(4,31,5,'Duplicate',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(5,34,5,'Other',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(6,32,6,'Transferred',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(7,31,6,'Duplicate',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(8,30,6,'Blacklisted',1);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(9,33,6,'Left program',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(10,34,6,'Other',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(11,28,11,'Withdraw',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(12,29,11,'Rejected',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(13,30,11,'Blacklisted',1);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(14,31,11,'Duplicate',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(15,34,11,'Other',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(16,32,12,'Transferred',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(17,31,12,'Duplicate',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(18,30,12,'Blacklisted',1);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(19,33,12,'Left program',0);
insert into customer_state_flag(flag_id,flag_lookup_id,status_id,flag_description,isblacklisted)
values(20,34,12,'Other',0);



/* The table PRD_STATUS contains the different status that would be associated to a product like loan active, savings active etc - System */
insert into prd_status(offering_status_id,prd_state_id,prd_type_id,currently_in_use,version_no)
values(1,1,1,1,1);
insert into prd_status(offering_status_id,prd_state_id,prd_type_id,currently_in_use,version_no)
values(2,1,2,1,1);
insert into prd_status(offering_status_id,prd_state_id,prd_type_id,currently_in_use,version_no)
values(4,2,1,1,1);
insert into prd_status(offering_status_id,prd_state_id,prd_type_id,currently_in_use,version_no)
values(5,2,2,1,1);

/* The table PRD_APPLICABLE_MASTER indicates to which type a product is being associated like client, group , center etc - System */
insert into prd_applicable_master(prd_applicable_master_id,lookup_id) 
values(1,68);
insert into prd_applicable_master(prd_applicable_master_id,lookup_id) 
values(2,69);
insert into prd_applicable_master(prd_applicable_master_id,lookup_id) 
values(3,70);


/* The table Account State contains the different states supported at each type of account, like Loan could have Partial, Pending state etc - System */
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(1,17,1,1,'Partial application');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(2,18,1,1,'Application pending approval');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(3,19,1,1,'Application approved');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(4,20,1,0,'Disbursed to loan officer');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(5,21,1,1,'Active in good standing');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(6,22,1,1,'Closed - obligation met');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(7,23,1,1,'Closed - written off');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(8,24,1,1,'Closed - rescheduled');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(9,25,1,1,'Active in bad standing');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(10,141,1,1,'Canceled');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(11,142,1,1,'Customer Account Active');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(12,143,1,1,'Customer Account Inactive');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(13,181,2,1,'Partial application');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(14,182,2,1,'Application pending approval');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(15,183,2,1,'Canceled');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(16,184,2,1,'Active');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(17,185,2,1,'Closed');
insert into account_state(account_state_id,lookup_id,prd_type_id,currently_in_use,status_description)
values(18,210,2,1,'Inactive');

/* The table Account State Flag contains the different flags associated at the various account states - System */
insert into account_state_flag(flag_id,lookup_id,status_id,flag_description,retain_flag)
values(1,174,10,'Withdraw',0);
insert into account_state_flag(flag_id,lookup_id,status_id,flag_description,retain_flag)
values(2,175,10,'Rejected',0);
insert into account_state_flag(flag_id,lookup_id,status_id,flag_description,retain_flag)
values(3,176,10,'Other',0);

/*Savings account flags*/
insert into account_state_flag(flag_id,lookup_id,status_id,flag_description,retain_flag)
values(4,211,15,'Withdraw',0);
insert into account_state_flag(flag_id,lookup_id,status_id,flag_description,retain_flag)
values(5,212,15,'Rejected',0);
insert into account_state_flag(flag_id,lookup_id,status_id,flag_description,retain_flag)
values(6,213,15,'Blacklisted',1);

/* The table PAYMENT_TYPE contains the different payment types associated like cash,etc - System */
insert into payment_type(payment_type_id,payment_type_lookup_id)
values(1,177);
insert into payment_type(payment_type_id,payment_type_lookup_id)
values(2,179);
insert into payment_type(payment_type_id,payment_type_lookup_id)
values(3,180);

/* The table ACCOUNT_ACTION contains the different account actions that can be performed like deposit,withdraw,etc - System */
insert into account_action(account_action_id,lookup_id)
values(1,167);
insert into account_action(account_action_id,lookup_id)
values(2,168);
insert into account_action(account_action_id,lookup_id)
values(3,169);
insert into account_action(account_action_id,lookup_id)
values(4,170);
insert into account_action(account_action_id,lookup_id)
values(5,171);
insert into account_action(account_action_id,lookup_id)
values(6,172);
insert into account_action(account_action_id,lookup_id)
values(7,173);
insert into account_action(account_action_id,lookup_id)
values(8,191);
insert into account_action(account_action_id,lookup_id)
values(9,192);
insert into account_action(account_action_id,lookup_id)
values(10,193);
insert into account_action(account_action_id,lookup_id)
values(11,214);
insert into account_action(account_action_id,lookup_id)
values(12,362);
insert into account_action(account_action_id,lookup_id)
values(13,364);
insert into account_action(account_action_id,lookup_id)
values(14,366);
insert into account_action(account_action_id,lookup_id)
values(15,547);
insert into account_action(account_action_id,lookup_id)
values(16,548);
insert into account_action(account_action_id,lookup_id)
values(17,549);
/*Postion Table */

/* The table Position contains the group and center position values that can be associated to a client like Group President, Group Secretary, etc. Note that Groups and Centers share the same set of position titles- Configuration */
insert into position (position_id,lookup_id)
values(1,186);
insert into position (position_id,lookup_id)
values(2,187);
insert into position (position_id,lookup_id)
values(3,188);
insert into position (position_id,lookup_id)
values(4,216);


/* STILL NEEDS COMMENTS */
insert into office_action_payment_type(office_id,prd_type_id,account_action_id,payment_type_id)
values(null,null,1,null);
insert into office_action_payment_type(office_id,prd_type_id,account_action_id,payment_type_id)
values(null,null,2,null);
insert into office_action_payment_type(office_id,prd_type_id,account_action_id,payment_type_id)
values(null,null,3,null);
insert into office_action_payment_type(office_id,prd_type_id,account_action_id,payment_type_id)
values(null,null,4,null);
insert into office_action_payment_type(office_id,prd_type_id,account_action_id,payment_type_id)
values(null,null,5,null);

/* The table Office Status contains the status that can be associated to a office like active,inactive etc - System */
insert into office_status(status_id,lookup_id)
values(1,15);
insert into office_status(status_id,lookup_id)
values(2,16);

/* The table Office will contain the default Head office information of the MFI like officename etc */
/* This information can be configured through the UI, so does not need to be configured in the script */
insert into office( parent_office_id, global_office_num, status_id, 
  office_code_id, office_level_id,  search_id, office_short_name, 
  max_child_count,local_remote_flag, display_name, 
  created_by, created_date, updated_by, updated_date, version_no)
values(null,'0001',1,
  null,1,'1.1','MIF1',
  2,1,'Mifos HO ',
  null,null,null,null,1);


/* STILL NEEDS COMMENTS */
insert into mfi_attribute(attribute_id, office_id, attribute_name, attribute_value) 
values(1,1,'CENTER','GROUP');
insert into mfi_attribute(attribute_id, office_id, attribute_name, attribute_value)
values(2,1,'CENTER','GROUP');
insert into mfi_attribute(attribute_id, office_id, attribute_name, attribute_value)
values(3,1,'CENTER','GROUP');
insert into mfi_attribute(attribute_id, office_id, attribute_name, attribute_value)
values(4,1,'CENTER','GROUP');
insert into mfi_attribute(attribute_id, office_id, attribute_name, attribute_value)
values(5,1,'CENTER','GROUP');


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
insert into entity_master(entity_type_id,entity_type)
values(1,'Client');
insert into entity_master(entity_type_id,entity_type)
values(2,'LoanProduct');
insert into entity_master(entity_type_id,entity_type)
values(3,'SavingsProduct');
insert into entity_master(entity_type_id,entity_type)
values(4,'ProductCategory');
insert into entity_master(entity_type_id,entity_type)
values(5,'ProductConfiguration');
insert into entity_master(entity_type_id,entity_type)
values(6,'Fees');
insert into entity_master(entity_type_id,entity_type)
values(7,'Accounts');
insert into entity_master(entity_type_id,entity_type)
values(8,'Admin');
insert into entity_master(entity_type_id,entity_type)
values(9,'Checklist');

insert into entity_master(entity_type_id,entity_type)
values(10,'Configuration');
insert into entity_master(entity_type_id,entity_type)
values(11,'Customer');
insert into entity_master(entity_type_id,entity_type)
values(12,'Group');
insert into entity_master(entity_type_id,entity_type)
values(13,'Login');
insert into entity_master(entity_type_id,entity_type)
values(14,'Meeting');
insert into entity_master(entity_type_id,entity_type)
values(15,'Office');
insert into entity_master(entity_type_id,entity_type)
values(16,'Penalty');
insert into entity_master(entity_type_id,entity_type)
values(17,'Personnel');

insert into entity_master(entity_type_id,entity_type)
values(19,'Roleandpermission');
insert into entity_master(entity_type_id,entity_type)
values(20,'Center');
insert into entity_master(entity_type_id,entity_type)
values(21,'Savings');
insert into entity_master(entity_type_id,entity_type)
values(22,'Loan');
insert into entity_master(entity_type_id,entity_type)
values(23,'BulkEntry');



/* The table Activity will contain all the activities supported by the system - System */
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(1,null,371,371 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(2,1,372,372 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(3,2,373,373 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(4,2,374,374 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(5,1,375,375 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(6,5,376,376 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(7,5,377,377 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(8,1,378,378 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(9,8,379,379 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(10,8,380,380 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(13,null,381,381 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(14,13,382,382 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(15,14,383,383 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(16,14,384,384 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(17,null,385,385 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(18,17,386,386 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(19,18,387,387 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(20,18,388,388 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(21,18,389,389 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(22,17,390,390 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(23,22,391,391 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(24,22,392,392 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(25,22,393,393 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(33,null,394,394 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(34,33,395,395 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(35,34,396,396 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(36,34,397,397 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(37,34,398,398);
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(38,34,399,399 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(39,34,400,400 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(40,34,401,401 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(41,34,402,402 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(42,34,403,403 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(43,34,404,404 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(44,34,405,405 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(46,34,407,407 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(47,34,408,408 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(48,34,409,409 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(49,34,410,410 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(50,34,411,411);
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(51,34,412,412 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(52,34,413,413 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(53,34,414,414 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(54,34,415,415 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(55,34,416,416 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(56,33,417,417 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(57,56,418,418 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(58,56,419,419 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(59,56,420,420 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(60,56,421,421 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(61,56,422,422 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(62,56,423,423 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(63,56,424,424 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(64,56,425,425 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(65,56,426,426 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(66,56,427,427 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(68,56,429,429 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(69,56,430,430 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(70,56,431,431 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(71,56,432,432 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(72,56,433,433 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(73,56,434,434 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(74,56,435,435 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(75,56,436,436 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(76,56,437,437 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(77,56,438,438 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(78,33,439,439 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(79,78,440,440 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(80,78,441,441 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(81,78,442,442 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(82,78,443,443 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(83,78,444,444 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(85,78,446,446 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(86,78,447,447 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(87,78,448,438 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(88,78,449,449 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(89,null,450,450 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(90,89,451,451 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(91,90,452,452 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(92,90,453,453 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(93,89,454,454 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(94,93,455,455 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(95,93,456,456 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(96,89,457,457 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(97,96,458,458 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(98,96,459,459 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(99,null,460,460 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(100,99,461,461 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(101,100,462,462 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(102,100,463,463 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(103,100,464,464 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(104,100,465,465 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(105,100,466,466 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(106,100,467,467 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(108,100,469,469 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(109,100,470,470 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(110,100,471,471 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(113,99,474,474 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(115,113,475,475 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(116,113,476,476 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(118,113,478,478 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(119,113,479,479 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(120,113,480,480 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(121,34,481,481 ); 
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(122,56,482,482 );
/* INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)VALUES(123,33,709,709 ); */
/* INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)VALUES(124,123,709,709 ); */
/* INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)VALUES(125,34,709,709 ); */
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(126,34,483,483 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(127,78,484,484 ); 
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(128,78,485,485 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(129,100,486,486 );
/* INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID) VALUES(130,100,709,709 );*/
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(131,113,487,487 );
/* INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)VALUES(133,123,709,709 );*/
/* INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)VALUES(134,123,709,709 );*/
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(135,18,488,488 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(136,null,489,489 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(137,136,490,490 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(138,136,491,491 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(139,136,492,492 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(140,136,493,493 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(141,null,494,494 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(145,141,498,498 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(146,141,499,499 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(147,141,500,500 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(148,141,501,501 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(149,141,502,502 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(150,141,503,503 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(151,141,504,504 );

-- commenting  activities for Jasper reports that no longer ship by default - pbiligi
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(152,145,505,505 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(153,145,506,506 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(154,145,507,507 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(155,145,508,508 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(156,145,509,509 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(157,145,510,510 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(158,145,511,511 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(159,146,512,512 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(160,147,513,513 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(161,147,514,514 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(162,147,515,515 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(163,147,516,516 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(164,148,517,517 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(165,148,518,518 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(166,148,519,519 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(167,148,520,520 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(168,148,521,521 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(169,148,522,522 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(170,148,523,523 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(171,148,524,524 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(172,149,525,525 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(173,149,526,526 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(174,149,527,527 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(175,149,528,528 );
-- 
-- 
-- 
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(176,150,529,529 );
-- INSERT INTO ACTIVITY(ACTIVITY_ID,PARENT_ID,ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID)
-- VALUES(177,151,530,530 );
-- 


insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(178,113,531,531 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(179,100,532,532 );


/* remaning saving states mappings */
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(180,136,533,533 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(181,136,534,534 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(182,136,535,535 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(183,136,536,536 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(184,136,537,537 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(185,136,538,538 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(186,136,546,546 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(187,136,551,551 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(188,136,552,552 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(189,113,553,553 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(190,136,554,554 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) 
values(191,136,555,555 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(193,13,562,562 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(194,18,563,563 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(195,90,561,561);
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(196,null,564,564 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(192,196,560,560 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(197,196,565,565 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(198,34,566,566 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(199,56,567,567 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id)
values(200,78,568,568 );
insert into activity(activity_id,parent_id,activity_name_lookup_id,description_lookup_id) 
values(201,196,569,569);


/* The table Role will contain the default admin role required for the MFI - System */
insert into role(role_id,role_name,version_no,created_by,created_date,
updated_by,updated_date)
values(1,'Admin',1,null,null,null,null);

/* The table Role Activity will contain the mapping between the role created for the mfi admin against the activities he can perform which could typically be all - System */
insert into roles_activity(activity_id,role_id)
values(3,1);
insert into roles_activity(activity_id,role_id)
values(4,1);
insert into roles_activity(activity_id,role_id)
values(6,1);
insert into roles_activity(activity_id,role_id)
values(7,1);
insert into roles_activity(activity_id,role_id)
values(9,1);
insert into roles_activity(activity_id,role_id)
values(10,1);

insert into roles_activity(activity_id,role_id)
values(15,1);
insert into roles_activity(activity_id,role_id)
values(16,1);
insert into roles_activity(activity_id,role_id)
values(19,1);
insert into roles_activity(activity_id,role_id)
values(20,1);
insert into roles_activity(activity_id,role_id)
values(21,1);
insert into roles_activity(activity_id,role_id)
values(23,1);
insert into roles_activity(activity_id,role_id)
values(24,1);
insert into roles_activity(activity_id,role_id)
values(25,1);

insert into roles_activity(activity_id,role_id)
values(35,1);
insert into roles_activity(activity_id,role_id)
values(36,1);
insert into roles_activity(activity_id,role_id)
values(37,1);
insert into roles_activity(activity_id,role_id)
values(38,1);
insert into roles_activity(activity_id,role_id)
values(39,1);
insert into roles_activity(activity_id,role_id)
values(40,1);
insert into roles_activity(activity_id,role_id)
values(41,1);
insert into roles_activity(activity_id,role_id)
values(42,1);
insert into roles_activity(activity_id,role_id)
values(43,1);
insert into roles_activity(activity_id,role_id)
values(44,1);
insert into roles_activity(activity_id,role_id)
values(46,1);
insert into roles_activity(activity_id,role_id)
values(47,1);
insert into roles_activity(activity_id,role_id)
values(48,1);
insert into roles_activity(activity_id,role_id)
values(49,1);
insert into roles_activity(activity_id,role_id)
values(50,1);
insert into roles_activity(activity_id,role_id)
values(51,1);
insert into roles_activity(activity_id,role_id)
values(52,1);
insert into roles_activity(activity_id,role_id)
values(53,1);
insert into roles_activity(activity_id,role_id)
values(54,1);
insert into roles_activity(activity_id,role_id)
values(55,1);
insert into roles_activity(activity_id,role_id)
values(57,1);
insert into roles_activity(activity_id,role_id)
values(58,1);
insert into roles_activity(activity_id,role_id)
values(59,1);
insert into roles_activity(activity_id,role_id)
values(60,1);
insert into roles_activity(activity_id,role_id)
values(61,1);
insert into roles_activity(activity_id,role_id)
values(62,1);
insert into roles_activity(activity_id,role_id)
values(63,1);
insert into roles_activity(activity_id,role_id)
values(64,1);
insert into roles_activity(activity_id,role_id)
values(65,1);
insert into roles_activity(activity_id,role_id)
values(66,1);
insert into roles_activity(activity_id,role_id)
values(68,1);
insert into roles_activity(activity_id,role_id)
values(69,1);
insert into roles_activity(activity_id,role_id)
values(70,1);
insert into roles_activity(activity_id,role_id)
values(71,1);
insert into roles_activity(activity_id,role_id)
values(72,1);
insert into roles_activity(activity_id,role_id)
values(73,1);
insert into roles_activity(activity_id,role_id)
values(74,1);
insert into roles_activity(activity_id,role_id)
values(75,1);
insert into roles_activity(activity_id,role_id)
values(76,1);
insert into roles_activity(activity_id,role_id)
values(77,1);
insert into roles_activity(activity_id,role_id)
values(79,1);
insert into roles_activity(activity_id,role_id)
values(80,1);
insert into roles_activity(activity_id,role_id)
values(81,1);
insert into roles_activity(activity_id,role_id)
values(82,1);
insert into roles_activity(activity_id,role_id)
values(83,1);
insert into roles_activity(activity_id,role_id)
values(85,1);
insert into roles_activity(activity_id,role_id)
values(86,1);
insert into roles_activity(activity_id,role_id)
values(87,1);
insert into roles_activity(activity_id,role_id)
values(88,1);
insert into roles_activity(activity_id,role_id)
values(91,1);
insert into roles_activity(activity_id,role_id)
values(92,1);
insert into roles_activity(activity_id,role_id)
values(94,1);
insert into roles_activity(activity_id,role_id)
values(95,1);
insert into roles_activity(activity_id,role_id)
values(97,1);
insert into roles_activity(activity_id,role_id)
values(98,1);
insert into roles_activity(activity_id,role_id)
values(101,1);
insert into roles_activity(activity_id,role_id)
values(102,1);
insert into roles_activity(activity_id,role_id)
values(103,1);
insert into roles_activity(activity_id,role_id)
values(104,1);
insert into roles_activity(activity_id,role_id)
values(105,1);
insert into roles_activity(activity_id,role_id)
values(106,1);
insert into roles_activity(activity_id,role_id)
values(108,1);
insert into roles_activity(activity_id,role_id)
values(109,1);
insert into roles_activity(activity_id,role_id)
values(110,1);
insert into roles_activity(activity_id,role_id)
values(115,1);
insert into roles_activity(activity_id,role_id)
values(116,1);
insert into roles_activity(activity_id,role_id)
values (118,1);
insert into roles_activity(activity_id,role_id)
values(119,1);
insert into roles_activity(activity_id,role_id)
values(120,1);
insert into roles_activity(activity_id,role_id) values(121,1);
insert into roles_activity(activity_id,role_id)values(122,1);
/*INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)VALUES(123,1);*/
/*INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)VALUES(124,1);*/
/* INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)VALUES(125,1);*/
insert into roles_activity(activity_id,role_id)
values(126,1);
insert into roles_activity(activity_id,role_id) values(127,1); 
insert into roles_activity(activity_id,role_id)values(128,1);
insert into roles_activity(activity_id,role_id)
values(129,1);
/* INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)VALUES(130,1); */
insert into roles_activity(activity_id,role_id)
values(131,1);
/*INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID) VALUES(133,1);*/
/*INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)VALUES(134,1);*/
insert into roles_activity(activity_id,role_id)
values(135,1);
insert into roles_activity(activity_id,role_id)
values(136,1);
insert into roles_activity(activity_id,role_id)
values(137,1);
insert into roles_activity(activity_id,role_id)
values(138,1);
insert into roles_activity(activity_id,role_id)
values(139,1);
insert into roles_activity(activity_id,role_id)
values(140,1);

insert into roles_activity(activity_id,role_id)
values(141,1);
insert into roles_activity(activity_id,role_id)
values(145,1);
insert into roles_activity(activity_id,role_id)
values(146,1);
insert into roles_activity(activity_id,role_id)
values(147,1);
insert into roles_activity(activity_id,role_id)
values(148,1);
insert into roles_activity(activity_id,role_id)
values(149,1);
insert into roles_activity(activity_id,role_id)
values(150,1);
insert into roles_activity(activity_id,role_id)
values(151,1);

-- commenting Admin activities related  to jasper reports which are not shipped by default - pbiligi
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(152,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(153,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(154,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(155,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(156,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(157,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(158,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(159,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(160,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(161,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(162,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(163,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(164,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(165,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(166,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(167,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(168,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(169,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(170,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(171,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(172,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(173,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(174,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(175,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(176,1);
-- INSERT INTO ROLES_ACTIVITY(ACTIVITY_ID,ROLE_ID)
-- VALUES(177,1);

insert into roles_activity(activity_id,role_id)
values(178,1);
insert into roles_activity(activity_id,role_id)
values(179,1);
insert into roles_activity(activity_id,role_id)
values(180,1);
insert into roles_activity(activity_id,role_id)
values(181,1);
insert into roles_activity(activity_id,role_id)
values(182,1);
insert into roles_activity(activity_id,role_id)
values(183,1);
insert into roles_activity(activity_id,role_id)
values(184,1);
insert into roles_activity(activity_id,role_id)
values(185,1);
insert into roles_activity(activity_id,role_id)
values(186,1);
insert into roles_activity(activity_id,role_id)
values(187,1);
insert into roles_activity(activity_id,role_id)
values(188,1);
insert into roles_activity(activity_id,role_id)
values(189,1);
insert into roles_activity(activity_id,role_id)
values(190,1);
insert into roles_activity(activity_id,role_id) 
values(191,1);
insert into roles_activity(activity_id,role_id) 
values(192,1);
insert into roles_activity(activity_id,role_id)
values (193,1);
insert into roles_activity(activity_id,role_id)
values (194,1);
insert into roles_activity(activity_id,role_id)
values (195,1);
insert into roles_activity(activity_id,role_id)
values (197,1);
insert into roles_activity(activity_id,role_id)
values (198,1);
insert into roles_activity(activity_id,role_id)
values (199,1);
insert into roles_activity(activity_id,role_id)
values (200,1);
insert into roles_activity(activity_id,role_id) values (201,1);


/* The table WEEK Days contains the week days information like Monday,Tuesday etc used by the system, it also contain the working day attribute against the same which can be configured by the MFI to indicate Monday is an offday - Configuration */
/* Enter 1 into the WORKING_DAY field to indicate that the day is working day; 0 to indicate a non-working day. */
/* Enter 1 into the START_OF_FISCAL_WEEK field to indicate that the day is the start of the fiscal week, and 0 for all other days.  Only one day should be marked as the start of the fiscal week.  This field is used for reporting purposes (ie, the timeperiod for weekly reports will start with the fiscal day) */
/* The look-up values for days of the week are defined above, in LOOKUP_VALUE_LOCALE.   This list below starts with Sunday */
insert into week_days_master(week_days_master_id,lookup_id,working_day,start_of_fiscal_week)
values(1,72,0,0);
insert into week_days_master(week_days_master_id,lookup_id,working_day,start_of_fiscal_week)
values(2,73,1,1);
insert into week_days_master(week_days_master_id,lookup_id,working_day,start_of_fiscal_week)
values(3,74,1,0);
insert into week_days_master(week_days_master_id,lookup_id,working_day,start_of_fiscal_week)
values(4,75,1,0);
insert into week_days_master(week_days_master_id,lookup_id,working_day,start_of_fiscal_week)
values(5,76,1,0);
insert into week_days_master(week_days_master_id,lookup_id,working_day,start_of_fiscal_week)
values(6,77,1,0);
insert into week_days_master(week_days_master_id,lookup_id,working_day,start_of_fiscal_week)
values(7,78,1,0);

/* inserting VALUES in interest_type table*/
/* The table Interest Types contains the different interest types supported by the system like Flat etc - System */
insert into interest_types (interest_type_id, lookup_id, category_id, descripton) 
values(1,79,1,'Flat');
insert into interest_types (interest_type_id, lookup_id, category_id, descripton)
values(2,80,1,'Declining');
insert into interest_types (interest_type_id, lookup_id, category_id, descripton) 
values(4,604,1,'Declining Balance-Equal Principal Installment');

insert into interest_calc_rule (interest_calc_rule_id, lookup_id) 
values(1,88);
insert into interest_calc_rule (interest_calc_rule_id, lookup_id) 
values(2,89);

/* Grace period types*/
/* The table GRACE_PERIOD_TYPE contains the different grace types supported by the system like principal only grace,interest only grace etc - System */ 
insert into grace_period_type (grace_period_type_id, lookup_id) 
values(1,96);
insert into grace_period_type (grace_period_type_id, lookup_id) 
values(2,97);
insert into grace_period_type (grace_period_type_id, lookup_id) 
values(3,98);

/* The table Rank Days contains data like first,second,third,fourth,last which will be used in meeting -System */
insert into rank_days_master(rank_days_master_id,lookup_id)
values(1,99);
insert into rank_days_master(rank_days_master_id,lookup_id)
values(2,100);
insert into rank_days_master(rank_days_master_id,lookup_id)
values(3,101);
insert into rank_days_master(rank_days_master_id,lookup_id)
values(4,102);
insert into rank_days_master(rank_days_master_id,lookup_id)
values(5,103);

/* master data for table fee_frequency_type*/
/* The table Fee Frequency contains type of fees like periodic, one time - System */
insert into fee_frequency_type(fee_frequency_id,lookup_id) values(1,558);
insert into fee_frequency_type(fee_frequency_id,lookup_id) values(2,559);

/*master data for table FIELD_CONFIGURATION */
/* The table Field Configuration will contain the MFI configuration related to fields being mandatory , hidden etc - Configuration */
/* ENTITY_ID is defined in the ENTITY_MASTER table and refers to entities such as client, personnel, groups, etc against which various data field can be marked as hidden and/or mandatory */

/*Middle Name for client and personnel*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(1,'MiddleName',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(2,'MiddleName',17,0,0);
/*Second Last Name for client and personnel*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(3,'SecondLastName',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(4,'SecondLastName',17,0,0);
/*Government id*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(5,'GovernmentId',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(6,'GovernmentId',17,0,0);
/*External id for cleint group center personnel*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(7,'ExternalId',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(8,'ExternalId',12,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(9,'ExternalId',20,0,0);
/*Ethnicity, Citizenship, Handicapped, business Activities, Education Level, Photo*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(10,'Ethinicity',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(11,'Citizenship',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(12,'Handicapped',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(13,'BusinessActivities',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(14,'EducationLevel',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(15,'Photo',1,0,0);
/*SpouseFatharMiddleName for Client*/ 
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(16,'SpouseFatherMiddleName',1,0,0);
/*SpouseFatharSecondLastName for Client*/ 
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(17,'SpouseFatherSecondLastName',1,0,0);
/*Trained and trained date*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(18,'Trained',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(19,'Trained',12,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(20,'TrainedDate',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(21,'TrainedDate',12,0,0);
/*Entire Address for client*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(22,'Address',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(23,'Address',12,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(24,'Address',20,0,0);
/**Address 1 for client group and center*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(25,'Address1',1,0,0,22);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(26,'Address1',12,0,0,23);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(27,'Address1',20,0,0,24);
/**Address 2 for client group and center*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(28,'Address2',1,0,0,22);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(29,'Address2',12,0,0,23);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(30,'Address2',20,0,0,24);
/**Address 3*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(31,'Address3',1,0,0,22);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(32,'Address3',12,0,0,23);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(33,'Address3',20,0,0,24);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(34,'Address3',15,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(35,'Address3',17,0,0);
/*City*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(36,'City',1,0,0,22);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(37,'City',12,0,0,23);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(38,'City',20,0,0,24);
/*State*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(39,'State',1,0,0,22);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(40,'State',12,0,0,23);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(41,'State',20,0,0,24);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(42,'State',15,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(43,'State',17,0,0);
/*Country*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(44,'Country',1,0,0,22);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(45,'Country',12,0,0,23);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(46,'Country',20,0,0,24);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(47,'Country',15,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(48,'Country',17,0,0);
/*Postal Code*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(49,'PostalCode',1,0,0,22);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(50,'PostalCode',12,0,0,23);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag,parent_field_config_id)
values(51,'PostalCode',20,0,0,24);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(52,'PostalCode',15,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(53,'PostalCode',17,0,0);
/**Phone number*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(54,'PhoneNumber',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(55,'PhoneNumber',12,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(56,'PhoneNumber',20,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(57,'PhoneNumber',17,0,0);
/*Purpose Of Loan*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(58,'PurposeOfLoan',22,0,0);
/*Collateral Type and Notes For Loan*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(59,'CollateralType',22,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(60,'CollateralNotes',22,0,0);
/*Receipt id and date*/
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(61,'ReceiptId',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(62,'ReceiptId',12,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(63,'ReceiptId',20,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(64,'ReceiptId',21,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(65,'ReceiptId',22,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(66,'ReceiptId',23,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(67,'ReceiptDate',1,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(68,'ReceiptDate',12,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(69,'ReceiptDate',20,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(70,'ReceiptDate',21,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(71,'ReceiptDate',22,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(72,'ReceiptDate',23,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(73,'PovertyStatus',1,1,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(75,'Address2',15,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(76,'Address2',17,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(77,'Address1',15,0,0);
insert into field_configuration(field_config_id,field_name,entity_id,mandatory_flag,hidden_flag)
values(78,'Address1',17,0,0);


/* The table CONFIG_KEY_VALUE_INTEGER holds generic key-value pair configuration data for integer values */
-- this row was CollectionSheetHelper.daysInAdvance
-- (value now stored in applicationConfiguration.default.properties)
-- Adam [ Thu Dec 20 22:28:57 PST 2007 ]
insert into config_key_value_integer(configuration_key, configuration_value) values ('x',0);
-- this row was sessionTimeout (see web.xml for configured session timeout)
-- Adam [ Thu Dec 20 22:28:58 PST 2007 ]
insert into config_key_value_integer(configuration_key, configuration_value) values (' ',0);
insert into config_key_value_integer(configuration_key, configuration_value) values ('jasperReportIsHidden',1);
insert into config_key_value_integer(configuration_key, configuration_value) values ('loanIndividualMonitoringIsEnabled',0);
insert into config_key_value_integer(configuration_key, configuration_value) values ('repaymentSchedulesIndependentOfMeetingIsEnabled',0);
/* Client Rules */
insert into config_key_value_integer(configuration_key, configuration_value) values ('CenterHierarchyExists',1);
insert into config_key_value_integer(configuration_key, configuration_value) values ('ClientCanExistOutsideGroup',0);
insert into config_key_value_integer(configuration_key, configuration_value) values ('GroupCanApplyLoans',0);
insert into config_key_value_integer(configuration_key, configuration_value) values ('minDaysBetweenDisbursalAndFirstRepaymentDay',1);
insert into config_key_value_integer(configuration_key, configuration_value) values ('maxDaysBetweenDisbursalAndFirstRepaymentDay',365);

insert into config_key_value_integer(configuration_key, configuration_value) values ('AdministrativeDocumentsIsEnabled',1);

/*Added Table Personnel_Role */
/* The table Personnel role will contain the role associated to the Admin user of the MFI - Configuration */
insert into personnel_role(personnel_role_id, role_id, personnel_id)values(1,1,1);

/* The table Financial Action will contain the different financial actions
   supported by the system like Payment , Adjutment etc - System. Constants in
   FinancialActionConstants map to IDs in this table. */
insert into financial_action
values(1,198);
insert into financial_action
values(2,199);
insert into financial_action
values(3,200);
insert into financial_action
values(4,229);
insert into financial_action
values(5,201);
insert into financial_action
values(6,361);
insert into financial_action
values(7,215);
insert into financial_action
values(8,202);
insert into financial_action values(9,203);
insert into financial_action values(10,204);
insert into financial_action values(11,205);
insert into financial_action values(12,206);
insert into financial_action values(13,207);
insert into financial_action values(14,208);
/* TODO: delete this one (and associated lookup values), it doesn't appear in
   FinancialActionConstants and can therefore never be used. */
insert into financial_action values(15,209);
insert into financial_action values(16,363);
/* TODO: delete this one (and associated lookup values), it doesn't appear in
   FinancialActionConstants and can therefore never be used. */
insert into financial_action values(17,365);
insert into financial_action values(18,367);
insert into financial_action values(19,368);
insert into financial_action values(20,369);
insert into financial_action values(21,370);
insert into financial_action values(22,550);


/* The table Transaction Type defines the transaction types for which the MFI can assign acceptable payment methods.  - System.*/
insert into transaction_type (transaction_id,transaction_name) values(1,'Loan Disbursement');
insert into transaction_type (transaction_id,transaction_name) values(2,'Loan Repayment');
insert into transaction_type (transaction_id,transaction_name) values(3,'Savings Deposit');
insert into transaction_type (transaction_id,transaction_name) values(4,'Savings Withdrawals');
insert into transaction_type (transaction_id,transaction_name) values(5,'Client Fees/penalty payments');

/* The table Accepted Payment Type will contain the different payment modes supported by the system - System*/
insert into accepted_payment_type (accepted_payment_type_id,transaction_id,payment_type_id) values (1,1,1);
insert into accepted_payment_type (accepted_payment_type_id,transaction_id,payment_type_id) values (2,2,1);
insert into accepted_payment_type (accepted_payment_type_id,transaction_id,payment_type_id) values (3,3,1);
insert into accepted_payment_type (accepted_payment_type_id,transaction_id,payment_type_id) values (4,4,1);
insert into accepted_payment_type (accepted_payment_type_id,transaction_id,payment_type_id) values (5,5,1);

/* The table FEE_UPDATE_TYPE indicates what in fee is changed like amount changed, status changed etc - System */ 
insert into fee_update_type(lookup_id) values(556);
insert into fee_update_type(lookup_id) values(557);

insert into fund_code(fundcode_id,fundcode_value) values(1,'00');
insert into fund_code(fundcode_id,fundcode_value) values(2,'01');
insert into fund_code(fundcode_id,fundcode_value) values(3,'02');
insert into fund_code(fundcode_id,fundcode_value) values(4,'03');
insert into fund_code(fundcode_id,fundcode_value) values(5,'04');

insert into fund(fund_id,fundcode_id,fund_name,version_no)
values(1,1,'Non Donor',0);
insert into fund(fund_id,fundcode_id,fund_name,version_no)
values(2,1,'Funding Org A',0);
insert into fund(fund_id,fundcode_id,fund_name,version_no)
values(3,1,'Funding Org B',0);
insert into fund(fund_id,fundcode_id,fund_name,version_no)
values(4,1,'Funding Org C',0);
insert into fund(fund_id,fundcode_id,fund_name,version_no)
values(5,1,'Funding Org D',0);


-- Begin data for Reports Mini Portal
insert into mis_bank values (1,'BANK1');
insert into mis_bank values (2,'BANK2');
insert into mis_bank values (3,'BANK3');

insert into mis_bankbranch values (1,1,'BRANCHB1',4);
insert into mis_bankbranch values (2,2,'BRANCHB2',5);
insert into mis_bankbranch values (3,3,'BRANCHB3',6);
insert into mis_bankbranch values (4,1,'BRANCHB11',7);
insert into mis_bankbranch values (5,2,'BRANCHB21',8);
insert into mis_bankbranch values (6,3,'BRANCHB31',9);

insert into mis_geographicalarea values (1,'A1',1,null);
insert into mis_geographicalarea values (2,'A2',1,null);
insert into mis_geographicalarea values (3,'A3',1,null);
insert into mis_geographicalarea values (4,'B1',2,1);
insert into mis_geographicalarea values (5,'B2',2,2);
insert into mis_geographicalarea values (6,'B3',2,3);
insert into mis_geographicalarea values (7,'B11',2,1);
insert into mis_geographicalarea values (8,'B21',2,2);
insert into mis_geographicalarea values (9,'B31',2,3);

insert into mis_geographicalareatype values (1,'A');
insert into mis_geographicalareatype values (2,'B');

insert into mis_shgmemberprofile values (1,1,'MEMBER1','YES',50,'YES');
insert into mis_shgmemberprofile values (1,2,'MEMBER2','YES',50,'YES');
insert into mis_shgmemberprofile values (1,3,'MEMBER3','NO',0,'YES');
insert into mis_shgmemberprofile values (1,4,'MEMBER4','YES',100,'NO');

insert into mis_shgprofile values 
(1,'G1',10,4,null,'LEADER 1','LEADER 2',1);
insert into mis_shgprofile values 
(2,'G2',10,4,null,'LEADER1','LEADER2',1);
insert into mis_shgprofile values 
(3,'G3',8,5,null,'LAEDER1','LEADER2',2);
insert into mis_shgprofile values 
(4,'G4',12,4,null,'LEADER1','LEADER2',1);
insert into mis_shgprofile values 
(5,'G5',15,5,null,'LEADER1','LEADER2',2);
insert into mis_shgprofile values 
(6,'G6',11,7,null,'LEADER1','LEADER2',1);

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


insert into repayment_rule(repayment_rule_id, repayment_rule_lookup_id)
values(1,576);
insert into repayment_rule(repayment_rule_id, repayment_rule_lookup_id)
values(2,577);
insert into repayment_rule(repayment_rule_id, repayment_rule_lookup_id)
values(3,578);

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
insert into report_category(report_category_id,report_category_value) 
  values(1,'Client Detail');
insert into report_category(report_category_id,report_category_value) 
  values(2,'Performance');
insert into report_category(report_category_id,report_category_value) 
  values(3,'Center');
insert into report_category(report_category_id,report_category_value) 
  values(4,'Loan Product Detail');
insert into report_category(report_category_id,report_category_value) 
  values(5,'Status');
insert into report_category(report_category_id,report_category_value) 
  values(6,'Analysis');
insert into report_category(report_category_id,report_category_value) 
  values(7,'Miscellaneous');

/* The table Report will contain the different reports supported by the MFI 
   like Client Summary etc. The report will be linked to the category 
   under which it belongs - Configuration */
insert into report(report_category_id,report_name,report_identifier, activity_id, report_active) 
  values(1,'Collection Sheet Report','collection_sheet_report',229,1);
insert into report(report_category_id,report_name,report_identifier, activity_id, report_active) 
  values(6,'Branch Cash Confirmation Report','branch_cash_confirmation_report',231,1);
insert into report(report_category_id,report_name,report_identifier, activity_id, report_active) 
  values(6,'Branch Progress Report','branch_progress_report',232,1);

insert into report_jasper_map (report_category_id,report_name,report_identifier,
report_jasper) values (1,'Collection Sheet Report','collection_sheet_report','CollectionSheetReport.rptdesign');
insert into report_jasper_map (report_category_id,report_name,report_identifier,
report_jasper) values (6,'Branch Cash Confirmation Report','branch_cash_confirmation_report','BranchCashConfirmationReport.rptdesign');
insert into report_jasper_map (report_category_id,report_name,report_identifier,
report_jasper) values (6,'Branch Progress Report','branch_progress_report','ProgressReport.rptdesign');

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
insert into country(country_id,country_name,country_short_name) values(11,'Kenya','KE');
insert into country(country_id,country_name,country_short_name) values(12,'Tanzania','TZ');
insert into country(country_id,country_name,country_short_name) values(13,'Uganda','UG');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(614,74,'Language-Swahili');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(6,'Swahili','sw',614);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(6,11,6,'Swahili-Kenya',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(7,12,6,'Swahili-Tanzania',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(8,13,6,'Swahili-Uganda',0);

/* Arabic locale support */  
insert into country(country_id,country_name,country_short_name) values(14,'Algeria','DZ');
insert into country(country_id,country_name,country_short_name) values(15,'Bahrain','BH');
insert into country(country_id,country_name,country_short_name) values(16,'Comoros','KM');
insert into country(country_id,country_name,country_short_name) values(17,'Chad','TD');
insert into country(country_id,country_name,country_short_name) values(18,'Djibouti','DJ');
insert into country(country_id,country_name,country_short_name) values(19,'Egypt','EG');
insert into country(country_id,country_name,country_short_name) values(20,'Eritrea','ER');
insert into country(country_id,country_name,country_short_name) values(21,'Iraq','IQ');
insert into country(country_id,country_name,country_short_name) values(22,'Israel','IL');
insert into country(country_id,country_name,country_short_name) values(23,'Jordan','JO');
insert into country(country_id,country_name,country_short_name) values(24,'Kuwait','KW');
insert into country(country_id,country_name,country_short_name) values(25,'Lebanon','LB');
insert into country(country_id,country_name,country_short_name) values(26,'Libyan Arab Rebublic','LY');
insert into country(country_id,country_name,country_short_name) values(27,'Mauritania','MR');
insert into country(country_id,country_name,country_short_name) values(28,'Morocco','MA');
insert into country(country_id,country_name,country_short_name) values(29,'Oman','OM');
insert into country(country_id,country_name,country_short_name) values(30,'Qatar','QA');
insert into country(country_id,country_name,country_short_name) values(31,'Saudi Arabia','SA');
insert into country(country_id,country_name,country_short_name) values(32,'Somalia','SO');
insert into country(country_id,country_name,country_short_name) values(33,'Sudan','SD');
insert into country(country_id,country_name,country_short_name) values(34,'Syrian Arab Republic','SY');
insert into country(country_id,country_name,country_short_name) values(35,'Tunisia','TN');
insert into country(country_id,country_name,country_short_name) values(36,'United Arab Emirates','AE');
insert into country(country_id,country_name,country_short_name) values(37,'Yemen','YE');
insert into country(country_id,country_name,country_short_name) values(38,'Palestinian Territory, Occupied','PS');
insert into country(country_id,country_name,country_short_name) values(39,'Western Sahara','EH');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(615,74,'Language-Arabic');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(7,'Arabic','ar',615);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(9,14,7,'Arabic-Algeria',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(10,15,7,'Arabic-Bahrain',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(11,16,7,'Arabic-Comoros',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(12,17,7,'Arabic-Chad',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(13,18,7,'Arabic-Djibouti',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(14,19,7,'Arabic-Egypt',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(15,20,7,'Arabic-Eritrea',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(16,21,7,'Arabic-Iraq',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(17,22,7,'Arabic-Israel',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(18,23,7,'Arabic-Jordan',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(19,24,7,'Arabic-Kuwait',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(20,25,7,'Arabic-Lebanon',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(21,26,7,'Arabic-Libyan Arab Rebublic',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(22,27,7,'Arabic-Mauritania',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(23,28,7,'Arabic-Morocco',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(24,29,7,'Arabic-Oman',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(25,30,7,'Arabic-Qatar',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(26,31,7,'Arabic-Saudi Arabia',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(27,32,7,'Arabic-Somalia',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(28,33,7,'Arabic-Sudan',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(29,34,7,'Arabic-Syrian Arab Republic',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(30,35,7,'Arabic-Tunisia',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(31,36,7,'Arabic-United Arab Emirates',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(32,37,7,'Arabic-Yemen',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(33,38,7,'Arabic-Palestinian Territory, Occupied',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(34,39,7,'Arabic-Western Sahara',0);

/* Portuguese locale support */
insert into country(country_id,country_name,country_short_name) values(40,'Angola','AO');
insert into country(country_id,country_name,country_short_name) values(41,'Brazil','BR');
insert into country(country_id,country_name,country_short_name) values(42,'Cape Verde','CV');
insert into country(country_id,country_name,country_short_name) values(43,'Guinea-Bissau','GW');
insert into country(country_id,country_name,country_short_name) values(44,'Equatorial Guinea','GQ');
insert into country(country_id,country_name,country_short_name) values(45,'Macau','MO');
insert into country(country_id,country_name,country_short_name) values(46,'Mozambique','MZ');
insert into country(country_id,country_name,country_short_name) values(47,'Portugal','PT');
insert into country(country_id,country_name,country_short_name) values(48,'Sao Tome and Principe','ST');
insert into lookup_value(lookup_id,entity_id,lookup_name) values(616,74,'Language-Portuguese');
insert into language(lang_id,lang_name,lang_short_name,lookup_id) values(8,'Portuguese','pt',616);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(35,40,8,'Portuguese-Angola',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(36,41,8,'Portuguese-Brazil',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(37,42,8,'Portuguese-Cape Verde',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(38,43,8,'Portuguese-Guinea-Bissau',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(39,44,8,'Portuguese-Equatorial Guinea',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(40,45,8,'Portuguese-Macau',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(41,46,8,'Portuguese-Mozambique',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(42,47,8,'Portuguese-Portugal',0);
insert into supported_locale(locale_id,country_id,lang_id,locale_name,default_locale) values(43,48,8,'Portuguese-Sao Tome and Principe',0);

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

