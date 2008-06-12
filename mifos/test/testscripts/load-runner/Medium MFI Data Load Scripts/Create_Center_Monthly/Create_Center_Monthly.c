#include "as_web.h"

// This script creates Monthly CENTERS for the existing branch offices.

Create_Center_Monthly()
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

	lr_think_time( 2 );

	/*
	 *  Login
	 */

	lr_think_time( 2 );

	web_submit_data("loginAction.do",
		"Action=http://9.161.154.14/mifos/loginAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/loginAction.do;jsessionid=A5266865BD620C4957E2F827AB825695?method=load",
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
	 *  Clients and accounts tab
	 */

	lr_think_time( 3 );

	web_url("Clients & Accounts",
		"URL=http://9.161.154.14/mifos/custSearchAction.do?method=loadMainSearch",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=getHomePage",
		"Snapshot=t3.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 1 );

	/*
	 *   Create new center
	 */

	lr_think_time( 1 );

	// [WCSPARAM WCSParam_Diff1 13 1212480230214] Parameter {WCSParam_Diff1} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff1", "LB=currentFlowKey=", "RB=\"", "Ord=1", "Search=Body", "RelFrameId=1", LAST );
	web_url("Create new Center",
		"URL=http://9.161.154.14/mifos/centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/custSearchAction.do?method=loadMainSearch",
		"Snapshot=t4.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	/*
	 *   Select branch office 
	 */

	lr_think_time( 2 );

	// [WCSPARAM WCSParam_Diff2 13 1212480246021] Parameter {WCSParam_Diff2} created by Correlation Studio
	web_reg_save_param( "WCSParam_Diff2", "LB= value=\"", "RB=\"", "Ord=29", "Search=Body", "RelFrameId=1", LAST );
	web_url("BRANCH_OFFICE_10",
		"URL=http://9.161.154.14/mifos/centerCustAction.do?method=load&office.officeId=2&office.officeName=BRANCH_OFFICE_10&officeId=2&officeName=BRANCH_OFFICE_10&currentFlowKey={WCSParam_Diff1}",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/centerCustAction.do?method=chooseOffice&recordOfficeId=0&recordLoanOfficerId=0",
		"Snapshot=t5.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 3 );

	web_submit_data("centerCustAction.do",
		"Action=http://9.161.154.14/mifos/centerCustAction.do?method=loadMeeting",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/centerCustAction.do?method=load&office.officeId=2&office.officeName=BRANCH_OFFICE_10&officeId=2&officeName=BRANCH_OFFICE_10&currentFlowKey={WCSParam_Diff1}",
		"Snapshot=t6.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=create", ENDITEM,
		"Name=displayName", "Value={Center_Name}", ENDITEM,
		"Name=loanOfficerId", "Value=2", ENDITEM,
		"Name=externalId", "Value=", ENDITEM,
		"Name=mfiJoiningDateDD", "Value=3", ENDITEM,
		"Name=mfiJoiningDateMM", "Value=6", ENDITEM,
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
		"Name=currentFlowKey", "Value={WCSParam_Diff2}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *   Select monthly
	 */

	lr_think_time( 2 );

	web_submit_data("meetingAction.do",
		"Action=http://9.161.154.14/mifos/meetingAction.do",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/centerCustAction.do?method=loadMeeting",
		"Snapshot=t7.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=frequency", "Value=2", ENDITEM,
		"Name=recurWeek", "Value=", ENDITEM,
		"Name=weekDay", "Value=", ENDITEM,
		"Name=monthType", "Value=1", ENDITEM,
		"Name=monthDay", "Value=1", ENDITEM,
		"Name=dayRecurMonth", "Value=1", ENDITEM,
		"Name=monthRank", "Value=1", ENDITEM,
		"Name=monthWeek", "Value=", ENDITEM,
		"Name=recurMonth", "Value=", ENDITEM,
		"Name=meetingPlace", "Value=dublin", ENDITEM,
		"Name=method", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff2}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 4 );

	/*
	 *   Preview
	 */

	lr_think_time( 1 );

	web_submit_data("centerCustAction.do_2",
		"Action=http://9.161.154.14/mifos/centerCustAction.do?method=preview",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/meetingAction.do",
		"Snapshot=t8.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=create", ENDITEM,
		"Name=displayName", "Value={Center_Name}", ENDITEM,
		"Name=loanOfficerId", "Value=2", ENDITEM,
		"Name=externalId", "Value=", ENDITEM,
		"Name=mfiJoiningDateDD", "Value=3", ENDITEM,
		"Name=mfiJoiningDateMM", "Value=6", ENDITEM,
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
		"Name=currentFlowKey", "Value={WCSParam_Diff2}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 1 );

	/*
	 *   Submit
	 */

	lr_think_time( 1 );

	web_submit_data("centerCustAction.do_3",
		"Action=http://9.161.154.14/mifos/centerCustAction.do?method=create",
		"Method=POST",
		"TargetFrame=",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/centerCustAction.do?method=preview",
		"Snapshot=t9.inf",
		"Mode=HTML",
		ITEMDATA,
		"Name=input", "Value=create", ENDITEM,
		"Name=currentFlowKey", "Value={WCSParam_Diff2}", ENDITEM,
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *   View center details now
	 */

	lr_think_time( 1 );

	web_url("View Center details now",
		"URL=http://9.161.154.14/mifos/centerCustAction.do?method=get&globalCustNum=0002-000110752&recordOfficeId=2&recordLoanOfficerId=2&randomNUm=",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/centerCustAction.do?method=create",
		"Snapshot=t10.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	lr_think_time( 2 );

	/*
	 *   Clients and accounts
	 */

	lr_think_time( 1 );

	web_url("Clients & Accounts_2",
		"URL=http://9.161.154.14/mifos/custSearchAction.do?method=loadMainSearch",
		"TargetFrame=",
		"Resource=0",
		"RecContentType=text/html",
		"Referer=http://9.161.154.14/mifos/centerCustAction.do?method=get&globalCustNum=0002-000110752&recordOfficeId=2&recordLoanOfficerId=2&randomNUm=",
		"Snapshot=t11.inf",
		"Mode=HTML",
		EXTRARES,
		LAST);

	return 0;

}






