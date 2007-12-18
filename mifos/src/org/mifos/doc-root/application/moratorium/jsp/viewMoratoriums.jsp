<!--
/**

* viewMoratoriums.jsp    version: 1.0



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

<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<html-el:form action="/moratoriumAction.do">
<table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
	          <span class="fontnormal8pt">
	          	 <html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="moratorium.labelLinkAdmin" bundle="moratoriumUIResources"/>	
				</html-el:link> /
	          </span><span class="fontnormal8ptbold">
          			<mifos:mifoslabel name="moratorium.labelLinkViewMoratoriums1" bundle="moratoriumUIResources"/>          			
          	   </span>
          </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="35%">	            
              	<span class="headingorange">
              	   <mifos:mifoslabel name="moratorium.labelLinkViewMoratoriums2" bundle="moratoriumUIResources"/>
              	</span>
              </td>
            </tr>
            
            <tr>
			  <td class="fontnormalbold">
			     <span class="fontnormal">
	                <mifos:mifoslabel name="moratorium.labelLinkListMOratoriums" bundle="moratoriumUIResources"/>
	                <mifos:mifoslabel name="moratorium.labelLinkClickHere" bundle="moratoriumUIResources"/>
					<html-el:link action="moratoriumAction.do?method=configure&currentFlowKey=${requestScope.currentFlowKey}">
						<mifos:mifoslabel name="moratorium.labelAddNewMoratorium" bundle="moratoriumUIResources"/>
					</html-el:link>
	             </span>
            	 <br><br>
                 
              </td>
            </tr>    
          </table>
          
	        
            <span class="fontnormalbold">
			  <mifos:mifoslabel name="moratorium.labelActiveMoratoriums" bundle="moratoriumUIResources"/>			  
    	    </span>
            <br><br>
            <table width="98%" border="0" cellpadding="3" cellspacing="0">
              <tr>              
                <td width="11%" class="drawtablehd">
               		 <mifos:mifoslabel name="moratorium.labelAppliedTo" bundle="moratoriumUIResources"/>
                </td>
                <td width="20%" class="drawtablehd">
               		 <mifos:mifoslabel name="moratorium.labelStartDateEndDate" bundle="moratoriumUIResources"/>
                </td>
                <td width="12%" class="drawtablehd">
                	<mifos:mifoslabel name="moratorium.labelCreatedBy" bundle="moratoriumUIResources"/>
				</td>
				<td width="50%" class="drawtablehd">
                	<mifos:mifoslabel name="moratorium.labelNotes" bundle="moratoriumUIResources"/>
				</td>
				<td width="7%" class="drawtablehd">
                	&nbsp;
				</td>
              </tr>
              <c:forEach var="moratorium" items="${requestScope.openMoratoriums}" >
	              <tr>
	                <td width="11%" class="drawtablerow">${moratorium.appliedTo}</td>
	                <td width="20%" class="drawtablerow">${moratorium.startDateString} - ${moratorium.endDateString}</td>
	                <td width="12%" class="drawtablerow">${moratorium.morCreatedBy}</td>
	                <td width="50%" class="drawtablerow">${moratorium.notes}</td>
	                <td width="7%" class="drawtablerow">
	                	<html-el:link href="moratoriumAction.do?method=edit&moratoriumId=${moratorium.moratoriumId}&customerId=${moratorium.customerId}&officeId=${moratorium.officeId}&currentFlowKey=${requestScope.currentFlowKey}">
	                		<mifos:mifoslabel name="moratorium.edit" bundle="moratoriumUIResources" /> / <mifos:mifoslabel name="moratorium.lift" bundle="moratoriumUIResources" />
						</html-el:link>
					</td>
	              </tr>
              </c:forEach>           
              <tr>
                <td width="11%" class="drawtablerow">&nbsp;</td>
                <td width="20%" class="drawtablerow">&nbsp;</td>
                <td width="12%" class="drawtablerow">&nbsp;</td>
                <td width="50%" class="drawtablerow">&nbsp;</td>
                <td width="7%" class="drawtablerow">&nbsp;</td>
              </tr>             
            </table>
            
            <span class="fontnormalbold">
			  <mifos:mifoslabel name="moratorium.labelClosedMoratoriums" bundle="moratoriumUIResources"/>			  
    	    </span>
            <br><br>
            <table width="98%" border="0" cellpadding="3" cellspacing="0">
              <tr>              
                <td width="11%" class="drawtablehd">
               		 <mifos:mifoslabel name="moratorium.labelAppliedTo" bundle="moratoriumUIResources"/>
                </td>
                <td width="20%" class="drawtablehd">
               		 <mifos:mifoslabel name="moratorium.labelStartDateEndDate" bundle="moratoriumUIResources"/>
                </td>
                <td width="12%" class="drawtablehd">
                	<mifos:mifoslabel name="moratorium.labelCreatedBy" bundle="moratoriumUIResources"/>
				</td>
				<td width="50%" class="drawtablehd">
                	<mifos:mifoslabel name="moratorium.labelNotes" bundle="moratoriumUIResources"/>
				</td>
				<td width="7%" class="drawtablehd">
                	&nbsp;
				</td>
              </tr>
              <c:forEach var="moratorium" items="${requestScope.closedMoratoriums}" >
	              <tr>
	                <td width="11%" class="drawtablerow">${moratorium.appliedTo}</td>
	                <td width="20%" class="drawtablerow">${moratorium.startDateString} - ${moratorium.endDateString}</td>
	                <td width="12%" class="drawtablerow">${moratorium.morCreatedBy}</td>
	                <td width="50%" class="drawtablerow">${moratorium.notes}</td>
	                <td width="7%" class="drawtablerow">
	                	<a href="#" disabled>Edit </a>
					</td>	                
	              </tr>
              </c:forEach>           
              <tr>
                <td width="11%" class="drawtablerow">&nbsp;</td>
                <td width="20%" class="drawtablerow">&nbsp;</td>
                <td width="12%" class="drawtablerow">&nbsp;</td>
                <td width="50%" class="drawtablerow">&nbsp;</td>
                <td width="7%" class="drawtablerow">&nbsp;</td>
              </tr>             
            </table>            
          
          
            <br>
          </td>
          </tr>
      </table>
      <br>
   	  <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
      </html-el:form>
</tiles:put>
</tiles:insert>
