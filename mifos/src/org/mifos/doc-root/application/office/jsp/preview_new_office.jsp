<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<tiles:insert definition=".create">
	<tiles:put name="body" type="string">
		<!-- Next is code for setting the hidden veriable to cancel -->
		<script language="javascript" type="text/javascript">
function goToCancelPage(){
	document.offActionForm.method.value="load";
	document.offActionForm.action="AdminAction.do";
	offActionForm.submit();
  }
  function goToPreviousPage()
  {
	document.offActionForm.method.value="previous";
	offActionForm.submit();
  
  }
</script>
		<html-el:form action="/offAction.do">
			<br>
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
											<td><img src="pages/framework/images/timeline/tick.gif"
												width="17" height="17" alt=""></td>
											<td class="timelineboldgray"><mifos:mifoslabel
												name="Office.labelOfficeInformation" /></td>
										</tr>
									</table>
									</td>
									<td width="73%" align="right">
									<table border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td><img src="pages/framework/images/timeline/bigarrow.gif"
												width="17" height="17" alt=""></td>
											<td class="timelineboldorange"><mifos:mifoslabel
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
						cellspacing="0" valign="top" class="bluetableborder">
						<tr>
							<td align="left" valign="top" class="paddingleftCreates">
							<table width="93%" border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td class="headingorange"><span class="heading"><mifos:mifoslabel
										name="Office.labelAddNewOffice" /></span><mifos:mifoslabel
										name="Office.labelReviewAndSubmit" /></td>
								</tr>
							</table>
							<br>

							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr >
									
								<logic:messagesPresent>
								<td>
								<font class="fontnormalRedBold"><html-el:errors
									bundle="OfficeUIResources" /></font>
									<br></td>
								</logic:messagesPresent>
								</tr>
								<tr>
									<td width="100%" height="23" class="fontnormalboldorange"><mifos:mifoslabel
										name="Office.labelOfficeInformation" /></td>
								</tr>
								<tr>

									<!-- actual information starts from here -->
									<td height="23" class="fontnormalbold"><mifos:mifoslabel
										name="Office.labelOfficeName" /> <span class="fontnormal"> <c:out
										value="${offActionForm.officeName}"></c:out> <br>
									</span> <mifos:mifoslabel name="Office.labelOfficeShortName" /><span
										class="fontnormal"> <c:out value="${offActionForm.shortName}"></c:out><br>
									</span><mifos:mifoslabel name="Office.labelOfficeType" />
									<span class="fontnormal"> <!--  code for setting the correct value for type-->

									<c:forEach var="level" items="${OfficeLevelList}">
										<c:if test="${level.levelId == offActionForm.officeLevel }">
											<c:out value="${level.levelName}"></c:out>
										</c:if>
									</c:forEach> </span> <br>
									<span class="fontnormal"> </span><mifos:mifoslabel
										name="Office.labelParentOffice" /><span class="fontnormal"> <!-- logic for showing the correct parent -->
									<c:forEach var="parent" items="${Parents}">
										<c:if
											test="${parent.officeId == offActionForm.parentOfficeId }">
											<c:out value="${parent.displayName}"></c:out>
										</c:if>
									</c:forEach></span><br>
									<br>
									<mifos:mifoslabel name="Office.labelAddress" /><span
								class="fontnormal"><br> <c:out
								value="${offActionForm.address.displayAddress}"></c:out> </span>
								</td>
								</tr>
								<tr>
									<td class="fontnormalbold">
									<mifos:mifoslabel name="${ConfigurationConstants.CITY}"
										bundle="OfficeResources"></mifos:mifoslabel>:<span
										class="fontnormal"> <c:out
										value="${offActionForm.address.city}"></c:out>
									</span>
								</tr>
								<tr id="Office.State">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.STATE}"
										bundle="OfficeResources" keyhm="Office.State"
										isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
										class="fontnormal"> <c:out
										value="${offActionForm.address.state}"></c:out>
									</span>
								</tr>
								<tr id="Office.Country">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="Office.labelCountry" keyhm="Office.Country"
										isManadatoryIndicationNotRequired="yes" /><span
										class="fontnormal"> <c:out
										value="${offActionForm.address.country}"></c:out><br>
									</span>
								</tr>
								<tr id="Office.PostalCode">
									<td class="fontnormalbold"><mifos:mifoslabel
										name="${ConfigurationConstants.POSTAL_CODE}"
										bundle="OfficeUIResources" keyhm="Office.PostalCode"
										isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
										class="fontnormal"> <c:out
										value="${offActionForm.address.zip}"></c:out> </span>
								</tr>
								<tr>
									<td class="fontnormalbold"><br>
									<mifos:mifoslabel name="Office.labelTelephone" /><span
										class="fontnormal"> <c:out
										value="${offActionForm.address.phoneNumber}"></c:out></span><br>
									<br>
									<c:if test="${!empty offActionForm.customFields}">
										<mifos:mifoslabel name="Office.labelAdditionInformation" />
									</c:if> <span class="fontnormal"><br>
									 <c:forEach var="cfdef"
										items="${offActionForm.customFields}">
										<c:forEach var="cf" items="${sessionScope.customFields}">
											<c:if test="${cfdef.fieldId==cf.fieldId}">
											
												<font class="fontnormalBold"> 
												
												<mifos:mifoslabel
											name="${cf.lookUpEntity.entityType}"
											bundle="OfficeResources"></mifos:mifoslabel>
												:
												</font>
												<span class="fontnormal"><c:out value="${cfdef.fieldValue}" /><br>
												</span>
											</c:if>
										</c:forEach>
									</c:forEach> <span class="fontnormal"></span> <br>
									<br>
									<span class="fontnormal"></span> <html-el:button
										onclick="goToPreviousPage();" property="cancelButton"
										styleClass="insidebuttn" style="width:150px">
										<mifos:mifoslabel name="Office.edit" />
									</html-el:button></span></td>
								</tr>
							</table>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center" class="blueline">&nbsp;</td>
								</tr>
							</table>


							<br>
							<table width="93%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td align="center">&nbsp; <html-el:submit styleClass="buttn"
										style="width:70px;"></html-el:submit> &nbsp; <html-el:button
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn" style="width:70px">
										<mifos:mifoslabel name="Office.cancel" />
									</html-el:button></td>
								</tr>

							</table>

							</td>


						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>

					</table>

					</td>
				</tr>
			</table>
			<!-- hidden veriable which will be set to method -->
			<html-el:hidden property="method" value="create" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="input" value="create" />
		</html-el:form>
	</tiles:put>

</tiles:insert>
