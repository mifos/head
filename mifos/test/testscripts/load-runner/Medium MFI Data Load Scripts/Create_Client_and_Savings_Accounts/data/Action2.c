Action2()
{

	web_set_max_html_param_len("1024");

	/* Registering parameter(s) from source task id 3
	// {JSESSIONID2} = "F2835C4FA266CEE38C32A954C8685E45"
	// */

	web_reg_save_param("JSESSIONID2", 
		"LB/IC=jsessionid=", 
		"RB/IC=\"", 
		"Ord=1", 
		"Search=body", 
		"RelFrameId=1", 
		LAST);

	web_url("loginAction.do", 
		"URL=http://9.161.154.46:8080/mifos/loginAction.do?method=load", 
		"TargetFrame=", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=", 
		"Snapshot=t27.inf", 
		"Mode=HTML", 
		EXTRARES, 
		"Url=pages/framework/images/buttons/buttonbg.jpg", "Referer=http://9.161.154.46:8080/mifos/loginAction.do?method=load", ENDITEM, 
		LAST);

	/*username and password*/

	web_submit_data("loginAction.do;jsessionid=F2835C4FA266CEE38C32A954C8685E45", 
		"Action=http://9.161.154.46:8080/mifos/loginAction.do;jsessionid={JSESSIONID2}", 
		"Method=POST", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/loginAction.do?method=load", 
		"Snapshot=t28.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=userName", "Value=mifos", ENDITEM, 
		"Name=password", "Value=testmifos", ENDITEM, 
		"Name=method", "Value=login", ENDITEM, 
		LAST);

	/*create new client*/

	lr_think_time(13);

	web_url("Create new Client", 
		"URL=http://9.161.154.46:8080/mifos/groupCustAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=createClient", 
		"TargetFrame=", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/custSearchAction.do?method=getHomePage", 
		"Snapshot=t29.inf", 
		"Mode=HTML", 
		EXTRARES, 
		"Url=pages/framework/images/buttons/buttonbgcancel.jpg", "Referer=http://9.161.154.46:8080/mifos/groupCustAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=createClient", ENDITEM, 
		LAST);

	web_url("Click here to continue if Group membership is not required for your Client.", 
		"URL=http://9.161.154.46:8080/mifos/clientCustAction.do?method=chooseOffice&groupFlag=0&currentFlowKey=1199706237546", 
		"TargetFrame=", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/groupCustAction.do?method=loadSearch&recordOfficeId=0&recordLoanOfficerId=0&input=createClient", 
		"Snapshot=t30.inf", 
		"Mode=HTML", 
		LAST);

	/*select branch from the list*/

	lr_think_time(21);

	web_url("BRANCH_OFFICE_1", 
		"URL=http://9.161.154.46:8080/mifos/clientCustAction.do?method=load&office.officeId=2&office.officeName=BRANCH_OFFICE_1&officeId=2&officeName=BRANCH_OFFICE_1&currentFlowKey=1199706239218", 
		"TargetFrame=", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=chooseOffice&groupFlag=0&currentFlowKey=1199706237546", 
		"Snapshot=t31.inf", 
		"Mode=HTML", 
		LAST);

	/*enter personal info*/

	lr_think_time(51);

	web_submit_data("clientCustAction.do", 
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=next", 
		"Method=POST", 
		"EncType=multipart/form-data", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=load&office.officeId=2&office.officeName=BRANCH_OFFICE_1&officeId=2&officeName=BRANCH_OFFICE_1&currentFlowKey=1199706239218", 
		"Snapshot=t32.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=input", "Value=personalInfo", ENDITEM, 
		"Name=nextOrPreview", "Value=next", ENDITEM, 
		"Name=clientName.salutation", "Value=47", ENDITEM, 
		"Name=clientName.firstName", "Value=Client", ENDITEM, 
		"Name=clientName.middleName", "Value=", ENDITEM, 
		"Name=clientName.secondLastName", "Value=", ENDITEM, 
		"Name=clientName.lastName", "Value=NO_04", ENDITEM, 
		"Name=clientName.nameType", "Value=3", ENDITEM, 
		"Name=governmentId", "Value=", ENDITEM, 
		"Name=dateOfBirthDD", "Value=22", ENDITEM, 
		"Name=dateOfBirthMM", "Value=10", ENDITEM, 
		"Name=dateOfBirthYY", "Value=1976", ENDITEM, 
		"Name=clientDetailView.gender", "Value=", ENDITEM, 
		"Name=clientDetailView.maritalStatus", "Value=", ENDITEM, 
		"Name=clientDetailView.numChildren", "Value=", ENDITEM, 
		"Name=clientDetailView.citizenship", "Value=", ENDITEM, 
		"Name=clientDetailView.ethinicity", "Value=", ENDITEM, 
		"Name=clientDetailView.educationLevel", "Value=", ENDITEM, 
		"Name=clientDetailView.businessActivities", "Value=", ENDITEM, 
		"Name=Client.PovertyStatus", "Value=clientDetailView.povertyStatus", ENDITEM, 
		"Name=clientDetailView.povertyStatus", "Value=41", ENDITEM, 
		"Name=clientDetailView.handicapped", "Value=", ENDITEM, 
		"Name=picture", "Value=", "File=Yes", ENDITEM, 
		"Name=spouseName.nameType", "Value=2", ENDITEM, 
		"Name=spouseName.firstName", "Value=father", ENDITEM, 
		"Name=spouseName.middleName", "Value=", ENDITEM, 
		"Name=spouseName.secondLastName", "Value=", ENDITEM, 
		"Name=spouseName.lastName", "Value=NO_04", ENDITEM, 
		"Name=address.line1", "Value=", ENDITEM, 
		"Name=address.line2", "Value=", ENDITEM, 
		"Name=address.line3", "Value=", ENDITEM, 
		"Name=address.city", "Value=", ENDITEM, 
		"Name=address.state", "Value=", ENDITEM, 
		"Name=address.country", "Value=", ENDITEM, 
		"Name=address.zip", "Value=", ENDITEM, 
		"Name=address.phoneNumber", "Value=", ENDITEM, 
		"Name=customField[0].fieldId", "Value=3", ENDITEM, 
		"Name=fieldTypeList", "Value=2", ENDITEM, 
		"Name=customField[0].fieldValue", "Value=", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706261140", ENDITEM, 
		LAST);

	lr_think_time(8);

	web_submit_data("clientCustAction.do_2", 
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=next", 
		"Method=POST", 
		"EncType=multipart/form-data", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=next", 
		"Snapshot=t33.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=input", "Value=personalInfo", ENDITEM, 
		"Name=nextOrPreview", "Value=next", ENDITEM, 
		"Name=clientName.salutation", "Value=47", ENDITEM, 
		"Name=clientName.firstName", "Value=Client", ENDITEM, 
		"Name=clientName.middleName", "Value=", ENDITEM, 
		"Name=clientName.secondLastName", "Value=", ENDITEM, 
		"Name=clientName.lastName", "Value=NO_04", ENDITEM, 
		"Name=clientName.nameType", "Value=3", ENDITEM, 
		"Name=governmentId", "Value=", ENDITEM, 
		"Name=dateOfBirthDD", "Value=22", ENDITEM, 
		"Name=dateOfBirthMM", "Value=10", ENDITEM, 
		"Name=dateOfBirthYY", "Value=1976", ENDITEM, 
		"Name=clientDetailView.gender", "Value=49", ENDITEM, 
		"Name=clientDetailView.maritalStatus", "Value=", ENDITEM, 
		"Name=clientDetailView.numChildren", "Value=", ENDITEM, 
		"Name=clientDetailView.citizenship", "Value=", ENDITEM, 
		"Name=clientDetailView.ethinicity", "Value=", ENDITEM, 
		"Name=clientDetailView.educationLevel", "Value=", ENDITEM, 
		"Name=clientDetailView.businessActivities", "Value=", ENDITEM, 
		"Name=Client.PovertyStatus", "Value=clientDetailView.povertyStatus", ENDITEM, 
		"Name=clientDetailView.povertyStatus", "Value=41", ENDITEM, 
		"Name=clientDetailView.handicapped", "Value=", ENDITEM, 
		"Name=picture", "Value=", "File=Yes", ENDITEM, 
		"Name=spouseName.nameType", "Value=2", ENDITEM, 
		"Name=spouseName.firstName", "Value=father", ENDITEM, 
		"Name=spouseName.middleName", "Value=", ENDITEM, 
		"Name=spouseName.secondLastName", "Value=", ENDITEM, 
		"Name=spouseName.lastName", "Value=NO_04", ENDITEM, 
		"Name=address.line1", "Value=", ENDITEM, 
		"Name=address.line2", "Value=", ENDITEM, 
		"Name=address.line3", "Value=", ENDITEM, 
		"Name=address.city", "Value=", ENDITEM, 
		"Name=address.state", "Value=", ENDITEM, 
		"Name=address.country", "Value=", ENDITEM, 
		"Name=address.zip", "Value=", ENDITEM, 
		"Name=address.phoneNumber", "Value=", ENDITEM, 
		"Name=customField[0].fieldId", "Value=3", ENDITEM, 
		"Name=fieldTypeList", "Value=2", ENDITEM, 
		"Name=customField[0].fieldValue", "Value=", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706261140", ENDITEM, 
		LAST);

	/*enter MFI info*/

	/*schedule meeting*/

	lr_think_time(25);

	web_submit_data("clientCustAction.do_3", 
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=loadMeeting", 
		"Method=POST", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=next", 
		"Snapshot=t34.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=input", "Value=mfiInfo", ENDITEM, 
		"Name=loanOfficerId", "Value=2", ENDITEM, 
		"Name=formedByPersonnel", "Value=2", ENDITEM, 
		"Name=externalId", "Value=", ENDITEM, 
		"Name=trainedDateDD", "Value=", ENDITEM, 
		"Name=trainedDateMM", "Value=", ENDITEM, 
		"Name=trainedDateYY", "Value=", ENDITEM, 
		"Name=trainedDate", "Value=", ENDITEM, 
		"Name=trainedDateFormat", "Value=D/M/Y", ENDITEM, 
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM, 
		"Name=selectedFee[0].feeId", "Value=", ENDITEM, 
		"Name=selectedFee[0].amount", "Value=", ENDITEM, 
		"Name=selectedFee[1].feeId", "Value=", ENDITEM, 
		"Name=selectedFee[1].amount", "Value=", ENDITEM, 
		"Name=selectedFee[2].feeId", "Value=", ENDITEM, 
		"Name=selectedFee[2].amount", "Value=", ENDITEM, 
		"Name=savingsOffering[0]", "Value=", ENDITEM, 
		"Name=savingsOffering[1]", "Value=", ENDITEM, 
		"Name=savingsOffering[2]", "Value=", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706261140", ENDITEM, 
		LAST);

	lr_think_time(10);

	web_submit_data("meetingAction.do", 
		"Action=http://9.161.154.46:8080/mifos/meetingAction.do", 
		"Method=POST", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=loadMeeting", 
		"Snapshot=t35.inf", 
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
		"Name=meetingPlace", "Value=Dublin", ENDITEM, 
		"Name=method", "Value=create", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706261140", ENDITEM, 
		LAST);

	lr_think_time(5);

	web_submit_data("clientCustAction.do_4", 
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=preview", 
		"Method=POST", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/meetingAction.do", 
		"Snapshot=t36.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=input", "Value=mfiInfo", ENDITEM, 
		"Name=loanOfficerId", "Value=2", ENDITEM, 
		"Name=formedByPersonnel", "Value=2", ENDITEM, 
		"Name=externalId", "Value=", ENDITEM, 
		"Name=trainedDateDD", "Value=", ENDITEM, 
		"Name=trainedDateMM", "Value=", ENDITEM, 
		"Name=trainedDateYY", "Value=", ENDITEM, 
		"Name=trainedDate", "Value=", ENDITEM, 
		"Name=trainedDateFormat", "Value=D/M/Y", ENDITEM, 
		"Name=datePattern", "Value=dd/MM/yy", ENDITEM, 
		"Name=selectedFee[0].feeId", "Value=", ENDITEM, 
		"Name=selectedFee[0].amount", "Value=", ENDITEM, 
		"Name=selectedFee[1].feeId", "Value=", ENDITEM, 
		"Name=selectedFee[1].amount", "Value=", ENDITEM, 
		"Name=selectedFee[2].feeId", "Value=", ENDITEM, 
		"Name=selectedFee[2].amount", "Value=", ENDITEM, 
		"Name=savingsOffering[0]", "Value=", ENDITEM, 
		"Name=savingsOffering[1]", "Value=", ENDITEM, 
		"Name=savingsOffering[2]", "Value=", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706261140", ENDITEM, 
		LAST);

	/*submit for approval*/

	lr_think_time(11);

	web_submit_data("clientCustAction.do_5", 
		"Action=http://9.161.154.46:8080/mifos/clientCustAction.do?method=create", 
		"Method=POST", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=preview", 
		"Snapshot=t37.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=input", "Value=create", ENDITEM, 
		"Name=status", "Value=2", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706261140", ENDITEM, 
		LAST);

	/*view Client deatils now*/

	lr_think_time(14);

	web_url("View Client details now", 
		"URL=http://9.161.154.46:8080/mifos/clientCustAction.do?method=get&globalCustNum=0002-000000006&recordOfficeId=2&recordLoanOfficerId=2", 
		"TargetFrame=", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=create", 
		"Snapshot=t38.inf", 
		"Mode=HTML", 
		LAST);

	/*edit client status*/

	lr_think_time(13);

	web_url("Edit Client status", 
		"URL=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=loadStatus&customerId=6&input=client&currentFlowKey=1199706446734", 
		"TargetFrame=", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/clientCustAction.do?method=get&globalCustNum=0002-000000006&recordOfficeId=2&recordLoanOfficerId=2", 
		"Snapshot=t39.inf", 
		"Mode=HTML", 
		LAST);

	/*active*/

	lr_think_time(10);

	web_submit_data("editCustomerStatusAction.do", 
		"Action=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=previewStatus", 
		"Method=POST", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=loadStatus&customerId=6&input=client&currentFlowKey=1199706446734", 
		"Snapshot=t40.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=newStatusId", "Value=3", ENDITEM, 
		"Name=notes", "Value=a", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706446734", ENDITEM, 
		LAST);

	/*submit*/

	lr_think_time(6);

	web_submit_data("editCustomerStatusAction.do_2", 
		"Action=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=updateStatus", 
		"Method=POST", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=previewStatus", 
		"Snapshot=t41.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=btn", "Value=Submit", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706446734", ENDITEM, 
		LAST);

	/*open savings account*/

	lr_think_time(15);

	web_url("Savings", 
		"URL=http://9.161.154.46:8080/mifos/savingsAction.do?method=getPrdOfferings&customerId=6&recordOfficeId=1&recordLoanOfficerId=1&randomNUm=-1262651595697202334", 
		"TargetFrame=", 
		"Resource=0", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/editCustomerStatusAction.do?method=updateStatus", 
		"Snapshot=t42.inf", 
		"Mode=HTML", 
		LAST);

	/*savings instance name*/

	lr_think_time(30);

	web_submit_data("savingsAction.do", 
		"Action=http://9.161.154.46:8080/mifos/savingsAction.do?method=load", 
		"Method=POST", 
		"TargetFrame=", 
		"RecContentType=text/html", 
		"Referer=http://9.161.154.46:8080/mifos/savingsAction.do?method=getPrdOfferings&customerId=6&recordOfficeId=1&recordLoanOfficerId=1&randomNUm=-1262651595697202334", 
		"Snapshot=t43.inf", 
		"Mode=HTML", 
		ITEMDATA, 
		"Name=selectedPrdOfferingId", "Value=", ENDITEM, 
		"Name=input", "Value=getPrdOfferings", ENDITEM, 
		"Name=currentFlowKey", "Value=1199706495453", ENDITEM, 
		LAST);

	return 0;
}