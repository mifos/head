//This script disburses loans. Client names are extracted from the database

Disburse_Loan()
{
	web_url("mifos",
		"URL=http://9.161.154.14/mifos",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t12.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *  Login
	 */

	lr_think_time( 2 );

	// [WCSPARAM WCSParam_Diff1 13 1212683547766] Parameter {WCSParam_Diff1} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff1", "LB= value=\"", "RB=\"", "Ord=6", "Search=Body", "RelFrameId=1", LAST );
	web_submit_data("loginAction.do",
		"Action=http://9.161.154.14/mifos/loginAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loginAction.do;jsessionid=C7616F5C1B4DEB985D8A8746618212C8?method=load",
		"Snapshot=t13.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=userName", "Value=mifos", ENDITEM,
		"Name=password", "Value=testmifos", ENDITEM,
		"Name=method", "Value=login", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *  Enter client name
	 */

	lr_think_time( 2 );
	web_reg_save_param( "globalAccountNum", "LB=globalAccountNum=", "RB=&", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff2 19 6556784602486829821] Parameter {WCSParam_Diff2} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff2", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_submit_data("custSearchAction.do",
		"Action=http://9.161.154.14/mifos/custSearchAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t14.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=searchString", "Value={GRCLIENT_NAME}", ENDITEM,
		"Name=officeId", "Value=0", ENDITEM,
		"Name=searchButton", "Value=Search", ENDITEM,
		"Name=officeId", "Value=0", ENDITEM,
		"Name=method", "Value=mainSearch", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff1}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 1 );
	/*
	 *  SELECT LOAN ACCOUNT LINK
	 */

	lr_think_time( 2 );
	web_reg_save_param( "accountId", "LB=accountId=", "RB=&", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff3 19 5390720853141255303] Parameter {WCSParam_Diff3} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff3", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff4 13 1212683591812] Parameter {WCSParam_Diff4} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff4", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("Account # 000100000116790",
		"URL=http://9.161.154.14/mifos/loanAccountAction.do?globalAccountNum={globalAccountNum}&method=get&recordOfficeId=2&recordLoanOfficerId=2&randomNUm={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do",
		"Snapshot=t15.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *   Disburse loan
	 */

	lr_think_time( 3 );

	web_url("Disburse Loan",
		"URL=http://9.161.154.14/mifos/loanDisbursmentAction.do?method=load&accountId={accountId}&globalAccountNum={globalAccountNum}&prdOfferingName=Weekly%20Flat%20Interest%20Rate%20for%20Clients&randomNUm={WCSParam_Diff3}&currentFlowKey={WCSParam_Diff4}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanAccountAction.do?globalAccountNum={globalAccountNum}&method=get&recordOfficeId=2&recordLoanOfficerId=2&randomNUm={WCSParam_Diff2}",
		"Snapshot=t16.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 1 );

	/*
	 *   Select cash
	 */

	lr_think_time( 2 );

	web_submit_data("loanDisbursmentAction.do",
		"Action=http://9.161.154.14/mifos/loanDisbursmentAction.do?method=preview&globalAccountNum={globalAccountNum}",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanDisbursmentAction.do?method=load&accountId={accountId}&globalAccountNum={globalAccountNum}&prdOfferingName=Weekly Flat Interest Rate for Clients&randomNUm={WCSParam_Diff3}&currentFlowKey={WCSParam_Diff4}",
		"Snapshot=t17.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff4}", ENDITEM,
		"Name=transactionDateDD", "Value=02", ENDITEM,
		"Name=transactionDateMM", "Value=06", ENDITEM,
		"Name=transactionDateYY", "Value=2008", ENDITEM,
		"Name=transactionDate", "Value=02/06/2008", ENDITEM,
		"Name=transactionDateFormat", "Value=D/M/Y", ENDITEM,
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM,
		"Name=receiptId", "Value=", ENDITEM,
		"Name=receiptDateDD", "Value=", ENDITEM,
		"Name=receiptDateMM", "Value=", ENDITEM,
		"Name=receiptDateYY", "Value=", ENDITEM,
		"Name=receiptDate", "Value=", ENDITEM,
		"Name=receiptDateFormat", "Value=D/M/Y", ENDITEM,
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM,
		"Name=paymentTypeId", "Value=1", ENDITEM,
		"Name=prdOfferingName", "Value=Weekly Flat Interest Rate for Clients", ENDITEM,
		"Name=globalAccountNum", "Value={globalAccountNum}", ENDITEM,
		"Name=accountId", "Value={accountId}", ENDITEM,
		"Name=method", "Value=", ENDITEM,
		EXTRARES,
		LAST);
	/*
	 *   Submit
	 */

	lr_think_time( 1 );

	web_submit_data("loanDisbursmentAction.do_2",
		"Action=http://9.161.154.14/mifos/loanDisbursmentAction.do?method=update",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loanDisbursmentAction.do?method=preview&globalAccountNum={globalAccountNum}",
		"Snapshot=t18.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff4}", ENDITEM,
		"Name=prdOfferingName", "Value=Weekly Flat Interest Rate for Clients", ENDITEM,
		"Name=globalAccountNum", "Value={globalAccountNum}", ENDITEM,
		"Name=accountId", "Value={accountId}", ENDITEM,
		"Name=method", "Value=", ENDITEM,
		EXTRARES,
		LAST);

	return 0;
}





