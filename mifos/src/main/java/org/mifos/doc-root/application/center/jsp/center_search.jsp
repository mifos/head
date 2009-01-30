<!--
/**

* center_search.jsp    version: 1.0



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

<%@taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-bean-el" prefix="bean-el"%>


<fmt:setLocale value='${sessionScope["LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.CenterUIResources"/>

<tiles:insert definition=".withoutmenu">
 <tiles:put name="body" type="string">
 <fmt:setLocale value='${sessionScope["LOCALE"]}'/>
		<fmt:setBundle basename="org.mifos.config.localizedResources.CenterUIResources"/>
 <script type="text/javascript">
 
 function goToCancelPage()
 {
     centerCustActionForm.input.value="create";
     centerCustActionForm.method.value="cancel";
     centerCustActionForm.submit();
 }
    
 </script>
<html-el:form method="post" action ="centerCustAction.do">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="470" align="left" valign="top" bgcolor="#FFFFFF"><table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
      </tr>
    </table>      
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead"><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="33%"><table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                <td class="timelineboldorange"><mifos:mifoslabel name="client.select" bundle="ClientUIResources"/> <mifos:mifoslabel name="${ConfigurationConstants.CENTER}" /></td>
              </tr>
            </table></td>
            <td width="34%" align="center">
			  <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight">
                <mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />
                <mifos:mifoslabel name="Center.Information" bundle="CenterUIResources"></mifos:mifoslabel></td>
              </tr>
            </table>
			</td>
            <td width="33%" align="right">
			  <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                <td class="timelineboldorangelight"><mifos:mifoslabel name="Center.ReviewSubmit" bundle="CenterUIResources"></mifos:mifoslabel></td>
              </tr>
            </table>
			</td>
            </tr>
        </table>
        </td>
      </tr>
    </table>
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
        <tr>
          <td width="70%" height="24" align="left" valign="top" class="paddingleftCreates"><table width="98%" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td class="headingorange"><span class="heading">
                    
                    <fmt:message key="Center.CreateGroupNew">
						<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.GROUP}" /></fmt:param>
				    </fmt:message>                  
                    <mifos:mifoslabel name="Center.dash"  bundle="CenterUIResources"/>
                    
                 	</span>
                 	<mifos:mifoslabel name="Center.SearchSelectCenterHeading"  bundle="CenterUIResources"/>
					<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/>
                </td>
              </tr>
              <tr>
                <td class="fontnormalbold"> <span class="fontnormal">
                <fmt:message key="Center.SearchInstructions">
	                <fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/></fmt:param>
                </fmt:message>
                <mifos:mifoslabel name="Center.CreatePageCancelInstruction"  bundle="CenterUIResources"></mifos:mifoslabel>
                </span>
                  </td>
              </tr>
              <tr><td><font class="fontnormalRedBold"><span id="center_search.error.message"><html-el:errors bundle="CenterUIResources" /></span> </font></td></tr>
            </table>
              <br>
              
               
                    
					<table width="90%" border="0" cellspacing="0" cellpadding="3">
					 
					 <tr class="fontnormal">
                 	    <td align="right">
                 	    	<span id="center_search.label.search">
                 	    	<fmt:message key="Center.centerName">
                 	    		<fmt:param><mifos:mifoslabel name="${ConfigurationConstants.CENTER}"/></fmt:param>
                 	    	</fmt:message></span>:</td>
		                <td>
		                  <html-el:text styleId="center_search.input.search" property="searchString" maxlength = "200"/>
		                </td>
	                </tr>
	               	<!-- Search results--->
	               	
	                
	                <tr class="fontnormal">
                  		<td width="30%">&nbsp; </td>
                  		<td width="70%"><br>
	                     <html-el:submit styleId="center_search.button.search" styleClass="buttn" ><mifos:mifoslabel name="button.Search" bundle ="CenterUIResources"></mifos:mifoslabel></html-el:submit>						  &nbsp;
	      				  <html-el:button styleId="center_search.button.cancel" property="cancelButton" onclick="goToCancelPage();" styleClass="cancelbuttn" ><mifos:mifoslabel name="button.cancel" bundle ="CenterUIResources"></mifos:mifoslabel> </html-el:button>
                  			<br><br>
                  		</td>
                	</tr>
                	</table>
				
					         
              <br>
          </td>
        </tr>
      </table>
      <br></td>
  </tr>
</table>
<html-el:hidden property="input" value="" />
<html-el:hidden property="method" value="search" />
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>



