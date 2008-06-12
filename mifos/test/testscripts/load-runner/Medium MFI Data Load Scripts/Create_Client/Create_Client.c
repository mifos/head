#include "as_web.h"

// Author Venkat
/* This script creates CLIENTS as well as SAVINGS_ACCOUNT for them. 
   Clients are assigned to different BRANCH OFFICES and different LOAN OFFICERS. 
   Clients numbers, Branch offices IDs and Loan officers IDs are incremented in the loops. 
   Each branch office has got 5 loan officers (mifos_officer_1, etc)
   (eg BRANCH_OFFICE_1 is assigned 2 ID and BRANCH_OFFICE_3 is assigned 4 ID)
*/
// branch office ID = 2 has got loan officers IDs 2 - 6  assigned
// branch office ID = 3 has got loan officers IDs 7 - 11  assigned and so on...

/*  Business Process : 
    User with Admin access or Loan Officer user logs into Mifos.
	On Home Tab, from Quickstart menu click "Create New Client" link
	On select Group screen click this link "Click here to continue if Group membership is not required for your Client"
	NOTE : *The above link creates a Client directly associated to a Branch Office*
	Select a Branch Office on next screen to assign Client to
	Enter Clients Personal information and click Continue
	Enter the MFI information and click Preview
	Review and Submit for Approval	
	Edit Client Status to make it Active
*/


Create_Client()
{

	char stringFound2[64]="\0";
    char WCSParam_Diff4_2[256]="\0";

	loan_officer_Id=40;     //'2' loan officer ID is associated with branch office_Id 2

	web_set_max_html_param_len("1024");

	/* Registering parameter(s) from source task id 2
	// {JSESSIONID2} = "2E759A56E54F115724A3758EEB70BC24"
	// */
	web_reg_save_param("JSESSIONID2",
		"LB/IC=jsessionid=",
		"RB/IC=\"",
		"Ord=1",
		"Search=body",
		"RelFrameId=1",
		LAST);

	// [WCSPARAM WCSParam_Diff1 32 2E759A56E54F115724A3758EEB70BC24] Parameter {WCSParam_Diff1} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff1", "LB=Set-Cookie: JSESSIONID=", "RB=;", "Ord=1", "Search=Headers", "RelFrameId=1", LAST );
	web_url("loginAction.do",
		"URL=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t1.inf",
		"Mode=HTML",
		EXTRARES,
	
		LAST);

	lr_think_time( 8 );

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
		EXTRARES,
		LAST);

	lr_think_time( 6 );

	/*
	 * click Create New Client from Quickstart menu
	 */

	// [WCSPARAM WCSParam_Diff2 13 1196957751718] Parameter {WCSParam_Diff2} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff2", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("Create new Client",
		"URL=http://9.161.154.46:8080/mifos/groupCustAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=createClient",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t3.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 10 );

	/*
	 * 
	 * Click here to continue if Group membership is not required for your Client. 
	 */

//Loop for incrementing Branch Office IDs   (eg Branch office ID 2 is BRANCH_OFFICE_1 )
for (j=13;j<=17;j++) {

      	office_Id=j;
		itoa( office_Id,anyString,10 );                  //Converts an integer to a string
		lr_save_string( anyString,"office_Id1" );
		lr_output_message("Present Office_Id: %d", office_Id); //Prints office ID
		
        itoa( loan_officer_Id,anyString,10 );                //Converts an integer to a string
		lr_save_string( anyString,"loan_officer_Id1" );
		lr_output_message("Present loan_Officer_Id: %d", loan_officer_Id);//prints Loanofficer ID

        // Loop for Creating Clients/Customers and Savings Account for each Branch Office

			for (i=1;i<=5;i++) {

				itoa( loan_officer_Id,anyString,10 );                
				lr_save_string( anyString,"loan_officer_Id1" );
				lr_output_message("Present loan_Officer_Id: %d", loan_officer_Id);

				client_lastname = client_lastname + 1;  
				lr_output_message("Client_lastname is : %d", client_lastname);

				itoa( client_lastname, anyString,10 );            //Converts an integer to a string
				lr_save_string( anyString,"client_lastname1" );   //saving anystring in variable client_lastname1

					//if (i==2) {
						//loan_officer_Id = loan_officer_Id + 1;    // Incrementing loan officers so that the clients
																  // are not assigned to only one loan officer
                     
				

	// [WCSPARAM WCSParam_Diff3 13 1196957769375] Parameter {WCSParam_Diff3} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff3", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("Click here to continue if Group membership is not required for your Client.",
		"URL=http://9.161.154.46:8080/mifos/clientCustAction.do?method=chooseOffice&groupFlag=0&currentFlowKey={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/groupCustAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=createClient",
		"Snapshot=t4.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);


	/*
	 * select Branch Office 
	 */

	// [WCSPARAM WCSParam_Diff4 13 1196957781921] Parameter {WCSParam_Diff4} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff4_2", "LB=currentFlowKey", "RB=h_user_locale", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("BRANCH_OFFICE_1",
		"URL=http://9.161.154.46:8080/mifos/clientCustAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff3}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=chooseOffice&groupFlag=0&currentFlowKey={WCSParam_Diff2}",
		"Snapshot=t5.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

			Length =  strlen(strstr(lr_eval_string ( "{WCSParam_Diff4_2}"), "value=\"")) -  strlen(strstr(lr_eval_string ( "{WCSParam_Diff4_2}"), "\">"));
			Found= (char *) strncpy ( stringFound2, (strstr(lr_eval_string ( "{WCSParam_Diff4_2}"), "value=\""))+7, (Length-7)); 
			lr_save_string(Found, "WCSParam_Diff4" );
			lr_output_message("WCSParam_Diff4 = %s", lr_eval_string ( "{WCSParam_Diff4}"));
	/*
	 * Enter Clients Personal information
	 */

	lr_think_time( 10 );

	web_submit_data("clientCustAction.do",
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=next",
		"Method=POST",
		"EncType=multipart/form-data",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff3}",
		"Snapshot=t6.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=personalInfo", ENDITEM,
		"Name=nextOrPreview", "Value=next", ENDITEM,
		"Name=clientName.salutation", "Value=48", ENDITEM,
		"Name=clientName.firstName", "Value=CLIENT", ENDITEM,
		"Name=clientName.middleName", "Value=", ENDITEM,
		"Name=clientName.secondLastName", "Value=", ENDITEM,
		"Name=clientName.lastName", "Value=NO_{client_lastname1}", ENDITEM,	//parameter for clients last name
		"Name=clientName.nameType", "Value=3", ENDITEM,
		"Name=governmentId", "Value=", ENDITEM,
		"Name=dateOfBirthDD", "Value=01", ENDITEM,
		"Name=dateOfBirthMM", "Value=04", ENDITEM,
		"Name=dateOfBirthYY", "Value=1970", ENDITEM,
		"Name=clientDetailView.gender", "Value=50", ENDITEM,
		"Name=clientDetailView.maritalStatus", "Value=67", ENDITEM,
		"Name=clientDetailView.numChildren", "Value=", ENDITEM,
		"Name=clientDetailView.citizenship", "Value=", ENDITEM,
		"Name=clientDetailView.ethinicity", "Value=", ENDITEM,
		"Name=clientDetailView.educationLevel", "Value=", ENDITEM,
		"Name=clientDetailView.businessActivities", "Value=", ENDITEM,
		"Name=Client.PovertyStatus", "Value=clientDetailView.povertyStatus", ENDITEM,
		"Name=clientDetailView.povertyStatus", "Value=41", ENDITEM,
		"Name=clientDetailView.handicapped", "Value=", ENDITEM,
		//"Name=picture", "Value=", ENDITEM,					//commented out as causes error in the App in runtime
		"Name=spouseName.nameType", "Value=2", ENDITEM,
		"Name=spouseName.firstName", "Value=Father", ENDITEM,
		"Name=spouseName.middleName", "Value=", ENDITEM,
		"Name=spouseName.secondLastName", "Value=", ENDITEM,
		"Name=spouseName.lastName", "Value=NO_{client_lastname1}", ENDITEM,
		"Name=address.line1", "Value=Some Street", ENDITEM,
		"Name=address.line2", "Value=Some Town", ENDITEM,
		"Name=address.line3", "Value=", ENDITEM,
		"Name=address.city", "Value=Some City", ENDITEM,
		"Name=address.state", "Value=", ENDITEM,
		"Name=address.country", "Value=Some Country", ENDITEM,
		"Name=address.zip", "Value=", ENDITEM,
		"Name=address.phoneNumber", "Value=", ENDITEM,
		"Name=customField[0].fieldId", "Value=3", ENDITEM,
		"Name=fieldTypeList", "Value=2", ENDITEM,
		"Name=customField[0].fieldValue", "Value=", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff4}", ENDITEM,
		EXTRARES,
		LAST);


	/*
	 * enter MFI Info
	 */

	lr_think_time( 10 );

	web_submit_data("clientCustAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=loadMeeting",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=next",
		"Snapshot=t7.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=mfiInfo", ENDITEM,
		"Name=loanOfficerId", "Value={loan_officer_Id1}", ENDITEM,	   //parameter for loan officer assigned to the branch
		"Name=formedByPersonnel", "Value={loan_officer_Id1}", ENDITEM, //if valid LO for branch is not selected App will give an error
		"Name=externalId", "Value=", ENDITEM,
		"Name=trainedDateDD", "Value=", ENDITEM,
		"Name=trainedDateMM", "Value=", ENDITEM,
		"Name=trainedDateYY", "Value=", ENDITEM,
		"Name=trainedDate", "Value=", ENDITEM,
		"Name=trainedDateFormat", "Value=D/M/Y", ENDITEM,
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM,
		"Name=selectedFee[0].feeId", "Value=", ENDITEM,		//Passbook Fee
		"Name=selectedFee[0].amount", "Value=", ENDITEM,		//Passbook Fee value
		"Name=selectedFeeAmntList", "Value=", ENDITEM,
		"Name=selectedFeeAmntList", "Value=", ENDITEM,
		"Name=selectedFee[1].feeId", "Value=", ENDITEM,
		"Name=selectedFee[1].amount", "Value=", ENDITEM,
		"Name=selectedFee[2].feeId", "Value=", ENDITEM,
		"Name=selectedFee[2].amount", "Value=", ENDITEM,
		"Name=savingsOffering[0]", "Value=", ENDITEM,
		"Name=savingsOffering[1]", "Value=", ENDITEM,
		"Name=savingsOffering[2]", "Value=", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff4}", ENDITEM,
		EXTRARES,
		LAST);


	/*
	 * schedule a meeting
	 */

	lr_think_time( 10 );

	web_submit_data("meetingAction.do",
		"Action=http://9.161.154.46:8080/mifos/meetingAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=loadMeeting",
		"Snapshot=t8.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=frequency", "Value=1", ENDITEM,				//Weekly Meeting assigned means must have a Weekly frequency of installments for the Loan Products assigned aswell or else data error
		"Name=recurWeek", "Value=1", ENDITEM,
		"Name=weekDay", "Value=2", ENDITEM,
		"Name=monthDay", "Value=", ENDITEM,
		"Name=dayRecurMonth", "Value=", ENDITEM,
		"Name=monthRank", "Value=", ENDITEM,
		"Name=monthWeek", "Value=", ENDITEM,
		"Name=recurMonth", "Value=", ENDITEM,
		"Name=meetingPlace", "Value=Clients Street", ENDITEM,
		"Name=method", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff4}", ENDITEM,
		EXTRARES,
		LAST);


	/*
	 * Enter MFI information 
	 */

	lr_think_time( 10 );

	web_submit_data("clientCustAction.do_3",
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/meetingAction.do",
		"Snapshot=t9.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=mfiInfo", ENDITEM,
		"Name=loanOfficerId", "Value={loan_officer_Id1}", ENDITEM,
		"Name=formedByPersonnel", "Value={loan_officer_Id1}", ENDITEM,
		"Name=externalId", "Value=", ENDITEM,
		"Name=trainedDateDD", "Value=", ENDITEM,
		"Name=trainedDateMM", "Value=", ENDITEM,
		"Name=trainedDateYY", "Value=", ENDITEM,
		"Name=trainedDate", "Value=", ENDITEM,
		"Name=trainedDateFormat", "Value=D/M/Y", ENDITEM,
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM,
		"Name=selectedFee[0].feeId", "Value=", ENDITEM, 		//Passbook Fee
		"Name=selectedFee[0].amount", "Value=", ENDITEM,		//Passbook Fee value
		"Name=selectedFeeAmntList", "Value=", ENDITEM,
		"Name=selectedFeeAmntList", "Value=", ENDITEM,
		"Name=selectedFee[1].feeId", "Value=", ENDITEM,
		"Name=selectedFee[1].amount", "Value=", ENDITEM,
		"Name=selectedFee[2].feeId", "Value=", ENDITEM,
		"Name=selectedFee[2].amount", "Value=", ENDITEM,
		"Name=savingsOffering[0]", "Value=", ENDITEM,
		"Name=savingsOffering[1]", "Value=", ENDITEM,
		"Name=savingsOffering[2]", "Value=", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff4}", ENDITEM,
		EXTRARES,
		
		LAST);


	/*
	 * Review & Submit
	 */

	lr_think_time( 8 );

	// [WCSPARAM WCSParam_Diff5 14 0002-000000001] Parameter {WCSParam_Diff5} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff5", "LB=globalCustNum=", "RB=&", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_submit_data("clientCustAction.do_4",
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=create",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=preview",
		"Snapshot=t10.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=create", ENDITEM,
		"Name=status", "Value=2", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff4}", ENDITEM,
		EXTRARES,
		LAST);


	/*
	 * Success, View Client Details now
	 */

	// [WCSPARAM WCSParam_Diff6 13 1196958110281] Parameter {WCSParam_Diff6} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff6", "LB=currentFlowKey=", "RB=\"", "Ord=2", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM customerId 4 2041] Parameter {customerId} created by Correlation Studio
	web_reg_save_param("customerId",
		"LB=customerId=",
		"RB=&",
		"Ord=2",
		"RelFrameId=1",
		"Search=Body",
		LAST);
	
	web_url("View Client details now",
		"URL=http://9.161.154.46:8080/mifos/clientCustAction.do?method=get&globalCustNum={WCSParam_Diff5}&recordOfficeId={office_Id1}&recordLoanOfficerId={loan_officer_Id1}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=create",
		"Snapshot=t11.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);


	/*
	 * Edit Client Status
	 */

	lr_think_time( 5 );

	web_url("Edit Client status",
		"URL=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=loadStatus&customerId={customerId}&input=client&currentFlowKey={WCSParam_Diff6}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=get&globalCustNum={WCSParam_Diff5}&recordOfficeId={office_Id1}&recordLoanOfficerId={loan_officer_Id1}",
		"Snapshot=t12.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);


	/*
	 * Change Client Status to Active
	 */

	lr_think_time( 13 );

	web_submit_data("editCustomerStatusAction.do",
		"Action=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=previewStatus",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=loadStatus&customerId={customerId}&input=client&currentFlowKey={WCSParam_Diff6}",
		"Snapshot=t13.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=newStatusId", "Value=3", ENDITEM,
		"Name=notes", "Value=Activate", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff6}", ENDITEM,
		EXTRARES,
		LAST);


	/*
	 * Submit
	 */

	// [WCSPARAM WCSParam_Diff7 17 62548853312799424] Parameter {WCSParam_Diff7} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff7", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_submit_data("editCustomerStatusAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=updateStatus",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=previewStatus",
		"Snapshot=t14.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=btn", "Value=Submit", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff6}", ENDITEM,
		EXTRARES,
		LAST);

			loan_officer_Id = loan_officer_Id + 1; //Incrementing loan officers so that the clients
												   //are not assigned to only one loan officer.

		}   //clients for loop
			//loan_officer_Id = loan_officer_Id - 1;    
            //loan_officer_Id = loan_officer_Id + 5;   //each Branch office had got 5 Loan officers assigned

	
	}   //branches for loop

	return 0;
}
