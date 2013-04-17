<%--
Copyright (c) 2005-2011 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>
<!-- create_ClientPersonalInfo.jsp -->

<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/customer/customerfunctions" prefix="customerfn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>


<style type="text/css">

    #changeOrder {
        font-size: 10px;
        color: #000166;
        cursor: pointer;
        text-decoration: underline;
        float: right;
        padding-right: 20px;
        font-family: sans-serif;
    }
    
   #personalInformation.changing #changeOrder {
        font-size: 13px;
        color: green;
        font-weight: bold;
    }
    
    .changeOrderArrows {
        display: none;
    }
    
    .changeOrderArrows img {
        cursor: pointer;
    }
    
    #personalInformation.changing .changeOrderArrows {
        display: inline;
    }
    
    #personalInformation.changing #personalInformationInner > tbody > tr {
        cursor: pointer;
    }
    
    #personalInformation.changing #personalInformationInner > tbody > tr:hover {
        background-color: #FFFFA7;
    }
        
    #personalInformationInner td {
        padding-top: 2px;
        padding-bottom: 2px;
    }
    
</style>

<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.ClientUIResources"/>

<!-- Tile  definitions -->
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="CreateClientPersonalInfo"></span>	
		<script type="text/javascript" src="pages/js/singleitem.js"></script>
		<script language="javascript" src="pages/application/client/js/client.js"></script>
		<script language="javascript">


  function goToCancelPage(){
	clientCustActionForm.action="clientCustAction.do?method=cancel";
	clientCustActionForm.submit();
  }

  function goToPreviewPage(){
	clientCustActionForm.action="clientCustAction.do?method=previewPersonalInfo";
	clientCustActionForm.submit();
  }
  
  function goToNextPage(){
      clientCustActionForm.action="clientCustAction.do?method=next";
      clientCustActionForm.submit();
   }
  
   function addFile() {
       var file = document.getElementById("create_ClientPersonalInfo.input.additionalfile");
       var description = document.getElementById("create_ClientPersonalInfo.input.additionalfile.description");
       if (file.value != null && file.value != "" && !document.getElementById(file.value)) {
           clientCustActionForm.action = "clientCustAction.do?method=addFile#attachements";
           clientCustActionForm.submit();
       }
   }

   function deleteFile(name) {
       document.getElementById("create_ClientPersonalInfo.input.additionalfile").value='';
       name = name.replace(/ /g, "%20");
       clientCustActionForm.action = "clientCustAction.do?method=deleteFile&fileName=" + name + "#attachements";
       clientCustActionForm.submit();
   }
</script>
<sec:authorize access="isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS')">
 <script type="text/javascript">
    
    $(document).ready(function() {
    
        $("#changeOrder").click(function() {
            if ($("#personalInformation").hasClass("changing")) {
                
                $("#personalInformationInner > tbody").sortable('disable').enableSelection();
                
                var order = {};
                
                $('#personalInformationInner > tbody > tr').each(function() {
                    order[$(this).attr("data-order-id")] = $('#personalInformationInner > tbody > tr').index(this);
                });
                
                $.ajax({
                     contentType: "application/json",
                        type: "POST",
                        url: "saveInformationOrder.ftl",
                        data: JSON.stringify(order),
                        dataType: "json"
                });
                
                $("#personalInformation").removeClass("changing");
                $(this).html("<spring:message code="informationOrder.changeFieldsOrder" />");
            } else {
                $("#personalInformation").addClass("changing");
                $(this).html("Save changes");
                
                $("#personalInformationInner > tbody").sortable({
                    helper: function(e, ui) {
                        ui.children().each(function() {
                            $(this).width($(this).width());
                        });
                        return ui;
                    }
                }).disableSelection();
                $("#personalInformationInner > tbody").sortable("enable");
            }
        });
        
        $(".moveUp").click(function() {
            var parentTr = $(this).parents("tr:first"); 
            parentTr.insertBefore(parentTr.prev());
        });
        
        $(".moveDown").click(function() {
            var parentTr = $(this).parents("tr:first"); 
            parentTr.insertAfter(parentTr.next());
        });
    
    });
    
    </script>
</sec:authorize>
<script language="javascript">

	/***<!-- NLO : Date: 15/07/2006  -->
	<!--
	     These functions check to see if the Father/Spouse Select is equal to Spouse
	     If Father/Spouse is equal to Spouse and the Marital Status has not already been selected
	     Then Marital Status is set to Married automatically
	  -->
	**/
	var bAlreadySelected;
	bAlreadySelected = false;

	function CheckMaritalStatus(){
		var sMaritalStatusValue = document.forms["clientCustActionForm"].elements["spouseName.nameType"].value;
		if(bAlreadySelected==false) {
			if(sMaritalStatusValue=='1')
				document.forms["clientCustActionForm"].elements["clientDetailView.maritalStatus"].value=66;
			else
				document.forms["clientCustActionForm"].elements["clientDetailView.maritalStatus"].value='';
		}
	}

	function MaritalStatusSelected(){
		bAlreadySelected = true;
	}

</script>

		<html-el:form action="/clientCustAction.do?method=next"	method="post" enctype="multipart/form-data"
			onsubmit="return chkForValidDates()">
			<!-- Hidden varaibles for the input page -->
			<html-el:hidden property="input" value="personalInfo" />
			<html-el:hidden property="nextOrPreview" value="next" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'CenterHierarchyExist')}" var="CenterHierarchyExist" />
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'meeting')}" var="meeting" />
	         <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personalInformationOrder')}" var="personalInformationOrder" />		
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<!-- Pipeline Bar -->
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="25%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="client.select" bundle="ClientUIResources"></mifos:mifoslabel>
												<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />/<mifos:mifoslabel
													name="${ConfigurationConstants.BRANCHOFFICE}" />
											</td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="client.PersonalInformationHeading"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="client.MFIInformationHeading"
												bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="25%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="client.ReviewSubmitHeading" bundle="ClientUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange">
										<span class="heading"> 
											<fmt:message key="client.createNewClient">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
											</fmt:message> </span>
											<mifos:mifoslabel name="client.CreatePersonalInformationTitle"
												bundle="ClientUIResources"></mifos:mifoslabel>
									</td>
								</tr>
								<!-- Instructions for the user -->
								<tr>
									<td class="fontnormal"><c:choose>
										<c:when test="${param.method eq 'prevPersonalInfo'}">
											<mifos:mifoslabel name="client.PreviewEditInfoInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<fmt:message key="client.PreviewEditCancelInstruction">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" /></fmt:param>
											</fmt:message>
											<span class="mandatorytext"><font color="#FF0000">*</font></span>
											<mifos:mifoslabel name="client.FieldInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</c:when>
										<c:otherwise>
											<mifos:mifoslabel
												name="client.CreatePersonalInformationInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
											<mifos:mifoslabel name="client.CreatePageCancelInstruction1"
												bundle="ClientUIResources"></mifos:mifoslabel>

											<br>
											<span class="mandatorytext"><font color="#FF0000">*</font></span>
											<mifos:mifoslabel name="client.FieldInstruction"
												bundle="ClientUIResources"></mifos:mifoslabel>
										</c:otherwise>
									</c:choose></td>
								</tr>
							</table>
							<br>
							<%-- Group information if client belongs to group --%>
							<c:choose>
								<c:when	test="${sessionScope.clientCustActionForm.groupFlag eq '1'}">
									<logic:messagesPresent>
										<table width="93%" border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td><font class="fontnormalRedBold"><span id="create_ClientPersonalInfo.error.message"><html-el:errors
													bundle="ClientUIResources" /></span> </font></td>
											</tr>
										</table>
										<br>
									</logic:messagesPresent>
									<!-- Displaying the selected office name -->
									<table width="93%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td class="fontnormal"><span class="fontnormalbold">
												<fmt:message key="client.BranchSelected">
													<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
												</fmt:message></span>
											<c:out value="${sessionScope.clientCustActionForm.officeName}" /></td>
										</tr>
										<tr>
											<td><br>
											<span class="fontnormalbold"><mifos:mifoslabel
												name="client.LoanOfficer" bundle="ClientUIResources"></mifos:mifoslabel></span>
											<span class="fontnormal"> <c:out
												value="${sessionScope.clientCustActionForm.loanOfficerName}" />
											</span> <html-el:hidden property="loanOfficerId"
												value="${sessionScope.clientCustActionForm.loanOfficerId}" />
											</td></tr>
											<c:choose>
												<c:when test="${CenterHierarchyExist == true}">
												<tr>
											<td>
													<span class="fontnormalbold">
														<fmt:message key="client.Centers">
															<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></fmt:param>
														</fmt:message>
													</span>
													<span class="fontnormal">
														<c:out value="${sessionScope.clientCustActionForm.centerDisplayName}" />
												</span>
											</td>
										</tr>
										<tr>
											<td>
													<span class="fontnormalbold">
														<fmt:message key="client.Centers">
															<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
														</fmt:message>
													</span>
													<span class="fontnormal">
														<c:out value="${sessionScope.clientCustActionForm.groupDisplayName}" />
													</span></td></tr>
												</c:when>
												<c:otherwise><tr>
											<td>
													<span class="fontnormalbold">
														<fmt:message key="client.Centers">
															<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
														</fmt:message>
													</span>
													<span class="fontnormal">
														<c:out value="${sessionScope.clientCustActionForm.groupDisplayName}" />
													</span></td></tr>
												</c:otherwise>
											</c:choose>
											<tr>
												<td class="fontnormal">
													<span class="fontnormalbold"><mifos:mifoslabel
														name="client.MeetingSchedule" bundle="ClientUIResources"></mifos:mifoslabel></span>
													<span class="fontnormal" id="create_ClientPersonalInfo.text.meetingSchedule"><c:out
														value="${meeting.meetingSchedule}" /><br>
													</span>
													<span class="fontnormalbold"><mifos:mifoslabel
														name="client.LocationOfMeeting" bundle="ClientUIResources"></mifos:mifoslabel></span>
														<span class="fontnormal" id="create_ClientPersonalInfo.text.meetingPlace"> <c:out
													value="${meeting.meetingPlace}" /><br>
													</span>
												</span></td>
										</tr>


									</table>
								</c:when>
								<c:otherwise>
									<table width="93%" border="0" cellspacing="0" cellpadding="3">
										<font class="fontnormalRedBold"><span id="create_ClientPersonalInfo.error.message"><html-el:errors
											bundle="ClientUIResources" /></span> </font>

										<!-- Displaying the selected office name -->
										<tr>
											<td class="fontnormal"><span class="fontnormalbold">
											<fmt:message key="client.BranchSelected">
												<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}" /></fmt:param>
											</fmt:message></span>
											<c:out value="${sessionScope.clientCustActionForm.officeName}" /></td>
										</tr>
									</table>

								</c:otherwise>
							</c:choose> <br>
							<%-- Personal Information Heading --%>
                             <html-el:hidden property="clientName.nameType" value="3" />
                             <input type="hidden" name="numberOfFamilyMembers" value="1"/>
							<table width="93%" border="0" cellpadding="3" cellspacing="0" id="personalInformation">


								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="client.PersonalInformationHeading"
										bundle="ClientUIResources"></mifos:mifoslabel><br>
									<br>
									</td>
                                        <sec:authorize access="isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS')">
                                    <td>
                                    <span id="changeOrder"><spring:message code="informationOrder.changeFieldsOrder" /></span>
                                    </td>
                                    </sec:authorize>
								</tr>
                                <tr colspan="2" width="100%">
                                    <td>
                                        <table width="100%" id="personalInformationInner" cellpadding="3" cellspacing="0" class="fontnormal">
                                        <tbody>
                                        <c:forEach items="${personalInformationOrder}" var="personalInformation">
                                        <c:set var="displayed" value="false" scope="request" />
                                        <c:choose>
                                            <c:when test="${personalInformation.name == 'salutation'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />">
                                                   <td align="right"><mifos:mifoslabel
                                                  name="client.Salutation" mandatory="yes"
                                                  bundle="ClientUIResources"></mifos:mifoslabel></td>
                                              <td>
                                               <mifos:select  name="clientCustActionForm" property="clientName.salutation" size="1" styleClass="noAutoSelect">
                                                  <c:forEach var="salutationEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'salutationEntity')}" >
                                                      <html-el:option value="${salutationEntityList.id}">${salutationEntityList.name}</html-el:option>
                                                  </c:forEach>
                                              </mifos:select></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'firstName'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />">
                                                  <td align="right"><span id="create_ClientPersonalInfo.label.firstName"><mifos:mifoslabel name="client.FirstName"
                                                             mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                                        <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.firstName" name="clientCustActionForm"
                                                     property="clientName.firstName" maxlength="200" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'middleName'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.MiddleName">
                                                  <td align="right"><span id="create_ClientPersonalInfo.label.middleName"><mifos:mifoslabel detachHidden="true" keyhm="Client.MiddleName" name="client.MiddleName"
                                        bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.middleName" keyhm="Client.MiddleName" name="clientCustActionForm"
                                        property="clientName.middleName" maxlength="200" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'lastName'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />">
                                                  <td align="right"><span id="create_ClientPersonalInfo.label.lastName"><mifos:mifoslabel name="client.LastName"
                                        mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.lastName" name="clientCustActionForm"
                                        property="clientName.lastName" maxlength="200" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'secondLastName'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.SecondLastName">
                                                 <td align="right"><span id="create_ClientPersonalInfo.label.secondLastName"><mifos:mifoslabel detachHidden="true" keyhm="Client.SecondLastName"
                                        name="client.SecondLastName" bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.secondLastName" keyhm="Client.SecondLastName" name="clientCustActionForm"
                                        property="clientName.secondLastName"
                                        maxlength="200" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                             <c:when test="${personalInformation.name == 'governmentId'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.GovernmentId">
                                                 <td align="right"><span id="create_ClientPersonalInfo.label.governmentId"><mifos:mifoslabel detachHidden="true" keyhm="Client.GovernmentId" isColonRequired="yes"
                                        name="${ConfigurationConstants.GOVERNMENT_ID}" bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.governmentId" keyhm="Client.GovernmentId" name="clientCustActionForm"
                                        property="governmentId" maxlength="50" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'dateOfBirth'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />">
                                                <td align="right"><mifos:mifoslabel name="client.DateOfBirth"
                                        mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
                                    <td><date:datetag renderstyle="simple" property="dateOfBirth" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                             <c:when test="${personalInformation.name == 'gender'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />">
                                                <td align="right"><mifos:mifoslabel name="client.Gender"
                                        mandatory="yes" bundle="ClientUIResources"></mifos:mifoslabel></td>
                                    <td><mifos:select name="clientCustActionForm" property="clientDetailView.gender" size="1">
                                        <c:forEach var="genderEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderEntity')}" >
                                            <html-el:option value="${genderEntityList.id}">${genderEntityList.name}</html-el:option>
                                        </c:forEach>
                                    </mifos:select></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'maritalStatus'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.MaritalStatus">
                                                <td align="right"><mifos:mifoslabel detachHidden="true" keyhm="Client.MaritalStatus" name="client.MaritalStatus" 
                                        bundle="ClientUIResources"></mifos:mifoslabel></td>

                                    <td><mifos:select name="clientCustActionForm" keyhm="Client.MaritalStatus" onchange="MaritalStatusSelected()"
                                        property="clientDetailView.maritalStatus" size="1" styleClass="noAutoSelect">
                                        <c:forEach var="maritalStatusEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusEntity')}" >
                                            <html-el:option value="${maritalStatusEntityList.id}">${maritalStatusEntityList.name}</html-el:option>
                                        </c:forEach>
                                    </mifos:select></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                              <c:when test="${personalInformation.name == 'numberOfChildren'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.NumberOfChildren">
                                                <td align="right" class="fontnormal"><span id="create_ClientPersonalInfo.label.numberOfChildren"><mifos:mifoslabel
                                        detachHidden="true" keyhm="Client.NumberOfChildren" name="client.NumberOfChildren" bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosnumbertext styleId="create_ClientPersonalInfo.input.numberOfChildren" name="clientCustActionForm"
                                        keyhm="Client.NumberOfChildren" property="clientDetailView.numChildren" maxlength="5" size="10" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'citizenship'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Citizenship">
                                                <td align="right"><mifos:mifoslabel detachHidden="true" keyhm="Client.Citizenship" isColonRequired="yes"
                                        name="${ConfigurationConstants.CITIZENSHIP}" ></mifos:mifoslabel></td>
                                    <td> <mifos:select keyhm="Client.Citizenship"
                                        name="clientCustActionForm"
                                        property="clientDetailView.citizenship" size="1" styleClass="noAutoSelect">
                                        <c:forEach var="citizenshipEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'citizenshipEntity')}" >
                                            <html-el:option value="${citizenshipEntityList.id}">${citizenshipEntityList.name}</html-el:option>
                                        </c:forEach>
                                    </mifos:select></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'ethnicity'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Ethnicity">
                                                <td align="right"><mifos:mifoslabel detachHidden="true" keyhm="Client.Ethnicity" isColonRequired="yes"
                                        name="${ConfigurationConstants.ETHNICITY}" bundle="ClientUIResources"></mifos:mifoslabel></td>
                                    <td><mifos:select keyhm="Client.Ethnicity"
                                        name="clientCustActionForm"
                                        property="clientDetailView.ethnicity" size="1" styleClass="noAutoSelect">
                                        <c:forEach var="ethnicityEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'ethnicityEntity')}" >
                                            <html-el:option value="${ethnicityEntityList.id}">${ethnicityEntityList.name}</html-el:option>
                                        </c:forEach>
                                    </mifos:select></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'educationLevel'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.EducationLevel">
                                                <td align="right"><mifos:mifoslabel detachHidden="true" keyhm="Client.EducationLevel"
                                        name="client.EducationLevel" bundle="ClientUIResources"></mifos:mifoslabel></td>

                                    <td><mifos:select keyhm="Client.EducationLevel"
                                        name="clientCustActionForm"
                                        property="clientDetailView.educationLevel" size="1" styleClass="noAutoSelect">
                                        <c:forEach var="educationLevelEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'educationLevelEntity')}" >
                                            <html-el:option value="${educationLevelEntityList.id}">${educationLevelEntityList.name}</html-el:option>
                                        </c:forEach>
                                    </mifos:select></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'businessActivity'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.BusinessActivities">
                                                <td align="right"><mifos:mifoslabel detachHidden="true" keyhm="Client.BusinessActivities"
                                        name="client.BusinessActivities" bundle="ClientUIResources"></mifos:mifoslabel></td>
                                    <td><mifos:select name="clientCustActionForm" keyhm="Client.BusinessActivities"
                                        property="clientDetailView.businessActivities" size="1" styleClass="noAutoSelect">
                                        <c:forEach var="businessActivitiesEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'businessActivitiesEntity')}" >
                                            <html-el:option value="${businessActivitiesEntityList.id}">${businessActivitiesEntityList.name}</html-el:option>
                                        </c:forEach>
                                    </mifos:select></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'povertyStatus'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.PovertyStatus">
                                                <td align="right"><mifos:mifoslabel detachHidden="true" keyhm="Client.PovertyStatus"
                                        name="client.PovertyStatus" bundle="ClientUIResources"></mifos:mifoslabel></td>
                                    <td>
                                        <mifos:select name="clientCustActionForm" keyhm="Client.PovertyStatus"
                                            property="clientDetailView.povertyStatus" size="1">
                                            <c:forEach var="povertyStatus" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'povertyStatus')}" >
                                                <html-el:option value="${povertyStatus.id}">${povertyStatus.name}</html-el:option>
                                            </c:forEach>
                                        </mifos:select>
                                    </td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'handicapped'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Handicapped">
                                                <td align="right"><mifos:mifoslabel detachHidden="true" keyhm="Client.Handicapped" isColonRequired="yes"
                                        name="${ConfigurationConstants.HANDICAPPED}"></mifos:mifoslabel></td>
                                    <td> <mifos:select keyhm="Client.Handicapped"
                                        name="clientCustActionForm"
                                        property="clientDetailView.handicapped" size="1" styleClass="noAutoSelect">
                                        <c:forEach var="handicappedEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'handicappedEntity')}" >
                                            <html-el:option value="${handicappedEntityList.id}">${handicappedEntityList.name}</html-el:option>
                                        </c:forEach>
                                    </mifos:select></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'photo'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Photo">
                                                  <td align="right"><span id="create_ClientPersonalInfo.label.file"><mifos:mifoslabel detachHidden="true" keyhm="Client.Photo" name="client.Photo"
                                        bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                    <td><mifos:file styleId="create_ClientPersonalInfo.input.file" keyhm="Client.Photo" property="picture" maxlength="200"
                                        onkeypress="return onKeyPressForFileComponent(this);" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'spouseDetails'}">
                                            <c:if test="${!session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areFamilyDetailsRequired') &&
                                !session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'areFamilyDetailsHidden')}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.SpouseFatherInformation">
                                              <td align="right" class="paddingL10"><mifos:mifoslabel detachHidden="true" keyhm="Client.SpouseFatherInformation"
                                        name="client.SpouseFatherName" bundle="ClientUIResources"/></td>
                                    <td>
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                        <tr class="fontnormal">
                                            <td width="14%"><mifos:mifoslabel name="client.Relationship"
                                                bundle="ClientUIResources"></mifos:mifoslabel>
                                                <mifos:select   style="width:80px;" onchange="CheckMaritalStatus()" name="clientCustActionForm"
                                                property="spouseName.nameType" size="1">
                                                <c:forEach var="spouseEntityList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'spouseEntity')}" begin="0" end="1" >
                                                    <html-el:option value="${spouseEntityList.id}">${spouseEntityList.name}</html-el:option>
                                                </c:forEach>
                                            </mifos:select></td>
                                            <td class="paddingL10">
                                                <table border="0" cellspacing="0" cellpadding="0">
                                                    <tr class="fontnormal">
                                                        <td class="paddingL10">
                                                            <span id="create_ClientPersonalInfo.label.spouseFirstName">
                                                            <mifos:mifoslabel keyhm="Client.SpouseFatherInformation" name="client.SpouseFirstName"
                                                               bundle="ClientUIResources">
                                                               </mifos:mifoslabel></span>
                                                        </td>
                                                    </tr>
                                                    <tr class="fontnormal">
                                                        <td class="paddingL10">
                                                            <mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.spouseFirstName" property="spouseName.firstName"
                                                               maxlength="200"  style="width:100px;" />
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                            <td class="paddingL10">
                                                <table border="0" cellspacing="0" cellpadding="0">
                                                    <tr class="fontnormal">
                                                        <td class="paddingL10">
                                                        <span id="create_ClientPersonalInfo.label.spouseMiddleName">
                                                                <mifos:mifoslabel  keyhm="Client.SpouseFatherMiddleName" name="client.SpouseMiddleName"
                                                                        bundle="ClientUIResources">
                                                                        </mifos:mifoslabel>
                                                        </span>
                                                        </td>
                                                    </tr>
                                                    <tr class="fontnormal">
                                                        <td class="paddingL10">
                                                                <mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.spouseMiddleName" keyhm="Client.SpouseFatherMiddleName" property="spouseName.middleName" maxlength="200"
                                                                    style="width:100px;" />
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                            <td class="paddingL10">
                                                <table border="0" cellspacing="0" cellpadding="0">
                                                    <tr class="fontnormal">
                                                        <td class="paddingL10">
                                                        <span id="create_ClientPersonalInfo.label.spouseSecondLastName">
                                                            <mifos:mifoslabel keyhm="Client.SpouseFatherSecondLastName" name="client.SpouseSecondLastName"
                                                                bundle="ClientUIResources">
                                                            </mifos:mifoslabel>
                                                        </span>
                                                        </td>
                                                    </tr>
                                                    <tr class="fontnormal">
                                                        <td class="paddingL10">
                                                            <mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.spouseSecondLastName" keyhm="Client.SpouseFatherSecondLastName"
                                                                property="spouseName.secondLastName"
                                                                    maxlength="200" style="width:100px;" />
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                            <td class="paddingL10">
                                                <table border="0" cellspacing="0" cellpadding="0">
                                                    <tr class="fontnormal">
                                                        <td class="paddingL10">
                                                        <span id="create_ClientPersonalInfo.label.spouseLastName">
                                                            <mifos:mifoslabel keyhm="Client.SpouseFatherInformation" name="client.SpouseLastName"
                                                                bundle="ClientUIResources"></mifos:mifoslabel>
                                                        </span>
                                                        </td>
                                                    </tr>
                                                    <tr class="fontnormal">
                                                        <td class="paddingL10">
                                                            <mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.spouseLastName" property="spouseName.lastName" maxlength="200"
                                                                style="width:100px;" />
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>
                                    </table>
                                    </td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                                    </c:if>
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'addressHeading'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Address">
                                                <td colspan="2" class="fontnormalbold"><mifos:mifoslabel detachHidden="true" keyhm="Client.Address" isManadatoryIndicationNotRequired="yes"
                                        name="client.AddressHeading" bundle="ClientUIResources"></mifos:mifoslabel>
                                    </td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'address1'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Address1">
                                                <td width="17%" align="right" class="fontnormal">
                                    <span id="create_ClientPersonalInfo.label.address1">
                                    <mifos:mifoslabel detachHidden="true" keyhm="Client.Address1"   isColonRequired="yes"
                                        name="${ConfigurationConstants.ADDRESS1}"></mifos:mifoslabel></span>
                                    </td>
                                    <td width="83%"><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.address1" keyhm="Client.Address1"
                                        name="clientCustActionForm"
                                        property="address.line1" maxlength="200" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'address2'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Address2">
                                                <td width="17%" align="right" class="fontnormal">
                                    <span id="create_ClientPersonalInfo.label.address2">
                                    <mifos:mifoslabel detachHidden="true" keyhm="Client.Address2" isColonRequired="yes"
                                        name="${ConfigurationConstants.ADDRESS2}"></mifos:mifoslabel></span></td>
                                    <td width="83%"><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.address2" keyhm="Client.Address2"
                                        name="clientCustActionForm"
                                        property="address.line2" maxlength="200" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'address3'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Address3">
                                                <td align="right" class="fontnormal">
                                    <span id="create_ClientPersonalInfo.label.address3">
                                    <mifos:mifoslabel detachHidden="true" keyhm="Client.Address3" isColonRequired="yes"
                                        name="${ConfigurationConstants.ADDRESS3}"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.address3" keyhm="Client.Address3" name="clientCustActionForm"
                                        property="address.line3" maxlength="200" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'city'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.City">
                                                <td align="right" class="fontnormal">
                                    <span id="create_ClientPersonalInfo.label.city">
                                    <mifos:mifoslabel  detachHidden="true" keyhm="Client.City" isColonRequired="yes"
                                        name="${ConfigurationConstants.CITY}"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.city" keyhm="Client.City" name="clientCustActionForm"
                                        property="address.city" maxlength="100" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'state'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.State">
                                                <td align="right" class="fontnormal">
                                    <span id="create_ClientPersonalInfo.label.state">
                                    <mifos:mifoslabel detachHidden="true" keyhm="Client.State" isColonRequired="yes"
                                        name="${ConfigurationConstants.STATE}"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.state" keyhm="Client.State" name="clientCustActionForm"
                                        property="address.state" maxlength="100" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'country'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.Country">
                                                <td align="right" class="fontnormal">
                                    <span id="create_ClientPersonalInfo.label.country">
                                    <mifos:mifoslabel detachHidden="true" keyhm="Client.Country"
                                        name="client.Country" bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.country" keyhm="Client.Country" name="clientCustActionForm"
                                        property="address.country" maxlength="100" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'postalCode'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.PostalCode">
                                                <td align="right" class="fontnormal">
                                    <span id="create_ClientPersonalInfo.label.postalCode">
                                    <mifos:mifoslabel detachHidden="true" keyhm="Client.PostalCode" isColonRequired="yes"
                                        name="${ConfigurationConstants.POSTAL_CODE}"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.postalCode" keyhm="Client.PostalCode" name="clientCustActionForm"
                                        property="address.zip" maxlength="20" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                            <c:when test="${personalInformation.name == 'telephone'}">
                                                <tr data-order-id="<c:out value="${personalInformation.id}" />" id="Client.PhoneNumber">
                                                <td align="right" class="fontnormal"><span id="create_ClientPersonalInfo.label.telephone"><mifos:mifoslabel detachHidden="true" keyhm="Client.PhoneNumber"
                                        name="client.Telephone" bundle="ClientUIResources"></mifos:mifoslabel></span></td>
                                    <td><mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.telephone" keyhm="Client.PhoneNumber" name="clientCustActionForm"
                                        property="address.phoneNumber" maxlength="20" /></td>
                                                    <c:set var="displayed" value="true" scope="request" />
                                            </c:when>
                                       </c:choose>
                                       <sec:authorize access="isFullyAuthenticated() and hasRole('ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS')">
                                       <c:if test="${displayed}">
                                            <td>
                                                <span class="changeOrderArrows">
                                                    <img class="moveUp" src="pages/framework/images/smallarrowtop.gif" />&nbsp;
                                                    <img class="moveDown" src="pages/framework/images/smallarrowdown.gif" />
                                                </span>
                                            </td>
                                            </tr>
                                        </c:if>
                                        </sec:authorize>
                                       </c:forEach>
                                       
                                </tbody>
                                        </table>
                                    </td>
                                </tr>
							</table>
							<br>
							<!-- Address end -->
                            <!-- Attachements -->
                            <table width="93%" border="0" cellpadding="3" cellspacing="0" id="attachements">
                                <tr>
                                    <td colspan="2" class="fontnormalbold">
                                             <mifos:mifoslabel
                                      name="client.Attachements" bundle="ClientUIResources"></mifos:mifoslabel>( 
                                      <mifos:mifoslabel name="client.MaxUploadSize"
                                      bundle="ClientUIResources"></mifos:mifoslabel> 2 MB )
                                    </td>
                                </tr>
                                <tr class="fontnormal">
                                    <td width="17%" align="right" class="fontnormal">
                                        <span>
                                            <mifos:mifoslabel
                                                    name="client.File"
                                                    bundle="ClientUIResources"></mifos:mifoslabel>
                                        </span>
                                    </td>
                                    <td width="83%"><mifos:file styleId="create_ClientPersonalInfo.input.additionalfile" 
                                        keyhm="Client.File" property="selectedFile" maxlength="200"
                                        onkeypress="return onKeyPressForFileComponent(this);" /></td>
                                </tr>
                                <tr class="fontnormal">
                                    <td width="17%" align="right" class="fontnormal">
                                    <span>
                                        <mifos:mifoslabel
                                                    name="client.description"
                                                    bundle="ClientUIResources"></mifos:mifoslabel></span>
                                    </td>
                                    <td width="83%">
                                        <mifos:mifosalphanumtext 
                                            styleId="create_ClientPersonalInfo.input.additionalfile.description" 
                                            name="clientCustActionForm"
                                            property="selectedFileDescription" maxlength="60" size="40" />
                                    </td>
                                </tr>
                                <tr class="fontnormal">
                                    <td width="17%" align="right" class="fontnormal">
                                    </td>
                                    <td width="83%">
	                                    <html-el:button onclick="addFile()" property="addFileButton">
	                                        <mifos:mifoslabel
	                                                    name="client.addFile"
	                                                    bundle="ClientUIResources"></mifos:mifoslabel>
	                                    </html-el:button> 
                                    </td>
                                </tr>
                                <tr class="fontnormal">
                                    <td colspan="2">&nbsp;</td>
                                </tr>
                                <tr class="fontnormal">
                                    <td class="fontnormal" colspan="2" style="padding-left: 150px; padding-bottom: 20px">
                                        <ol id="filesToUpload">
                                            <c:forEach var="fileToUpload" items="${sessionScope.clientCustActionForm.filesMetadata}">
                                                <li id="${fileToUpload.name}">
                                                    <b>${fileToUpload.name}</b><br/>
                                                    ${fileToUpload.description}<br/>
                                                    <input type="button" id=${fileToUpload.name} value="X" 
                                                           onclick="deleteFile('${fileToUpload.name}')" />
                                                    <br/><br/>
                                                </li>
                                            </c:forEach>
                                        </ol>
                                    </td>
                                </tr>
                            </table>
                            <br>
                            <!-- Attachements end -->
                            <!-- Custom Fields -->
                            
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel name="client.additionalInformation"
													bundle="ClientUIResources"/><br>
									<br>
									</td>
								</tr>

								<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->
								<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="17%" align="right"><span id="create_ClientPersonalInfo.label.customField"><mifos:mifoslabel
											name="${cf.lookUpEntityType}"
											mandatory="${cf.mandatoryString}"
											bundle="ClientUIResources"></mifos:mifoslabel></span>:</td>
										<td width="83%">
										<html-el:hidden property='customField[${ctr}].fieldId' value="${cf.fieldId}"></html-el:hidden>
										<html-el:hidden property='fieldTypeList' value='${cf.fieldType}' />

										<c:if test="${cf.fieldType == 1}">
											<mifos:mifosnumbertext styleId="create_ClientPersonalInfo.input.customField" name="clientCustActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 2}">
											<mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.customField" name="clientCustActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 3}">
											<date:datetag property="customField[${ctr}].fieldValue" />
										</c:if>
										</td>
									</tr>
								</c:forEach>

							</table>
							<br>
							</c:if>
							<!-- Custom Fields end -->
							<!-- Question Groups -->
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'questionGroups')}">
                                <table width="93%" border="0" cellpadding="3" cellspacing="0">
                                    <c:forEach var="qg" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'questionGroups')}"
                                        varStatus="qgLoopStatus">
                                        <bean:define id="qgCtr">
                                            <c:out value="${qgLoopStatus.index}" />
                                        </bean:define>
                                        <c:forEach var="sec" items="${qg.sections}" varStatus="secLoopStatus">
                                            <bean:define id="secCtr">
                                                <c:out value="${secLoopStatus.index}" />
                                            </bean:define>
                                            <tr><td>&nbsp;</td></tr>
                                            <tr>
                                                <td colspan="2" class="fontnormalbold">
                                                    <c:out value="${sec.name}" />
                                                    <br>
                                                </td>
                                            </tr>
                                            <c:forEach var="ques" items="${sec.questions}" varStatus="quesLoopStatus">
                                                <bean:define id="quesCtr">
                                                    <c:out value="${quesLoopStatus.index}" />
                                                </bean:define>
                                                <tr class="fontnormal">
                                                    <td width="17%" align="right">
                                                        <span id="create_ClientPersonalInfo.label.question">
                                                            <c:if test="${ques.requiredString == 'true'}">
                                                                <span class="mandatorytext">
                                                                    <font color="#FF0000">*</font>
                                                                </span>
                                                            </c:if>
                                                            <c:out value="${ques.text}" />
                                                        </span>:
                                                    </td>
                                                    <td width="83%">
                                                    <html-el:hidden property='questionGroup[${qgCtr}].section[${secCtr}].question[${quesCtr}].id' value="${ques.id}"></html-el:hidden>
                                                    <html-el:hidden property='fieldTypeList' value='${ques.questionTypeAsNum}' />
                                                    <c:if test="${ques.questionTypeAsNum == 1}">
                                                        <mifos:mifosalphanumtext styleId="create_ClientPersonalInfo.input.customField" name="clientCustActionForm"
                                                            property='questionGroup[${qgCtr}].section[${secCtr}].question[${quesCtr}].value' maxlength="200" />
                                                    </c:if> <c:if test="${ques.questionTypeAsNum == 2}">
                                                        <mifos:mifosnumbertext styleId="create_ClientPersonalInfo.input.customField" name="clientCustActionForm"
                                                            property='questionGroup[${qgCtr}].section[${secCtr}].question[${quesCtr}].value' maxlength="200" />
                                                    </c:if> <c:if test="${ques.questionTypeAsNum == 5}">
                                                        <date:datetag property="questionGroup[${qgCtr}].section[${secCtr}].question[${quesCtr}].value" />
                                                    </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:forEach>
                                    </c:forEach>
                                </table>
							</c:if>
							<!-- Question Groups end -->
							<!-- Buttons -->
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><c:choose>

										<c:when
											test="${param.method eq 'prevPersonalInfo' or sessionScope.clientCustActionForm.nextOrPreview eq 'preview'}">
											<html:hidden property="nextOrPreview" value="preview" />

											<html-el:button styleId="create_ClientPersonalInfo.button.preview" onclick="goToPreviewPage();"
												property="submitButton" styleClass="buttn">
												<mifos:mifoslabel name="button.preview"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:button>
										</c:when>
										<c:otherwise>
											<html-el:submit onclick="goToNextPage()" 
											                styleId="create_ClientPersonalInfo.button.continue" styleClass="buttn">
												<mifos:mifoslabel name="button.continue"
													bundle="ClientUIResources"></mifos:mifoslabel>
											</html-el:submit>
										</c:otherwise>
									</c:choose> &nbsp; &nbsp; <html-el:button styleId="create_ClientPersonalInfo.button.cancel"
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="button.cancel"
											bundle="ClientUIResources"></mifos:mifoslabel>
									</html-el:button>
									</td>
								</tr>
							</table>
							<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
							<!-- Button end --> <br>
							<!-- before main closing --></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
	</tiles:put>
</tiles:insert>
