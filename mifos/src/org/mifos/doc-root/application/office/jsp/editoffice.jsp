
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
<script language="javascript">
	function goToCancelPage(){
		document.offActionForm.action="offAction.do?method=get";
		offActionForm.submit();
	}
  function papulateParent()
  {
	document.offActionForm.action="offAction.do?method=loadParent&input=edit";
	offActionForm.submit();
  }
</script>
		<html-el:form action="/offAction.do?method=editpreview">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"></c:set>
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="Office.labelLinkAdmin" />
					</html-el:link> / <html-el:link
						href="offAction.do?method=getAllOffices&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="Office.labelLinkViewOffices" />
					</html-el:link> / <html-el:link
						href="offAction.do?method=get&officeId=${offActionForm.officeId}&randomNUm=${sessionScope.randomNUm}">
						<c:out value="${BusinessKey.officeName}"></c:out>
					</html-el:link> </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"><c:out
								value="${BusinessKey.officeName}"></c:out> - </span><mifos:mifoslabel
								name="Office.labelEditOfficeInfo" /></td>
						</tr>
						<tr>
							<td class="fontnormal"><mifos:mifoslabel
								name="Office.labelMandatoryInstructions" /> <mifos:mifoslabel
								name="Office.labelFieldsMarkedWithAsterisk" mandatory="yes" /></td>
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
								name="Office.labelOfficeName" mandatory="yes" /></td>
							<td width="80%"><mifos:mifosalphanumtext property="officeName"
								maxlength="200" name="offActionForm" ></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="Office.labelOfficeShortName" mandatory="yes" /></td>
							<td><mifos:mifosalphanumtext property="shortName" size="4"
								maxlength="4" name="offActionForm"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right">
							<c:choose>
							<c:when test="${offActionForm.officeLevel eq OfficeLevel.HEADOFFICE.value}">
							<mifos:mifoslabel name="Office.labelOfficeType"
								 />
							</c:when>
							<c:otherwise>
							<mifos:mifoslabel name="Office.labelOfficeType"
								mandatory="yes" />
							</c:otherwise>

							</c:choose>
								</td>
							<td>

							<c:choose>
							<c:when test="${offActionForm.officeLevel eq OfficeLevel.HEADOFFICE.value or BusinessKey.level.id eq OfficeLevel.BRANCHOFFICE.value}">
							  <mifos:select name="offActionForm" property="officeLevel" disabled="true"
								size="1" >
									<html-el:option value="${BusinessKey.level.id}">${BusinessKey.level.name}</html-el:option>
							  </mifos:select>

							</c:when>
							<c:otherwise>
							<mifos:select name="offActionForm" property="officeLevel"
								size="1" onchange="papulateParent()">
								<c:forEach var="levelList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeLevelList')}" >
									<html-el:option value="${levelList.levelId}">${levelList.levelName}</html-el:option>
								</c:forEach>
							</mifos:select>
							</c:otherwise>
							</c:choose>

							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="Office.labelParentOffice" mandatory="yes" /></td>
							<td>
							<c:if test="${not empty session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}">
								<mifos:select name="officeActionForm"
									property="parentOfficeId" size="1"
									value="${offActionForm.parentOfficeId}">
									<c:forEach var="parentList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Parents')}" >
										<html-el:option value="${parentList.officeId}">${parentList.displayName}</html-el:option>
									</c:forEach>
								</mifos:select>
							</c:if>
							</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
								name="Office.labelStatus" /><br>
							<br>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="20%" align="right"><mifos:mifoslabel
								name="Office.labelChangeStatus" /></td>
							<td width="80%"><c:choose>
								<c:when
									test='${offActionForm.officeLevel eq OfficeLevel.HEADOFFICE.value}'>
									<html-el:select name="offActionForm" disabled="true"
										property="officeStatus" size="1"
										value="${offActionForm.officeStatus}">
										<c:forEach var="statusList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeStatusList')}" >
											<html-el:option value="${statusList.levelId}">${statusList.levelName}</html-el:option>
										</c:forEach>
									</html-el:select>

								</c:when>
								<c:otherwise>
									<html-el:select name="officeActionForm" property="officeStatus"
										size="1" value="${offActionForm.officeStatus}">
										<c:forEach var="statusList" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficeStatusList')}" >
											<html-el:option value="${statusList.levelId}">${statusList.levelName}</html-el:option>
										</c:forEach>

									</html-el:select>
								</c:otherwise>
							</c:choose></td>
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
								property="address.line1"
								value="${offActionForm.address.line1}"
								maxlength="200"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.ADDRESS2}"
								bundle="OfficeUIResources"/>:</td>
							<td><mifos:mifosalphanumtext property="address.line2"
								value="${offActionForm.address.line2}"
								maxlength="200"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal" id="Office.Address3">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.ADDRESS3}"
								bundle="OfficeUIResources" keyhm="Office.Address3"
								isColonRequired="yes"></mifos:mifoslabel>:</td>
							<td><mifos:mifosalphanumtext property="address.line3"
								value="${offActionForm.address.line3}"
								maxlength="200" keyhm="Office.Address3">
							</mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.CITY}" bundle="OfficeUIResources"/>:</td>
							<td><mifos:mifosalphanumtext property="address.city"
								value="${offActionForm.address.city}" maxlength="100"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.STATE}" bundle="OfficeUIResources"
								keyhm="Office.State" isColonRequired="yes"/></td>
							<td><mifos:mifosalphanumtext property="address.state"
								value="${offActionForm.address.state}" maxlength="100"
								keyhm="Office.State"/></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="Office.labelCountry"
								 keyhm="Office.Country"/></td>
							<td><mifos:mifosalphanumtext property="address.country"
								value="${offActionForm.address.country}" maxlength="100"
								keyhm="Office.Country"/></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.POSTAL_CODE}"
								bundle="OfficeUIResources" keyhm="Office.PostalCode"
								isColonRequired="yes"/></td>
							<td><mifos:mifosalphanumtext property="address.zip"
								value="${offActionForm.address.zip}"
								maxlength="20" keyhm="Office.PostalCode"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="Office.labelTelephone"
								/></td>

							<td><mifos:mifosalphanumtext property="address.phoneNumber"
								value="${offActionForm.address.phoneNumber}"
								maxlength="20"/></td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<c:if test="${!empty BusinessKey.customFields}">
							<tr>
								<td colspan="2" class="fontnormalbold"><mifos:mifoslabel
									name="office.labelAdditionInformation" bundle="OfficeResources"></mifos:mifoslabel>
								<br>
								<br>
								</td>
							</tr>
						</c:if>
						<!-- For each custom field definition in the list custom field entity is passed as key to mifos label -->
						<c:forEach var="customFieldDef"
							items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'customFields')}" varStatus="loopStatus">
							<bean:define id="ctr">
								<c:out value="${loopStatus.index}" />
							</bean:define>
							<c:forEach var="cf"
								items="${offActionForm.customFields}">
								<c:if test="${customFieldDef.fieldId==cf.fieldId}">
									<tr class="fontnormal">
										<td width="21%" align="right"><mifos:mifoslabel
											name="${customFieldDef.lookUpEntity.entityType}"
											mandatory="${customFieldDef.mandatoryStringValue}"
											bundle="OfficeUIResources"></mifos:mifoslabel>:
										</td>
										<td width="79%"><c:if test="${customFieldDef.fieldType == 1}">
											<mifos:mifosnumbertext name="offActionForm"
												property='customField[${ctr}].fieldValue'
												value="${cf.fieldValue}" maxlength="200" />
										</c:if> <c:if test="${customFieldDef.fieldType == 2}">
											<mifos:mifosalphanumtext name="offActionForm"
												property='customField[${ctr}].fieldValue'
												value="${cf.fieldValue}" maxlength="200" />
										</c:if> <c:if test="${customFieldDef.fieldType == 3}">
											<mifos:mifosalphanumtext name="offActionForm"
												property='customField[${ctr}].fieldValue'
												value="${cf.fieldValue}" maxlength="200" />

										</c:if> <html-el:hidden property='customField[${ctr}].fieldId'
											value="${cf.fieldId}"></html-el:hidden></td>
									</tr>
								</c:if>
							</c:forEach>
						</c:forEach>

					</table>

					<!--Custom Fields end  -->
			</table>
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
						<mifos:mifoslabel name="office.button.preview"
							bundle="OfficeResources"></mifos:mifoslabel>
					</html-el:submit> &nbsp; <html-el:button
						onclick="goToCancelPage();" property="cancelButton" value="Cancel"
						styleClass="cancelbuttn" style="width:70px">
						<mifos:mifoslabel name="office.button.cancel"
							bundle="OfficeResources"></mifos:mifoslabel>
					</html-el:button></td>
				</tr>
			</table>
			<br>
			<br>
			<html-el:hidden property="officeId" value="${BusinessKey.officeId}" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>

	</tiles:put>

</tiles:insert>

