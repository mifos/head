//	Script to create Centers for various EXISTING Branches in the Mifos Application

   
Create_Center()
{

	char stringFound2[64]="\0";
    char WCSParam_Diff4_2[256]="\0";

	loan_officer_Id=40;     //'2' loan officer ID is associated with branch office_Id 2



	// [WCSPARAM WCSParam_Diff1 32 C47D385D72123E9A5D3869FB7A55C73E] Parameter {WCSParam_Diff1} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff1", "LB=Set-Cookie: JSESSIONID=", "RB=;", "Ord=1", "Search=Headers", "RelFrameId=1", LAST );
	web_url("loginAction.do",
		"URL=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t11.inf",
		"Mode=HTML",
		EXTRARES,
		
		LAST);

	lr_think_time( 10 );

	/*
	 * Log In
	 */

	lr_think_time( 10 );

	web_submit_data("loginAction.do;jsessionid={WCSParam_Diff1}",
		"Action=http://9.161.154.46:8080/mifos/loginAction.do;jsessionid={WCSParam_Diff1}",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/loginAction.do?method=load",
		"Snapshot=t12.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=userName", "Value=mifos", ENDITEM,
		"Name=password", "Value=testmifos", ENDITEM,
		"Name=method", "Value=login", ENDITEM,
		EXTRARES,
		
		LAST);

	lr_think_time( 6 );

	/*
	 * Click Clients & Accounts Tab
	 */

	web_url("Clients & Accounts",
		"URL=http://9.161.154.46:8080/mifos/custSearchAction.do?method=loadMainSearch",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t13.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);


	/*
	 * Click Create New Center
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

				center_name = center_name + 1;  
				lr_output_message("center_name is : %d", center_name);

				itoa( center_name, anyString,10 );            //Converts an integer to a string
				lr_save_string( anyString,"center_name1" );   //saving anystring in variable client_lastname1

					//if (i==2) {
						//loan_officer_Id = loan_officer_Id + 1;    // Incrementing loan officers so that the clients
																  // are not assigned to only one loan officer
                     
				


	// [WCSPARAM WCSParam_Diff2 13 1200457381937] Parameter {WCSParam_Diff2} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff2", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("Create new Center",
		"URL=http://9.161.154.46:8080/mifos/centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/custSearchAction.do?method=loadMainSearch",
		"Snapshot=t14.inf",
		"Mode=HTML",
		EXTRARES,
		
		LAST);


	/*
	 * Choose Branch Office to assign Center
	 */

	// [WCSPARAM WCSParam_Diff3 13 1200457404000] Parameter {WCSParam_Diff3} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff3_2", "LB=currentFlowKey", "RB=h_user_locale", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("BRANCH_OFFICE_1",
		"URL=http://9.161.154.46:8080/mifos/centerCustAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0",
		"Snapshot=t15.inf",
		"Mode=HTML",
		EXTRARES,
	
		LAST);
		
		Length =  strlen(strstr(lr_eval_string ( "{WCSParam_Diff3_2}"), "value=\"")) -  strlen(strstr(lr_eval_string ( "{WCSParam_Diff3_2}"), "\">"));
		Found= (char *) strncpy ( stringFound2, (strstr(lr_eval_string ( "{WCSParam_Diff3_2}"), "value=\""))+7, (Length-7)); 
		lr_save_string(Found, "WCSParam_Diff3" );
		lr_output_message("WCSParam_Diff3 = %s", lr_eval_string ( "{WCSParam_Diff3}"));

	/*
	 * Enter Center Information
	 */

	lr_think_time( 10 );

	web_submit_data("centerCustAction.do",
		"Action=http://9.161.154.46:8080/mifos/centerCustAction.do?method=loadMeeting",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=load&office.officeId={office_Id1}&office.officeName=BRANCH_OFFICE_1&officeId={office_Id1}&officeName=BRANCH_OFFICE_1&currentFlowKey={WCSParam_Diff2}",
		"Snapshot=t16.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=create", ENDITEM,
		"Name=displayName", "Value=CENTER_{center_name1}", ENDITEM,
		"Name=loanOfficerId", "Value={loan_officer_Id1}", ENDITEM,
		"Name=externalId", "Value=", ENDITEM,
		"Name=mfiJoiningDateDD", "Value=16", ENDITEM,
		"Name=mfiJoiningDateMM", "Value=1", ENDITEM,
		"Name=mfiJoiningDateYY", "Value=2008", ENDITEM,
		"Name=address.line1", "Value=", ENDITEM,
		"Name=address.line2", "Value=", ENDITEM,
		"Name=address.line3", "Value=", ENDITEM,
		"Name=address.city", "Value=", ENDITEM,
		"Name=address.state", "Value=", ENDITEM,
		"Name=address.country", "Value=", ENDITEM,
		"Name=address.zip", "Value=", ENDITEM,
		"Name=address.phoneNumber", "Value=", ENDITEM,
		"Name=selectedFee[0].feeId", "Value=", ENDITEM,
		"Name=selectedFee[0].amount", "Value=", ENDITEM,
		"Name=selectedFee[1].feeId", "Value=", ENDITEM,
		"Name=selectedFee[1].amount", "Value=", ENDITEM,
		"Name=selectedFee[2].feeId", "Value=", ENDITEM,
		"Name=selectedFee[2].amount", "Value=", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		EXTRARES,
		

		LAST);


	/*
	 * Schedule a Meeting - Weekly
	 */

	lr_think_time( 11 );

	web_submit_data("meetingAction.do",
		"Action=http://9.161.154.46:8080/mifos/meetingAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=loadMeeting",
		"Snapshot=t17.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=frequency", "Value=1", ENDITEM,
		"Name=recurWeek", "Value=1", ENDITEM,
		"Name=weekDay", "Value=7", ENDITEM,
		"Name=monthDay", "Value=", ENDITEM,
		"Name=dayRecurMonth", "Value=", ENDITEM,
		"Name=monthRank", "Value=", ENDITEM,
		"Name=monthWeek", "Value=", ENDITEM,
		"Name=recurMonth", "Value=", ENDITEM,
		"Name=meetingPlace", "Value=Center Street", ENDITEM,
		"Name=method", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		EXTRARES,
		
		LAST);

	lr_think_time( 10 );

	web_submit_data("centerCustAction.do_2",
		"Action=http://9.161.154.46:8080/mifos/centerCustAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/meetingAction.do",
		"Snapshot=t18.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=create", ENDITEM,
		"Name=displayName", "Value=CENTER_{center_name1}", ENDITEM,
		"Name=loanOfficerId", "Value={loan_officer_Id1}", ENDITEM,
		"Name=externalId", "Value=", ENDITEM,
		"Name=mfiJoiningDateDD", "Value=23", ENDITEM,
		"Name=mfiJoiningDateMM", "Value=1", ENDITEM,
		"Name=mfiJoiningDateYY", "Value=2008", ENDITEM,
		"Name=address.line1", "Value=Center Street", ENDITEM,
		"Name=address.line2", "Value=", ENDITEM,
		"Name=address.line3", "Value=", ENDITEM,
		"Name=address.city", "Value=Center City", ENDITEM,
		"Name=address.state", "Value=", ENDITEM,
		"Name=address.country", "Value=", ENDITEM,
		"Name=address.zip", "Value=", ENDITEM,
		"Name=address.phoneNumber", "Value=", ENDITEM,
		"Name=selectedFee[0].feeId", "Value=", ENDITEM,
		"Name=selectedFee[0].amount", "Value=", ENDITEM,
		"Name=selectedFee[1].feeId", "Value=", ENDITEM,
		"Name=selectedFee[1].amount", "Value=", ENDITEM,
		"Name=selectedFee[2].feeId", "Value=", ENDITEM,
		"Name=selectedFee[2].amount", "Value=", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		EXTRARES,
		
		LAST);


	/*
	 * Review & Submit
	 */

	lr_think_time( 6 );

	web_submit_data("centerCustAction.do_3",
		"Action=http://9.161.154.46:8080/mifos/centerCustAction.do?method=create",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=preview",
		"Snapshot=t19.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		EXTRARES,
		
		LAST);

	lr_think_time( 5 );

	/*
	 * Click Create New Center link from Success Page
	 */

	web_url("centerCustAction.do_4",
		"URL=http://9.161.154.46:8080/mifos/centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0&randomNUm=",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.46:8080/mifos/centerCustAction.do?method=create",
		"Snapshot=t21.inf",
		"Mode=HTML",
		EXTRARES,
	
		LAST);

			loan_officer_Id = loan_officer_Id + 1; //Incrementing loan officers so that the clients
												   //are not assigned to only one loan officer.

		}   //clients for loop
			//loan_officer_Id = loan_officer_Id - 1;    
            //loan_officer_Id = loan_officer_Id + 5;   //each Branch office had got 5 Loan officers assigned

	
	}   //branches for loop

	return 0;
