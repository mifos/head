# 1 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_loan_officer\\\\combined_Create_Loan_Officer.c"
# 1 "globals.h" 1



 
 
# 1 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h" 1
 












 











# 102 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"








































































	

 


















 
 
 
 
 


 
 
 
 
 
 














int     lr_start_transaction   (char * transaction_name);
int lr_start_sub_transaction          (char * transaction_name, char * trans_parent);
long lr_start_transaction_instance    (char * transaction_name, long parent_handle);



int     lr_end_transaction     (char * transaction_name, int status);
int lr_end_sub_transaction            (char * transaction_name, int status);
int lr_end_transaction_instance       (long transaction, int status);


 
typedef char* lr_uuid_t;
 



lr_uuid_t lr_generate_uuid();

 


int lr_generate_uuid_free(lr_uuid_t uuid);

 



int lr_generate_uuid_on_buf(lr_uuid_t buf);

   
# 262 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
int lr_start_distributed_transaction  (char * transaction_name, lr_uuid_t correlator, long timeout  );

   







int lr_end_distributed_transaction  (lr_uuid_t correlator, int status);


double lr_stop_transaction            (char * transaction_name);
double lr_stop_transaction_instance   (long parent_handle);


void lr_resume_transaction           (char * trans_name);
void lr_resume_transaction_instance  (long trans_handle);


int lr_update_transaction            (const char *trans_name);


 
void lr_wasted_time(long time);


 
int lr_set_transaction(const char *name, double duration, int status);
 
long lr_set_transaction_instance(const char *name, double duration, int status, long parent_handle);


int   lr_user_data_point                      (char *, double);
long lr_user_data_point_instance                   (char *, double, long);
 



int lr_user_data_point_ex(const char *dp_name, double value, int log_flag);
long lr_user_data_point_instance_ex(const char *dp_name, double value, long parent_handle, int log_flag);


int lr_transaction_add_info      (const char *trans_name, char *info);
int lr_transaction_instance_add_info   (long trans_handle, char *info);
int lr_dpoint_add_info           (const char *dpoint_name, char *info);
int lr_dpoint_instance_add_info        (long dpoint_handle, char *info);


double lr_get_transaction_duration       (char * trans_name);
double lr_get_trans_instance_duration    (long trans_handle);
double lr_get_transaction_think_time     (char * trans_name);
double lr_get_trans_instance_think_time  (long trans_handle);
double lr_get_transaction_wasted_time    (char * trans_name);
double lr_get_trans_instance_wasted_time (long trans_handle);
int    lr_get_transaction_status		 (char * trans_name);
int	   lr_get_trans_instance_status		 (long trans_handle);

 



int lr_set_transaction_status(int status);

 



int lr_set_transaction_status_by_name(int status, const char *trans_name);
int lr_set_transaction_instance_status(int status, long trans_handle);


typedef void* merc_timer_handle_t;
 

merc_timer_handle_t lr_start_timer();
double lr_end_timer(merc_timer_handle_t timer_handle);


 
 
 
 
 
 











 



int   lr_rendezvous  (char * rendezvous_name);
 




int   lr_rendezvous_ex (char * rendezvous_name);



 
 
 
 
 
char *lr_get_vuser_ip (void);
void   lr_whoami (int *vuser_id, char ** sgroup, int *scid);
char *	  lr_get_host_name (void);
char *	  lr_get_master_host_name (void);

 
long     lr_get_attrib_long	(char * attr_name);
char *   lr_get_attrib_string	(char * attr_name);
double   lr_get_attrib_double      (char * attr_name);


 
 
static void *ci_this_context = 0;






 








void lr_continue_on_error (int lr_continue);
char *   lr_decrypt (const char *EncodedString);


 
 
 
 
 
 



 






 














void   lr_abort (void);
void lr_exit(int exit_option, int exit_status);
void lr_abort_ex (unsigned long flags);

void   lr_peek_events (void);


 
 
 
 
 


void   lr_think_time (double secs);

 


void lr_force_think_time (double secs);


 
 
 
 
 



















int   lr_msg (char * fmt, ...);
int   lr_debug_message (unsigned int msg_class,
									    char * format,
										...);
# 492 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
void   lr_new_prefix (int type,
                                 char * filename,
                                 int line);
# 495 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
int   lr_log_message (char * fmt, ...);
int   lr_message (char * fmt, ...);
int   lr_error_message (char * fmt, ...);
int   lr_output_message (char * fmt, ...);
int   lr_vuser_status_message (char * fmt, ...);
int   lr_error_message_without_fileline (char * fmt, ...);
int   lr_fail_trans_with_error (char * fmt, ...);

 
 
 
 
 
# 518 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"

 
 
 
 
 





int   lr_next_row ( char * table);
int lr_advance_param ( char * param);



														  
														  

														  
														  

													      
 


char *   lr_eval_string (char * str);
int   lr_eval_string_ext (const char *in_str,
                                     unsigned long const in_len,
                                     char ** const out_str,
                                     unsigned long * const out_len,
                                     unsigned long const options,
                                     const char *file,
								     long const line);
# 552 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
void   lr_eval_string_ext_free (char * * pstr);

 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
int lr_param_increment (char * dst_name,
                              char * src_name);
# 575 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"













											  
											  

											  
											  
											  

int	  lr_save_var (char *              param_val,
							  unsigned long const param_val_len,
							  unsigned long const options,
							  char *			  param_name);
# 599 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
int   lr_save_string (const char * param_val, const char * param_name);


 
 
 
 
 
 
# 664 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
void   lr_save_datetime (const char *format, int offset, const char *name);









 











 
 
 
 
 








 



char * lr_error_context_get_entry (char * key);

 



long   lr_error_context_get_error_id (void);

 
 
 
 
 
 
 
 

 
 
 
 
 
 
int   lr_param_substit (char * file,
                                   int const line,
                                   char * in_str,
                                   int const in_len,
                                   char * * const out_str,
                                   int * const out_len);
# 732 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
void   lr_param_substit_free (char * * pstr);


 
# 744 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"





char *   lrfnc_eval_string (char * str,
                                      char * file_name,
                                      long const line_num);
# 752 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"


int   lrfnc_save_string ( const char * param_val,
                                     const char * param_name,
                                     const char * file_name,
                                     long const line_num);
# 758 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"

int lr_save_searched_string(char *buffer, long buf_size, unsigned int occurrence,
			    char *search_string, int offset, unsigned int param_val_len, 
			    char *param_name);

 
char *   lr_string (char * str);

 
# 824 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"

int   lr_save_value (char * param_val,
                                unsigned long const param_val_len,
                                unsigned long const options,
                                char * param_name,
                                char * file_name,
                                long const line_num);
# 831 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"


 
 
 
 
 











int   lr_printf (char * fmt, ...);
 
int   lr_set_debug_message (unsigned int msg_class,
                                       unsigned int swtch);
# 853 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
unsigned int   lr_get_debug_message (void);


 
 
 
 
 

void   lr_double_think_time ( double secs);
void   lr_usleep (long);


 
 
 
 
 
 




int *   lr_localtime (long offset);


int   lr_send_port (long port);


# 929 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"



struct _lr_declare_identifier{
	char signature[24];
	char value[128];
};

int   lr_pt_abort (void);

void vuser_declaration (void);






# 958 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"


# 970 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrun.h"
















 
 
 
 
 







int    _lr_declare_transaction   (char * transaction_name);


 
 
 
 
 







int   _lr_declare_rendezvous  (char * rendezvous_name);

 
 
 
 
 

 
int lr_enable_ip_spoofing();
int lr_disable_ip_spoofing();








# 6 "globals.h" 2

# 1 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/web_api.h" 1







# 1 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/as_web.h" 1


























































 
 
 

  int
	web_add_filter(
		const char *		mpszArg,
		...
	);									 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 

  int
	web_add_auto_filter(
		const char *		mpszArg,
		...
	);									 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
	
  int
	web_add_auto_header(
		const char *		mpszHeader,
		const char *		mpszValue);

  int
	web_add_header(
		const char *		mpszHeader,
		const char *		mpszValue);
  int
	web_add_cookie(
		const char *		mpszCookie);
  int
	web_cleanup_auto_headers(void);
  int
	web_cleanup_cookies(void);
  int
	web_concurrent_end(
		const char * const	mpszReserved,
										 
		...								 
	);
  int
	web_concurrent_start(
		const char * const	mpszConcurrentGroupName,
										 
										 
		...								 
										 
	);
  int
	web_create_html_param(
		const char *		mpszParamName,
		const char *		mpszLeftDelim,
		const char *		mpszRightDelim);
  int
	web_create_html_param_ex(
		const char *		mpszParamName,
		const char *		mpszLeftDelim,
		const char *		mpszRightDelim,
		const char *		mpszNum);
  int
	web_custom_request(
		const char *		mpszReqestName,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
  int
	web_disable_keep_alive(void);
  int
	web_enable_keep_alive(void);
  int
	web_find(
		const char *		mpszStepName,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										 
  int
	web_get_int_property(
		const int			miHttpInfoType);
  int
	web_image(
		const char *		mpszStepName,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
  int
	web_image_check(
		const char *		mpszName,
		...);
  int
	web_java_check(
		const char *		mpszName,
		...);
  int
	web_link(
		const char *		mpszStepName,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 

	
  int
	web_global_verification(
		const char *		mpszArg1,
		...);							 
										 
										 
										 
										 
										 

  int
	web_reg_find(
		const char *		mpszArg1,
		...);							 
										 
										 
										 
										 
										 
										 
										 
				
  int
	web_reg_save_param(
		const char *		mpszParamName,
		...);							 
										 
										 
										 
										 
										 
										 

  int
	web_convert_param(
		const char * 		mpszParamName, 
										 
		...);							 
										 
										 


										 

										 
  int
	web_remove_auto_filter(
		const char *		mpszArg,
		...
	);									 
										 
				
  int
	web_remove_auto_header(
		const char *		mpszHeaderName,
		...);							 
										 



  int
	web_remove_cookie(
		const char *		mpszCookie);

  int
	web_save_header(
		const char *		mpszType,	 
		const char *		mpszName);	 
  int
	web_set_certificate(
		const char *		mpszIndex);
  int
	web_set_certificate_ex(
		const char *		mpszArg1,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
  int
	web_set_connections_limit(
		const char *		mpszLimit);
  int
	web_set_max_html_param_len(
		const char *		mpszLen);
  int
	web_set_max_retries(
		const char *		mpszMaxRetries);
  int
	web_set_proxy(
		const char *		mpszProxyHost);
  int
	web_set_proxy_bypass(
		const char *		mpszBypass);
  int
	web_set_secure_proxy(
		const char *		mpszProxyHost);
  int
	web_set_sockets_option(
		const char *		mpszOptionID,
		const char *		mpszOptionValue
	);
  int
	web_set_option(
		const char *		mpszOptionID,
		const char *		mpszOptionValue,
		...								 
	);
  int
	web_set_timeout(
		const char *		mpszWhat,
		const char *		mpszTimeout);
  int
	web_set_user(
		const char *		mpszUserName,
		const char *		mpszPwd,
		const char *		mpszHost);

  int
	web_sjis_to_euc_param(
		const char *		mpszParamName,
										 
		const char *		mpszParamValSjis);
										 

  int
	web_submit_data(
		const char *		mpszStepName,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
  int
	web_submit_form(
		const char *		mpszStepName,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										  
										 
										 
										 
										 
										 
										  
										 
										 
										 
										 
										 
										 
										 
										  
										 
										 
										 
										 
										 
										  
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
  int
	web_url(
		const char *		mpszUrlName,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 

  int 
	web_set_proxy_bypass_local(
		const char * mpszNoLocal
		);

  int 
	web_cache_cleanup(void);

  int
	web_create_html_query(
		const char* mpszStartQuery,
		...);							 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 
										 

  int 
	web_create_radio_button_param(
				const char *NameFiled,
				const char *NameAndVal,
				const char *ParamName
				);











# 530 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/as_web.h"


# 543 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/as_web.h"



























# 581 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/as_web.h"

 
 
 


  int
	FormSubmit(
		const char *		mpszFormName,
		...);
  int
	InitWebVuser(void);
  int
	SetUser(
		const char *		mpszUserName,
		const char *		mpszPwd,
		const char *		mpszHost);
  int
	TerminateWebVuser(void);
  int
	URL(
		const char *		mpszUrlName);
























# 649 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/as_web.h"



 
 
 









# 9 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/web_api.h" 2



















  int
	web_reg_add_cookie(
		const char *		mpszCookie,
		...);							 
										 

  int
	web_report_data_point(
		const char *		mpszEventType,
		const char *		mpszEventName,
		const char *		mpszDataPointName,
		const char *		mpszLAST);	 
										 
										 
										 

  int
	web_text_link(
		const char *		mpszStepName,
		...);

  int
	web_image_link(
		const char *		mpszStepName,
		...);

  int
	web_button(
		const char *		mpszStepName,
		...);


  int
	web_edit_field(
		const char *		mpszStepName,
		...);


  int
	web_radio_group(
		const char *		mpszStepName,
		...);


  int
	web_list(
		const char *		mpszStepName,
		...);


  int
	web_text_area(
		const char *		mpszStepName,
		...);

  int
	web_save_param_length(
		const char * 		mpszParamName,
		...);

  int
	web_save_timestamp_param(
		const char * 		mpszParamName,
		...);





 
 
 





# 7 "globals.h" 2

# 1 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrw_custom_body.h" 1
 





# 8 "globals.h" 2


 
 


# 1 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_loan_officer\\\\combined_Create_Loan_Officer.c" 2


# 1 "vuser_init.c" 1

# 1 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrw_custom_body.h" 1
 





# 2 "vuser_init.c" 2



 

int i=0;                    
char anyString[10];
int j;
int lastname=1;  
int office_Id;

vuser_init()
{
	return 0;
}


# 3 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_loan_officer\\\\combined_Create_Loan_Officer.c" 2

# 1 "Create_Loan_Officer.c" 1
 
 
 
 
 



Create_Loan_Officer()
{
	web_set_max_html_param_len("1024");

 


	web_reg_save_param("JSESSIONID2",
		"LB/IC=jsessionid=",
		"RB/IC=\"",
		"Ord=1",
		"RelFrameId=1",
		"Search=body",
		"LAST");

	 
	web_reg_save_param("WCSParam_Diff1",
		"LB=Set-Cookie: JSESSIONID=",
		"RB=;",
		"Ord=1",
		"RelFrameId=1",
		"Search=Headers",
		"LAST");

	web_url("loginAction.do",
		"URL=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t1.inf",
		"Mode=HTML",
		"LAST");
	lr_think_time(11);


 
     
	lr_think_time(8);

	web_submit_data("loginAction.do;jsessionid={WCSParam_Diff1}",
		"Action=http://9.161.154.46:8080/mifos/loginAction.do;jsessionid={JSESSIONID2}",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"Snapshot=t2.inf",
		"Mode=HTML",
		"ITEMDATA",
		"Name=userName", "Value=mifos", "ENDITEM",
		"Name=password", "Value=testmifos", "ENDITEM",
		"Name=method", "Value=login", "ENDITEM",
		"LAST");

 


	 
	web_reg_save_param("WCSParam_Diff2",
		"LB=randomNUm=",
		"RB=\"",
		"Ord=1",
		"RelFrameId=1",
		"Search=Body",
		"LAST");

	web_url("Admin",
		"URL=http://9.161.154.46:8080/mifos/AdminAction.do?method=load",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t3.inf",
		"Mode=HTML",
		"LAST");


	lr_think_time(5);


 
	 

for (j=2;j<=7;j++) {      
        
		office_Id=j;
		itoa( office_Id,anyString,10 );                 
		lr_save_string( anyString,"office_Id1" );
		lr_output_message("office_Id1");

		for (i=0;i<5;i++) {                 

                lastname = lastname + 1;  
				lr_output_message("The loanofficer_lastname is : %d", lastname);

				itoa( lastname, anyString,10 );             
				lr_save_string( anyString,"lastname1" );    
				lr_output_message("The office_Id is: %d", office_Id);


	 
	web_reg_save_param("WCSParam_Diff3",
		"LB=currentFlowKey=",
		"RB=\"",
		"Ord=All",
		"RelFrameId=1",
		"Search=Body",
		"LAST");
	web_url("Define new system user",
		"URL=http://9.161.154.46:8080/mifos/PersonAction.do?method=chooseOffice&recordOfficeId={office_Id1}&recordLoanOfficerId=1&randomNUm={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/AdminAction.do?method=load",
		"Snapshot=t4.inf",
		"Mode=HTML",
		"LAST");


 
	 

	web_url("BRANCH_OFFICE_1",
		"URL=http://9.161.154.46:8080/mifos/PersonAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff3}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/PersonAction.do?method=chooseOffice&recordOfficeId=1&recordLoanOfficerId=1&randomNUm={WCSParam_Diff2}",
		"Snapshot=t5.inf",
		"Mode=HTML",
		"LAST");


 
	 

	web_submit_data("PersonAction.do",
		"Action=http://9.161.154.46:8080/mifos/PersonAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/PersonAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff3}",
		"Snapshot=t6.inf",
		"Mode=HTML",
		"ITEMDATA",
		"Name=firstName", "Value=mifos_officer", "ENDITEM",
		"Name=middleName", "Value=", "ENDITEM",
		"Name=secondLastName", "Value=", "ENDITEM",
		"Name=lastName", "Value={lastname1}", "ENDITEM",
		"Name=governmentIdNumber", "Value=", "ENDITEM",
		"Name=emailId", "Value=", "ENDITEM",
		"Name=dobDD", "Value=12", "ENDITEM",
		"Name=dobMM", "Value=12", "ENDITEM",
		"Name=dobYY", "Value=1970", "ENDITEM",
		"Name=maritalStatus", "Value=", "ENDITEM",
		"Name=gender", "Value=49", "ENDITEM",
		"Name=preferredLocale", "Value=1", "ENDITEM",
		"Name=dateOfJoiningMFIDD", "Value=5", "ENDITEM",
		"Name=dateOfJoiningMFIMM", "Value=12", "ENDITEM",
		"Name=dateOfJoiningMFIYY", "Value=2007", "ENDITEM",
		"Name=dateOfJoiningMFI", "Value=5/12/2007", "ENDITEM",
		"Name=dateOfJoiningMFIFormat", "Value=D/M/Y", "ENDITEM",
		"Name=datePattern", "Value=dd/MM/yy", "ENDITEM",
		"Name=address.line1", "Value=Some Street", "ENDITEM",
		"Name=address.line2", "Value=Some Town", "ENDITEM",
		"Name=address.line3", "Value=", "ENDITEM",
		"Name=address.city", "Value=Some City", "ENDITEM",
		"Name=address.state", "Value=Some State", "ENDITEM",
		"Name=address.country", "Value=Some Country", "ENDITEM",
		"Name=address.zip", "Value=", "ENDITEM",
		"Name=address.phoneNumber", "Value=00353198765432", "ENDITEM",
		"Name=title", "Value=", "ENDITEM",
		"Name=level", "Value=1", "ENDITEM",
		"Name=personnelRoles", "Value=1", "ENDITEM",
		"Name=loginName", "Value=mifos_officer{lastname1}", "ENDITEM",
		"Name=userPassword", "Value=112233", "ENDITEM",
		"Name=passwordRepeat", "Value=112233", "ENDITEM",
		"Name=customField[0].fieldValue", "Value=", "ENDITEM",
		"Name=input", "Value=CreateUser", "ENDITEM",
		"Name=method", "Value=preview", "ENDITEM",
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", "ENDITEM",
		"LAST");
	lr_think_time(5);


 
	 

	lr_think_time(7);

	 
	web_reg_save_param("WCSParam_Diff4",
		"LB=randomNUm=",
		"RB=\"",
		"Ord=1",
		"RelFrameId=1",
		"Search=Body",
		"LAST");

	web_submit_data("PersonAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/PersonAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/PersonAction.do",
		"Snapshot=t7.inf",
		"Mode=HTML",
		"ITEMDATA",
		"Name=input", "Value=CreateUser", "ENDITEM",
		"Name=method", "Value=create", "ENDITEM",
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", "ENDITEM",
		"LAST");
	lr_think_time(5);

 
     
	lr_think_time(4);

	web_url("Add a new user",
		"URL=http://9.161.154.46:8080/mifos/PersonAction.do?method=chooseOffice&randomNUm={WCSParam_Diff4}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/PersonAction.do",
		"Snapshot=t8.inf",
		"Mode=HTML",
		"LAST");

		}    

    }    


	return 0;
}



# 4 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_loan_officer\\\\combined_Create_Loan_Officer.c" 2

# 1 "vuser_end.c" 1
vuser_end()
{
	return 0;
}
# 5 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_loan_officer\\\\combined_Create_Loan_Officer.c" 2

