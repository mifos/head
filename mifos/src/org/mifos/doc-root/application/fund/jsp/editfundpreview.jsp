<!--
/**

* editfundpreview.jsp    version: 1.0



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
<script language="javascript">
	function fnCreateCancel(){
		fundActionForm.action="fundAction.do?method=cancelManage";
		fundActionForm.submit();
  	}
  	function fnPrevious(){
		fundActionForm.action="fundAction.do?method=previousManage";
		fundActionForm.submit();
	}
</script>
<html-el:form action="/fundAction.do?method=update">
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
			<span class="fontnormal8ptbold"><c:out value="${sessionScope.fundActionForm.fundName}"/></span>
			</td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="headingorange"><span class="heading"><c:out value="${sessionScope.fundActionForm.fundName}"/> - </span>
                <mifos:mifoslabel name="funds.preview_fundInfo" bundle="fundUIResources"/>                
                </td>
              </tr>
              <tr>
                <td class="fontnormal">
                <mifos:mifoslabel name="funds.edit_preview_instructions" bundle="fundUIResources"/>                
                 </td>
              </tr>
            </table>  
              <br>
              
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
                  <mifos:mifoslabel name="funds.fund_name" bundle="fundUIResources"/>:                
                  </span>
				  <c:out value="${sessionScope.fundActionForm.fundCode}"/>
				  <br>
			      <br>
			      <span class="fontnormal"></span>
			      <html-el:button property="button" styleClass="insidebuttn" onclick="javascript:fnPrevious(this.form);">
						<mifos:mifoslabel name="funds.editfund" bundle="fundUIResources"></mifos:mifoslabel>
				  </html-el:button>
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
                  <html-el:submit style="width:65px;" property="button" styleClass="buttn">
							<mifos:mifoslabel name="funds.submit" bundle="fundUIResources"/>
				</html-el:submit>&nbsp;
				<html-el:button property="calcelButton" style="width:65px;"	styleClass="cancelbuttn" onclick="javascript:fnCreateCancel();">
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
