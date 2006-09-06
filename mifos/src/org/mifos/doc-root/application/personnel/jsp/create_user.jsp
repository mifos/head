<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<script language="javascript">

  function goToCancelPage(){
	personActionForm.action="PersonAction.do";
	personActionForm.method.value="cancel";
	personActionForm.submit();
  }
 
</script>

		<SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
		<html-el:form action="PersonAction.do"
			onsubmit="return (validateMyForm(dateOfJoiningMFI,dateOfJoiningMFIFormat,dateOfJoiningMFIYY) && validateMyForm(dob,dobFormat,dobYY))"
			focus="firstName">

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
												name="Personnel.ChooseOffice" /></td>
										</tr>
									</table>
									</td>
									<td width="34%" align="center">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="Personnel.UserInformation" /></td>
										</tr>
									</table>
									</td>
									<td width="33%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17" height="17"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="Personnel.ReviewSubmit" /></td>
										</tr>
									</table>
									</td>
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
										name="Personnel.AddNewUser" /> - </span> <mifos:mifoslabel
										name="Personnel.EnterUserInformation" /></td>
								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="Personnel.CompleteFields" /> <mifos:mifoslabel
										name="Personnel.ClickPreview" /> <mifos:mifoslabel
										name="Personnel.ClickCancel" /> <br>

									<mifos:mifoslabel name="Personnel.FieldsMandatory"
										mandatory="yes" /></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellspacing="0" cellpadding="3">
								<tr>
									<td><font class="fontnormalRedBold"> <html-el:errors
										bundle="PersonnelUIResources" /> </font></td>
								</tr>
								<tr>
									<td class="fontnormal"><span class="fontnormalbold"> <mifos:mifoslabel
										name="Personnel.Office" /> </span> <c:out
										value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'office').officeName}" /></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr class="fontnormal">
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.UserDetails" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" height="28" align="right"><mifos:mifoslabel
										name="Personnel.FirstName" bundle="PersonnelUIResources"
										mandatory="yes"></mifos:mifoslabel></td>
									<td width="78%"><mifos:mifosalphanumtext
										name="personActionForm"
										property="firstName" /></td>
										
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.MiddleName"
										bundle="PersonnelUIResources" keyhm="Personnel.MiddleName"></mifos:mifoslabel>
									</td>
									<td><mifos:mifosalphanumtext keyhm="Personnel.MiddleName"
										name="personActionForm"
										property="middleName" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										keyhm="Personnel.SecondLastName"
										name="Personnel.SecondLastName" /></td>
									<td><mifos:mifosalphanumtext keyhm="Personnel.SecondLastName"
										name="personActionForm"
										property="secondLastName" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.LastName"
										mandatory="yes" /></td>
									<td><mifos:mifosalphanumtext name="personActionForm"
										property="lastName" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										keyhm="Personnel.GovernmentId" isColonRequired="yes"
										name="${ConfigurationConstants.GOVERNMENT_ID}" bundle="PersonnelUIResources"/></td>
									<td><mifos:mifosalphanumtext keyhm="Personnel.GovernmentId"
										name="personActionForm"
										property="governmentIdNumber" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Email" /></td>
									<td><mifos:mifosalphanumtext property="emailId" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.DOB"
										mandatory="yes" /></td>
									<td><date:datetag property="dob" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.MaritalStatus" /></td>
									<td><mifos:select name="personActionForm"
										property="maritalStatus">
											<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'maritalStatusList')}" 
											var="maritalStatus">
											<html-el:option value="${maritalStatus.id}">${maritalStatus.name}</html-el:option>
											</c:forEach>
											
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Gender"
										mandatory="yes" /></td>
									<td><mifos:select name="personActionForm"
										property="gender">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'genderList')}" 
											var="genderlist">
											<html-el:option value="${genderlist.id}">${genderlist.name}</html-el:option>
											</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.LanguagePreferred" /></td>
									<td>
									<mifos:select name="personActionForm"
										property="preferredLocale">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'languageList')}" 
											var="languagelist">
											<html-el:option value="${languagelist.id}">${languagelist.name}</html-el:option>
											</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.DOJMFI" /></td>
									<td><date:datetag property="dateOfJoiningMFI" /></td>
								</tr>

							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Address" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS1}" bundle="PersonnelUIResources"/>:</td>
									<td width="78%"><mifos:mifosalphanumtext
										name="personActionForm"
										property="address.line1" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS2}" bundle="PersonnelUIResources"/>:</td>
									<td><mifos:mifosalphanumtext name="personActionForm"
										property="address.line2" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel keyhm="Personnel.Address3"
										isColonRequired="yes"
										name="${ConfigurationConstants.ADDRESS3}" bundle="PersonnelUIResources"/></td>
									<td><mifos:mifosalphanumtext keyhm="Personnel.Address3"
										name="personActionForm"
										property="address.line3" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.CITY}" bundle="PersonnelUIResources"/>:</td>
									<td><mifos:mifosalphanumtext name="personActionForm"
										property="address.city" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}"
										bundle="PersonnelUIResources" keyhm="Personnel.State"
										isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext name="personActionForm"
										property="address.state" keyhm="Personnel.State" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Country"
										bundle="PersonnelUIResources" keyhm="Personnel.Country"></mifos:mifoslabel>
									</td>
									<td><mifos:mifosalphanumtext name="personActionForm"
										property="address.country" keyhm="Personnel.Country" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}"
										bundle="PersonnelUIResources" isColonRequired="yes"
										keyhm="Personnel.PostalCode"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext name="personActionForm"
										property="address.zip"
										keyhm="Personnel.PostalCode" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										keyhm="Personnel.PhoneNumber" name="Personnel.Telephone" /></td>
									<td><mifos:mifosalphanumtext keyhm="Personnel.PhoneNumber"
										name="personActionForm"
										property="address.phoneNumber" /></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr class="fontnormal">
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.Permission" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" align="right"><mifos:mifoslabel
										name="Personnel.UserTitle" /></td>
									<td width="78%">
										 <mifos:select
										name="personActionForm" property="title">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'titleList')}" 
											var="titlelist">
											<html-el:option value="${titlelist.id}">${titlelist.name}</html-el:option>
											</c:forEach>
									</mifos:select> </td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.UserHierarchy" mandatory="yes" /></td>

									<td>
										<mifos:select
										name="personActionForm" property="level">
										<c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelLevelList')}" 
											var="item">
											<html-el:option value="${item.id}">${item.name}</html-el:option>
											</c:forEach>
									</mifos:select></td>
								</tr>

								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Roles" /></td>
									<td>
									<c:set var="rolelist" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'rolesList')}"/> 
									<c:set var="personnelRolesList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'personnelRolesList')}"/> 
									<mifos:MifosSelect property="personnelRoles"
										input="rolelist" output="personnelRolesList" property1="id"
										property2="name" multiple="true">
									</mifos:MifosSelect>  </td>
								</tr>

							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Personnel.LoginInformation" /> <br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" align="right"><mifos:mifoslabel
										name="Personnel.UserName" mandatory="yes" /></td>
									<td width="78%"><mifos:mifosalphanumtext property="loginName" name="personActionForm" />
									</td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Personnel.Password"
										mandatory="yes" /></td>
									<td><html-el:password property="userPassword" name="personActionForm" style="width:136px;"
										redisplay="false" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Personnel.ConfirmPassword" mandatory="yes" /></td>
									<td><html-el:password property="passwordRepeat"
										style="width:136px;" name="personActionForm" redisplay="false" /></td>
								</tr>
							</table>
							<br>
							
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
							
							<c:set var="customFieldsList" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}"/> 
								<c:if test="${requestScope.customFieldsList !=null}">
									<tr>
										<td colspan="2" class="fontnormalbold" >
										<mifos:mifoslabel	name="Personnel.AdditionalInfo"	/><br>
										<br>
										</td>
									</tr>
								</c:if>
								<c:forEach var="cf" items="${requestScope.customFieldsList}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="22%" align="right"><mifos:mifoslabel
											name="${cf.lookUpEntity.entityType}"
											mandatory="${cf.mandatoryStringValue}"
											bundle="PersonnelUIResources"></mifos:mifoslabel>: 
										</td>
										<td width="78%" ><c:if test="${cf.fieldType == 1}">
											<mifos:mifosnumbertext name="personActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 2}">
											<mifos:mifosalphanumtext name="personActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 3}">
											<mifos:mifosalphanumtext name="personActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />

										</c:if></td>
									</tr>
								</c:forEach>

							</table>

							<!--Custom Fields end  -->
							
							
							

							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px;"
										onclick="transferData(this.form.personnelRoles)">
										<mifos:mifoslabel name="button.preview"
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
			<html-el:hidden property="method" value="preview" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>

