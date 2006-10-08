<!-- /**
 
 * groupsearchresultsclienttransfer.jsp    version: 1.0
 
 
 
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
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">

<script language="javascript">
  function goToCancelPage(){
	clientTransferActionForm.submit();
  }
</script>
<html-el:form method="post" action ="clientTransferAction.do?method=cancel">
	<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
<html-el:form action="groupCustAction.do?method=search">
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
<c:set var="BusinessKey" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}"/>
     <table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
	        <td class="bluetablehead05">
		        <span class="fontnormal8pt">
	            	 <customtags:headerLink/>	            	
	            </span>
	        </td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td width="62%" class="headingorange">
                  <span class="heading">
                  <c:out value="${BusinessKey.displayName}"/>
                  </span> - <mifos:mifoslabel name="Group.change"/> 
                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
                  <mifos:mifoslabel name="Group.membership"/></td>
                </tr>
              </table>
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td class="fontnormalbold"> 
                  <span class="fontnormal">
                  <mifos:mifoslabel name="Group.groupdetailMsg1"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
                  <mifos:mifoslabel name="Group.nameToChangeThe"/>
                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/> 
                  <mifos:mifoslabel name="Group.membership"/>. 
                  <mifos:mifoslabel name="Group.editMag2"/> 
                  <mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"/>
                  <mifos:mifoslabel name="Group.editMag3"/></span> </td>
                </tr>
              </table>
              
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr class="fontnormal">
                  <td colspan="2" align="right">&nbsp;</td>
                </tr>
                <tr class="fontnormal">
                  <td width="20%" align="right">
                  <mifos:mifoslabel name="${ConfigurationConstants.GROUP}"/>
                  <mifos:mifoslabel name="Group.groupname"/></td>
                  <td width="80%"><html-el:text property="searchNode(searchString)" maxlength="200"/>
                  	<html-el:hidden property="searchNode(search_name)" value="GroupList"/>
	                  <html-el:submit styleClass="buttn" style="width:70px;">
	                  	<mifos:mifoslabel name="button.search" bundle="GroupUIResources"></mifos:mifoslabel>
	                  </html-el:submit>
				  </td>
                </tr>
              </table>
              
              
              <table width="96%" border="0" cellpadding="3" cellspacing="0">
                <tr>
   					<td>
   						<font class="fontnormalRedBold"><html-el:errors bundle="GroupUIResources"/></font>
					</td>
				</tr>
                <tr>
                  <td>
                 	   <mifos:mifostabletagdata name="groupSearch_clientTransfer" key="GroupList" type="single" 
	        		   className="GroupSearchResults" width="100%" border="0" cellspacing="0" cellpadding="0"/>
                  </td>
                  </tr>
              </table>
              <table width="96%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td align="center">
                  <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
	    	        <mifos:mifoslabel name="button.cancel" bundle="GroupUIResources"></mifos:mifoslabel>
		    	  </html-el:button>
                  </td>
                </tr>
              </table>             
          </td>
          </tr>
        </table>        
<html-el:hidden property="input" value="GroupSearch_ClientTransfer"/> 
</html-el:form>
</tiles:put>
</tiles:insert>