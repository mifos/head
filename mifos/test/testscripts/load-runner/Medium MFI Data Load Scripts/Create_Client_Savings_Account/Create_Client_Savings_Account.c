#include "as_web.h"
// This script creates Savings accounts for the existing clients.
// Client names are extracted from the database into parameter GRCLIENT_NAME


Create_Client_Savings_Account()
{
	
	web_url("mifos",
		"URL=http://9.161.154.14/mifos",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=",
		"Snapshot=t20.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *  LOGIN
	 */

	lr_think_time( 2 );

	web_submit_data("loginAction.do",
		"Action=http://9.161.154.14/mifos/loginAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loginAction.do;jsessionid=50B0F600DDCD65EE12D01ED30E2E7F62?method=load",
		"Snapshot=t21.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=userName", "Value=mifos", ENDITEM,
		"Name=password", "Value=testmifos", ENDITEM,
		"Name=method", "Value=login", ENDITEM,
		EXTRARES,
		LAST);

	/*
	 *  Clients and accounts
	 */

	lr_think_time( 1 );

	web_url("Clients & Accounts",
		"URL=http://9.161.154.14/mifos/custSearchAction.do?method=loadMainSearch",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t22.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *   Create savings account
	 */

	lr_think_time( 1 );

	// [WCSPARAM WCSParam_Diff1 13 1212741830517] Parameter {WCSParam_Diff1} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff1", "LB= value=\"", "RB=\"", "Ord=6", "Search=Body", "RelFrameId=1", LAST );
	web_url("Create Savings Account",
		"URL=http://9.161.154.14/mifos/custSearchAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=savings",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=loadMainSearch",
		"Snapshot=t23.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);
	/*
	 *   Enter client name in the search box
	 */

	lr_think_time( 3 );
	web_reg_save_param( "customerId", "LB=customerId=", "RB=&", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff2 20 -7161667860183507296] Parameter {WCSParam_Diff2} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff2", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_submit_data("custSearchAction.do",
		"Action=http://9.161.154.14/mifos/custSearchAction.do?method=search",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=savings",
		"Snapshot=t24.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=searchString", "Value={GRCLIENT_NAME}", ENDITEM,
		"Name=searchButton", "Value=Search", ENDITEM,
		"Name=method", "Value=search", ENDITEM,
		"Name=perspective", "Value=", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff1}", ENDITEM,
		EXTRARES,
		LAST);

//	 SELECT LINK	

	lr_think_time( 1 );

	// [WCSPARAM WCSParam_Diff3 13 1212741863939] Parameter {WCSParam_Diff3} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff3", "LB= value=\"", "RB=\"", "Ord=7", "Search=Body", "RelFrameId=1", LAST );
	web_url("GRCLIENT NO_140001:ID0035-000058471",
		"URL=http://9.161.154.14/mifos/savingsAction.do?method=getPrdOfferings&customerId={customerId}&randomNUm={WCSParam_Diff2}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=search",
		"Snapshot=t25.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *  SELECT SAVINGS INSTANCE
	 */

	web_submit_data("savingsAction.do",
		"Action=http://9.161.154.14/mifos/savingsAction.do?method=load",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/savingsAction.do?method=getPrdOfferings&customerId={customerId}&randomNUm={WCSParam_Diff2}",
		"Snapshot=t26.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=selectedPrdOfferingId", "Value=8", ENDITEM,
		"Name=input", "Value=getPrdOfferings", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		EXTRARES,
		LAST);
	/*
	 *  PREVIEW
	 */

	lr_think_time( 1 );

	web_submit_data("savingsAction.do_2",
		"Action=http://9.161.154.14/mifos/savingsAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/savingsAction.do?method=load",
		"Snapshot=t27.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=selectedPrdOfferingId", "Value=8", ENDITEM,
		"Name=recommendedAmount", "Value=10.0", ENDITEM,
		"Name=input", "Value=preview", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		EXTRARES,
		LAST);
	/*
	 *   SUBMIT FOR APPROVAL
	 */

	lr_think_time( 1 );
	web_reg_save_param( "globalAccountNum", "LB=globalAccountNum=", "RB=&", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_submit_data("savingsAction.do_3",
		"Action=http://9.161.154.14/mifos/savingsAction.do?method=create",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/savingsAction.do?method=preview",
		"Snapshot=t28.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=stateSelected", "Value=14", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff3}", ENDITEM,
		EXTRARES,
		LAST);
	/*
	 *    VIEW SAVINGS ACCOUNT DETAILS NOW
	 */

	lr_think_time( 2 );
	web_reg_save_param( "accountId", "LB=accountId=", "RB=&", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff4 20 -7910405217550272738] Parameter {WCSParam_Diff4} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff4", "LB=randomNUm=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	// [WCSPARAM WCSParam_Diff5 13 1212741915864] Parameter {WCSParam_Diff5} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff5", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("View Savings account details now",
		"URL=http://9.161.154.14/mifos/savingsAction.do?method=get&globalAccountNum={globalAccountNum}&recordOfficeId=35&recordLoanOfficerId=1",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/savingsAction.do?method=create",
		"Snapshot=t29.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *   EDIT ACCOUNT STATUS
	 */

	lr_think_time( 1 );

	web_url("Edit account status",
		"URL=http://9.161.154.14/mifos/editStatusAction.do?method=load&accountId={accountId}&randomNUm={WCSParam_Diff4}&currentFlowKey={WCSParam_Diff5}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/savingsAction.do?method=get&globalAccountNum={globalAccountNum}&recordOfficeId=35&recordLoanOfficerId=1",
		"Snapshot=t30.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *   SELECT ACTIVE
	 */

	lr_think_time( 3 );

	web_submit_data("editStatusAction.do",
		"Action=http://9.161.154.14/mifos/editStatusAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/editStatusAction.do?method=load&accountId={accountId}&randomNUm={WCSParam_Diff4}&currentFlowKey={WCSParam_Diff5}",
		"Snapshot=t31.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff5}", ENDITEM,
		"Name=newStatusId", "Value=16", ENDITEM,
		"Name=notes", "Value=A", ENDITEM,
		"Name=globalAccountNum", "Value={globalAccountNum}", ENDITEM,
		EXTRARES,
		LAST);
	/*
	 *   SUBMIT
	 */

	lr_think_time( 1 );

	web_submit_data("editStatusAction.do_2",
		"Action=http://9.161.154.14/mifos/editStatusAction.do?method=update",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/editStatusAction.do?method=preview",
		"Snapshot=t32.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=currentFlowKey", "Value={WCSParam_Diff5}", ENDITEM,
		"Name=btn", "Value=Submit", ENDITEM,
		"Name=globalAccountNum", "Value={globalAccountNum}", ENDITEM,
		EXTRARES,
		LAST);

	return 0;
}







