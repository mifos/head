// Author *Venkat*
// Creating Branch offices eg BRANCH_OFFICE_1, BRANCH_OFFICE_2 etc for a Medium MFI.
// Specify no of the Branches u want to create in the for loop.

#include "as_web.h"

Create_Branch_Office()
{
         
	web_set_max_html_param_len("1024");

/*  Registering parameter(s) from source task id 2
	{JSESSIONID2} = "A2B89F094D20619E48D3E216B449B446"
*/

	web_reg_save_param("JSESSIONID2",

		"LB/IC=jsessionid=",
		"RB/IC=\"",
		"Ord=1",
		"RelFrameId=1",
		"Search=body",
		LAST);

	// [WCSPARAM WCSParam_Diff1 32 A2B89F094D20619E48D3E216B449B446] Parameter {WCSParam_Diff1} created by Correlation Studio
        "LB=Set-Cookie: JSESSIONID=",
		"RB=;",
		"Ord=1",
		"RelFrameId=1",
		"Search=Headers",
		LAST;
	web_url("loginAction.do",
		"URL=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t1.inf",
		"Mode=HTML",
		LAST);

//	 Log Into Application
	 
	lr_think_time(3);

	web_submit_data("loginAction.do;jsessionid={WCSParam_Diff1}",
		"Action=http://9.161.154.46:8080/mifos/loginAction.do;jsessionid={JSESSIONID2}",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"Snapshot=t2.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=userName", "Value=mifos", ENDITEM,
		"Name=password", "Value=testmifos", ENDITEM,
		"Name=method", "Value=login", ENDITEM,
		LAST);


//	 Go To Admin Tab
	 

	// [WCSPARAM WCSParam_Diff2 19 7349999441098444999] Parameter {WCSParam_Diff2} created by Correlation Studio
	web_reg_save_param("WCSParam_Diff2",
		"LB=randomNUm=",
		"RB=\"",
		"Ord=1",
		"RelFrameId=1",
		"Search=Body",
		LAST);
	web_url("Admin",
		"URL=http://9.161.154.46:8080/mifos/AdminAction.do?method=load",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t3.inf",
		"Mode=HTML",
		LAST);

//	 Click "Define a new office" link

	for (i=1;i<=45;i++) {

		office_num = office_num + 1;  
		lr_output_message ( "The office_num is : %d", office_num);

		itoa(office_num,anyString,10);        //Converts an integer to a string
		lr_save_string( anyString, "office_num2");


	// [WCSPARAM WCSParam_Diff3 13 1196873259203] Parameter {WCSParam_Diff3} created by Correlation Studio
	web_reg_save_param("WCSParam_Diff3",
		"LB= value=\"",
		"RB=\"",
		"Ord=24",
		"RelFrameId=1",
		"Search=Body",
		LAST);
	web_url("Define a new office",
		"URL=http://9.161.154.46:8080/mifos/offAction.do?method=load&randomNUm={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/AdminAction.do?method=load",
		"Snapshot=t4.inf",
		"Mode=HTML",
		LAST);

//  Enter Office Information
	 
	lr_think_time(3);

	
	web_submit_data("offAction.do",
		"Action=http://9.161.154.46:8080/mifos/offAction.do?method=loadParent",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/offAction.do?method=load&randomNUm={WCSParam_Diff2}",
		"Snapshot=t5.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=officeName", "Value=BRANCH_OFFICE_{office_num2}", ENDITEM,
		"Name=shortName", "Value=BO{office_num2}", ENDITEM,
		"Name=officeLevel", "Value=5", ENDITEM,
		"Name=parentOfficeId", "Value=", ENDITEM,
		"Name=address.line1", "Value=", ENDITEM,
		"Name=address.line2", "Value=", ENDITEM,
		"Name=address.line3", "Value=", ENDITEM,
		"Name=address.city", "Value=", ENDITEM,
		"Name=address.state", "Value=", ENDITEM,
		"Name=address.country", "Value=", ENDITEM,
		"Name=address.zip", "Value=", ENDITEM,
		"Name=address.phoneNumber", "Value=", ENDITEM,
		"Name=customField[0].fieldValue", "Value=", ENDITEM,
		"Name=customField[1].fieldValue", "Value=", ENDITEM,
		"Name=customField[2].fieldValue", "Value=", ENDITEM,
		"Name=customField[3].fieldValue", "Value=", ENDITEM,
		"Name=input", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		LAST);
	lr_think_time(3);

	web_submit_data("offAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/offAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/offAction.do?method=loadParent",
		"Snapshot=t6.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=officeName", "Value=BRANCH_OFFICE_{office_num2}", ENDITEM,
		"Name=shortName", "Value=BO{office_num2}", ENDITEM,
		"Name=officeLevel", "Value=5", ENDITEM,
		"Name=parentOfficeId", "Value=1", ENDITEM,
		"Name=address.line1", "Value=Some Street ", ENDITEM,
		"Name=address.line2", "Value=Some Town", ENDITEM,
		"Name=address.line3", "Value=", ENDITEM,
		"Name=address.city", "Value=Some City", ENDITEM,
		"Name=address.state", "Value=Some State", ENDITEM,
		"Name=address.country", "Value=Some Country", ENDITEM,
		"Name=address.zip", "Value=", ENDITEM,
		"Name=address.phoneNumber", "Value=0035319876543", ENDITEM,
		"Name=customField[0].fieldValue", "Value=", ENDITEM,
		"Name=customField[1].fieldValue", "Value=10", ENDITEM,
		"Name=customField[2].fieldValue", "Value=10", ENDITEM,
		"Name=customField[3].fieldValue", "Value=", ENDITEM,
		"Name=input", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		LAST);

//  Review & Submit
	 
	lr_think_time(3);

	web_submit_data("offAction.do_3",
		"Action=http://9.161.154.46:8080/mifos/offAction.do?method=create",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/offAction.do?method=preview",
		"Snapshot=t7.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		LAST);

	lr_think_time(2);

//	Success Screen

	}	 

	return 0;
}



