/* Author *Venkat*
   This script creates CLIENTS as well as SAVINGS ACCOUNT for them. 
   Clients are assigned to different BRANCH OFFICES and different LOAN OFFICERS. 
   Clients numbers, Branch offices IDs and Loan officers IDs are incremented in the for loops. 
   Each branch office has got 5 loan officers assigned.
   (eg IBM_OFFICE_07 is assigned 110 ID and IBM_OFFICE_08 is assigned 111 ID
*/

// loan officer ID 12 is associated with branch office_Id 4
// branch office ID 109 has got loan officers 484 - 488 IDs assigned
// branch office ID 110 has got loan officers 489 - 493 IDs assigned and so on.....

#include "as_web.h"

Create_Client_and_Savings_Account()
{

loan_officer_Id = 2;     //loan officer IDs 2 - 6 are associated with Branch office ID 2
                         //loan officer ID 7 is associated with branch office_Id 3	

	web_set_max_html_param_len("1024");

	/* Registering parameter(s) from source task id 2
	 {JSESSIONID2} = "2E759A56E54F115724A3758EEB70BC24"
	 */

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

	lr_think_time( 3 );

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

	lr_think_time( 3 );

	
//	 Click 'Create New Client' from Quickstart menu
	 

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

	lr_think_time( 3 );

// Click here to continue if Group membership is not required for your Client. 
     
// Loop for incrementing Branch Office IDs   (eg Branch office ID 110 is IBM_OFFICE_07)
for (j=2;j<=6;j++) {

      	office_Id=j;
		itoa( office_Id,anyString,10 );                     //Converts an integer to a string
		lr_save_string( anyString,"office_Id1" );			//Saving anystring in office_Id1 variable
		lr_output_message("Present Office_Id: %d", office_Id); //Prints office ID
		
        //Loop for Creating Clients/Customers and Savings Account for each Branch Office

		for (i=1;i<=5;i++) {

				itoa( loan_officer_Id,anyString,10 );                //Converts an integer to a string
				lr_save_string( anyString,"loan_officer_Id1" );
				lr_output_message("Present loan_Officer_Id: %d", loan_officer_Id); //Prints Loanofficer ID

				client_lastname = client_lastname + 1;  
				lr_output_message("Client_lastname is : %d", client_lastname);   //Prints client lastname

				itoa( client_lastname, anyString,10 );            //Converts an integer to a string
				lr_save_string( anyString,"client_lastname1" );   //Saving anystring in variable client_lastname1

                    
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
	
//  Select Branch Office 
	

	// [WCSPARAM WCSParam_Diff4 13 1196957781921] Parameter {WCSParam_Diff4} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff4", "LB= value=\"", "RB=\"", "Ord=75", "Search=Body", "RelFrameId=1", LAST );
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

	lr_think_time( 3 );

	
// Enter Clients Personal information
	 

	lr_think_time( 3 );

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
		"Name=clientName.lastName", "Value=NO_{client_lastname1}", ENDITEM,
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
		//"Name=picture", "Value=", ENDITEM,
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

	
// Enter MFI Info
    
	lr_think_time( 3 );

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
		"Name=loanOfficerId", "Value={loan_officer_Id1}", ENDITEM,
		"Name=formedByPersonnel", "Value={loan_officer_Id1}", ENDITEM,
		"Name=externalId", "Value=", ENDITEM,
		"Name=trainedDateDD", "Value=", ENDITEM,
		"Name=trainedDateMM", "Value=", ENDITEM,
		"Name=trainedDateYY", "Value=", ENDITEM,
		"Name=trainedDate", "Value=", ENDITEM,
		"Name=trainedDateFormat", "Value=D/M/Y", ENDITEM,
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM,
		"Name=selectedFee[0].feeId", "Value=10", ENDITEM,		//Passbook Fee
		"Name=selectedFee[0].amount", "Value=1.0", ENDITEM,		//Passbook Fee value
		"Name=selectedFeeAmntList", "Value=1.0", ENDITEM,
		"Name=selectedFeeAmntList", "Value=1.0", ENDITEM,
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

	
//  Schedule a meeting
	 
	lr_think_time( 3 );

	web_submit_data("meetingAction.do",
		"Action=http://9.161.154.46:8080/mifos/meetingAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=loadMeeting",
		"Snapshot=t8.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=frequency", "Value=1", ENDITEM,
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

	
//  Enter MFI information 
    
	lr_think_time( 3 );

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
		"Name=selectedFee[0].feeId", "Value=10", ENDITEM, 		//Passbook Fee
		"Name=selectedFee[0].amount", "Value=1.0", ENDITEM,		//Passbook Fee value
		"Name=selectedFeeAmntList", "Value=1.0", ENDITEM,
		"Name=selectedFeeAmntList", "Value=1.0", ENDITEM,
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
	
//  Review & Submit
    
	lr_think_time( 3 );

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

	
// Success screen, View Client Details now
	 

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

	
// Click 'Edit Client Status'
	 

	lr_think_time( 3 );

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

// Change Client Status to Active
	 

	lr_think_time( 3 );

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

//  Submit
	 

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

	lr_think_time( 3 );

	
//  Click 'Open New Savings account' link
     
	lr_think_time( 3 );

	// [WCSPARAM WCSParam_Diff8 13 1196958199328] Parameter {WCSParam_Diff8} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff8", "LB= value=\"", "RB=\"", "Ord=6", "Search=Body", "RelFrameId=1", LAST );
	web_url("Savings",
		"URL=http://9.161.154.46:8080/mifos/savingsAction.do?method=getPrdOfferings&customerId={customerId}&recordOfficeId=1&recordLoanOfficerId=1&randomNUm={WCSParam_Diff7}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=updateStatus",
		"Snapshot=t15.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 3 );


//  Select Savings Instance
	
	lr_think_time( 3 );

	web_submit_data("savingsAction.do",
		"Action=http://9.161.154.46:8080/mifos/savingsAction.do?method=load",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/savingsAction.do?method=getPrdOfferings&customerId={customerId}&recordOfficeId=1&recordLoanOfficerId=1&randomNUm={WCSParam_Diff7}",
		"Snapshot=t16.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=selectedPrdOfferingId", "Value=3", ENDITEM,
		"Name=input", "Value=getPrdOfferings", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff8}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 5 );

	
//  Click 'Preview' on Savings Information screen
	 

	web_submit_data("savingsAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/savingsAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/savingsAction.do?method=load",
		"Snapshot=t17.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=selectedPrdOfferingId", "Value=3", ENDITEM,
		"Name=recommendedAmount", "Value=2.0", ENDITEM,
		"Name=customField[0].fieldValue", "Value=", ENDITEM,
		"Name=customField[0].fieldId", "Value=8", ENDITEM,
		"Name=input", "Value=preview", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff8}", ENDITEM,
		EXTRARES,
		LAST);

	
//  Click 'Submit for Approval'
	 

	// [WCSPARAM globalAccountNum 15 000100000006084] Parameter {globalAccountNum} created by Correlation Studio
	web_reg_save_param("globalAccountNum",
		"LB=globalAccountNum=",
		"RB=&",
		"Ord=1",
		"RelFrameId=1",
		"Search=Body",
		LAST);

	web_submit_data("savingsAction.do_3",
		"Action=http://9.161.154.46:8080/mifos/savingsAction.do?method=create",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/savingsAction.do?method=preview",
		"Snapshot=t18.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=stateSelected", "Value=14", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff8}", ENDITEM,
		EXTRARES,
		LAST);

	
//  On success screen click 'View Savings account details now'
	 

	// [WCSPARAM WCSParam_Diff9 19 3910460711510698707] Parameter {WCSParam_Diff9} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff9", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff10 13 1196958345093] Parameter {WCSParam_Diff10} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff10", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM accountId 4 6084] Parameter {accountId} created by Correlation Studio
	web_reg_save_param("accountId",
		"LB=accountId=",
		"RB=&",
		"Ord=1",
		"RelFrameId=1",
		"Search=Body",
		LAST);

	web_url("savingsAction.do_4",
		"URL=http://9.161.154.46:8080/mifos/savingsAction.do?method=get&globalAccountNum={globalAccountNum}&recordOfficeId={office_Id1}&recordLoanOfficerId=1",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/savingsAction.do?method=create",
		"Snapshot=t19.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	
//  Click 'Edit Account Status'
	 

	lr_think_time( 3 );

	web_url("Edit account status",
		"URL=http://9.161.154.46:8080/mifos/editStatusAction.do?method=load&accountId={accountId}&randomNUm={WCSParam_Diff9}&currentFlowKey={WCSParam_Diff10}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/savingsAction.do?method=get&globalAccountNum={globalAccountNum}&recordOfficeId={office_Id1}&recordLoanOfficerId=1",
		"Snapshot=t20.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 4 );

	
//	Change Status to active
     
	lr_think_time( 4 );

	web_submit_data("editStatusAction.do",
		"Action=http://9.161.154.46:8080/mifos/editStatusAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/editStatusAction.do?method=load&accountId={accountId}&randomNUm={WCSParam_Diff9}&currentFlowKey={WCSParam_Diff10}",
		"Snapshot=t21.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff10}", ENDITEM,
		"Name=newStatusId", "Value=16", ENDITEM,
		"Name=notes", "Value=Activate", ENDITEM,
		"Name=globalAccountNum", "Value={globalAccountNum}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 6 );

	
// Confirm Status change
     
	lr_think_time( 5 );

	web_submit_data("editStatusAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/editStatusAction.do?method=update",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/editStatusAction.do?method=preview",
		"Snapshot=t22.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff10}", ENDITEM,
		"Name=btn", "Value=Submit", ENDITEM,
		"Name=globalAccountNum", "Value={globalAccountNum}", ENDITEM,
		EXTRARES,
		LAST);

			
			loan_officer_Id = loan_officer_Id + 1; //Incrementing loan officers so that the clients
												   //are not assigned to only one loan officer.

		}   //Clients for loop
		  
		
	    //loan_officer_Id = loan_officer_Id + 5;    //each Branch office has got 5 Loan officers assigned. increment
													//loan officer Id by 5 so that the loan officer assigned is valid
													//for that particular branch office. otherwise we will get an 
													// error stating 'loan officer made inactive'.
													
	}   //Branches for loop

	return 0;

}




