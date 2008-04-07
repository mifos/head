<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">

		<script language="javascript">
	function goToCancelPage(){
		document.offActionForm.action="AdminAction.do?method=load";
		offActionForm.submit();
  	}

  function papulateParent(selectBox){
		if(selectBox.selectedIndex > 0)
		{
	  document.offActionForm.action="offAction.do?method=loadParent";
	  offActionForm.submit();
	}
  }
</script>
		<html-el:form action="/offAction.do?method=preview">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td class="bluetablehead">
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td width="27%">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17px" height="17px"></td>
											<td class="timelineboldorange"><mifos:mifoslabel
												name="Office.labelOfficeInformation" /></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0" align="right">
										<tr>
											<td><img
												src="pages/framework/images/timeline/orangearrow.gif"
												width="17px" height="17px"></td>
											<td class="timelineboldorangelight"><mifos:mifoslabel
												name="Office.labelReviewAndSubmit" /></td>
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
									<td class="headingorange"><span class="heading"><mifos:mifoslabel
										name="Office.labelAddNewOffice" /></span>
									<mifos:mifoslabel name="Office.labelEnterOfficeInformation"
										/></td>

								</tr>
								<tr>
									<td class="fontnormal"><mifos:mifoslabel
										name="Office.labelCompleteTheFields" />
									<br>
									<mifos:mifoslabel name="Office.labelFieldsMarkedWithAsterisk"
										mandatory="yes" /></td>
								</tr>
							</table>
							<br>
							<font class="fontnormalRedBold"><html-el:errors
								bundle="OfficeUIResources" /> </font>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Office.labelOfficeDetails" /><br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="20%" align="right"><mifos:mifoslabel
										name="Office.labelOfficeName" mandatory="yes"
										/></td>
									<td width="80%"><mifos:mifosalphanumtext property="officeName"
										maxlength="200" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Office.labelOfficeShortName" mandatory="yes"
										/></td>
									<td><mifos:mifosalphanumtext property="shortName" size="4"
										maxlength="4"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Office.labelOfficeType" mandatory="yes"
										/></td>
									<td><mifos:select property="officeLevel"
										onchange="return papulateParent(this)" >
										<c:forEach var="levelList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeLevelList')}" >
											<html-el:option value="${levelList.levelId}">${levelList.levelName}</html-el:option>
										</c:forEach>
									</mifos:select></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Office.labelParentOffice" mandatory="yes"
										/></td>
									<td><mifos:select property="parentOfficeId">
										<c:if test="${not empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}">
										<c:forEach var="parentList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}" >
											<html-el:option value="${parentList.officeId}">${parentList.displayName}</html-el:option>
										</c:forEach>
										</c:if>

									</mifos:select></td>
								</tr>
							</table>
							<br>
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
										name="Office.labelOfficeAddress" /><br>
									<br>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="20%" align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS1}"
										bundle="OfficeUIResources"/>:</td>
									<td width="80%"><mifos:mifosalphanumtext
										property="address.line1" maxlength="200" /></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS2}"
										bundle="OfficeUIResources"/>:</td>
									<td><mifos:mifosalphanumtext property="address.line2"
										maxlength="200"/></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.ADDRESS3}"
										bundle="OfficeUIResources" keyhm="Office.Address3"
										isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.line3"
										maxlength="200" keyhm="Office.Address3"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.CITY}" bundle="OfficeResources"></mifos:mifoslabel>:</td>
									<td><mifos:mifosalphanumtext property="address.city"
										maxlength="100"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}"
										bundle="OfficeResources" keyhm="Office.State"
										isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.state"
										maxlength="100" keyhm="Office.State"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel name="Office.labelCountry"
										 keyhm="Office.Country"/></td>
									<td><mifos:mifosalphanumtext property="address.country"
										maxlength="100" keyhm="Office.Country"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}"
										bundle="OfficeResources" keyhm="Office.PostalCode"
										isColonRequired="yes"></mifos:mifoslabel></td>
									<td><mifos:mifosalphanumtext property="address.zip"
										maxlength="20" keyhm="Office.PostalCode"></mifos:mifosalphanumtext></td>
								</tr>
								<tr class="fontnormal">
									<td align="right"><mifos:mifoslabel
										name="Office.labelTelephone" />

									</td>

									<td><mifos:mifosalphanumtext property="address.phoneNumber"
										maxlength="20"></mifos:mifosalphanumtext></td>
								</tr>
							</table>
							<br>
							<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<c:if test="${!empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}">
									<tr>
										<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
											name="Office.labelAdditionInformation"
											/> <br>
										<br>
										</td>
									</tr>
								</c:if>
								<c:forEach var="cf" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="21%" align="right"><mifos:mifoslabel
											name="${cf.lookUpEntity.entityType}"
											mandatory="${cf.mandatoryStringValue}"
											bundle="OfficeResources"></mifos:mifoslabel>:
										</td>
										<td width="79%"><c:if test="${cf.fieldType == 1}">
											<mifos:mifosnumbertext name="offActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 2}">
											<mifos:mifosalphanumtext name="offActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />
										</c:if> <c:if test="${cf.fieldType == 3}">
											<mifos:mifosalphanumtext name="offActionForm"
												property='customField[${ctr}].fieldValue' maxlength="200" />

										</c:if></td>
									</tr>
								</c:forEach>

							</table>
						</c:if>
							<!--Custom Fields end  -->

							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>

							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<!-- Next are submit and cancel button -->
									<td align="center"><html-el:submit styleClass="buttn"
										style="width:70px;">
										<mifos:mifoslabel name="Office.preview"
											/>
									</html-el:submit> &nbsp; <html-el:button
										onclick="goToCancelPage();" property="cancelButton"
										value="Cancel" styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="Office.cancel"
											/>
									</html-el:button></td>
								</tr>
							</table>
							<br>
					</table>
				</tr>

				<br>

			</table>
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="input" value="create" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<br>
			<br>

		</html-el:form>
	</tiles:put>

</tiles:insert>
