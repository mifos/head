<!--
/**

* view_organizational_holidays.jsp    version: 1.0



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
* 
*/
 -->

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<%@ page import="java.util.Calendar"%>

<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<html-el:form action="/holidayAction.do">
<table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
	          <span class="fontnormal8pt">
	          	 <html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="holiday.labelLinkAdmin" bundle="HolidayUIResources"/>	
				</html-el:link> /
	          </span><span class="fontnormal8ptbold">
          			<mifos:mifoslabel name="holiday.labelLinkViewHolidays" bundle="HolidayUIResources"/> -
          			<mifos:mifoslabel name="holiday.labelLinkViewHolidaysOrganizationWide" bundle="HolidayUIResources"/>          			        			
          	   </span>
          </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="35%">
	            <span class="heading">
    	          	<mifos:mifoslabel name="holiday.labelLinkViewHolidays" bundle="HolidayUIResources"/>
    	        </span> - 
              	<span class="headingorange">
              	   <mifos:mifoslabel name="holiday.labelLinkViewHolidaysOrganizationWide" bundle="HolidayUIResources"/>
              	</span>
              </td>
            </tr>
            
            <tr>
			  <td class="fontnormalbold">
			     <span class="fontnormal">
	                <mifos:mifoslabel name="holiday.labelLinkListOrganizationalHolidays" bundle="HolidayUIResources"/>
	                <mifos:mifoslabel name="holiday.labelLinkClickHere" bundle="HolidayUIResources"/>
					<html-el:link action="holidayAction.do?method=addHoliday&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
						<mifos:mifoslabel name="holiday.labelAddNewHolidayNow" bundle="HolidayUIResources"/>
					</html-el:link>
	             </span>
            	 <br><br>
                 
              </td>
            </tr>    
          </table>
          
          <c:forEach var="item" begin="1" end="2">
	        <%pageContext.setAttribute("holidayList", "holidayList" + pageContext.getAttribute("item"));%>
	        
            <span class="fontnormalbold">
			  <mifos:mifoslabel name="holiday.labelHolidaysForYear" bundle="HolidayUIResources"/>
			  <%=(Calendar.getInstance().get(Calendar.YEAR)+Integer.parseInt(pageContext.getAttribute("item").toString())-1)%>
    	    </span>
            <br><br>
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
              <c:forEach var="holidayItem" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey, holidayList)}" >
	              <tr>
	                <td width="11%" class="drawtablerow">${holidayItem.holidayPK.holidayFromDate}</td>
	                <td width="11%" class="drawtablerow">${holidayItem.holidayThruDate}&nbsp;</td>
	                <td width="28%" class="drawtablerow">${holidayItem.holidayName}</td>
	                <td width="50%" class="drawtablerow">${holidayItem.repaymentRule}</td>
	              </tr>
              </c:forEach>           
              <tr>
                <td width="11%" class="drawtablerow">&nbsp;</td>
                <td width="11%" class="drawtablerow">&nbsp;</td>
                <td width="28%" class="drawtablerow">&nbsp;</td>
                <td width="50%" class="drawtablerow">&nbsp;</td>
              </tr>
             
            </table>            
            </c:forEach>
            <br>
          </td>
          </tr>
      </table>
      <br>
   	  <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
      </html-el:form>
</tiles:put>
</tiles:insert>
