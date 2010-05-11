<%--
Copyright (c) 2005-2009 Grameen Foundation USA
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

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
<%@ taglib uri="/sessionaccess" prefix="session"%>
	<tiles:put name="body" type="string">
	<span id="page.id" title="Review_holidayCreation" />
	

		<script>
			function fun_return(form)
					{
						form.action="holidayAction.do";
						form.method.value="get";
						form.submit();
					}
					
			function fun_edit(form)
					{
						
						form.action="holidayAction.do?method=previous";
						form.submit();
					}
	</script>
		<html-el:form action="holidayAction.do?method=update">
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			  <tr>
          		<td class="bluetablehead05">
	             <span class="fontnormal8pt">
	          	 	<html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="holiday.labelLinkAdmin" bundle="HolidayUIResources"/>	
					</html-el:link> /
	              </span>
	              <span class="fontnormal8pt">
	              	<html-el:link action="/offAction.do?method=getAllOfficesHolidays&randomNUm=${sessionScope.randomNUm}">
          				<mifos:mifoslabel name="holiday.labelLinkViewHolidays" bundle="HolidayUIResources"/>
          			</html-el:link> /
	              </span>
	              <span class="fontnormal8pt">
					<html-el:link action="holidayAction.do?method=getHolidays&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
          				<mifos:mifoslabel name="holiday.labelLinkViewHolidaysOrganizationWide" bundle="HolidayUIResources"/>
          			</html-el:link>
          	     </span>
    	       </td>
	      </tr>
		</table>
		
		<table width="98%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="35%">
	            <span class="heading">
    	          	<mifos:mifoslabel name="holiday.labelLinkViewHolidaysOrganizationWide" bundle="HolidayUIResources"/>
    	        </span> - 
              	<span class="headingorange">
              	   <mifos:mifoslabel name="holiday.labelPreviewHoliday" bundle="HolidayUIResources"/>
              	</span>
              </td>
            </tr>  
          </table>
          <br>
          <table width="98%">
          <tr><td>
	          <table width="98%" border="0" cellpadding="3" cellspacing="0">
              <tr>
              <font class="fontnormalRedBold">
				<html-el:errors	bundle="HolidayUIResources" /> 
			  </font>
                <td width="11%" class="drawtablehd">
               		 <mifos:mifoslabel name="holiday.HolidayFromDate" bundle="HolidayUIResources"/>
                </td>
                <td width="11%" class="drawtablehd">
               		 <mifos:mifoslabel name="holiday.HolidayThruDate" bundle="HolidayUIResources"/>
                </td>
                <td width="28%" class="drawtablehd">
                	<mifos:mifoslabel name="holiday.HolidayName" bundle="HolidayUIResources"/>
				</td>
				<td width="50%" class="drawtablehd">
                	<mifos:mifoslabel name="holiday.HolidayRepaymentRule" bundle="HolidayUIResources"/>
				</td>
              </tr>
	          <tr>
	          	<td width="11%" class="drawtablerow">${holidayActionForm.holidayFromDate}</td>
	            	<td width="11%" class="drawtablerow">${holidayActionForm.holidayThruDate}</td>
	                <td width="28%" class="drawtablerow">${holidayActionForm.holidayName}</td>
	                <td width="50%" class="drawtablerow">
	                	<c:forEach var="RRT" items="${RepaymentRuleType}" >
							<c:if
								  test="${RRT.key == sessionScope.holidayActionForm.repaymentRuleId}">
									<c:out value="${RRT.value}" />
							</c:if>
						</c:forEach>
				</td>
	          </tr>          
              <tr>
                <td width="11%" class="drawtablerow">&nbsp;</td>
                <td width="11%" class="drawtablerow">&nbsp;</td>
                <td width="28%" class="drawtablerow">&nbsp;</td>
                <td width="50%" class="drawtablerow">&nbsp;</td>
              </tr>
             
            </table>
          </td>
          </tr>
      </table>			
	  <table width="98%" border="0" cellspacing="0" cellpadding="1">
		<tr align="center">
			<td height="3" align="left">&nbsp;</td>
		</tr>
						<tr align="center">
							<td height="3" align="left"><html-el:button property="editButton"
								styleClass="insidebuttn"
								onclick="javascript:fun_edit(this.form)">
								<mifos:mifoslabel name="holiday.editTrxn"  bundle="HolidayUIResources"/>
							</html-el:button></td>
						</tr>
						<tr align="center">
							<td width="98%" height="3" colspan="2" align="center"
								class="blueline">&nbsp;</td>
						</tr>
						<tr align="center">
							<td height="3" colspan="2" align="center" class="fontnormal">&nbsp;</td>
						</tr>
					</table>
					<table width="96%" border="0" cellspacing="0" cellpadding="1">
						<tr>
							<td align="center"><html-el:submit styleClass="buttn" styleId="holiday.button.submit">
								<mifos:mifoslabel name="loan.submit" />
							</html-el:submit> &nbsp; <html-el:button property="cancelButton"
								styleClass="cancelbuttn"
								onclick="javascript:fun_return(this.form)">
								<mifos:mifoslabel name="loan.cancel" />
							</html-el:button></td>
						</tr>
					</table>
					
			<html-el:hidden property="holidayName"
				value="${holidayActionForm.holidayName}" />
			<html-el:hidden property="repaymentRuleId"
				value="${holidayActionForm.repaymentRuleId}" />
			<html-el:hidden property="holidayFromDate"
				value="${holidayActionForm.holidayFromDate}" />
			<html-el:hidden property="holidayThruDate"
				value="${holidayActionForm.holidayThruDate}" />
			<html-el:hidden property="method" value="" />

		</html-el:form>

	</tiles:put>
</tiles:insert>
