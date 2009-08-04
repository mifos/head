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
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<span id="page.id" title="CreateFeesConfirmation" />


<html-el:form action="/feeaction.do?method=get">
          <table width="95%" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td width="70%" align="left" valign="top" class="paddingL15T15">
              <table width="98%" border="0" cellspacing="0" cellpadding="3">
			      <tr>
                    <td class="headingorange">
                    <mifos:mifoslabel name="Fees.successmessage" bundle="FeesUIResources">
                    </mifos:mifoslabel>
                    <br>
                     <br>
                    </td>
                  </tr>
				  <tr>
                    <td class="fontnormalbold">
                    <span class="fontnormal">
						<mifos:mifoslabel name="Fees.clickOnViewOrDefineNewFees" bundle="FeesUIResources">
		                </mifos:mifoslabel>
					</span>

					<span class="fontnormal">
					</span>
					<span class="fontnormal"><font class="fontnormalRedBold"><html-el:errors bundle="FeesUIResources" /> </font>
                    <br>
                    <br>
                    </span>
					<html-el:link href="feeaction.do?method=get&feeId=${sessionScope.feeactionform.feeId}&randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="Fees.viewfeedetail" bundle="FeesUIResources"></mifos:mifoslabel>
					</html-el:link>
                    
                    <span class="fontnormal">
                    <br>
                    <br>
                    <span>
                    <span class="fontnormal">
                    <html-el:link href="feeaction.do?method=load&randomNUm=${sessionScope.randomNUm}">
                    <mifos:mifoslabel name="Fees.definenewfee" bundle="FeesUIResources">
                    </mifos:mifoslabel>
                    </html-el:link>
                    </span>
                    </td>
                  </tr>
                  <html-el:hidden property="feeId" value=""/>
				  
                  </table>
                  <br>
              </td>
            </tr>
          </table>
</html-el:form>
</tiles:put>
</tiles:insert>
