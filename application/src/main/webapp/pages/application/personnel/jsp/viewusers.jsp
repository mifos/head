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
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".view">
 <tiles:put name="body" type="string">
 <span id="page.id" title="viewusers" />
 
<html-el:form action="PersonAction.do?method=search" focus="searchString">

       <table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead05">
        <span class="fontnormal8pt">
        	<a id="viewusers.link.admin" href="AdminAction.do?method=load">
	           <mifos:mifoslabel name="Personnel.Admin" bundle="PersonnelUIResources"></mifos:mifoslabel>             	
         	</a> / 
        </span>
            <span class="fontnormal8ptbold">
            <mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
            </span></td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td align="left" valign="top" class="paddingL15T15"><table width="95%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td width="63%" class="headingorange"><span class="headingorange">
                <mifos:mifoslabel name="Personnel.ViewUsers" bundle="PersonnelUIResources"></mifos:mifoslabel>
                </span></td>
              </tr>
            </table>
              <br>
              <table width="95%" border="0" cellpadding="2" cellspacing="0">
               <tr>
   				<td colspan="2">
   				<font class="fontnormalRedBold">
   					<span id="viewusers.error.message"><html-el:errors bundle="PersonnelUIResources"/></span>
   				</font>
				</td>
				</tr>
                <tr class="fontnormal">
                  <td colspan="2">
                  <span id="viewusers.label.search">
                  <mifos:mifoslabel name="Personnel.SearchMsg" bundle="PersonnelUIResources"></mifos:mifoslabel></span>
                   </td>
                </tr>
                <tr class="fontnormal8pt">
                  <td width="17%">
                  	<html-el:text styleId="viewusers.input.search" property="searchString" maxlength="200"/>
 	                
                  </td>
                  <td width="83%">&nbsp;
                  	<html-el:submit styleId="viewusers.button.search" styleClass="buttn">
                  		<mifos:mifoslabel name="button.search" bundle ="PersonnelUIResources"></mifos:mifoslabel>
                  	</html-el:submit>
                  </td>
                </tr>
                <tr class="fontnormal8pt">
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td>
                  <span class="fontnormal">
                  <c:if test="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'office').size>0}">
	                  <mifos:mifoslabel name="Personnel.ClickUserLink" bundle ="PersonnelUIResources"></mifos:mifoslabel>
                  </c:if>
                  </span></td>
                </tr>
                <tr>
                   <mifos:mifostabletagdata name="viewUsers" key="viewUsers" type="single" 
              		className="UserSearchResults" width="100%" border="0" cellspacing="0" cellpadding="0"/>
                </tr>
              </table>
        </tr>
      </table>
      <br>
<br>
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />

</html-el:form>
</tiles:put>
</tiles:insert>

