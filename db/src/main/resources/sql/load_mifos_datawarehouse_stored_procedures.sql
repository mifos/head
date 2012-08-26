--
-- Dumping routines for database 'mifos_dwh'
--
/*!50003 DROP PROCEDURE IF EXISTS `SPcascade_change_to_accounts` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcascade_change_to_accounts`(IN current_customer_key int, IN effective_date_param date, IN new_customer_key int, 
IN new_group_key int, IN new_center_key int, IN new_loan_officer_key int, IN new_branch_key int, 
IN new_formed_by_loan_officer_key int)
BEGIN
declare done int default 0;

declare loan_account_id_var int;
declare loan_account_key_var int;
declare loan_status_var varchar(100);
declare product_key_var int;
declare currency_id_var int;
declare savings_account_id_var int;
declare savings_account_key_var int;
declare savings_status_var varchar(100);

declare loan_accounts_referenced_cursor cursor for 
select loan_account_key, loan_account_id, loan_status, product_key, currency_id
from dim_loan
where customer_key = current_customer_key
and valid_from <= effective_date_param
and valid_to > effective_date_param;

declare savings_accounts_referenced_cursor cursor for 
select savings_account_key, savings_account_id, savings_status, product_key, currency_id
from dim_savings
where customer_key = current_customer_key
and valid_from <= effective_date_param
and valid_to > effective_date_param;

declare continue handler for not found set done = 1;

open loan_accounts_referenced_cursor;
repeat
    fetch loan_accounts_referenced_cursor into loan_account_key_var, loan_account_id_var, loan_status_var, product_key_var, currency_id_var;
                            
    if not done then
                                    
        call SPloan_account_insert(loan_account_id_var, new_customer_key, product_key_var, 
                                            new_group_key, new_center_key, new_loan_officer_key,
                                            new_formed_by_loan_officer_key, new_branch_key,
                                            currency_id_var,
                                            loan_status_var, 
                                            effective_date_param,
                                            @new_loan_account_key);   
 
    end if;
until done end repeat;

close loan_accounts_referenced_cursor;



update dim_loan
set valid_to = effective_date_param
where customer_key = current_customer_key
and valid_from <= effective_date_param
and valid_to > effective_date_param;



set done = 0;

open savings_accounts_referenced_cursor;
repeat
    fetch savings_accounts_referenced_cursor into savings_account_key_var, savings_account_id_var, savings_status_var, product_key_var, currency_id_var;
                            
    if not done then
                                    
        call SPsavings_account_insert(savings_account_id_var, new_customer_key, product_key_var, 
                                            new_group_key, new_center_key, new_loan_officer_key,
                                            new_formed_by_loan_officer_key, new_branch_key,
                                            currency_id_var,
                                            savings_status_var, 
                                            effective_date_param,
                                            @new_savings_account_key);   
 
    end if;
until done end repeat;

close savings_accounts_referenced_cursor;



update dim_savings
set valid_to = effective_date_param
where customer_key = current_customer_key
and valid_from <= effective_date_param
and valid_to > effective_date_param;




END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcascade_change_to_clients` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcascade_change_to_clients`(IN current_group_key int, IN effective_date_param date, IN new_group_key int, IN new_center_key int,
IN new_loan_officer_key int, IN new_branch_key int)
BEGIN
declare done int default 0;

declare customer_id_var int;
declare customer_key_var int;
declare customer_status_var varchar(100);
declare customer_level_id_var smallint;
declare formed_by_loan_officer_key_var int;

declare clients_referenced_cursor cursor for 
select customer_key, customer_id, customer_status, customer_level_id, formed_by_loan_officer_key 
from dim_customer
where group_key = current_group_key
and customer_level_id = 1
and valid_from <= effective_date_param
and valid_to > effective_date_param;

declare continue handler for not found set done = 1;


open clients_referenced_cursor;
repeat
    fetch clients_referenced_cursor into customer_key_var, customer_id_var, customer_status_var, customer_level_id_var, formed_by_loan_officer_key_var;
                            
    if not done then
        
        call SPcustomer_insert(customer_id_var, customer_status_var, customer_level_id_var,  
                                    new_group_key, new_center_key, new_loan_officer_key, new_branch_key,
                                    formed_by_loan_officer_key_var,
                                    effective_date_param,
                                    @new_client_key);       
        
        call SPcascade_change_to_accounts(customer_key_var, effective_date_param, @new_client_key, 
                    new_group_key, new_center_key, new_loan_officer_key, new_branch_key,
                    formed_by_loan_officer_key_var);
 
    end if;
until done end repeat;

close clients_referenced_cursor;




update dim_customer
set valid_to = effective_date_param
where group_key = current_group_key
and customer_level_id = 1
and valid_from <= effective_date_param
and valid_to > effective_date_param;






END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcascade_change_to_groups` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcascade_change_to_groups`(IN current_center_key int, IN date_param date, IN new_center_key int,
IN new_loan_officer_key int, IN new_branch_key int)
BEGIN
declare done int default 0;

declare customer_id_var int;
declare customer_key_var int;
declare customer_status_var varchar(100);
declare customer_level_id_var smallint;
declare group_key_var int;
declare formed_by_loan_officer_key_var int;

declare groups_referenced_cursor cursor for 
select customer_key, customer_id, customer_status, customer_level_id, group_key, formed_by_loan_officer_key 
from dim_customer
where center_key = current_center_key
and customer_level_id = 2
and valid_from <= date_param
and valid_to > date_param;

declare continue handler for not found set done = 1;


open groups_referenced_cursor;
repeat
    fetch groups_referenced_cursor into customer_key_var, customer_id_var, customer_status_var, customer_level_id_var, 
                            group_key_var, formed_by_loan_officer_key_var;
                            
    if not done then
        
        call SPcustomer_insert(customer_id_var, customer_status_var, customer_level_id_var, 
                                    group_key_var, new_center_key, new_loan_officer_key, new_branch_key,
                                    formed_by_loan_officer_key_var,
                                    date_param,
                                    @new_group_key);       
        
        call SPcascade_change_to_clients(customer_key_var, date_param, @new_group_key, new_center_key, new_loan_officer_key, new_branch_key);

        call SPcascade_change_to_accounts(customer_key_var, date_param, @new_group_key, 
                    @new_group_key, new_center_key, new_loan_officer_key, new_branch_key,
                    formed_by_loan_officer_key_var);
                    
    end if;
until done end repeat;

close groups_referenced_cursor;




update dim_customer
set valid_to = date_param
where center_key = current_center_key
and customer_level_id = 2
and valid_from <= date_param
and valid_to > date_param;






END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcreate_new_customer` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcreate_new_customer`(IN customer_id_param int, IN customer_status_param varchar(100), IN customer_level_id_param int,  
IN parent_id_param int, IN loan_officer_id_param int, IN customer_formedby_id_param int, IN created_date_param date)
BEGIN

declare entry_found int;
declare group_key_var int;
declare center_key_var int;

select count(*) into entry_found
from dim_customer
where customer_id = customer_id_param;


if entry_found = 0 then 
    
    if parent_id_param is null then 
        set group_key_var = 0;
        set center_key_var = 0;
        
        
        call SPpersonnel_return_current_key_values(loan_officer_id_param, created_date_param, @parent_loan_officer_key, @parent_branch_key);
    else
    
        call SPcustomer_return_current_key_values(parent_id_param, created_date_param,
                                                                    @parent_customer_key,
                                                                    @parent_customer_status,
                                                                    @parent_group_key, @parent_center_key, 
                                                                    @parent_loan_officer_key, @parent_branch_key, 
                                                                    @parent_formed_by_loan_officer_key);
                                                                    
        if customer_level_id_param = 2 then
            set group_key_var = 0;
            set center_key_var = @parent_customer_key;
        else
            set group_key_var = @parent_customer_key;
            set center_key_var = @parent_center_key;
        end if;
    end if;
    
    call SPpersonnel_return_current_key_values(customer_formedby_id_param, created_date_param, @fb_loan_officer_key_latest, @fb_branch_key_latest);
    
    call SPcustomer_insert(customer_id_param, customer_status_param, customer_level_id_param,  
                                    group_key_var, center_key_var, @parent_loan_officer_key, @parent_branch_key,
                                    @fb_loan_officer_key_latest,
                                    created_date_param,
                                    @new_customer_key);                                        
else
    call SPfallover(concat('SPcreate_new_customer for customer id: ', customer_id_param, ' - ', entry_found, ' entries already exist(s)')); 
end if;


END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcreate_new_loan_account` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcreate_new_loan_account`(IN entity_id_param int, IN updated_status_param varchar(100), IN updated_parent_id_param int,
                                                        IN account_prd_offering_id_param smallint, IN account_currency_id_param smallint, 
                                                        IN effective_date_param date)
BEGIN

declare entry_found int;

select count(*) into entry_found
from dim_loan
where loan_account_id = entity_id_param;


if entry_found = 0 then 
    
    
    call SPcustomer_return_current_key_values(updated_parent_id_param, effective_date_param,
                                                                    @parent_customer_key,
                                                                    @parent_customer_status,
                                                                    @parent_group_key, @parent_center_key, 
                                                                    @parent_loan_officer_key, @parent_branch_key, 
                                                                    @parent_formed_by_loan_officer_key);
                                                                    
    call SPproduct_return_current_key_value(account_prd_offering_id_param, effective_date_param, @product_key);
  
    call SPloan_account_insert(entity_id_param, @parent_customer_key, @product_key, 
                                            @parent_group_key, @parent_center_key, @parent_loan_officer_key,
                                            @parent_formed_by_loan_officer_key, @parent_branch_key,
                                            account_currency_id_param,
                                            updated_status_param, 
                                            effective_date_param,
                                            @new_loan_account_key);                                   
                                                                        
else
    call SPfallover(concat('SPcreate_new_loan_account for account id: ', entity_id_param, ' - ', entry_found, ' entries already exist(s)')); 
end if;


END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcreate_new_savings_account` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcreate_new_savings_account`(IN entity_id_param int, IN updated_status_param varchar(100), IN updated_parent_id_param int,
                                                        IN account_prd_offering_id_param smallint, IN account_currency_id_param smallint, 
                                                        IN effective_date_param date)
BEGIN

declare entry_found int;

select count(*) into entry_found
from dim_savings
where savings_account_id = entity_id_param;


if entry_found = 0 then 
    
    
    call SPcustomer_return_current_key_values(updated_parent_id_param, effective_date_param,
                                                                    @parent_customer_key,
                                                                    @parent_customer_status,
                                                                    @parent_group_key, @parent_center_key, 
                                                                    @parent_loan_officer_key, @parent_branch_key, 
                                                                    @parent_formed_by_loan_officer_key);
                                                                    
    call SPproduct_return_current_key_value(account_prd_offering_id_param, effective_date_param, @product_key);
  
    call SPsavings_account_insert(entity_id_param, @parent_customer_key, @product_key, 
                                            @parent_group_key, @parent_center_key, @parent_loan_officer_key,
                                            @parent_formed_by_loan_officer_key, @parent_branch_key,
                                            account_currency_id_param,
                                            updated_status_param, 
                                            effective_date_param,
                                            @new_savings_account_key);                                   
                                                                        
else
    call SPfallover(concat('SPcreate_new_savings_account for account id: ', entity_id_param, ' - ', entry_found, ' entries already exist(s)')); 
end if;


END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcustomer_insert` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcustomer_insert`(IN customer_id_param int,  IN customer_status_param varchar(100), IN customer_level_id_param smallint,  
IN group_key_param int, IN center_key_param int, IN loan_officer_key_param int, IN branch_key_param int, IN formed_by_loan_officer_key_param int,
IN created_date_param date, OUT new_customer_key int)
BEGIN


set @insert_customer_key = @insert_customer_key + 1;


if customer_level_id_param = 3 then 
    set center_key_param = @insert_customer_key;
end if;

if customer_level_id_param = 2 then 
    set group_key_param = @insert_customer_key;
end if;

insert into dim_customer(customer_key, customer_id, 
    customer_status, customer_level_id,  group_key, center_key, loan_officer_key, branch_key, formed_by_loan_officer_key, valid_from)
values (@insert_customer_key, customer_id_param, 
    customer_status_param, customer_level_id_param, group_key_param, center_key_param, loan_officer_key_param, branch_key_param, 
    formed_by_loan_officer_key_param, created_date_param);
    
if row_count() <> 1 then
    call SPfallover(concat('SPcustomer_insert for customer id: ', customer_id_param, ' - expected one customer entry but found ', row_count())); 
end if;
            
select @insert_customer_key into new_customer_key;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcustomer_return_current_key_values` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcustomer_return_current_key_values`(IN customer_id_param int, IN created_date_param date,
OUT current_customer_key int,
OUT current_customer_status varchar(100),
OUT current_group_key int, OUT current_center_key int, 
OUT current_loan_officer_key int, OUT current_branch_key int, 
OUT current_formed_by_loan_officer_key int)
BEGIN


select customer_key, customer_status, group_key, center_key, loan_officer_key, branch_key, formed_by_loan_officer_key 
into current_customer_key, current_customer_status, 
current_group_key, current_center_key, 
current_loan_officer_key, current_branch_key, 
current_formed_by_loan_officer_key
from dim_customer
where customer_id = customer_id_param
    and valid_from <= created_date_param
    and valid_to > created_date_param;

if current_customer_key is null then
    call SPfallover(concat('SPcustomer_return_current_key_values for customer id: ', customer_id_param, ' - expected one current dim_customer entry but found none')); 
end if;


END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcustomer_status_change` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcustomer_status_change`(IN customer_id_param int,  IN customer_level_id_param int, 
IN customer_status_param varchar(100), IN effective_date_param date)
BEGIN

call SPcustomer_return_current_key_values(customer_id_param, effective_date_param,
                                                                @current_customer_key,
                                                                @current_customer_status,
                                                                @current_group_key, @current_center_key, 
                                                                @current_loan_officer_key, @current_branch_key, 
                                                                @current_formed_by_loan_officer_key);
                                                                                            
call SPcustomer_update_validto(@current_customer_key, effective_date_param);

call SPcustomer_insert(customer_id_param, customer_status_param, customer_level_id_param, 
                                @current_group_key, @current_center_key, @current_loan_officer_key, @current_branch_key,
                                @current_formed_by_loan_officer_key,
                                effective_date_param,
                                @new_customer_key);
                                
CASE customer_level_id_param
WHEN 3 THEN 
    call SPcascade_change_to_groups(@current_customer_key, effective_date_param, @new_customer_key, 
                    @current_loan_officer_key, @current_branch_key);
    call SPcascade_change_to_accounts(@current_customer_key, effective_date_param, @new_customer_key, 
                    @current_group_key, @current_center_key, @current_loan_officer_key, @current_branch_key,
                    @current_formed_by_loan_officer_key);
WHEN 2 THEN  
    call SPcascade_change_to_clients(@current_customer_key, effective_date_param, @new_customer_key, 
                    @current_center_key, @current_loan_officer_key, @current_branch_key);
    call SPcascade_change_to_accounts(@current_customer_key, effective_date_param, @new_customer_key, 
                    @current_group_key, @current_center_key, @current_loan_officer_key, @current_branch_key,
                    @current_formed_by_loan_officer_key);
WHEN 1 THEN  
    call SPcascade_change_to_accounts(@current_customer_key, effective_date_param, @new_customer_key, 
                    @current_group_key, @current_center_key, @current_loan_officer_key, @current_branch_key,
                    @current_formed_by_loan_officer_key);
END CASE;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPcustomer_update_validto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPcustomer_update_validto`(IN customer_key_param int, IN effective_date_param date)
BEGIN



update dim_customer
set valid_to = effective_date_param
where customer_key = customer_key_param;

if row_count() <> 1 then
    call SPfallover(concat('SPcustomer_update_validto for customer key: ', customer_key_param, ' - expected one current dim_customer entry but found ', row_count())); 
end if;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPetl_customers_and_accounts` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPetl_customers_and_accounts`()
BEGIN
declare done int default 0;

declare loop_count int default 0;

declare effective_date_var date;
declare level_id_var smallint;
declare entity_id_var int;
declare change_type_var varchar(1);
declare change_order_var int;

declare updated_status_var varchar(100);
declare updated_parent_id_var int;
declare updated_loan_officer_id_var int;
declare updated_branch_id_var int;

declare customer_formedby_id_var int;

declare account_type_var varchar(10);
declare account_prd_offering_id_var smallint;
declare account_currency_id_var smallint;


declare customer_and_account_changes_cursor cursor for 
select effective_date, level_id, entity_id, change_type, change_order, 
        updated_status, updated_parent_id, updated_loan_officer_id, updated_branch_id, customer_formedby_id, 
        account_type, account_prd_offering_id, account_currency_id
from stg_customer_and_account_updates
order by effective_date, level_id desc, entity_id, change_type, change_order, updated_parent_id desc, updated_loan_officer_id desc;

declare continue handler for not found set done = 1;


select ifnull(max(customer_key), 0) into @insert_customer_key
from dim_customer;
set @start_customer_key = @insert_customer_key; 

select ifnull(max(loan_account_key), 0) into @insert_loan_account_key
from dim_loan;
set @start_loan_account_key = @insert_loan_account_key; 
select ifnull(max(savings_account_key), 0) into @insert_savings_account_key
from dim_savings;
set @start_savings_account_key = @insert_savings_account_key; 

truncate table stg_error_message;

call SPshow_stats(1);

open customer_and_account_changes_cursor;
start transaction;
repeat
    fetch customer_and_account_changes_cursor into effective_date_var, level_id_var, entity_id_var, change_type_var, change_order_var, 
        updated_status_var, updated_parent_id_var, updated_loan_officer_id_var, updated_branch_id_var, customer_formedby_id_var, 
        account_type_var, account_prd_offering_id_var, account_currency_id_var;
        
    if not done then
        call SPvalidate_input_collect_stats(entity_id_var, level_id_var, change_type_var, updated_status_var, updated_parent_id_var,
                    updated_loan_officer_id_var, updated_branch_id_var, account_type_var);
    
        if level_id_var = -5 then 
            CASE change_type_var
            WHEN 'A' THEN                      
                            if account_type_var = 'loan' THEN 
                                    call SPcreate_new_loan_account(entity_id_var, updated_status_var, updated_parent_id_var,
                                            account_prd_offering_id_var, account_currency_id_var, effective_date_var);
                            else 
                                    call SPcreate_new_savings_account(entity_id_var, updated_status_var, updated_parent_id_var,
                                            account_prd_offering_id_var, account_currency_id_var, effective_date_var);
                            end if;
                            
            WHEN 'S'  THEN           
                            if account_type_var = 'loan' THEN 
                                    call SPloan_status_change(entity_id_var, updated_status_var, effective_date_var);
                            else 
                                    call SPsavings_status_change(entity_id_var, updated_status_var, effective_date_var);
                            end if;
            END CASE;
        
        else         
            CASE change_type_var
            WHEN 'A' THEN                     
                    call SPcreate_new_customer(entity_id_var, updated_status_var, level_id_var, 
                                updated_parent_id_var, updated_loan_officer_id_var, customer_formedby_id_var, effective_date_var);
                    
            WHEN 'H'  THEN 
                    call SPhierarchy_change(entity_id_var, level_id_var, 
                            updated_parent_id_var, updated_loan_officer_id_var, updated_branch_id_var, effective_date_var);
                    
            WHEN 'S'  THEN 
                    call SPcustomer_status_change(entity_id_var, level_id_var, updated_status_var, effective_date_var);
                    
            END CASE;
        
        end if;
    
        set loop_count := loop_count + 1;
        if loop_count > 1000 then
            set loop_count := 0;
            commit;
            start transaction;
        end if;

    end if;
until done end repeat;

close customer_and_account_changes_cursor;

commit;

call SPshow_stats(0);

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPfallover` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPfallover`(IN errormsg varchar(300))
BEGIN

insert into stg_error_message
select errormsg;
commit;

/* then fallover anyway */
select fallover from pretend_fallover_table;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPhierarchy_change` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPhierarchy_change`(IN customer_id_param int, IN customer_level_id_param int, 
IN parent_id_param int, IN loan_officer_id_param int, IN branch_id_param int, IN created_date_param date)
BEGIN
declare group_key_var int;
declare center_key_var int;
declare loan_officer_key_var int;
declare branch_key_var int;



call SPcustomer_return_current_key_values(customer_id_param, created_date_param,
                                                                @current_customer_key,
                                                                @current_customer_status,
                                                                @current_group_key, @current_center_key, 
                                                                @current_loan_officer_key, @current_branch_key, 
                                                                @current_formed_by_loan_officer_key);

call SPcustomer_update_validto(@current_customer_key, created_date_param);


if parent_id_param is not null then 
    if parent_id_param = 0 then 
        if customer_level_id_param = 1 then 
            set group_key_var = 0;
            set center_key_var = @current_center_key;
            set loan_officer_key_var = @current_loan_officer_key;
            set branch_key_var = @current_branch_key;
        else 
            call SPfallover(concat('Customer: ', customer_id_param, ' SPhierarchy_change - parent_id_param of 0 invalid for customer_level_id : ' , customer_level_id_param));
        end if;               
    else
        call SPcustomer_return_current_key_values(parent_id_param, created_date_param,
                                                                @parent_customer_key,
                                                                @parent_customer_status,
                                                                @parent_group_key, @parent_center_key, 
                                                                @parent_loan_officer_key, @parent_branch_key, 
                                                                @parent_formed_by_loan_officer_key);
    
        if customer_level_id_param = 2 then
            set group_key_var = @current_group_key;
        else
            set group_key_var = @parent_customer_key;
        end if;               
        set center_key_var = @parent_center_key;
        set loan_officer_key_var = @parent_loan_officer_key;
        set branch_key_var = @parent_branch_key;
    end if;
    
end if;

if loan_officer_id_param is not null then
    call SPpersonnel_return_current_key_values(loan_officer_id_param, created_date_param, @loan_officer_key_latest, @branch_key_latest);
    
    set group_key_var = @current_group_key;
    set center_key_var = @current_center_key;   
        set loan_officer_key_var = @loan_officer_key_latest;
    if @loan_officer_key_latest > 0 then
        set branch_key_var = @branch_key_latest; 
    else 
        set branch_key_var = @current_branch_key; 
    end if;

end if;

if branch_id_param is not null then    

    call SPoffice_return_current_key_values(branch_id_param, created_date_param, @branch_key_latest);
    
    set group_key_var = @current_group_key;
    set center_key_var = @current_center_key;   
    set loan_officer_key_var = 0; 
    set branch_key_var = @branch_key_latest; 
    
end if;


call SPcustomer_insert(customer_id_param, @current_customer_status, customer_level_id_param, 
                                group_key_var, center_key_var, loan_officer_key_var, branch_key_var,                    
                                @current_formed_by_loan_officer_key,
                                created_date_param,
                                @new_customer_key);

CASE customer_level_id_param
WHEN 3 THEN                     
    call SPcascade_change_to_groups(@current_customer_key, created_date_param, @new_customer_key, 
                loan_officer_key_var, branch_key_var);
    call SPcascade_change_to_accounts(@current_customer_key, created_date_param, @new_customer_key, 
                    group_key_var, @new_customer_key, loan_officer_key_var, branch_key_var,
                    @current_formed_by_loan_officer_key);
WHEN 2 THEN 
    call SPcascade_change_to_clients(@current_customer_key, created_date_param, @new_customer_key, 
                center_key_var, loan_officer_key_var, branch_key_var);
    call SPcascade_change_to_accounts(@current_customer_key, created_date_param, @new_customer_key, 
                    @new_customer_key, center_key_var, loan_officer_key_var, branch_key_var,
                    @current_formed_by_loan_officer_key);
WHEN 1  THEN 
    call SPcascade_change_to_accounts(@current_customer_key, created_date_param, @new_customer_key, 
                    group_key_var, center_key_var, loan_officer_key_var, branch_key_var,
                    @current_formed_by_loan_officer_key);
END CASE;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPloan_account_insert` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPloan_account_insert`(IN loan_account_id_param int, IN customer_key_param int, IN product_key_param int, 
                IN group_key_param int, IN center_key_param int, IN loan_officer_key_param int,
                IN formed_by_loan_officer_key_param int, IN branch_key_param int,
                IN currency_id_param int, IN loan_status_param varchar(100), 
                IN effective_date_param date,
                OUT new_loan_account_key int)
BEGIN                              

set @insert_loan_account_key = @insert_loan_account_key + 1;

insert into dim_loan(loan_account_key, loan_account_id, loan_status, currency_id, customer_key, product_key,
group_key, center_key, loan_officer_key, branch_key, formed_by_loan_officer_key,
valid_from)
values (@insert_loan_account_key, loan_account_id_param, loan_status_param, currency_id_param, customer_key_param, product_key_param,
group_key_param, center_key_param, loan_officer_key_param, branch_key_param, formed_by_loan_officer_key_param,
effective_date_param);
            
select @insert_loan_account_key into new_loan_account_key;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPloan_account_return_current_key_values` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPloan_account_return_current_key_values`(IN loan_account_id_param int, IN effective_date_param date,
OUT current_loan_account_key int,
OUT current_customer_key int,
OUT current_group_key int, OUT current_center_key int, 
OUT current_loan_officer_key int, OUT current_branch_key int, 
OUT current_formed_by_loan_officer_key int,
OUT current_product_key int,
OUT current_currency_id int)
BEGIN

select loan_account_key, customer_key, group_key, center_key, loan_officer_key, branch_key, formed_by_loan_officer_key,
            product_key, currency_id
into current_loan_account_key, current_customer_key, 
current_group_key, current_center_key, 
current_loan_officer_key, current_branch_key, 
current_formed_by_loan_officer_key,
current_product_key, current_currency_id
from dim_loan
where loan_account_id = loan_account_id_param
    and valid_from <= effective_date_param
    and valid_to > effective_date_param;

if current_loan_account_key is null then
    call SPfallover(concat('SPloan_account_return_current_key_values for loan account id: ', loan_account_id_param, ' - expected one current dim_loan entry but found none')); 
end if;


END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPloan_account_update_validto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPloan_account_update_validto`(IN loan_account_key_param int, IN effective_date_param date)
BEGIN

update dim_loan
set valid_to = effective_date_param
where loan_account_key = loan_account_key_param;

if row_count() <> 1 then
    call SPfallover(concat('SPloan_account_update_validto for loan account key: ', loan_account_key_param, ' - expected one current dim_loan entry but found ', row_count())); 
end if;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPloan_status_change` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPloan_status_change`(IN entity_id_param int,  IN updated_status_param varchar(100), IN effective_date_param date)
BEGIN

call SPloan_account_return_current_key_values(entity_id_param, effective_date_param,
                                                                @current_loan_account_key,
                                                                @current_customer_key,
                                                                @current_group_key, @current_center_key, 
                                                                @current_loan_officer_key, @current_branch_key, 
                                                                @current_formed_by_loan_officer_key,
                                                                @current_product_key, @current_currency_id);
                                                                                            
call SPloan_account_update_validto(@current_loan_account_key, effective_date_param);
                                
call SPloan_account_insert(entity_id_param, @current_customer_key, @current_product_key, 
                                @current_group_key, @current_center_key, @current_loan_officer_key,
                                @current_formed_by_loan_officer_key, @current_branch_key,
                                @current_currency_id,
                                updated_status_param, 
                                effective_date_param,
                                @new_loan_account_key);                                   
                                

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPoffice_return_current_key_values` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPoffice_return_current_key_values`(IN office_id_param smallint, IN date_param date, OUT current_office_key smallint)
BEGIN
    
select o.office_key into current_office_key
from dim_office o 
where o.office_id = office_id_param
and o.valid_from <= date_param
and o.valid_to > date_param;

if current_office_key is null then
/* return the first entry for the office */
	select o.office_key into current_office_key
	from dim_office o 
	where o.office_id = office_id_param
	order by o.valid_from
	limit 1;

    if current_office_key is null then
    	call SPfallover(concat('SPoffice_return_current_key_values for office id: ', office_id_param, ' date: ', date_param, 
            ' - No dim_office entry found')); 
    end if;
end if;                                                                
                                                                
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPpersonnel_return_current_key_values` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPpersonnel_return_current_key_values`(IN personnel_id_param smallint, IN date_param date, 
OUT current_personnel_key smallint, OUT current_office_key smallint)
BEGIN
    
select p.personnel_key, p.office_key into current_personnel_key, current_office_key
from dim_personnel p 
where p.personnel_id = personnel_id_param
and p.valid_from <= date_param
and p.valid_to > date_param;

if current_personnel_key is null then
/* return the first entry for the personnel */
	select p.personnel_key, p.office_key into current_personnel_key, current_office_key
	from dim_personnel p 
	where p.personnel_id = personnel_id_param
	order by p.valid_from
	limit 1;

    if current_personnel_key is null then
        call SPfallover(concat('SPpersonnel_return_current_key_values for personnel id: ', personnel_id_param, ' date: ', date_param, 
            ' - No dim_personnel entry found')); 
    end if;
end if;                                                                
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPproduct_return_current_key_value` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPproduct_return_current_key_value`(IN prd_offering_id_param smallint, IN effective_date_param date, 
OUT current_product_key smallint)
BEGIN
    
select p.product_key into current_product_key
from dim_product p 
where p.prd_offering_id = prd_offering_id_param
and p.valid_from <= effective_date_param
and p.valid_to > effective_date_param;

if current_product_key is null then
/* return the first entry for the product */
	select p.product_key into current_product_key
	from dim_product p 
	where p.prd_offering_id = prd_offering_id_param
	order by p.valid_from
	limit 1;

    if current_product_key is null then
    	call SPfallover(concat('SPproduct_return_current_key_value for product id: ', prd_offering_id_param, ' - No dim_product entry found')); 
    end if;
end if;                                                                
                                                                

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPsavings_account_insert` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPsavings_account_insert`(IN savings_account_id_param int, IN customer_key_param int, IN product_key_param int, 
                IN group_key_param int, IN center_key_param int, IN loan_officer_key_param int,
                IN formed_by_loan_officer_key_param int, IN branch_key_param int,
                IN currency_id_param int, IN savings_status_param varchar(100), 
                IN effective_date_param date,
                OUT new_savings_account_key int)
BEGIN                              

set @insert_savings_account_key = @insert_savings_account_key + 1;

insert into dim_savings(savings_account_key, savings_account_id, savings_status, currency_id, customer_key, product_key,
group_key, center_key, loan_officer_key, branch_key, formed_by_loan_officer_key,
valid_from)
values (@insert_savings_account_key, savings_account_id_param, savings_status_param, currency_id_param, customer_key_param, product_key_param,
group_key_param, center_key_param, loan_officer_key_param, branch_key_param, formed_by_loan_officer_key_param,
effective_date_param);
            
select @insert_savings_account_key into new_savings_account_key;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPsavings_account_return_current_key_values` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPsavings_account_return_current_key_values`(IN savings_account_id_param int, IN effective_date_param date,
OUT current_savings_account_key int,
OUT current_customer_key int,
OUT current_group_key int, OUT current_center_key int, 
OUT current_loan_officer_key int, OUT current_branch_key int, 
OUT current_formed_by_loan_officer_key int,
OUT current_product_key int,
OUT current_currency_id int)
BEGIN

select savings_account_key, customer_key, group_key, center_key, loan_officer_key, branch_key, formed_by_loan_officer_key,
            product_key, currency_id
into current_savings_account_key, current_customer_key, 
current_group_key, current_center_key, 
current_loan_officer_key, current_branch_key, 
current_formed_by_loan_officer_key,
current_product_key, current_currency_id
from dim_savings
where savings_account_id = savings_account_id_param
    and valid_from <= effective_date_param
    and valid_to > effective_date_param;

if current_savings_account_key is null then
    call SPfallover(concat('SPsavings_account_return_current_key_values for savings account id: ', savings_account_id_param, ' - expected one current dim_savings entry but found none')); 
end if;


END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPsavings_account_update_validto` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPsavings_account_update_validto`(IN savings_account_key_param int, IN effective_date_param date)
BEGIN

update dim_savings
set valid_to = effective_date_param
where savings_account_key = savings_account_key_param;

if row_count() <> 1 then
    call SPfallover(concat('SPsavings_account_update_validto for savings account key: ', savings_account_key_param, ' - expected one current dim_savings entry but found ', row_count())); 
end if;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPsavings_status_change` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPsavings_status_change`(IN entity_id_param int,  IN updated_status_param varchar(100), IN effective_date_param date)
BEGIN

call SPsavings_account_return_current_key_values(entity_id_param, effective_date_param,
                                                                @current_savings_account_key,
                                                                @current_customer_key,
                                                                @current_group_key, @current_center_key, 
                                                                @current_loan_officer_key, @current_branch_key, 
                                                                @current_formed_by_loan_officer_key,
                                                                @current_product_key, @current_currency_id);
                                                                                            
call SPsavings_account_update_validto(@current_savings_account_key, effective_date_param);
                                
call SPsavings_account_insert(entity_id_param, @current_customer_key, @current_product_key, 
                                @current_group_key, @current_center_key, @current_loan_officer_key,
                                @current_formed_by_loan_officer_key, @current_branch_key,
                                @current_currency_id,
                                updated_status_param, 
                                effective_date_param,
                                @new_savings_account_key);                                   
                                

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPshow_stats` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPshow_stats`(IN initialise_params int)
BEGIN

if initialise_params = 1 then
    set @centers_new = 0;
    set @centers_hierarchy_changes = 0;
    set @centers_status_changes = 0;
    set @groups_new = 0;
    set @groups_hierarchy_changes = 0;
    set @groups_status_changes = 0;
    set @groups_np_new = 0;
    set @groups_np_hierarchy_changes = 0;
    set @clients_new = 0;
    set @clients_hierarchy_changes = 0;
    set @clients_status_changes = 0;
    set @clients_np_new = 0;
    set @clients_np_hierarchy_changes = 0;
    set @loan_accounts_new = 0;
    set @loan_accounts_status_changes = 0;
    set @savings_accounts_new = 0;
    set @savings_accounts_status_changes = 0;
else

    select round((ifnull(max(customer_key), 0) - @start_customer_key), 0) as customers_added
    from dim_customer;

    select @centers_new as centers, 
        @centers_hierarchy_changes as centers_h, 
        @centers_status_changes as centers_s;
    select @groups_new as groups, 
        @groups_hierarchy_changes as groups_h, 
        @groups_status_changes as groups_s;
    select @groups_np_new as groups_without_center, 
        @groups_np_hierarchy_changes as groups_without_center_h;
    select @clients_new as clients, 
        @clients_hierarchy_changes as clients_h, 
        @clients_status_changes as clients_s;
    select @clients_np_new as clients_without_group, 
        @clients_np_hierarchy_changes as clients_without_group_h;

    select round((ifnull(max(loan_account_key), 0) - @start_loan_account_key), 0) as loans_added, 
        @loan_accounts_new loans, 
        @loan_accounts_status_changes loans_s
    from dim_loan;

    select round((ifnull(max(savings_account_key), 0) - @start_savings_account_key), 0) as savings_added, 
        @savings_accounts_new savings, 
        @savings_accounts_status_changes savings_s
    from dim_savings;

end if;

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `SPvalidate_input_collect_stats` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=CURRENT_USER*/ /*!50003 PROCEDURE `SPvalidate_input_collect_stats`(IN entity_id_param int, IN level_id_param smallint, IN change_type_param varchar(1), 
    IN  updated_status_param varchar(100), IN updated_parent_id_param int, IN updated_loan_officer_id_param int, IN updated_branch_id_param int, 
    IN account_type_param varchar(10))
BEGIN
declare hierarchy_check int;

if (level_id_param <> -5 and level_id_param <> 1 and level_id_param <> 2 and level_id_param <> 3) then
    call SPfallover(concat('ID: ', entity_id_param, ' SPvalidate_input_collect_stats - level_id_param invalid: ' , level_id_param));
end if;

if (level_id_param = -5) then 
    if (change_type_param <> 'A' and change_type_param <> 'S') then
        call SPfallover(concat('Account: ', entity_id_param, ' SPvalidate_input_collect_stats - change_type_param invalid: ' , change_type_param));
    end if;
    if (account_type_param <> 'loan' and account_type_param <> 'savings') then
        call SPfallover(concat('Account: ', entity_id_param, ' SPvalidate_input_collect_stats - account_type_param invalid: ' , account_type_param));
    end if;
    
end if;


if (level_id_param <> -5) then 
    if (change_type_param <> 'A' and change_type_param <> 'S' and change_type_param <> 'H') then
        call SPfallover(concat('Customer: ', entity_id_param, ' SPvalidate_input_collect_stats - change_type_param invalid: ' , change_type_param));
    end if;
    if (account_type_param <> 'n/a') then
        call SPfallover(concat('Customer: ', entity_id_param, ' SPvalidate_input_collect_stats - account_type_param invalid: ' , account_type_param));
    end if;
    
    set hierarchy_check = 0;
    if (updated_parent_id_param is not null) then
        set hierarchy_check = hierarchy_check + 1;
    end if;
    if (updated_loan_officer_id_param is not null) then
        set hierarchy_check = hierarchy_check + 1;
    end if;
    if (updated_branch_id_param is not null) then
        set hierarchy_check = hierarchy_check + 1;
    end if;
    if (change_type_param <> 'S' and hierarchy_check <> 1) then
        call SPfallover(concat('Customer: ', entity_id_param, ' SPvalidate_input_collect_stats: one and only one of the hierarchy fields can be populated for a hierarchy change: ', 
                ' updated_parent_id_param: ', ifnull(updated_parent_id_param, 'null'),' updated_loan_officer_id_param: ', ifnull(updated_loan_officer_id_param, 'null'),
                ' updated_branch_id_param: ', ifnull(updated_branch_id_param, 'null')));
     end if;
    if (change_type_param = 'S' and hierarchy_check <> 0) then
        call SPfallover(concat('Customer: ', entity_id_param, ' SPvalidate_input_collect_stats: none of the hierarchy fields can be populated for a non-hierarchy change: change_type_param - ', 
                change_type_param, ' : updated_parent_id_param: ', ifnull(updated_parent_id_param, 'null'),' updated_loan_officer_id_param: ', ifnull(updated_loan_officer_id_param, 'null'),
                ' updated_branch_id_param: ', ifnull(updated_branch_id_param, 'null')));
     end if;
end if;


if level_id_param = -5 then  
    CASE change_type_param
    WHEN 'A' THEN                      
        if account_type_param = 'loan' THEN 
            set @loan_accounts_new = @loan_accounts_new + 1;
        else 
            set @savings_accounts_new = @savings_accounts_new + 1;
        end if;
    WHEN 'S'  THEN           
        if account_type_param = 'loan' THEN 
            set @loan_accounts_status_changes = @loan_accounts_status_changes + 1;
        else 
            set @savings_accounts_status_changes = @savings_accounts_status_changes + 1;
        end if;
    END CASE;
        
else         
        
    CASE change_type_param
    WHEN 'A' THEN 
        CASE level_id_param
            WHEN 3 THEN 
                set @centers_new = @centers_new + 1;
            WHEN 2 THEN 
                if updated_parent_id_param is not null then
                    set @groups_new = @groups_new + 1;
                else
                    set @groups_np_new = @groups_np_new + 1;
                end if;                                
            WHEN 1 THEN 
                if updated_parent_id_param is not null then
                    set @clients_new = @clients_new + 1;
                else
                    set @clients_np_new = @clients_np_new + 1;
                end if;                                
            END CASE;
    WHEN 'H'  THEN 
            CASE level_id_param
            WHEN 3 THEN 
                set @centers_hierarchy_changes = @centers_hierarchy_changes + 1; 
            WHEN 2 THEN 
                if updated_parent_id_param is not null then
                    set @groups_hierarchy_changes = @groups_hierarchy_changes + 1;
                else
                    set @groups_np_hierarchy_changes = @groups_np_hierarchy_changes + 1;
                end if;                                 
            WHEN 1 THEN 
                if updated_parent_id_param is not null then
                    set @clients_hierarchy_changes = @clients_hierarchy_changes + 1;
                else
                    set @clients_np_hierarchy_changes = @clients_np_hierarchy_changes + 1;
                end if;                                 
            END CASE;
        WHEN 'S'  THEN 
            CASE level_id_param
            WHEN 3 THEN 
                            set @centers_status_changes = @centers_status_changes + 1;
            WHEN 2 THEN 
                            set @groups_status_changes = @groups_status_changes + 1;
            WHEN 1 THEN 
                            set @clients_status_changes = @clients_status_changes + 1;
            END CASE;
                                        
        END CASE;
end if;

END */;;
DELIMITER ;
