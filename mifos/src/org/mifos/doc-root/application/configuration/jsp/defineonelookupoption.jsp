<!--

/**

 * defineonelookupoptions.jsp    version: 1.0



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

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<script language="javascript">
function goToLookupOptionPage(){
	lookupoptionsactionform.action="lookupOptionsAction.do";
	lookupoptionsactionform.method.value="update";
	lookupoptionsactionform.submit();
  }
  </script>
<tiles:insert definition=".view">
	<tiles:put name="body" type="string">

		<html-el:form action="lookupOptionsAction.do" onsubmit="func_disableSubmitBtn('submitButton')" >
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt">
							<html-el:link href="lookupOptionsAction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="configuration.admin" />
							</html-el:link> / 
							<html-el:link href="lookupOptionsAction.do?method=addEditLookupOption_cancel&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="configuration.define" />  <mifos:mifoslabel name="configuration.lookupoptions" />
							</html-el:link> / 
							
						</span><span class="fontnormal8ptbold"><mifos:mifoslabel name="configuration.add_edit" /> <mifos:mifoslabel name="configuration.lookupoptions" /></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="98%" border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td width="35%" class="headingorange">
									<mifos:mifoslabel name="configuration.add_edit" />
									<mifos:mifoslabel name="configuration.lookupoptions" />
								</td>
							</tr>
						</table>
						<br>
						<font class="fontnormalRedBold"><html-el:errors bundle="configurationUIResources" /> </font>
		                     <table width="93%" border="0" cellpadding="3" cellspacing="0">
		                     <tr class="fontnormal">
				                <td align="right" valign="top">
				                   <mifos:mifoslabel name="configuration.lookupvalue" isColonRequired="Yes"  />
				                </td>
				                <td width="28%" valign="top">
			                        <mifos:mifosalphanumtext property="lookupValue" maxlength="300"/>
			                        </td>
		                     </table>
		                     <br>
						<table width="98%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									<html-el:button property="submitButton" styleClass="buttn" style="width:70px;" onclick="goToLookupOptionPage();">
										<mifos:mifoslabel name="configuration.submit" />
									</html-el:button>
									&nbsp;

									<html-el:button property="cancelButton" onclick="location.href='lookupOptionsAction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}'" styleClass="cancelbuttn" style="width:70px;">
										<mifos:mifoslabel name="configuration.cancel" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
              </tr>
			</table>
			<html-el:hidden property="method" value="update" />
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
			
		</html-el:form>
	</tiles:put>
</tiles:insert>

