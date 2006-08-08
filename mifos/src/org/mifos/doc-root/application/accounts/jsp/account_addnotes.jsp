<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

		<script language="javascript">

function goToCancelPage(){
	notesActionForm.action="notesAction.do?method=cancel";
	notesActionForm.submit();
  }
</script>
		<html-el:form
			action="notesAction.do?method=preview&globalAccountNum=${sessionScope.notesActionForm.globalAccountNum}">

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
							<td width="83%" class="headingorange"><span class="heading"><c:out
								value="${sessionScope.notesActionForm.prdOfferingName}" />&nbsp;#<c:out
								value="${sessionScope.notesActionForm.globalAccountNum}" /> - </span><mifos:mifoslabel
								name="Account.AddNote" bundle="accountsUIResources" /></td>
						</tr>
						<tr>
							<logic:messagesPresent>
								<td><br><font class="fontnormalRedBold"><html-el:errors
									bundle="accountsUIResources" /></font></td>
							</logic:messagesPresent>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><br>
							<span class="fontnormal"> 
								<mifos:mifoslabel name="Account.EnterANote" bundle="accountsUIResources"></mifos:mifoslabel>
								<mifos:mifoslabel name="Account.ClickPreview" bundle="accountsUIResources"></mifos:mifoslabel>
								<mifos:mifoslabel name="Savings.clickCancelToReturn" bundle="SavingsUIResources"/>
					            <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
					            <mifos:mifoslabel name="Savings.accountDetailsPage" bundle="SavingsUIResources"/> 
					         </span></td>
						</tr>
						<tr>
							<td class="blueline"><img src="pages/framework/images/trans.gif"
								width="10" height="12"></td>
						</tr>

					</table>
					<br>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">

						<tr>
							<td width="7%" align="left" valign="top" class="fontnormalbold">
							<mifos:mifoslabel name="Account.Note"
								bundle="accountsUIResources" mandatory="yes"></mifos:mifoslabel></td>
							<td width="93%" align="left" valign="top"><html-el:textarea
								property="comment" cols="37" style="width:320px; height:110px;">
							</html-el:textarea></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleClass="buttn"
								style="width:70px;">
								<mifos:mifoslabel name="accounts.preview"
									bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button
								property="cancelBtn" styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="accounts.cancel"
									bundle="accountsUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<br>
			<mifos:SecurityParam property="${param.securityParamInput}" />
			<html-el:hidden property="securityParamInput" value="${sessionScope.notesActionForm.securityParamInput}" />
			<html-el:hidden property="globalAccountNum" value="${sessionScope.notesActionForm.globalAccountNum}"/>
			<html-el:hidden property="accountTypeId" value="${sessionScope.notesActionForm.accountTypeId}"/>
		</html-el:form>
	</tiles:put>
</tiles:insert>

