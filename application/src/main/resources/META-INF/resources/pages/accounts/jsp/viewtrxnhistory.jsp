<%--
Copyright (c) 2005-2011 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>

<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>


<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="ViewTransactionHistory"></span>
	<c:set value="${requestScope.GlNamesMode}" var="GlNamesMode"/>

<SCRIPT >
	function ViewDetails(){
		customerAccountActionForm.action="customerAccountAction.do?method=load";
		customerAccountActionForm.submit();
	}
	function ViewLoanDetails(form, typeOfGroupLoan){
		if (typeOfGroupLoan === 'parentAcc') {
			  form.action="viewLoanAccountDetails.ftl";
		}
		if (typeOfGroupLoan === 'memberAcc') {
			form.action="groupIndividualLoanAccountAction.do?";
		}
		form.submit();
	}

</SCRIPT>
	<form method="get" action="viewLoanAccountDetails.ftl" >
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'typeOfGroupLoan')}" var="typeOfGroupLoan" />
	<c:if test="${typeOfGroupLoan == 'memberAcc'}">
	   <html-el:hidden property="method" value="get" />
	</c:if>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
			  <span class="fontnormal8pt">
	          	<customtags:headerLink/>
	          <c:url value="customerAccountAction.do" var="customerAccountActionLoadMethodUrl" >
	          	<c:param name="method" value="load" />
	          </c:url >
	          	<c:choose>
	          	<c:when test="${param.input == 'LoanDetails'}">
	          	</c:when>
	          	<c:otherwise>
	          	<html-el:link styleId="viewtrxhistory.link.viewCharges" href="customerAccountAction.do?method=load">
	          	<c:if test="${param.input == 'ViewCenterCharges'}">
	          		<mifos:mifoslabel name="Center.CenterCharges" bundle="CenterUIResources"/>
	          	</c:if>
	          	<c:if test="${param.input == 'ViewGroupCharges'}">
	          		<mifos:mifoslabel name="Center.GroupCharges" bundle="CenterUIResources"/>
	          	</c:if>
	          	<c:if test="${param.input == 'ViewClientCharges'}">
	          		<mifos:mifoslabel name="Center.ClientCharges" bundle="CenterUIResources"/>
	          	</c:if>
	          	</html-el:link>
	          	</c:otherwise>
	          	</c:choose>
	          </span>
          </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15" >

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="83%" class="headingorange">
                	<span class="heading">
                	<c:choose>
			          	<c:when test="${param.input == 'LoanDetails'}">
	    	            	<c:out value="${param.prdOfferingName}"></c:out> -
			          	</c:when>
			          	<c:otherwise>
	    	            	<c:out value="${BusinessKey.customer.displayName}"></c:out> -
	   	            	</c:otherwise>
	   	            </c:choose>
                	</span>
                	<mifos:mifoslabel name="Savings.Transactionhistory"/>
	            </td>
              </tr>
            </table>
            <br>

            <mifoscustom:mifostabletag source="trxnhistoryList" scope="session_flow" xmlFileName="TrxnHistory.xml" moduleName="org/mifos/accounts/util/resources" passLocale="true" glMode="${GlNamesMode}"/>
            <br>

            <table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center">


					<c:choose>
					<c:when test="${param.input == 'LoanDetails'}">
					<html-el:button styleId="viewtrxnhistory.button.back" property="returnToAccountDetailsbutton"
					       onclick="javascript:ViewLoanDetails(this.form, '${typeOfGroupLoan}')"
						     styleClass="buttn">
								<mifos:mifoslabel name="accounts.returndetails" />
						</html-el:button>
					</c:when>
					<c:otherwise>
					   <html-el:button styleId="viewtrxnhistory.button.back" property="returnToAccountDetailsbutton"
					       onclick="javascript:ViewDetails()"
						     styleClass="buttn">
								<mifos:mifoslabel name="accounts.backtocharges" />
						</html-el:button>
					</c:otherwise>
					</c:choose>
					</td>
				</tr>
    		</table>
          </td>
        </tr>
      </table>
<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/>
</form>

<html-el:form action="customerAccountAction.do">
	<html-el:hidden property="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
</html-el:form>
</tiles:put>
</tiles:insert>
