<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script language="javascript">

			function goToCancelPage(form){
				form.action="editSavingsAction.do?method=cancel";
				form.submit();
 			 }
			function manageFlag(i) {
   				if(editSavingsStatusActionForm.flagId!=undefined){
					if(i==15){
						editSavingsStatusActionForm.flagId.disabled=false;
					}else{
						editSavingsStatusActionForm.flagId.selectedIndex=0;
						editSavingsStatusActionForm.flagId.disabled=true;
					}
				}
 			 }
 			
  
		</script>

		<html-el:form action="editSavingsAction.do?method=preview">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
	          				<customtags:headerLink/> 
	        			</span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading"> <c:out
								value="${sessionScope.editSavingsStatusActionForm.accountName}" />&nbsp;#&nbsp;<c:out
								value="${sessionScope.editSavingsStatusActionForm.globalAccountNum}" />&nbsp;-&nbsp;
							</span><mifos:mifoslabel name="savings.changestatus"
								bundle="SavingsUIResources" /></td>
						</tr>
						<tr>
							<td class="headingorange"><span class="fontnormalbold"> <mifos:mifoslabel
								name="savings.currentstatus" bundle="SavingsUIResources" />:<mifoscustom:MifosImage
								id="${sessionScope.editSavingsStatusActionForm.currentStatusId}"
								moduleName="accounts.savings" /></span> <span class="fontnormal"><c:out value="${sessionScope.BusinessKey.accountState.name}" />
								<%--mifoscustom:lookUpValue
										id="${sessionScope.editSavingsStatusActionForm.currentStatusId}"
										searchResultName="AccountStates" mapToSeperateMasterTable="true">
									</mifoscustom:lookUpValue--%> 
								</span></td>
						</tr>
						<tr><logic:messagesPresent>
							<td><br><font class="fontnormalRedBold"><html-el:errors bundle="SavingsUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
						<tr>
							<td class="fontnormal"><br>
							<mifos:mifoslabel name="savings.SelectStatus"
								bundle="SavingsUIResources"></mifos:mifoslabel> <mifos:mifoslabel
								name="savings.ClickContinue" bundle="SavingsUIResources"></mifos:mifoslabel>
							<mifos:mifoslabel name="savings.ClickCancel1"
								bundle="SavingsUIResources"></mifos:mifoslabel>
							<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"></mifos:mifoslabel>
							<mifos:mifoslabel name="savings.ClickCancel2"
								bundle="SavingsUIResources"></mifos:mifoslabel>
								
								</td>
						</tr>
						<tr>
							<td class="blueline"><img src="pages/framework/images/trans.gif"
								width="10" height="12"></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="left" valign="top" class="fontnormalbold"><mifos:mifoslabel
								name="savings.status" mandatory="yes" bundle="SavingsUIResources"></mifos:mifoslabel>
							</td>
							<td align="left" valign="top">
							<table width="95%" border="0" cellpadding="0" cellspacing="0">

								<c:forEach var="status" items="${sessionScope.statusList}"
									varStatus="loopStatus">
									<bean:define id="ctr">
										<c:out value="${loopStatus.index}" />
									</bean:define>
									<tr class="fontnormal">
										<td width="2%" align="center"><html-el:radio
											property="newStatusId" value="${status.id}"
											onclick="manageFlag(${status.id})" /></td>
										<td width="98%"><c:out value="${status.name}" /></td>
									</tr>
								</c:forEach>
								<c:forEach var="status" items="${sessionScope.statusList}">
									<c:if test="${status.id == AccountStates.SAVINGS_ACC_CANCEL}">
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><mifos:mifoslabel name="savings.SelectExplaination1"
												bundle="SavingsUIResources"></mifos:mifoslabel>
												<mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"></mifos:mifoslabel>												
												<mifos:mifoslabel name="savings.SelectExplaination2" bundle="SavingsUIResources"></mifos:mifoslabel>
												</td>
										</tr>
										<tr class="fontnormal">
											<td align="center">&nbsp;</td>
											<td><c:set var="flags" scope="request"
												value="${status.flagSet}" /> <mifos:select
												name="editSavingsStatusActionForm" property="flagId" size="1" disabled="true">
												<html-el:options collection="flags" property="id"
													labelProperty="flagDescription" />
											</mifos:select></td>
										</tr>
									</c:if>
								</c:forEach>
								<tr class="fontnormal">
									<td align="center">&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td width="7%" align="left" valign="top" class="fontnormalbold">

							<mifos:mifoslabel name="savings.note" mandatory="yes"
								bundle="SavingsUIResources"></mifos:mifoslabel></td>
							<td width="93%" align="left" valign="top"
								style="padding-left:4px;"><html-el:textarea
								property="accountNotes.comment" style="width:320px; height:110px;" /></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td height="25" align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:70px;">
								<mifos:mifoslabel name="Savings.Preview" bundle="SavingsUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button property="btn"
								styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage(this.form)">
								<mifos:mifoslabel name="savings.Cancel" bundle="SavingsUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
		</html-el:form>
		<script language="javascript">
				if(editSavingsStatusActionForm.newStatusId.length != undefined){
					for(j=0;j<editSavingsStatusActionForm.newStatusId.length;j++){ 
						if (editSavingsStatusActionForm.newStatusId[j].checked) {
							manageFlag(editSavingsStatusActionForm.newStatusId[j].value);
						}
					}
				}else{
					manageFlag(editSavingsStatusActionForm.newStatusId.value);
				}
			</script>
		

	</tiles:put>
</tiles:insert>
