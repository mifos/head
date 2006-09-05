<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
		<script language="javascript">
  function goToCancelPage(){
	custHistoricalDataActionForm.action="custHistoricalDataAction.do?method=getHistoricalData";
	custHistoricalDataActionForm.submit();
  }
  function goToEditPage(){
	custHistoricalDataActionForm.action="custHistoricalDataAction.do?method=previousHistoricalData";
	custHistoricalDataActionForm.submit();
  }
  
 </script>

		<html-el:form action="custHistoricalDataAction.do?method=updateHistoricalData"
			onsubmit="func_disableSubmitBtn('submitBtn')">
			<c:set
				value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
				var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink /></span>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td><font class="fontnormalRedBold"><html-el:errors
						bundle="CustomerUIResources" /></font></td>
				</tr>
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="41%" class="headingorange"><span class="heading"> <c:out
								value="${BusinessKey.displayName}" /> - </span> <mifos:mifoslabel
								name="label.previewhistoricaldata" bundle="CustomerUIResources"></mifos:mifoslabel></td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="left" valign="top" class="fontnormalbold"><span
								class="fontnormal"></span> <mifos:mifoslabel
								name="label.MFIjoiningdate" bundle="CustomerUIResources"></mifos:mifoslabel>
							<span class="fontnormal"> <c:out
								value="${sessionScope.custHistoricalDataActionForm.mfiJoiningDate}" />
							<br>
							</span> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
							<mifos:mifoslabel name="label.loancyclenumber"
								bundle="CustomerUIResources"></mifos:mifoslabel> <span
								class="fontnormal"> <c:out
								value="${sessionScope.custHistoricalDataActionForm.loanCycleNumber}" /><br>
							</span> <mifos:mifoslabel name="label.productname"
								bundle="CustomerUIResources"></mifos:mifoslabel> <span
								class="fontnormal"> <c:out
								value="${sessionScope.custHistoricalDataActionForm.productName}" /><br>
							</span> <mifos:mifoslabel name="label.amountof"
								bundle="CustomerUIResources" /><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="label.colon" bundle="CustomerUIResources" /> <span
								class="fontnormal"> <c:out
								value="${sessionScope.custHistoricalDataActionForm.loanAmount}" /><br>
							</span> <mifos:mifoslabel name="label.totalamountpaid"
								bundle="CustomerUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
								value="${sessionScope.custHistoricalDataActionForm.totalAmountPaid}" /><br>
							</span> <mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
								name="label.interestpaid" bundle="CustomerUIResources"></mifos:mifoslabel>
							<span class="fontnormal"> <c:out
								value="${sessionScope.custHistoricalDataActionForm.interestPaid}" /><br>
							</span> <mifos:mifoslabel name="label.numberofmissedpayments"
								bundle="CustomerUIResources"></mifos:mifoslabel> <span
								class="fontnormal"> <c:out
								value="${sessionScope.custHistoricalDataActionForm.missedPaymentsCount}" /><br>
							</span> <mifos:mifoslabel name="label.totalnumberofpayments"
								bundle="CustomerUIResources"></mifos:mifoslabel> <span
								class="fontnormal"><c:out
								value="${sessionScope.custHistoricalDataActionForm.totalPaymentsCount}" /><br>
							<br>
							</span> <mifos:mifoslabel name="label.notes"
								bundle="CustomerUIResources"></mifos:mifoslabel>: <span
								class="fontnormal"><c:out
								value="${sessionScope.custHistoricalDataActionForm.commentNotes}" /><br>
							</span></td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td style="padding-top:5px;"><html-el:button
								onclick="goToEditPage()" property="btn" styleClass="insidebuttn"
								style="width:125px;">
								<mifos:mifoslabel name="label.edithistoricaldata"
									bundle="CustomerUIResources"></mifos:mifoslabel>
							</html-el:button></td>
						</tr>
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit property="submitBtn"
								styleClass="buttn" style="width:70px;">
								<mifos:mifoslabel name="button.submit"
									bundle="CustomerUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button
								property="cancelBtn" styleClass="cancelbuttn" style="width:70px"
								onclick="goToCancelPage()">
								<mifos:mifoslabel name="button.cancel"
									bundle="CustomerUIResources"></mifos:mifoslabel>
							</html-el:button></td>

						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="globalCustNum" value="${BusinessKey.globalCustNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
