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


<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<script language="javascript">
	function fnCreateCancel(){
		fundActionForm.action="fundAction.do?method=cancelManage";
		fundActionForm.submit();
  	}
</script>
<html-el:form action="/fundAction.do?method=previewManage">
<c:set var="oldFundName" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'oldFundName')}"/>
<table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead05">
        	<span class="fontnormal8pt">
		       <html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="funds.admin" bundle="fundUIResources"/>	
				</html-el:link> / 
       			 <html-el:link action="/fundAction.do?method=viewAllFunds&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="funds.viewfunds" bundle="fundUIResources"/> 
				</html-el:link>/ 
			</span>
			<span class="fontnormal8ptbold"><c:out value="${oldFundName}"/> </span>
		</td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15">
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="headingorange">
                	<span class="heading"><c:out value="${oldFundName}"/>- </span>
	                	<mifos:mifoslabel name="funds.editfund" bundle="fundUIResources"/>
                </td>
              </tr>
              <tr>
                <td class="fontnormal">
					<mifos:mifoslabel name="funds.editfund_pageinstructions" bundle="fundUIResources"/>                
                	<br>
                	<mifos:mifoslabel name="funds.mandatoryinstructions" mandatory="yes" bundle="fundUIResources"/>   
				</td>
              </tr>
            </table>  
              <br>
              <table width="93%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                <font class="fontnormalRedBold"> 
                	<html-el:errors	bundle="fundUIResources" />
                 </font>
                  <td colspan="2" class="fontnormalbold">
                  <mifos:mifoslabel name="funds.fund_details" bundle="fundUIResources"/>                  
                  <br>
                  <br>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td width="27%" align="right">
                 		<mifos:mifoslabel name="funds.fund_name" mandatory="yes" bundle="fundUIResources"/>:</td>
                  <td width="73%" valign="top">
                  	<mifos:mifosalphanumtext property="fundName" value="${sessionScope.fundActionForm.fundName}" maxlength="100"/>
                  </td>
                </tr>
                <tr class="fontnormal">
                  <td align="right" valign="top">
                 		 <mifos:mifoslabel name="funds.fundcode" mandatory="yes" bundle="fundUIResources" isColonRequired="yes"/>
                  </td>
                  <td valign="top">
                  <c:out value="${sessionScope.fundActionForm.fundCode}"/>
                  <html-el:hidden property="fundCode" value="${sessionScope.fundActionForm.fundCode}"/>
                  </td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="93%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">
                  
                <html-el:submit property="button" styleClass="buttn">
							<mifos:mifoslabel name="funds.preview" bundle="fundUIResources"/>
				</html-el:submit>&nbsp;
				<html-el:button property="calcelButton" styleClass="cancelbuttn" onclick="javascript:fnCreateCancel();">
							<mifos:mifoslabel name="funds.cancel"  bundle="fundUIResources"/>
				</html-el:button>     
                  </td>
                </tr>
              </table>              <br></td>
          </tr>
        </table>        
      <br><html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>
