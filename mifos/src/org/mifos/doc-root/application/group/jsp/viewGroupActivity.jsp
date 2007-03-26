<!--

/**

 * viewGroupActivity.jsp    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

-->

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom" %>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">

	<script>
		function fun_cancel(){
			customerAccountActionForm.action="customerAccountAction.do?method=load";
			customerAccountActionForm.submit();
		}
</script>
	  <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"
			   var="BusinessKey" />
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink/>
				<html-el:link href="customerAccountAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
	          	   <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
			<mifos:mifoslabel name="Group.charges" bundle="GroupUIResources"/>
	          	</html-el:link></span>

			</tr>
		</table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <font class="fontnormalRedBold"><html-el:errors bundle="CenterUIResources" /></font>
              <tr>
	            <td width="70%" class="headingorange">
	            	<span class="heading">
	            	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
					<c:out value="${param.prdOfferingName}" /> -
	                 </span>
	                 <mifos:mifoslabel name="Customer.Accountstatementasof" bundle="CustomerUIResources"/>
				     <c:out value="${loanfn:getCurrrentDate(sessionScope.UserContext.preferredLocale)}" />
                </td>
                </tr>
              <tr>
                <td align="right" class="headingorange"><img src="images/trans.gif" width="10" height="5"></td>
              </tr>
            </table>
            <c:if test="${param.statusId == 7 || param.statusId == 8 || param.statusId == 9 || param.statusId == 10}">
	           <table width="96%" border="0" cellpadding="0" cellspacing="0">
	              <tr>
	                <td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
	                	<span class="fontnormalbold">
	                	<mifos:mifoslabel name="Center.ApplyTransaction" bundle="CenterUIResources"/></span>
	                &nbsp;&nbsp;&nbsp;&nbsp;
	                	<html-el:link href="applyPaymentAction.do?method=load&globalCustNum=${param.globalCustNum}&prdOfferingName=${param.prdOfferingName}&input=ViewGroupCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
	                    	<mifos:mifoslabel name="accounts.apply_payment" />
	                    </html-el:link>
	                <c:if test="${param.statusId == 9 || param.statusId == 10}">
	                	&nbsp;&nbsp;&nbsp;&nbsp;
	                    <html-el:link href="custApplyAdjustment.do?method=loadAdjustment&globalCustNum=${param.globalCustNum}&prdOfferingName=${param.prdOfferingName}&input=ViewGroupCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
	                    	<mifos:mifoslabel name="Center.ApplyAdjustment" bundle="CenterUIResources"/>
	                    </html-el:link>
	                 </c:if>
		            <c:if test="${param.statusId == 7 || param.statusId == 8 || param.statusId == 9 || param.statusId == 10}">
	                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	                    <html-el:link href="applyChargeAction.do?method=load&globalCustNum=${param.globalCustNum}&prdOfferingName=${param.prdOfferingName}&input=ViewGroupCharges&globalAccountNum=${param.globalAccountNum}&accountType=${param.accountType}&accountId=${param.accountId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
	                    	<mifos:mifoslabel name="Center.ApplyCharges" />
						</html-el:link>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</c:if>
						</td>
	              </tr>
	            </table>
	            <br>
	            </c:if>
 			<mifoscustom:mifostabletag moduleName="customer" scope="session" source="customerAccountActivityList" xmlFileName="CustomerRecentActivity.xml" passLocale="true"/>
            <br>
             <table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center">
					   <html-el:button property="returnToAccountDetailsbutton"
					       onclick="fun_cancel();"
						     styleClass="buttn" style="width:165px;">
						<mifos:mifoslabel name="label.backtodetailspage" bundle="CustomerUIResources"/>
						</html-el:button>
					</td>
				</tr>
    		</table>
        </tr>
      </table>
		<html:form action="customerAccountAction.do">
       		<html-el:hidden property="globalCustNum" value="${BusinessKey.customer.globalCustNum}" />
     	</html:form>
</tiles:put>
</tiles:insert>
