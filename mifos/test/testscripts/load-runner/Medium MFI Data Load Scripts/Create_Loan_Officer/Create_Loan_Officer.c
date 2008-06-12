// Author *Venkat*
// Script for creating Loan officers
// For each Branch office create 5 Loan officers. 
// ie each Branch office has got 5 Loan officers assigned. this script creates loan officers for
// 45 branch offices.

#include "as_web.h"

Create_Loan_Officer()
{
	web_set_max_html_param_len("1024");

/* Registering parameter(s) from source task id 2
	// {JSESSIONID2} = "CE6B2F07EA4974BEE586BF3CABCADC13"
	// */
	web_reg_save_param("JSESSIONID2",
		"LB/IC=jsessionid=",
		"RB/IC=\"",
		"Ord=1",
		"RelFrameId=1",
		"Search=body",
		LAST);

	// [WCSPARAM WCSParam_Diff1 32 CE6B2F07EA4974BEE586BF3CABCADC13] Parameter {WCSParam_Diff1} created by Correlation Studio
	web_reg_save_param("WCSParam_Diff1",
		"LB=Set-Cookie: JSESSIONID=",
		"RB=;",
		"Ord=1",
		"RelFrameId=1",
		"Search=Headers",
		LAST);

	web_url("loginAction.do",
		"URL=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t1.inf",
		"Mode=HTML",
		LAST);
	lr_think_time(11);


//	  Log in to the application
     
	lr_think_time(8);

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

// Go to Admin Tab

	// [WCSPARAM WCSParam_Diff2 19 6783662536277500211] Parameter {WCSParam_Diff2} created by Correlation Studio
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


	lr_think_time(5);


// Click "Define a New System User" link
	 

for (j=2;j<=7;j++) {     //Branch office Id loop
        
		office_Id=j;
		itoa( office_Id,anyString,10 );                //Converts an integer to a string
		lr_save_string( anyString,"office_Id1" );
		lr_output_message("office_Id1");

		for (i=0;i<5;i++) {                //Loan officer Id loop. 5 loan officers per branch.

                lastname = lastname + 1;  
				lr_output_message("The loanofficer_lastname is : %d", lastname);

				itoa( lastname, anyString,10 );            //Converts an integer to a string
				lr_save_string( anyString,"lastname1" );   //Saving anystring in variable client_lastname1
				lr_output_message("The office_Id is: %d", office_Id);


	// [WCSPARAM WCSParam_Diff3 13 1196895996343] Parameter {WCSParam_Diff3} created by Correlation Studio
	web_reg_save_param("WCSParam_Diff3",
		"LB=currentFlowKey=",
		"RB=\"",
		"Ord=All",
		"RelFrameId=1",
		"Search=Body",
		LAST);
	web_url("Define new system user",
		"URL=http://9.161.154.46:8080/mifos/PersonAction.do?method=chooseOffice&recordOfficeId={office_Id1}&recordLoanOfficerId=1&randomNUm={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/AdminAction.do?method=load",
		"Snapshot=t4.inf",
		"Mode=HTML",
		LAST);


// Select Branch Office to assign Loan officer to
	 

	web_url("BRANCH_OFFICE_1",
		"URL=http://9.161.154.46:8080/mifos/PersonAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff3}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/PersonAction.do?method=chooseOffice&recordOfficeId=1&recordLoanOfficerId=1&randomNUm={WCSParam_Diff2}",
		"Snapshot=t5.inf",
		"Mode=HTML",
		LAST);


//	 Enter New User Information
	 

	web_submit_data("PersonAction.do",
		"Action=http://9.161.154.46:8080/mifos/PersonAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/PersonAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff3}",
		"Snapshot=t6.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=firstName", "Value=mifos_officer", ENDITEM,
		"Name=middleName", "Value=", ENDITEM,
		"Name=secondLastName", "Value=", ENDITEM,
		"Name=lastName", "Value={lastname1}", ENDITEM,
		"Name=governmentIdNumber", "Value=", ENDITEM,
		"Name=emailId", "Value=", ENDITEM,
		"Name=dobDD", "Value=12", ENDITEM,
		"Name=dobMM", "Value=12", ENDITEM,
		"Name=dobYY", "Value=1970", ENDITEM,
		"Name=maritalStatus", "Value=", ENDITEM,
		"Name=gender", "Value=49", ENDITEM,
		"Name=preferredLocale", "Value=1", ENDITEM,
		"Name=dateOfJoiningMFIDD", "Value=5", ENDITEM,
		"Name=dateOfJoiningMFIMM", "Value=12", ENDITEM,
		"Name=dateOfJoiningMFIYY", "Value=2007", ENDITEM,
		"Name=dateOfJoiningMFI", "Value=5/12/2007", ENDITEM,
		"Name=dateOfJoiningMFIFormat", "Value=D/M/Y", ENDITEM,
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM,
		"Name=address.line1", "Value=Some Street", ENDITEM,
		"Name=address.line2", "Value=Some Town", ENDITEM,
		"Name=address.line3", "Value=", ENDITEM,
		"Name=address.city", "Value=Some City", ENDITEM,
		"Name=address.state", "Value=Some State", ENDITEM,
		"Name=address.country", "Value=Some Country", ENDITEM,
		"Name=address.zip", "Value=", ENDITEM,
		"Name=address.phoneNumber", "Value=00353198765432", ENDITEM,
		"Name=title", "Value=", ENDITEM,
		"Name=level", "Value=1", ENDITEM,
		"Name=personnelRoles", "Value=1", ENDITEM,
		"Name=loginName", "Value=mifos_officer{lastname1}", ENDITEM,
		"Name=userPassword", "Value=112233", ENDITEM,
		"Name=passwordRepeat", "Value=112233", ENDITEM,
		"Name=customField[0].fieldValue", "Value=", ENDITEM,
		"Name=input", "Value=CreateUser", ENDITEM,
		"Name=method", "Value=preview", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		LAST);
	lr_think_time(5);


//	  Click Submit
	 

	lr_think_time(7);

	// [WCSPARAM WCSParam_Diff4 20 -3300905288780668842] Parameter {WCSParam_Diff4} created by Correlation Studio
	web_reg_save_param("WCSParam_Diff4",
		"LB=randomNUm=",
		"RB=\"",
		"Ord=1",
		"RelFrameId=1",
		"Search=Body",
		LAST);

	web_submit_data("PersonAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/PersonAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/PersonAction.do",
		"Snapshot=t7.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=CreateUser", ENDITEM,
		"Name=method", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		LAST);
	lr_think_time(5);

//	 Click Add a New User
     
	lr_think_time(4);

	web_url("Add a new user",
		"URL=http://9.161.154.46:8080/mifos/PersonAction.do?method=chooseOffice&randomNUm={WCSParam_Diff4}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/PersonAction.do",
		"Snapshot=t8.inf",
		"Mode=HTML",
		LAST);

		}   // Loan officer id loop (1 to 5 users)

    }   // Branch office loop (2 to 46 office ids)


	return 0;
}



