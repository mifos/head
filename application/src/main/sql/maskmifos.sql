-- This script is used for masking/hiding the sensitive information from a given Mifos database
-- TODO
-- * Replace password hash to default password hash 

update personnel set display_name = concat("user-",personnel_id), email_id=NULL;
update personnel set login_name = concat("user-",personnel_id) where personnel_id>1;
update personnel_details set first_name = substr(first_name,1,1), last_name= substr(last_name,1,1), middle_name = "",city = concat("city-",first_name),address_1=NULL,address_2=NULL,address_3=NULL,country="US",telephone=NULL,postal_code=NULL,state=NULL;
update customer set display_name = concat("center-",customer_id), first_name=NULL,last_name=NULL, display_address = substr(display_address,1,1) where customer_level_id=3;
update customer set display_name = concat("group-",customer_id), first_name=NULL,last_name=NULL, display_address = substr(display_address,1,1) where customer_level_id=2;
update customer set display_name = concat("client-",customer_id), first_name=NULL,last_name=NULL, display_address = substr(display_address,1,1) where customer_level_id=1;

update office set display_name = concat("office-",office_id), office_short_name = substr(office_short_name,1,1);

update customer_address_detail set address_name=null,line_1=null,line_2=null,line_3=null,zip=null,phone_number=null,country=null;

update customer_name_detail set first_name = substr(first_name,1,1),middle_name="",last_name= substr(last_name,1,1),DISPLAY_NAME=concat("customer-name-",CUSTOMER_NAME_ID),SECOND_LAST_NAME="",SECOND_MIDDLE_NAME="";

update meeting set meeting_place="meetingPlace";

update change_log set modifier_name=substr(modifier_name,1,1);

update change_log_detail set old_value=substr(old_value,1,1), new_value=substr(new_value,1,1) where field_name like  "%ame";
update change_log_detail set old_value=substr(old_value,1,1), new_value=substr(new_value,1,1) where field_name like  "Loan%";
update change_log_detail set old_value=substr(old_value,1,1), new_value=substr(new_value,1,1) where field_name like  "Handi%";
update change_log_detail set new_value=substr(new_value,1,1) where field_name like  "City%";
update change_log_detail set new_value="US" where field_name like  "Country%";

update batch_staff_summary set personnel_name=substr(personnel_name,1,1);

update loan_arrears_aging set customer_name=substr(customer_name,1,1);
