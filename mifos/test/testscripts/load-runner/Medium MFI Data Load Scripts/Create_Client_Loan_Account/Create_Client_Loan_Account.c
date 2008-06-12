// This script creates loan accounts for Branch clients(clients associated with branches)
// Client names are extracted from database into a paramater BRCLIENT_NAME


Create_Client_Loan_Account()
{
	
	web_url("mifos",
		"URL=http://9.161.154.14/mifos",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t1.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *  login
	 */

	lr_think_time( 2 );

	web_submit_data("loginAction.do",
		"Action=http://9.161.154.14/mifos/loginAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loginAction.do;jsessionid=17898CD1AE077179C11C8F82463E156D?method=load",
		"Snapshot=t2.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=userName", "Value=mifos", ENDITEM,
		"Name=password", "Value=testmifos", ENDITEM,
		"Name=method", "Value=login", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *  Click Open new loan account
	 */

	lr_think_time( 1 );

	// [WCSPARAM WCSParam_Diff1 13 1211964764851] Parameter {WCSParam_Diff1} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff1", "LB= value=\"", "RB=\"", "Ord=6", "Search=Body", "RelFrameId=1", LAST );
	web_url("Open new Loan Account",
		"URL=http://9.161.154.14/mifos/custSearchAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=loan",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t3.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 1 );	

	//  Enter 'Client Name' in the search box
 
	lr_think_time( 2 );

	web_reg_save_param( "customerId", "LB=customerId=", "RB=&", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff2 20 -3681379466057753136] Parameter {WCSParam_Diff2} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff2", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_submit_data("custSearchAction.do",
		"Action=http://9.161.154.14/mifos/custSearchAction.do?method=search",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=loan",
		"Snapshot=t4.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=searchString", "Value={BRCLIENT_NAME}", ENDITEM,
		"Name=searchButton", "Value=Search", ENDITEM,
		"Name=method", "Value=search", ENDITEM,
		"Name=perspective", "Value=", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff1}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *   Select client link
	 */

	lr_think_time( 2 );

	// [WCSPARAM WCSParam_Diff3 13 1211964801084] Parameter {WCSParam_Diff3} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff3", "LB= value=\"", "RB=\"", "Ord=11", "Search=Body", "RelFrameId=1", LAST );
	web_url("BRCLIENT NO_1001:ID0002-000103217",
		"URL=http://9.161.154.14/mifos/loanAccountAction.do?method=getPrdOfferings&customerId={customerId}&perspective=&randomNUm={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=search",
		"Snapshot=t5.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 1 );

	/*
	 *   Select loan instance name
	 */

	lr_think_time( 2 );

	web_submit_data("loanAccountAction.do",
		"Action=http://9.161.154.14/mifos/loanAccountAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanAccountAction.do?method=getPrdOfferings&customerId={customerId}&perspective=&randomNUm={WCSParam_Diff2}",
		"Snapshot=t6.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=prdOfferingId", "Value=12", ENDITEM,   // Value=11 weekly declining balance
		"Name=continueBtn", "Value=Continue", ENDITEM,
		"Name=method", "Value=load", ENDITEM,
		"Name=input", "Value=loan", ENDITEM,
		"Name=perspective", "Value=", ENDITEM,
		"Name=customerId", "Value={customerId}", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *   Click Continue
	 */

	lr_think_time( 1 );

	web_submit_data("loanAccountAction.do_2",
		"Action=http://9.161.154.14/mifos/loanAccountAction.do?method=schedulePreview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanAccountAction.do",
		"Snapshot=t7.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=prdOfferingId", "Value=12", ENDITEM,
		"Name=loanAmount", "Value=10000.0", ENDITEM,
		"Name=interestRate", "Value=10.0", ENDITEM,
		"Name=noOfInstallments", "Value=26", ENDITEM,
		//"Name=disbursementDateDD", "Value=02", ENDITEM,
		//"Name=disbursementDateMM", "Value=06", ENDITEM,
		//"Name=disbursementDateYY", "Value=2008", ENDITEM,
		//"Name=disbursementDate", "Value=02/06/2008", ENDITEM,
		"Name=disbursementDateFormat", "Value=D/M/Y", ENDITEM,
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM,
		"Name=gracePeriodDuration", "Value=0", ENDITEM,
		"Name=loanOfferingFund", "Value=", ENDITEM,
		"Name=businessActivityId", "Value=", ENDITEM,
		"Name=collateralTypeId", "Value=", ENDITEM,
		"Name=collateralNote", "Value=", ENDITEM,
		"Name=selectedFee[0].feeId", "Value=", ENDITEM,
		"Name=selectedFee[0].amount", "Value=", ENDITEM,
		"Name=selectedFee[1].feeId", "Value=", ENDITEM,
		"Name=selectedFee[1].amount", "Value=", ENDITEM,
		"Name=selectedFee[2].feeId", "Value=", ENDITEM,
		"Name=selectedFee[2].amount", "Value=", ENDITEM,
		"Name=continueButton", "Value=Continue", ENDITEM,
		"Name=gracePeriodTypeId", "Value=1", ENDITEM,
		"Name=gracePeriodname", "Value=None", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		"Name=perspective", "Value=", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 1 );

	/*
	 *  Click Preview
	 */

	lr_think_time( 1 );

	web_submit_data("loanAccountAction.do_3",
		"Action=http://9.161.154.14/mifos/loanAccountAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanAccountAction.do?method=schedulePreview",
		"Snapshot=t8.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		"Name=previewBtn", "Value=Preview", ENDITEM,
		"Name=method", "Value=preview", ENDITEM,
		"Name=perspective", "Value=", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 1 );

	/*
	 *   Submit for approval
	 */

	lr_think_time( 1 );
	web_reg_save_param( "globalAccountNum", "LB=globalAccountNum=", "RB=\r", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff4 19 4098667750508468691] Parameter {WCSParam_Diff4} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff4", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_submit_data("loanAccountAction.do_4",
		"Action=http://9.161.154.14/mifos/loanAccountAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanAccountAction.do",
		"Snapshot=t9.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=method", "Value=create", ENDITEM,
		"Name=input", "Value=accountPreview", ENDITEM,
		"Name=stateSelected", "Value=2", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		"Name=perspective", "Value=", ENDITEM,
		EXTRARES,
		LAST);

	/*
	 *   Click View loan account details
	 */

	lr_think_time( 2 );
	web_reg_save_param( "accountId", "LB=accountId=", "RB=&", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff5 20 -6147561173558637247] Parameter {WCSParam_Diff5} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff5", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff6 13 1211964883205] Parameter {WCSParam_Diff6} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff6", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("View Loan account details now",
		"URL=http://9.161.154.14/mifos/loanAccountAction.do?method=get&customerId={customerId}&globalAccountNum={globalAccountNum}&recordOfficeId=2&recordLoanOfficerId=2&randomNUm={WCSParam_Diff4}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanAccountAction.do",
		"Snapshot=t10.inf",
		"Mode=HTML",
		EXTRARES,
		"Url=pages/framework/images/buttons/buttonbg.jpg", "Referer=http://9.161.154.14/mifos/loanAccountAction.do?method=get&customerId={customerId}&globalAccountNum={globalAccountNum}&recordOfficeId=2&recordLoanOfficerId=2&randomNUm={WCSParam_Diff4}", ENDITEM,
		LAST);

	/*
	 *   Click Edit account status
	 */

	lr_think_time( 1 );

	web_url("Edit account status",
		"URL=http://9.161.154.14/mifos/editStatusAction.do?method=load&accountId={accountId}&randomNUm={WCSParam_Diff5}&currentFlowKey={WCSParam_Diff6}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanAccountAction.do?method=get&customerId={customerId}&globalAccountNum={globalAccountNum}&recordOfficeId=2&recordLoanOfficerId=2&randomNUm={WCSParam_Diff4}",
		"Snapshot=t11.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *   Select radio button and type 'approved'
	 */

	lr_think_time( 2 );

	web_submit_data("editStatusAction.do",
		"Action=http://9.161.154.14/mifos/editStatusAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/editStatusAction.do?method=load&accountId={accountId}&randomNUm={WCSParam_Diff5}&currentFlowKey={WCSParam_Diff6}",
		"Snapshot=t12.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff6}", ENDITEM,
		"Name=newStatusId", "Value=3", ENDITEM,
		"Name=notes", "Value=a", ENDITEM,
		"Name=globalAccountNum", "Value={globalAccountNum}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *  Click 'Submit'
	 */

	lr_think_time( 1 );

	web_submit_data("editStatusAction.do_2",
		"Action=http://9.161.154.14/mifos/editStatusAction.do?method=update",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/editStatusAction.do?method=preview",
		"Snapshot=t13.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff6}", ENDITEM,
		"Name=btn", "Value=Submit", ENDITEM,
		"Name=globalAccountNum", "Value={globalAccountNum}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 2 );

//	Click 'Create New Loan Account' link from menu
    
	lr_think_time(2);

	web_url("Create Loan Account",
		"URL=http://9.161.154.14/mifos/custSearchAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=loan",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.146.83:8080/mifos/editStatusAction.do?method=update",
		"Snapshot=t14.inf",
		"Mode=HTML",
		LAST);


	
	 //  Loan account is created
	 

	return 0;
}



