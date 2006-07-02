<!-- /**
 
 * confirmcentermembership.jsp    version: 1.0
 
 
 
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
 
 */-->
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>


<tiles:insert definition=".withmenu">
 <tiles:put name="body" type="string">
<SCRIPT SRC="pages/framework/js/CommonUtilities.js"></SCRIPT>
<script>
  function goToCancelPage(){
	groupActionForm.action="GroupAction.do?method=cancel";
	groupActionForm.submit();
  }
</script>
<html-el:form action="/GroupAction.do?method=updateParent" onsubmit="func_disableSubmitBtn('submitBtn')">
     <table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead05">
        	<span class="fontnormal8pt">
       	   <a href="CustomerSearchAction.do?method=getOfficeHomePage&officeId=<c:out value="${sessionScope.linkValues.customerOfficeId}"/>&officeName=<c:out value="${sessionScope.linkValues.customerOfficeName}"/>&loanOfficerId=<c:out value="${requestScope.Context.userContext.id}"/>">
	           <c:out value="${sessionScope.linkValues.customerOfficeName}"/>            	
           </a> /
           	<c:if test="${!empty sessionScope.linkValues.customerParentName}">
               	<a href="centerAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.customerParentGCNum}"/>">
			       	<c:out value="${sessionScope.linkValues.customerParentName}"/>
		       	</a> /  
		    </c:if>
          <a href="GroupAction.do?method=get&globalCustNum=<c:out value="${sessionScope.linkValues.globalCustNum}"/>">
          	<c:out value="${sessionScope.linkValues.customerName}"/>
           </a>
        	</span></td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="62%" class="headingorange">
                  	<span class="heading">
	                  <c:out value="${sessionScope.linkValues.customerName}"/> - 
    	            </span> 
        	            <mifos:mifoslabel name="Group.change" bundle="GroupUIResources"></mifos:mifoslabel>
        	            <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" ></mifos:mifoslabel>
        	            <mifos:mifoslabel name="Group.membership" bundle="GroupUIResources"></mifos:mifoslabel>
        	            <br>
                    <span class="fontnormal">
                            <mifos:mifoslabel name="Group.confirmcenterMsg1" bundle="GroupUIResources"></mifos:mifoslabel>
                            <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" ></mifos:mifoslabel>
                            <mifos:mifoslabel name="Group.confirmcenterMsg2" bundle="GroupUIResources"></mifos:mifoslabel>
                            <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" ></mifos:mifoslabel>
                            <mifos:mifoslabel name="Group.confirmcenterMsg3" bundle="GroupUIResources"></mifos:mifoslabel>
                            <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" ></mifos:mifoslabel>
                    
                    </span></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
   					<td>
   					<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font>
					</td>
				</tr>
                <tr>
                  <td class="fontnormalbold"> <span class="fontnormal"><br>
                    </span>
                        <mifos:mifoslabel name="Group.change" bundle="GroupUIResources"></mifos:mifoslabel>
                        <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" ></mifos:mifoslabel>
                        <mifos:mifoslabel name="Group.membership" bundle="GroupUIResources"></mifos:mifoslabel>
                        
                    <span class="fontnormal"> 
	                  <c:out value="${sessionScope.groupActionForm.centerName}"/>
             
                      <br>
                     </span></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="blueline"><img src="pages/framework/images/trans.gif" width="10" height="5"></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td width="80%" align="center"><br>
                    <html-el:submit property="submitBtn" styleClass="buttn" style="width:70px;" >
		                <mifos:mifoslabel name="button.submit" bundle="GroupUIResources"></mifos:mifoslabel>
	                </html-el:submit>
        	        	&nbsp; &nbsp;
    	            <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
                    </html-el:button>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>        
      <br>
       <html-el:hidden property="input" value="confirmParentTransfer"/> 
</html-el:form>
</tiles:put>
</tiles:insert>
