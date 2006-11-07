
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/personnel/personnelfunctions" prefix="personnelfn"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">
  function goToEditPage(){
	personActionForm.action="PersonAction.do?method=previous";
	personActionForm.submit();
  }
  function goToCancelPage(){
	personActionForm.action="PersonAction.do";
	personActionForm.method.value="cancel";
	personActionForm.submit();
  }
</script>
		<html-el:form action="PersonAction.do"
			onsubmit="func_disableSubmitBtn('submitBtn')">

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading">&nbsp;</td>
						</tr>
					</table>
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="33%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="Personnel.ChooseOffice" bundle="PersonnelUIResources"></mifos:mifoslabel>
											</td>
										</tr>
									</table>
									</td>
									<td width="34%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17"></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="Personnel.UserInformation"
												bundle="PersonnelUIResources"></mifos:mifoslabel></td>
										</tr>
									</table>
									</td>
									<td width="33%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="Personnel.ReviewSubmit" bundle="PersonnelUIResources"></mifos:mifoslabel>
											</td>
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
									<td class="headingorange"><span class="heading"> <mifos:mifoslabel
										name="Personnel.AddNewUser" bundle="PersonnelUIResources"></mifos:mifoslabel>
									- </span> <mifos:mifoslabel name="Personnel.ReviewSubmit"
										bundle="PersonnelUIResources"></mifos:mifoslabel></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="Personnel.ReviewInformation"
										bundle="PersonnelUIResources"></mifos:mifoslabel> <mifos:mifoslabel
										name="Personnel.SubmitOrEdit" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<mifos:mifoslabel name="Personnel.ClickCancel"
										bundle="PersonnelUIResources"></mifos:mifoslabel></td>
								</tr>
							</table>
							<table width="93%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td><font class="fontnormalRedBold"> <html-el:errors
										bundle="PersonnelUIResources" /> </font></td>
								</tr>
								<tr>
									<td><span class="fontnormalbold"> <mifos:mifoslabel
										name="Personnel.Office" bundle="PersonnelUIResources"></mifos:mifoslabel>
									</span> <span class="fontnormal"><c:out
										value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'office').officeName}" />
									</span></td>
								</tr>
							</table>


							<table width="93%" border="0" cellpadding="0" cellspacing="0">

								<tr>
									<td width="100%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
										name="Personnel.UserInformation" bundle="PersonnelUIResources"></mifos:mifoslabel>
									</td>
								</tr>

								<tr>
									<td height="23" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.FirstName" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.firstName}" /> </span><br>
									</td>
								</tr>

								<tr id="Personnel.MiddleName">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.MiddleName" bundle="PersonnelUIResources"
										keyhm="Personnel.MiddleName"
										isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"> <c:out
										value="${personActionForm.middleName}" /> </span><br>
									</td>
								</tr>

								<tr id="Personnel.SecondLastName">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.SecondLastName" bundle="PersonnelUIResources"
										keyhm="Personnel.SecondLastName"
										isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"> <c:out
										value="${personActionForm.secondLastName}" /> </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.LastName" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.lastName}" /> </span><br>
									</td>
								</tr>

								<tr id="Personnel.GovernmentId">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.GOVERNMENT_ID}"
										bundle="PersonnelUIResources" keyhm="Personnel.GovernmentId"
										isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.governmentIdNumber}" /> </span> <br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Email" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.emailId}" /> </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.DOB" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.dob}" />
									</span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Age" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.age}" /> </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.MaritalStatus" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:forEach
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusList')}"
										var="item">
										<c:if test="${personActionForm.maritalStatus == item.id}">
											${item.name}
								</c:if>
									</c:forEach> </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Gender" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:forEach
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderList')}"
										var="item">

										<c:if test="${personActionForm.gender == item.id}">
											${item.name}
								</c:if>
									</c:forEach> </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.LanguagePreferred"
										bundle="PersonnelUIResources"></mifos:mifoslabel> <span
										class="fontnormal"> <c:forEach
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'languageList')}"
										var="item">

										<c:if test="${personActionForm.preferredLocale == item.localeId}">
											${item.language.name}
								</c:if>
									</c:forEach>  </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.DOJMFI" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.dateOfJoiningMFI}" />

									</span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.DOJBranch" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.dateOfJoiningBranch}" />

									</span><br>
									<br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Address" bundle="PersonnelUIResources"></mifos:mifoslabel><br>
									<span class="fontnormal"> <c:out
										value="${personActionForm.address.displayAddress}" /> <br>
									</span></td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.CITY}"
										bundle="PersonnelUIResources"></mifos:mifoslabel>: <span
										class="fontnormal"> <c:out
										value="${personActionForm.address.city}" /> </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold" id="Personnel.State"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}"
										bundle="PersonnelUIResources" keyhm="Personnel.State"
										isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.address.state}" /> </span><br>
									</td>
								</tr>

								<tr id="Personnel.Country">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Country" bundle="PersonnelUIResources"
										keyhm="Personnel.Country"
										isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"> <c:out
										value="${personActionForm.address.country}" /> </span><br>
									</td>
								</tr>

								<tr id="Personnel.PostalCode">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}"
										bundle="PersonnelUIResources" keyhm="Personnel.PostalCode"
										isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel>
									<span class="fontnormal"> <c:out
										value="${personActionForm.address.zip}" /> </span><br>
									</td>
								</tr>

								<tr id="Personnel.PhoneNumber">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Telephone" bundle="PersonnelUIResources"
										keyhm="Personnel.PhoneNumber"
										isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel> <span
										class="fontnormal"> <c:out
										value="${personActionForm.address.phoneNumber}" /> </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><br>
									<mifos:mifoslabel name="Personnel.OfficePermissions"
										bundle="PersonnelUIResources"></mifos:mifoslabel> <br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.UserTitle" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:forEach
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'titleList')}"
										var="item">

										<c:if test="${personActionForm.title == item.id}">
											${item.name}
								</c:if>
									</c:forEach> <br>


									</span></td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.UserHierarchy" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal"> <c:forEach
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelLevelList')}"
										var="item">

										<c:if test="${personActionForm.level == item.id}">
											${item.name}
								</c:if>
									</c:forEach> </span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Roles" bundle="PersonnelUIResources"></mifos:mifoslabel>
									<span class="fontnormal">
									
									 
										<c:forEach
										items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelRolesList')}"
										var="item"  varStatus="loopStatus">
										<c:if test="${loopStatus.index == 0}">
										      ${item.name}
										</c:if>
										<c:if test="${loopStatus.index != 0}">
										      ,${item.name}
										</c:if>
										
									</c:forEach>
										
									</span><br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><br>
									<mifos:mifoslabel name="Personnel.LoginInformation"
										bundle="PersonnelUIResources"></mifos:mifoslabel> <br>
									<mifos:mifoslabel name="Personnel.UserName"
										bundle="PersonnelUIResources"></mifos:mifoslabel> <c:out
										value="${personActionForm.loginName}" /> <br>
									</td>
								</tr>

								<tr>
									<td class="fontnormalbold"><br>
									<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
										<mifos:mifoslabel name="Personnel.AdditionalInfo"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
										<br>
									 <c:forEach var="cfdef" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
										<c:forEach var="cf"
											items="${personActionForm.customFields}">
											<c:if test="${cfdef.fieldId==cf.fieldId}">
												<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}"
													bundle="PersonnelUIResources"></mifos:mifoslabel>:
												<span class="fontnormal"> <c:out value="${cf.fieldValue}" />
												</span>
												<br>
											</c:if>
										</c:forEach>
									</c:forEach> <br>
									</c:if>
									<html-el:button property="btn" styleClass="insidebuttn"
										style="width:130px;" onclick="goToEditPage()">
										<mifos:mifoslabel name="button.EditUserInformation"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
								<tr>
									<td align="center">&nbsp;</td>
								</tr>
								<tr>
									<td align="center">&nbsp; <html-el:submit property="submitBtn"
										styleClass="buttn" style="width:70px;">
										<mifos:mifoslabel name="button.submit"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button property="cancelBtn"
										styleClass="cancelbuttn" style="width:70px"
										onclick="goToCancelPage()">
										<mifos:mifoslabel name="button.cancel"
											bundle="PersonnelUIResources"></mifos:mifoslabel>
									</html-el:button></td>
								</tr>
							</table>
							<br>
							</td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="input" value="CreateUser" />
			<html-el:hidden property="method" value="create" />
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
