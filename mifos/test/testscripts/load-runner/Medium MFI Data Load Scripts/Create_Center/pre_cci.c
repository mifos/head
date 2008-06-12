<<<<<<< .mine
# 1 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c"
=======
# 1 "c:\\mifos\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c"
>>>>>>> .r274
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








<<<<<<< .mine
# 1 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c" 2
=======
# 1 "c:\\mifos\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c" 2
>>>>>>> .r274

# 1 "vuser_init.c" 1

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





 
 
 





# 2 "vuser_init.c" 2

# 1 "C:\\Program Files\\Mercury Interactive\\LoadRunner\\include/lrw_custom_body.h" 1
 





# 3 "vuser_init.c" 2



int i=0;                      	 
int j;
char anyString[10]; 			 
int office_Id;
int center_name=25;      
int loan_officer_Id;		 
int Length;
char *Found;

vuser_init()
{
	return 0;
}
<<<<<<< .mine
# 2 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c" 2
=======
# 2 "c:\\mifos\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c" 2
>>>>>>> .r274

# 1 "Create_Center.c" 1
 

Create_Center()
{
<<<<<<< .mine
   
=======
	 



	char stringFound2[64]="\0";
    char WCSParam_Diff4_2[256]="\0";

>>>>>>> .r274
	loan_officer_Id=2;      


	 
	web_reg_save_param( "WCSParam_Diff1", "LB=Set-Cookie: JSESSIONID=", "RB=;", "Ord=1", "Search=Headers", "RelFrameId=1", "LAST" );
	web_url("loginAction.do",
		"URL=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t11.inf",
		"Mode=HTML",
		"EXTRARES",
		
		"LAST");

	lr_think_time( 10 );

	 



	lr_think_time( 10 );

	web_submit_data("loginAction.do;jsessionid={WCSParam_Diff1}",
		"Action=http://9.161.154.46:8080/mifos/loginAction.do;jsessionid={WCSParam_Diff1}",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"Snapshot=t12.inf",
		"Mode=HTML",
		"ITEMDATA",
		"Name=userName", "Value=mifos", "ENDITEM",
		"Name=password", "Value=testmifos", "ENDITEM",
		"Name=method", "Value=login", "ENDITEM",
		"EXTRARES",
		
		"LAST");

	lr_think_time( 6 );

	 



	web_url("Clients & Accounts",
		"URL=http://9.161.154.46:8080/mifos/custSearchAction.do?method=loadMainSearch",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t13.inf",
		"Mode=HTML",
		"EXTRARES",
		"LAST");

	 



	 
	for (j=2;j<=6;j++) {

      	office_Id=j;
		itoa( office_Id,anyString,10 );                   
		lr_save_string( anyString,"office_Id1" );
		lr_output_message("Present Office_Id: %d", office_Id);  
		
        itoa( loan_officer_Id,anyString,10 );                 
		lr_save_string( anyString,"loan_officer_Id1" );
		lr_output_message("Present loan_Officer_Id: %d", loan_officer_Id); 

         

			for (i=1;i<=5;i++) {

				itoa( loan_officer_Id,anyString,10 );                
				lr_save_string( anyString,"loan_officer_Id1" );
				lr_output_message("Present loan_Officer_Id: %d", loan_officer_Id);

				center_name = center_name + 1;  
				lr_output_message("center_name is : %d", center_name);

				itoa( center_name, anyString,10 );             
				lr_save_string( anyString,"center_name1" );    

					 
						 
																   
                     
				


	 
	web_reg_save_param( "WCSParam_Diff2", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", "LAST" );
	web_url("Create new Center",
		"URL=http://9.161.154.46:8080/mifos/centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/custSearchAction.do?method=loadMainSearch",
		"Snapshot=t14.inf",
		"Mode=HTML",
		"EXTRARES",
		
		"LAST");


	 



	 
	web_reg_save_param( "WCSParam_Diff3_2", "LB=currentFlowKey", "RB=h_user_locale", "Ord=1", "Search=Body", "RelFrameId=1", "LAST" );
	web_url("BRANCH_OFFICE_1",
		"URL=http://9.161.154.46:8080/mifos/centerCustAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0",
		"Snapshot=t15.inf",
		"Mode=HTML",
		"EXTRARES",
	
		"LAST");
		
		Length =  strlen(strstr(lr_eval_string ( "{WCSParam_Diff3_2}"), "value=\"")) -  strlen(strstr(lr_eval_string ( "{WCSParam_Diff3_2}"), "\">"));
		Found= (char *) strncpy ( stringFound2, (strstr(lr_eval_string ( "{WCSParam_Diff3_2}"), "value=\""))+7, (Length-7)); 
		lr_save_string(Found, "WCSParam_Diff3" );
		lr_output_message("WCSParam_Diff3 = %s", lr_eval_string ( "{WCSParam_Diff3}"));

	 



	lr_think_time( 10 );

	web_submit_data("centerCustAction.do",
		"Action=http://9.161.154.46:8080/mifos/centerCustAction.do?method=loadMeeting",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff2}",
		"Snapshot=t16.inf",
		"Mode=HTML",
		"ITEMDATA",
		"Name=input", "Value=create", "ENDITEM",
		"Name=displayName", "Value=CENTER_{center_name1}", "ENDITEM",
		"Name=loanOfficerId", "Value={loan_officer_Id1}", "ENDITEM",
		"Name=externalId", "Value=", "ENDITEM",
		"Name=mfiJoiningDateDD", "Value=16", "ENDITEM",
		"Name=mfiJoiningDateMM", "Value=1", "ENDITEM",
		"Name=mfiJoiningDateYY", "Value=2008", "ENDITEM",
		"Name=address.line1", "Value=", "ENDITEM",
		"Name=address.line2", "Value=", "ENDITEM",
		"Name=address.line3", "Value=", "ENDITEM",
		"Name=address.city", "Value=", "ENDITEM",
		"Name=address.state", "Value=", "ENDITEM",
		"Name=address.country", "Value=", "ENDITEM",
		"Name=address.zip", "Value=", "ENDITEM",
		"Name=address.phoneNumber", "Value=", "ENDITEM",
		"Name=selectedFee[0].feeId", "Value=", "ENDITEM",
		"Name=selectedFee[0].amount", "Value=", "ENDITEM",
		"Name=selectedFee[1].feeId", "Value=", "ENDITEM",
		"Name=selectedFee[1].amount", "Value=", "ENDITEM",
		"Name=selectedFee[2].feeId", "Value=", "ENDITEM",
		"Name=selectedFee[2].amount", "Value=", "ENDITEM",
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", "ENDITEM",
		"EXTRARES",
		

		"LAST");


	 



	lr_think_time( 11 );

	web_submit_data("meetingAction.do",
		"Action=http://9.161.154.46:8080/mifos/meetingAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=loadMeeting",
		"Snapshot=t17.inf",
		"Mode=HTML",
		"ITEMDATA",
		"Name=frequency", "Value=1", "ENDITEM",
		"Name=recurWeek", "Value=1", "ENDITEM",
		"Name=weekDay", "Value=7", "ENDITEM",
		"Name=monthDay", "Value=", "ENDITEM",
		"Name=dayRecurMonth", "Value=", "ENDITEM",
		"Name=monthRank", "Value=", "ENDITEM",
		"Name=monthWeek", "Value=", "ENDITEM",
		"Name=recurMonth", "Value=", "ENDITEM",
		"Name=meetingPlace", "Value=Center Street", "ENDITEM",
		"Name=method", "Value=create", "ENDITEM",
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", "ENDITEM",
		"EXTRARES",
		
		"LAST");

	lr_think_time( 10 );

	web_submit_data("centerCustAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/centerCustAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/meetingAction.do",
		"Snapshot=t18.inf",
		"Mode=HTML",
		"ITEMDATA",
		"Name=input", "Value=create", "ENDITEM",
		"Name=displayName", "Value=CENTER_{center_name1}", "ENDITEM",
		"Name=loanOfficerId", "Value={loan_officer_Id1}", "ENDITEM",
		"Name=externalId", "Value=", "ENDITEM",
		"Name=mfiJoiningDateDD", "Value=23", "ENDITEM",
		"Name=mfiJoiningDateMM", "Value=1", "ENDITEM",
		"Name=mfiJoiningDateYY", "Value=2008", "ENDITEM",
		"Name=address.line1", "Value=Center Street", "ENDITEM",
		"Name=address.line2", "Value=", "ENDITEM",
		"Name=address.line3", "Value=", "ENDITEM",
		"Name=address.city", "Value=Center City", "ENDITEM",
		"Name=address.state", "Value=", "ENDITEM",
		"Name=address.country", "Value=", "ENDITEM",
		"Name=address.zip", "Value=", "ENDITEM",
		"Name=address.phoneNumber", "Value=", "ENDITEM",
		"Name=selectedFee[0].feeId", "Value=", "ENDITEM",
		"Name=selectedFee[0].amount", "Value=", "ENDITEM",
		"Name=selectedFee[1].feeId", "Value=", "ENDITEM",
		"Name=selectedFee[1].amount", "Value=", "ENDITEM",
		"Name=selectedFee[2].feeId", "Value=", "ENDITEM",
		"Name=selectedFee[2].amount", "Value=", "ENDITEM",
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", "ENDITEM",
		"EXTRARES",
		
		"LAST");


	 



	lr_think_time( 6 );

	web_submit_data("centerCustAction.do_3",
		"Action=http://9.161.154.46:8080/mifos/centerCustAction.do?method=create",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=preview",
		"Snapshot=t19.inf",
		"Mode=HTML",
		"ITEMDATA",
		"Name=input", "Value=create", "ENDITEM",
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", "ENDITEM",
		"EXTRARES",
		
		"LAST");

	lr_think_time( 5 );

	 



	web_url("centerCustAction.do_4",
		"URL=http://9.161.154.46:8080/mifos/centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0&randomNUm=",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=create",
		"Snapshot=t21.inf",
		"Mode=HTML",
		"EXTRARES",
	
		"LAST");

			loan_officer_Id = loan_officer_Id + 1;  
												    

		}    
			 
             

	
	}    

	return 0;
}
<<<<<<< .mine
# 3 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c" 2
=======
# 3 "c:\\mifos\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c" 2
>>>>>>> .r274

# 1 "vuser_end.c" 1



vuser_end()
{
	  return 0;
}
<<<<<<< .mine
# 4 "c:\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c" 2
=======
# 4 "c:\\mifos\\mifos ibm\\trunk\\documentation\\03 functional architecture\\08 test model\\medium mfi data load scripts\\create_center\\\\combined_Create_Center.c" 2
>>>>>>> .r274

