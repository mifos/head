<!--
/**

* DefineFunds.jsp    version: 1.0



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
<tiles:insert definition=".create">
<tiles:put name="body" type="string">
<script language="JavaScript" src="pages/application/fund/js/fundValidator.js"	type="text/javascript">
</script>
<html-el:form action="/fundAction.do?method=preview" focus="fundName">
<html-el:hidden property="method" value=""/>
<html-el:hidden property="input" value="create"/>
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
                      <td><img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorange">
                      <mifos:mifoslabel name="funds.fundInformation" bundle="fundUIResources"/>                      
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="73%" align="right">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td><img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17"></td>
                      <td class="timelineboldorangelight">
                      <mifos:mifoslabel name="funds.review" bundle="fundUIResources"/>                      
                       &amp;
                      <mifos:mifoslabel name="funds.sub" bundle="fundUIResources"/>                      
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
              				 <mifos:mifoslabel name="funds.enter_fund" bundle="fundUIResources"/>							
							</td>
						</tr>
						<tr>
							<td class="fontnormal">
								<mifos:mifoslabel name="funds.newfund_pageinstructions" bundle="fundUIResources"/>
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
								<mifos:mifoslabel name="funds.fund_name" mandatory="yes" bundle="fundUIResources"/>:								
							</td>
							<td width="73%" valign="top">
								<mifos:mifosalphanumtext property="fundName" style="width:136px;" maxlength ="100" />								
							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right" valign="top">
								<mifos:mifoslabel name="funds.fund_glcode" mandatory="yes" bundle="fundUIResources"/>:
							</td>
							<td valign="top">
								<mifos:select property="glCode.glCodeId" size="1" style="width:136px;">
									<html-el:options collection="glCodeList" property="glCodeId" labelProperty="glCodeValue"/>
								</mifos:select> 
							</td>
						</tr>
					</table>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center">							
								<html-el:submit style="width:65px;"	property="button" styleClass="buttn" onclick="javascript:fnValidateOnPreview();">
											<mifos:mifoslabel name="funds.preview" bundle="fundUIResources"/>
								</html-el:submit>&nbsp;
								<html-el:cancel style="width:65px;"	styleClass="cancelbuttn" onclick="javascript:fnCreateCancel();">
											<mifos:mifoslabel name="funds.cancel" bundle="fundUIResources"/>
								</html-el:cancel>										
									
							</td>
            			 </tr>
		            </table>            
	        <br>
		  </tr>
		</table>
	<br>
	
	</html-el:form>
	</tiles:put>
</tiles:insert>
