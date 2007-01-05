<!--

/**

 * undoloandisbursalsearch.jsp    version: 1.0



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

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form action="/reverseloandisbaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"><html-el:link href="reverseloandisbaction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="loan.admin" />
							</html-el:link> / </span> <span class="fontnormal8ptbold"><mifos:mifoslabel name="loan.reverse" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.disbursal" /></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
						<table width="95%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="63%" class="headingorange">
									<span class="headingorange"><mifos:mifoslabel name="loan.reverse" /> <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel name="loan.disbursal" /></span>
								</td>
							</tr>
						</table>
						<br>
						<font class="fontnormalRedBold"> <html-el:errors bundle="loanUIResources" /> </font>
						<table width="95%" border="0" cellpadding="2" cellspacing="0">
							<tr class="fontnormal">
								<td colspan="2">
									<mifos:mifoslabel name="loan.search" />
									<mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />
									<mifos:mifoslabel name="loan.account" />
									<mifos:mifoslabel name="loan.byid" />
								</td>
							</tr>
							<tr class="fontnormal8pt">
								<td width="17%">
									<mifos:mifosalphanumtext property="searchString" />
								</td>
								<td width="83%">
									&nbsp;
									<html-el:submit property="continueButton" styleClass="buttn" style="width:70px;">
										<mifos:mifoslabel name="loan.search" />
									</html-el:submit>
								</td>
							</tr>
							<tr class="fontnormal8pt">
								<td>
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			<br>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="method" value="load" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
