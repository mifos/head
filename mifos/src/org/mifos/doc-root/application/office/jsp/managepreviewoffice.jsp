<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<!-- Next is code for setting the hidden veriable to cancel -->
		<script language="javascript" type="text/javascript">
function goToCancelPage(){
	document.officeActionForm.method.value="get";
	officeActionForm.submit();
  }
  function goToPreviousPage()
  {
	document.officeActionForm.method.value="previous";
	officeActionForm.submit();
  
  }
     function submitViewOfficesLink(){
	document.officeActionForm.method.value="loadall";
		
		
	officeActionForm.submit();
  }
function getOffice(officeid){
	document.officeActionForm.method.value="get";
	document.officeActionForm.officeId.value=officeid;	
	officeActionForm.submit();
  }  
function  submitAdminLink()
{
		document.officeActionForm.method.value="load";
		document.officeActionForm.action="AdminAction.do";
		officeActionForm.submit();
}
</script>
		<html-el:form action="/OfficeAction.do" method="POST">

			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="javascript:submitAdminLink()">
						<mifos:mifoslabel name="office.labelLinkAdmin"
							bundle="OfficeResources"></mifos:mifoslabel>
					</html-el:link> / <html-el:link
						href="javascript:submitViewOfficesLink()">
						<mifos:mifoslabel name="office.labelLinkViewOffices"
							bundle="OfficeResources"></mifos:mifoslabel>

					</html-el:link> / <html-el:link
						href="javascript:getOffice(${requestScope.OfficeVo.officeId})">
						<c:out value="${requestScope.OfficeVo.officeName}"></c:out>
					</html-el:link> </span></td>
				</tr>
			</table>
			<table table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">

					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td class="headingorange"><span class="heading"> <c:out
								value="${requestScope.OfficeVo.officeName}"></c:out> </span> - <mifos:mifoslabel
								name="office.labelPreviewOfficeInformation"
								bundle="OfficeResources"></mifos:mifoslabel></td>
						</tr>
					</table>
					<table width="93%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<font class="fontnormalRedBold"><html-el:errors
								bundle="OfficeResources" /> </font>
						</tr>
						<tr>
							<!-- actual information starts from here -->
							<td height="23" class="fontnormalbold"><mifos:mifoslabel
								name="office.labelOfficeName" bundle="OfficeResources">
							</mifos:mifoslabel> <span class="fontnormal"> <c:out
								value="${requestScope.OfficeVo.officeName}"></c:out> <br>
							</span> <mifos:mifoslabel name="office.labelOfficeShortName"
								bundle="OfficeResources"></mifos:mifoslabel><span
								class="fontnormal"> <c:out
								value="${requestScope.OfficeVo.shortName}"></c:out><br>
							</span> <%-- office code not required any more 
									<mifos:mifoslabel name="office.labelOfficeCode"
										bundle="OfficeResources"></mifos:mifoslabel><span
										class="fontnormal"> <!--  code for setting the correct value for code-->

									<c:forEach var="code" items="${OfficeCodeList}">
										<c:if test="${code.codeId == OfficeVo.officeCode.codeId }">
											<c:out value="${code.lookUpName}"></c:out>
										</c:if>
									</c:forEach> <!--  END code for setting the correct value for code-->
									</span>
									--%> <mifos:mifoslabel name="office.labelOfficeType"
								bundle="OfficeResources"></mifos:mifoslabel> <span
								class="fontnormal"> <!--  code for setting the correct value for type-->

							<c:forEach var="level" items="${OfficeLevelList}">
								<c:if test="${level.levelId == OfficeVo.level.levelId }">
									<c:out value="${level.levelName}"></c:out>
								</c:if>
							</c:forEach> <!--  END code for setting the correct value for type-->

							</span> <br>




							<span class="fontnormal"> </span><mifos:mifoslabel
								name="office.labelParentOffice" bundle="OfficeResources"></mifos:mifoslabel><span
								class="fontnormal"> <!-- logic for showing the correct parent -->
							<c:forEach var="parent" items="${Parents}">
								<c:if
									test="${parent.officeId == OfficeVo.parentOffice.officeId }">
									<c:out value="${parent.displayName}"></c:out>
								</c:if>
							</c:forEach> </span><br>
							<!-- End for showing the correct parent --> <!-- office status  -->

							<span class="fontnormalbold"> </span><mifos:mifoslabel
								name="office.labelStatus" bundle="OfficeResources"></mifos:mifoslabel>:<span
								class="fontnormal"> <c:forEach var="status"
								items="${requestScope.OfficeStatusList}">
								<c:if
									test="${status.statusId ==requestScope.OfficeVo.status.statusId}">
									<c:out value="${status.statusName}"></c:out>
								</c:if>
							</c:forEach> <!-- end office status  --> </span><br>
							<span class="fontnormal"></span><span class="fontnormal"> </span><span
								class="fontnormal"></span><br>
							<mifos:mifoslabel name="office.labelAddress"
								bundle="OfficeResources"></mifos:mifoslabel><span
								class="fontnormal"><br>
							</span><span
										class="fontnormal"><c:out
										value="${requestScope.OfficeVo.address.address1}"></c:out><c:if
										test="${not empty requestScope.OfficeVo.address.address1 &&(not empty requestScope.OfficeVo.address.address2||not empty requestScope.OfficeVo.address.address3)}">, </c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address2}">${requestScope.OfficeVo.address.address2}</c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address3&&not empty requestScope.OfficeVo.address.address2}">, </c:if><c:if
										test="${not empty requestScope.OfficeVo.address.address3}">${requestScope.OfficeVo.address.address3}</c:if><br>
									</span>
							</tr>
								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.CITY}"
								bundle="OfficeResources"></mifos:mifoslabel>:<span
								class="fontnormal"> <c:out
								value="${requestScope.OfficeVo.address.city}"></c:out><br>
							</span>
						</tr>
								<tr id="Office.State">
									<td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.STATE}"
								bundle="OfficeResources" keyhm="Office.State" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
								class="fontnormal"> <c:out
								value="${requestScope.OfficeVo.address.state}"></c:out><br>
							</span>
						</tr>
								<tr id="Office.Country">
									<td class="fontnormalbold"><mifos:mifoslabel name="office.labelCountry"
								bundle="OfficeResources" keyhm="Office.Country" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
								class="fontnormal"> <c:out
								value="${requestScope.OfficeVo.address.country}"></c:out><br>
							</span>
						</tr>
								<tr id="Office.PostalCode">
									<td class="fontnormalbold"><mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}"
								bundle="OfficeResources" keyhm="Office.PostalCode" isColonRequired="yes" isManadatoryIndicationNotRequired="yes"></mifos:mifoslabel><span
								class="fontnormal"> <c:out
								value="${requestScope.OfficeVo.address.postalCode}"></c:out><br>
							<br>
							</span>
						</tr>
								<tr>
									<td class="fontnormalbold"><mifos:mifoslabel name="office.labelTelephone"
								bundle="OfficeResources"></mifos:mifoslabel><span
								class="fontnormal"> <c:out
								value="${requestScope.OfficeVo.address.telephoneNo}"></c:out></span><br>
								       <c:if test="${!empty requestScope.customFields}">
							<br>
							<mifos:mifoslabel name="office.labelAdditionInformation"
								bundle="OfficeResources"></mifos:mifoslabel>
								</c:if>
								<span class="fontnormal"><br>
							<!-- custom field preview --> <c:forEach var="cfdef"
								items="${requestScope.customFields}">
								<c:forEach var="cf"
									items="${requestScope.OfficeVo.customFieldSet}">
									<c:if test="${cfdef.fieldId==cf.fieldId}">
										<font class="fontnormalBold">
									<mifos:mifoslabel name="${cfdef.lookUpEntity.entityType}" bundle="OfficeResources"></mifos:mifoslabel>:
									<!-- 	<mifos:mifoslabel
											name="cfdef.entityId" bundle="OfficeResources"></mifos:mifoslabel>-->
										</font>
										<span class="fontnormal"><c:out value="${cf.fieldValue}" /><br>
										</span>
									</c:if>
								</c:forEach>
							</c:forEach> <span class="fontnormal"></span> <br>
							<br>
							<span class="fontnormal"></span> <html-el:button
								onclick="goToPreviousPage();" property="cancelButton"
								 styleClass="insidebuttn"
								style="width:150px">
								<mifos:mifoslabel name="office.button.edit"
									bundle="OfficeResources"></mifos:mifoslabel>
							</html-el:button></span></td>
						</tr>
					</table>


					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>

					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">&nbsp; <!-- Next are submit and cancel button -->

							<html-el:submit styleClass="buttn" style="width:70px;"></html-el:submit>
							&nbsp; <html-el:button onclick="goToCancelPage();"
								property="cancelButton" value="Cancel" styleClass="cancelbuttn"
								style="width:70px">
								<mifos:mifoslabel name="office.button.cancel"
									bundle="OfficeResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>

					<br>

					<br>
					</td>
				</tr>
			</table>



			<!-- hidden veriable which will be set to method -->
			<html-el:hidden property="method" value="update" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="input" value="manage" />
			<html-el:hidden property="formOfficeStatus" />
			<html-el:hidden property="officeId"
				value="${requestScope.OfficeVo.officeId}" />
			<html-el:hidden property="address.officeAdressId"
				value="${requestScope.OfficeVo.address.officeAdressId}" />
			<html-el:hidden property="versionNo"
				value="${requestScope.OfficeVo.versionNo}" />
			<!-- 
			<html-el:hidden property="parentVersion"  /> 
			-->
		</html-el:form>
	</tiles:put>
</tiles:insert>
