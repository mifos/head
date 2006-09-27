<!--
/**

* EditFunds.jsp    version: 1.0



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
<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<script language="JavaScript" src="pages/application/fund/js/fundValidator.js"	type="text/javascript">
</script>
<html-el:form action="/fundAction.do?method=update">
<html-el:hidden property="method" value=""/>
<html-el:hidden property="input" value="manage"/>

   <table width="95%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="bluetablehead05">
        <span class="fontnormal8pt">
		       <html-el:link action="AdminAction.do?method=load">
					<mifos:mifoslabel name="funds.admin" bundle="fundUIResources"/>	
				</html-el:link> / 
       			 <html-el:link action="/fundAction.do?method=getAllFunds">
						<mifos:mifoslabel name="funds.viewfunds" bundle="fundUIResources"/> 
				</html-el:link>/ 
			</span>
			<span class="fontnormal8ptbold"><c:out value="${requestScope.fundVO.fundName}"/></span>
			</td>
      </tr>
    </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="headingorange"><span class="heading"><c:out value="${requestScope.fundVO.fundName}"/> - </span>
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
                  <c:out value="${requestScope.fundVO.fundName}"/> 
                  <br>
                  <span class="fontnormalbold"> 
                  <mifos:mifoslabel name="funds.fund_glcode" bundle="fundUIResources"/>:                
                  </span>
				  <c:out value="${requestScope.fundVO.glCode.glCodeValue}"/>
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
                  <html-el:submit style="width:65px;"	property="button" styleClass="buttn" onclick="javascript:fnOnEdit(this.form);">
							<mifos:mifoslabel name="funds.submit" bundle="fundUIResources"/>
				</html-el:submit>&nbsp;
				<html-el:cancel style="width:65px;"	styleClass="cancelbuttn" onclick="javascript:fnEditCancel(this.form);">
							<mifos:mifoslabel name="funds.cancel" bundle="fundUIResources"/>
				</html-el:cancel> 
                  </td>
                </tr>
              </table>              <br></td>
          </tr>
        </table>        
      <br>
  
 
</html-el:form>
</tiles:put>
</tiles:insert>
