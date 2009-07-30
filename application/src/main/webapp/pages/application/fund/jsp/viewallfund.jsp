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

<span style="display: none" id="page.id">viewFunds</span>
<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<html-el:form action="/fundAction.do">
<table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
	          <span class="fontnormal8pt">
	          	 <html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="funds.admin" bundle="fundUIResources"/>	
				</html-el:link> /
	          </span><span class="fontnormal8ptbold">
          			<mifos:mifoslabel name="funds.viewfunds" bundle="fundUIResources"/>          			        			
          	   </span>
          </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="35%" class="headingorange">
              	<mifos:mifoslabel name="funds.viewfunds" bundle="fundUIResources"/>
              </td>
            </tr>
          </table>            
            <br>
            <table id="fundDisplayTable" width="80%" border="0" cellpadding="3" cellspacing="0">
              <tr>
              <font class="fontnormalRedBold">
				<html-el:errors	bundle="fundUIResources" /> 
			  </font>
                <td width="33%" class="drawtablerowboldnoline">
               		 <mifos:mifoslabel name="funds.fund_name" bundle="fundUIResources"/>
                </td>
                <td width="44%" class="drawtablerowboldnoline">
               		 <mifos:mifoslabel name="funds.fundcode" bundle="fundUIResources"/>
                </td>
                <td width="23%" class="drawtablerowboldnoline">&nbsp;</td>
              </tr>  
              
              <c:forEach var="fundItem" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'fundList')}" >
	              <tr>
	                <td width="33%" class="drawtablerow">${fundItem.fundName}</td>
	                <td width="44%" class="drawtablerow">${fundItem.fundCode.fundCodeValue}</td>
	                <td width="23%" align="right" class="drawtablerow">
		               	<html-el:link action="/fundAction.do?method=manage&fundCodeId=${fundItem.fundId}&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
		               		<mifos:mifoslabel name="funds.edit" bundle="fundUIResources"/>                		
		               	</html-el:link>
	                </td>
	              </tr>
              </c:forEach>           
              <tr>
                <td width="33%" class="drawtablerow">&nbsp;</td>
                <td width="44%" class="drawtablerow">&nbsp;</td>
                <td width="23%" class="drawtablerow">&nbsp;</td>
              </tr>
             
            </table>
            <br>
          </td>
          </tr>
      </table>
      <br>
      </html-el:form>
</tiles:put>
</tiles:insert>
