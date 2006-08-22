
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<script language="javascript">
function goToCancelPage(){
	document.offActionForm.method.value="get";
	offActionForm.submit();

	}
  function papulateParent()
  {
		
		document.offActionForm.method.value="loadParent";
		document.offActionForm.method.value="edit";
		offActionForm.submit();		
  }	
   function submitViewOfficesLink(){
	document.offActionForm.method.value="loadall";
		
		
	offActionForm.submit();
  }
function getOffice(officeid){
	document.offActionForm.method.value="get";
	document.offActionForm.officeId.value=officeid;	
	offActionForm.submit();
  }  
   function  submitAdminLink()
{
		document.offActionForm.method.value="load";
		document.offActionForm.action="AdminAction.do";
		offActionForm.submit();
}
</script>
		<html-el:form action="/offAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="javascript:submitAdminLink()">
						<mifos:mifoslabel name="Office.labelLinkAdmin" />
					</html-el:link> / <html-el:link
						href="javascript:submitViewOfficesLink()">
						<mifos:mifoslabel name="Office.labelLinkViewOffices" />
					</html-el:link> / <html-el:link
						href="javascript:getOffice(${BusinessKey.officeId})">
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
								maxlength="200" value="${BusinessKey.officeName}"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="Office.labelOfficeShortName" mandatory="yes" /></td>
							<td><mifos:mifosalphanumtext property="shortName" size="4"
								maxlength="4" value="${BusinessKey.shortName}"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="Office.labelOfficeType"
								mandatory="yes" /></td>
							<td><mifos:select name="offActionForm" property="officeLevel"
								size="1" onchange="papulateParent()">
								<html-el:options collection="OfficeLevelList" property="levelId"
									labelProperty="levelName" />
							</mifos:select></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="Office.labelParentOffice" mandatory="yes" /></td>
							<td><c:if test="${not empty Parents}">
								<mifos:select name="officeActionForm"
									property="formParentOffice" size="1"
									value="${BusinessKey.parentOffice.officeId}">
									<html-el:options collection="Parents" property="officeId"
										labelProperty="displayName" />
								</mifos:select>
							</c:if></td>
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
									test='${BusinessKey.officeLevel==OfficeLevel.HEADOFFICE}'>
									<html-el:select name="offActionForm" disabled="true"
										property="officeStatus" size="1"
										value="${BusinessKey.status.id}">
										<html-el:options collection="OfficeStatusList"
											property="levelId" labelProperty="levelName" />
									</html-el:select>

								</c:when>
								<c:otherwise>
									<html-el:select name="officeActionForm" property="officeStatus"
										size="1" value="${BusinessKey.status.id}">
										<html-el:options collection="OfficeStatusList"
											property="levelId" labelProperty="levelName" />
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
								value="${BusinessKey.address.address.line1}"
								maxlength="200"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.ADDRESS2}"
								bundle="OfficeUIResources"/>:</td>
							<td><mifos:mifosalphanumtext property="address.line2"
								value="${BusinessKey.address.address.line2}"
								maxlength="200"></mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal" id="Office.Address3">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.ADDRESS3}"
								bundle="OfficeUIResources" keyhm="Office.Address3"
								isColonRequired="yes"></mifos:mifoslabel>:</td>
							<td><mifos:mifosalphanumtext property="address.line3"
								value="${BusinessKey.address.address.line3}"
								maxlength="200" keyhm="Office.Address3">
							</mifos:mifosalphanumtext></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.CITY}" bundle="OfficeUIResources"/>:</td>
							<td><mifos:mifosalphanumtext property="address.city"
								value="${BusinessKey.address.address.city}" maxlength="100"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.STATE}" bundle="OfficeUIResources"
								keyhm="Office.State" isColonRequired="yes"/></td>
							<td><mifos:mifosalphanumtext property="address.state"
								value="${BusinessKey.address.address.state}" maxlength="100"
								keyhm="Office.State"/></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="Office.labelCountry"
								 keyhm="Office.Country"/></td>
							<td><mifos:mifosalphanumtext property="address.country"
								value="${BusinessKey.address.address.country}" maxlength="100"
								keyhm="Office.Country"/></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="${ConfigurationConstants.POSTAL_CODE}"
								bundle="OfficeUIResources" keyhm="Office.PostalCode"
								isColonRequired="yes"/></td>
							<td><mifos:mifosalphanumtext property="address.postalCode"
								value="${BusinessKey.address.address.zip}"
								maxlength="20" keyhm="Office.PostalCode"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel name="Office.labelTelephone"
								/></td>

							<td><mifos:mifosalphanumtext property="address.telephoneNo"
								value="${BusinessKey.address.address.phoneNumber}"
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
							items="${customFields}" varStatus="loopStatus">
							<bean:define id="ctr">
								<c:out value="${loopStatus.index}" />
							</bean:define>
							<c:forEach var="cf"
								items="${BusinessKey.customFields}">
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
			<html-el:hidden property="method" value="editpreview" />
		</html-el:form>

	</tiles:put>

</tiles:insert>

