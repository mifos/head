<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<input type="hidden" id="page.id" value="CustEditHistoricalData"/>
		<script language="javascript" SRC="pages/framework/js/date.js"></script>
		<script src="pages/framework/js/conversion.js"></script>
		<script src="pages/framework/js/con_en.js"></script>
		<script src="pages/framework/js/con_${sessionScope["UserContext"].currentLocale}.js"></script>
		<script language="javascript">
  function goToCancelPage(){
	custHistoricalDataActionForm.action="custHistoricalDataAction.do?method=getHistoricalData";
	custHistoricalDataActionForm.submit();
  }
 </script>
		<html-el:form action="custHistoricalDataAction.do?method=previewHistoricalData"
			onsubmit="return (validateMyForm(mfiJoiningDate,mfiJoiningDateFormat,mfiJoiningDateYY))">
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
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="41%" class="headingorange"><span class="heading"> <c:out
								value="${BusinessKey.displayName}" /> - </span> <mifos:mifoslabel
								name="label.edithistoricaldata" bundle="CustomerUIResources"></mifos:mifoslabel></td>
						</tr>
						<tr>
							<td><font class="fontnormalRedBold"><span id="cust_edithistoricaldata.error.message"><html-el:errors
								bundle="CustomerUIResources" /></span></font></td>
						</tr>

					</table>
					<br>
					<table width="95%" border="0" cellpadding="3" cellspacing="0">
						<tr class="fontnormal">
							<td width="27%" align="right"><mifos:mifoslabel
								name="label.MFIjoiningdate" bundle="CustomerUIResources"></mifos:mifoslabel></td>
							<td width="73%"><date:datetag property="mfiJoiningDate" /></td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="cust_edithistoricaldata.label.loanCycleNumber"><mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /><mifos:mifoslabel
								name="label.loancyclenumber" bundle="CustomerUIResources"></mifos:mifoslabel></span></td>
							<td><mifos:mifosnumbertext styleId="cust_edithistoricaldata.input.loanCycleNumber"
								name="customerHistoricalDataActionForm"
								property="loanCycleNumber"
								value="${sessionScope.custHistoricalDataActionForm.loanCycleNumber}" maxlength="4"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="cust_edithistoricaldata.label.productName"><mifos:mifoslabel name="label.productname"
								bundle="CustomerUIResources"></mifos:mifoslabel></span></td>
							<td><mifos:mifosalphanumtext styleId="cust_edithistoricaldata.input.productName"
								name="customerHistoricalDataActionForm" property="productName"
								value="${sessionScope.custHistoricalDataActionForm.productName}" maxlength="100"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="cust_edithistoricaldata.label.loanAmount"><mifos:mifoslabel name="label.amountof"
								bundle="CustomerUIResources" /> <mifos:mifoslabel
								name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
								name="label.colon" bundle="CustomerUIResources" /></span></td>
							<td><mifos:mifosdecimalinput styleId="cust_edithistoricaldata.input.loanAmount"
								name="customerHistoricalDataActionForm" property="loanAmount"
								value="${sessionScope.custHistoricalDataActionForm.loanAmount}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="cust_edithistoricaldata.label.totalAmountPaid"><mifos:mifoslabel name="label.totalamountpaid"
								bundle="CustomerUIResources"></mifos:mifoslabel></span></td>
							<td><mifos:mifosdecimalinput styleId="cust_edithistoricaldata.input.totalAmountPaid"
								name="customerHistoricalDataActionForm"
								property="totalAmountPaid"
								value="${sessionScope.custHistoricalDataActionForm.totalAmountPaid}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="cust_edithistoricaldata.label.interestPaid"><mifos:mifoslabel
								name="${ConfigurationConstants.INTEREST}" /> <mifos:mifoslabel
								name="label.interestpaid" bundle="CustomerUIResources"></mifos:mifoslabel></span></td>
							<td><mifos:mifosdecimalinput styleId="cust_edithistoricaldata.input.interestPaid"
								name="customerHistoricalDataActionForm" property="interestPaid"
								value="${sessionScope.custHistoricalDataActionForm.interestPaid}" />
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="cust_edithistoricaldata.label.numberOfMissedPayments"><mifos:mifoslabel
								name="label.numberofmissedpayments" bundle="CustomerUIResources"></mifos:mifoslabel></span></td>
							<td><mifos:mifosnumbertext styleId="cust_edithistoricaldata.input.numberOfMissedPayments"
								name="customerHistoricalDataActionForm"
								property="missedPaymentsCount"
								value="${sessionScope.custHistoricalDataActionForm.missedPaymentsCount}" maxlength="4"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><span id="cust_edithistoricaldata.label.totalNumberOfPayments"><mifos:mifoslabel
								name="label.totalnumberofpayments" bundle="CustomerUIResources"></mifos:mifoslabel></span></td>
							<td><mifos:mifosnumbertext styleId="cust_edithistoricaldata.input.totalNumberOfPayments"
								name="customerHistoricalDataActionForm"
								property="totalPaymentsCount"
								value="${sessionScope.custHistoricalDataActionForm.totalPaymentsCount}" maxlength="4"/>
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top"><span id="cust_edithistoricaldata.label.notes"><mifos:mifoslabel
								name="label.notes" bundle="CustomerUIResources"></mifos:mifoslabel></span>:</td>
							<td><span class="fontnormal"> <html-el:textarea styleId="cust_edithistoricaldata.input.notes"
								property="commentNotes" cols="37"
								style="width:320px; height:110px;"
								value="${sessionScope.custHistoricalDataActionForm.commentNotes}" /> </span>
							</td>
						</tr>
					</table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:submit styleId="cust_edithistoricaldata.button.preview" styleClass="buttn">
								<mifos:mifoslabel name="button.preview"
									bundle="CustomerUIResources"></mifos:mifoslabel>
							</html-el:submit> &nbsp;&nbsp; <html-el:button styleId="cust_edithistoricaldata.button.cancel"
								property="cancelBtn" styleClass="cancelbuttn"
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
