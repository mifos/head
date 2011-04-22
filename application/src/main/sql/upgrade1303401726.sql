DROP PROCEDURE IF EXISTS reassignSearchIds;


CREATE PROCEDURE reassignSearchIds()
BEGIN
    DECLARE v_done INT DEFAULT 0
    ;DECLARE v_branch_id INT
    ;DECLARE v_customer_id INT
    ;DECLARE v_kid_id INT
    ;DECLARE v_kid_count INT

    ;DECLARE v_search_id varchar(100)
    ;DECLARE v_customer_level_id INT
    ;DECLARE v_output VARCHAR(5000) DEFAULT ''
    
    ;DECLARE used_branches CURSOR FOR
        select distinct(branch_id) as branch_id  
        from customer
        where parent_customer_id is null
        order by 1
        
    ;DECLARE top_customers CURSOR FOR
        SELECT customer_id, search_id
        FROM   customer
        WHERE  branch_id = v_branch_id AND customer_level_id = v_customer_level_id
        ORDER BY customer_id
        
    ;DECLARE kids CURSOR FOR
        SELECT customer_id
        FROM   customer
        WHERE  parent_customer_id = v_customer_id 
        ORDER BY customer_id
        
    ;DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET v_done = TRUE

    ;OPEN used_branches
    ;REPEAT
        FETCH used_branches into v_branch_id
    
        ;if not v_done then
            update customer
            set search_id = concat('1.', customer_id)
            where parent_customer_id is null
            and branch_id = v_branch_id
        ;end if
    ;UNTIL v_done END REPEAT
    ;CLOSE used_branches
    
  
    ;SET v_customer_level_id := 3
    
	;REPEAT
	
	    SET v_done = 0
	
	    ;OPEN used_branches
	    ;branches_loop: LOOP
	        FETCH used_branches into v_branch_id
	    
	        ;IF v_done THEN
	            CLOSE used_branches
	            ;LEAVE branches_loop
	        ;END IF
	        
	
	        ;START TRANSACTION
	
	        ;SET v_output := concat(v_output, ' , ', v_branch_id)
	
	        ;OPEN top_customers
	        ;top_customers_loop: LOOP
	            FETCH top_customers into v_customer_id, v_search_id
	            ;IF v_done THEN
	                 CLOSE top_customers
	                 ;SET v_done = 0
	                 ;LEAVE top_customers_loop
	            ;END IF
	                         
	            ;OPEN kids
	            ;SET v_kid_count := 0
	            ;kids_loop: LOOP
	                FETCH kids into v_kid_id
	                ;IF v_done THEN
	                    CLOSE kids
	                    ;SET v_done = 0
	                    ;LEAVE kids_loop
	                ;END IF
	            
	                ;SET v_kid_count := v_kid_count + 1
	                
	                ;update customer
			    		set search_id = concat(v_search_id, '.', v_kid_count)
	                    where customer_id = v_kid_id
	            
	            ;END LOOP kids_loop            
	            
	        ;END LOOP top_customers_loop
	        
	        ;COMMIT
	        
	    ;END LOOP branches_loop
	
	    ;SET v_customer_level_id := v_customer_level_id - 1
	;UNTIL v_customer_level_id = 1 END REPEAT

;END
;

CALL reassignSearchIds();
DROP PROCEDURE IF EXISTS reassignSearchIds;

