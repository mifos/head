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
<tiles:insert definition=".create">
<tiles:put name="body" type="string">

<script language="javascript">
			function fnCreateCancel(){
				fundActionForm.action="fundAction.do?method=cancelCreate";
				fundActionForm.submit();
		  	}
		  	
		  	function fnPrevious(){
				fundActionForm.action="fundAction.do?method=previous";
				fundActionForm.submit();
		  	}
		</script>
<html-el:form action="/fundAction.do?method=create">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="450" align="left" valign="top" bgcolor="#FFFFFF">      
     <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td align="center" class="heading">&nbsp;</td>
      </tr>
    </table>
      <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="27%">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/tick.gif" width="17" height="17"></td>
                      <td class="timelineboldgray">
                      <mifos:mifoslabel name="funds.fundInformation" bundle="fundUIResources"/>                      
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="73%" align="right">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorange">
                      <mifos:mifoslabel name="funds.reviewAndSubmit" bundle="fundUIResources"/> 
                      </td>
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
          <td align="left" valign="top" class="paddingleftCreates">
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="headingorange">
                <span class="heading">
				<mifos:mifoslabel name="funds.new_fund" bundle="fundUIResources"/>
              	 - </span>
              	 <mifos:mifoslabel name="funds.reviewAndSubmit" bundle="fundUIResources"/>
              	 </td>
              </tr>
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="funds.preview_instructions" bundle="fundUIResources"/>
                </td>
              </tr>
            </table>            <br>
            
            
            <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <font class="fontnormalRedBold"> 
					<html-el:errors	bundle="fundUIResources" /> 
				</font>
                <td width="100%" height="23" class="fontnormal">
                
                <span class="fontnormalbold">
                <mifos:mifoslabel name="funds.fund_details" bundle="fundUIResources"/>	
                </span>
                <br>
                
                <span class="fontnormalbold">
     	 		<mifos:mifoslabel name="funds.fund_name" bundle="fundUIResources"/>:	
     	 		</span>							     	 
     	 
     	 		<c:out value="${sessionScope.fundActionForm.fundName}"/>  	 
		    	<br>      
		    	
		    	<span class="fontnormalbold">
		        <mifos:mifoslabel name="funds.fundcode" bundle="fundUIResources" isColonRequired="yes"/>
		        </span>
		        <c:forEach items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'allFunds')}" var="fund">
					<c:if test="${fund.fundCodeId == sessionScope.fundActionForm.fundCode}">
						<c:out value="${fund.fundCodeValue}" />
					</c:if>
				</c:forEach>
		        <br>		        
		    	<br>
			    <br>
     			
     			<span class="fontnormal"></span>
			    
			    <html-el:button property="button" styleClass="insidebuttn" onclick="javascript:fnPrevious();">
					<mifos:mifoslabel name="funds.editfund" bundle="fundUIResources"></mifos:mifoslabel>
				</html-el:button>      
				
                </td>
              </tr>
            </table>
            
            
            <table width="93%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline">&nbsp; </td>
              </tr>
            </table>
            <br>
            <table width="93%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">
                <html-el:submit property="button" styleClass="buttn">
							<mifos:mifoslabel name="funds.submit" bundle="fundUIResources"/>
				</html-el:submit>&nbsp;
				<html-el:button property="calcelButton" styleClass="cancelbuttn" onclick="javascript:fnCreateCancel();">
							<mifos:mifoslabel name="funds.cancel" bundle="fundUIResources"/>
				</html-el:button>                
                </td>
              </tr>
            </table>
            <br>
          </td>
        </tr>
      </table>      <br></td>
  </tr>
</table>
<br><html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
	</html-el:form>
	</tiles:put>
</tiles:insert>

