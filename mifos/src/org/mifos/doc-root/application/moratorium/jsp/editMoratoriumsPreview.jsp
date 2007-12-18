<!--
/**

* editMoratoriumsPreview.jsp    version: 1.0



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
<%@ taglib uri="/tags/date" prefix="date"%>
<tiles:insert definition=".view">
<tiles:put name="body" type="string">		
		<script>
				function fnCancel(form) {
				form.action="moratoriumAction.do?method=cancel";
				form.submit();
			}
			
			function goToEditPage(form){
			form.action="moratoriumAction.do?method=previousEdit";
			form.submit();
		  }
		</script>
<html-el:form action="/moratoriumAction.do?method=updateMoratorium">
<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
<table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
	          <span class="fontnormal8pt">
	          	 <html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="moratorium.labelLinkAdmin" bundle="moratoriumUIResources"/>	
				</html-el:link> /
	          </span>
	          <span class="fontnormal8pt">
	          	<html-el:link action="moratoriumAction.do?method=get&randomNUm=${sessionScope.randomNUm}">
          			<mifos:mifoslabel name="moratorium.labelLinkViewMoratoriums1" bundle="moratoriumUIResources"/>          			
          		</html-el:link> /
          	   </span>
          	   <span class="fontnormal8ptbold">
          			<mifos:mifoslabel name="moratorium.labelViewDetails" bundle="moratoriumUIResources"/>          			
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
    	          	<c:out value="${BusinessKey.appliedTo}"/>
    	        </span> - 	            
              	<span class="headingorange">
              	   <mifos:mifoslabel name="moratorium.labelPreviewEditMoratoriums" bundle="moratoriumUIResources"/>
              	</span>
              </td>
            </tr>
            
            <tr>
			  <td class="fontnormalbold">			     
	            <span class="fontnormal">
					<mifos:mifoslabel name="moratorium.labelPreviewEditMoratoriumsInfo1" bundle="moratoriumUIResources" />					
				</span>
            	 <br><br>
                 
              </td>
            </tr>
            
            <tr>
				<td class="fontnormalbold">
					<span class="fontnormalbold">
						<mifos:mifoslabel name="moratorium.labelEditMoratoriumDetails" />
					</span>					
				</td>
			</tr>    
          </table>
          
	        <table width="96%" border="0" cellspacing="0" cellpadding="0">	        	
				<tr>
					<td>
						<span class="fontnormalbold">
							<mifos:mifoslabel isColonRequired="Yes" name="moratorium.StartDate" bundle="moratoriumUIResources"/>
						</span>&nbsp;
						<span class="fontnormal">
							<c:out value="${sessionScope.moratoriumActionForm.moratoriumFromDateDD}"/>/<c:out value="${sessionScope.moratoriumActionForm.moratoriumFromDateMM}"/>/<c:out value="${sessionScope.moratoriumActionForm.moratoriumFromDateYY}"/>
						</span>
					</td>									
				</tr>
				<tr>
					<td>
						<span class="fontnormalbold">
							<mifos:mifoslabel isColonRequired="Yes" name="moratorium.EndDate" bundle="moratoriumUIResources"/>
						</span>&nbsp;
						<span class="fontnormal">
							<c:out value="${sessionScope.moratoriumActionForm.moratoriumEndDateDD}"/>/<c:out value="${sessionScope.moratoriumActionForm.moratoriumEndDateMM}"/>/<c:out value="${sessionScope.moratoriumActionForm.moratoriumEndDateYY}"/>
						</span>
					</td>									
				</tr>
				<tr>
					<td>
						<span class="fontnormalbold">
							<mifos:mifoslabel isColonRequired="Yes" name="moratorium.notes" bundle="moratoriumUIResources"/>
						</span>
					</td>									
				</tr>
				<tr>
					<td class="fontnormal">
						<span class="fontnormal">
							<c:out value="${sessionScope.moratoriumActionForm.moratoriumNotes}"/>
						</span>
					</td>
				</tr>
				<tr></tr><tr></tr>
				<tr></tr><tr></tr>
				<tr></tr><tr></tr>
				<tr>
					<td align="left" valign="top">
						<html-el:button property="btn" style="width:200px;" styleClass="insidebuttn" onclick="goToEditPage(this.form);">
					    	<mifos:mifoslabel name="moratorium.editLabel" bundle="moratoriumUIResources"></mifos:mifoslabel>
					    </html-el:button>
				    </td>
				</tr>								
			</table><br>
			<table width="96%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td align="center">
						<html-el:submit styleClass="buttn" style="width:70px;">
							<mifos:mifoslabel name="moratorium.submit" />
						</html-el:submit>
						&nbsp;&nbsp;
						<html-el:button property="cancelButton" styleClass="cancelbuttn" style="width:70px;" onclick="fnCancel(this.form);">
							<mifos:mifoslabel name="moratorium.cancel" bundle="moratoriumUIResources" />
						</html-el:button>
					</td>
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
