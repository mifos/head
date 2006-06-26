<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/mifos/customtags" prefix="customtable"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<script>
			function fun_return(form)
					{
						closedaccsearchactionform.submit();
					}
			function ViewDetails()
					{
						closedaccsearchactionform.submit();
					}		
	    </script>

		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05">
					<span class="fontnormal8pt"> 
						<a href="CustomerSearchAction.do?method=preview&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>">
							<c:out value="${sessionScope.linkValues.customerOfficeName}" />
						</a> /
						<c:if test="${!empty sessionScope.linkValues.customerCenterName}">
						<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerCenterGCNum}"/>">
							<c:out value="${sessionScope.linkValues.customerCenterName}" />
						</a> / 
						</c:if> 
						<c:if test="${!empty sessionScope.linkValues.customerParentName}">
						<a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
							<c:out value="${sessionScope.linkValues.customerParentName}" />
						</a> /
						</c:if>
						<a href="clientCreationAction.do?method=get&customerId=<c:out value="${sessionScope.linkValues.customerId}"/>">
							<c:out value="${sessionScope.linkValues.customerName}" /> </a>/ 
						<c:choose>
								<c:when test="${param.headingInput=='ViewClientCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
										<mifos:mifoslabel name="loan.charges" />
									</html-el:link> 
								</c:when>
								<c:when test="${param.headingInput=='ViewGroupCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
										<mifos:mifoslabel name="loan.charges" />
									</html-el:link> 
								</c:when>
								<c:when test="${param.headingInput=='ViewCenterCharges'}">
									<html-el:link href="javascript:ViewDetails()">
									    <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" />
										<mifos:mifoslabel name="loan.charges" />
									</html-el:link> 
								</c:when>
								<c:otherwise>
									<html-el:link href="loanAction.do?method=get&globalAccountNum=${param.globalAccountNum}">
										<c:out value="${param.prdOfferingName}" />
									</html-el:link> 
								</c:otherwise>
							</c:choose> 
					</span>
				</td>
			</tr>
		</table>

		<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>

					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"><span class="heading"> <c:out
								value="${param.prdOfferingName}" />&nbsp;- </span><mifos:mifoslabel name="loan.acc_statement" />&nbsp; <c:out
								value="${loanfn:getCurrrentDate(sessionScope.UserContext.pereferedLocale)}" /><br>
							<br>

							</td>
						</tr>
					</table>
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5"
								style="padding-left:10px; padding-bottom:3px;"><span
								class="fontnormalbold"><mifos:mifoslabel name="loan.apply_trans" /></span>&nbsp;&nbsp;&nbsp;&nbsp;
							<html-el:link
								href="accountTrxn.do?method=load&prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountType}&input=${param.headingInput}">
								<mifos:mifoslabel name="loan.apply_payment" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html-el:link
								href="#">
								<mifos:mifoslabel name="loan.apply_adjustment" />
							</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html-el:link
								href="AccountsApplyChargesAction.do?method=load&accountId=${param.accountId}&input=${param.headingInput}&prdOfferingName=${param.prdOfferingName}&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}">
								<mifos:mifoslabel name="loan.apply_charges" />
							</html-el:link></td>
						</tr>
					</table>
					<br>
					<mifoscustom:mifostabletag moduleName="accounts" scope="request"
						source="AllRecentAcctActivityList"
						xmlFileName="RecentActivity.xml" passLocale="true"/>
					<br>
					<table width="750" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="returnToAccountDetailsbutton"
								onclick="javascript:fun_return(this.form)" styleClass="buttn"
								style="width:165px;">
								<mifos:mifoslabel name="accounts.returnToAccountDetails" />
							</html-el:button></td>
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:form action="/closedaccsearchaction.do?method=search">
			<html-el:hidden property="searchNode(search_name)" value="ClientChargesDetails" />
			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}" />
			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}" />
			<html-el:hidden property="accountId" value="${param.accountId}" />
			<html-el:hidden property="accountType" value="${param.accountType}" />
			<html-el:hidden property="input" value="${param.headingInput}" />
			<html-el:hidden property="headingInput" value="${param.headingInput}"/>
 			</html-el:form>
	</tiles:put>
</tiles:insert>
