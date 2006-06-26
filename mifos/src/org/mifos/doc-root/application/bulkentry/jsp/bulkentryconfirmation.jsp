<!-- 

/**

 * bulkentryconfirmation.jsp    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/date" prefix="date"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<html-el:form action="/bulkentryaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="98%" border="0" cellspacing="0" cellpadding="3">
						<tr>
							<td class="headingorange"><mifos:mifoslabel
								name="bulkEntry.entersucc" /> <br>
							<br>
							</td>
						</tr>
						<tr>
							<td class="fontnormalbold"><mifos:mifoslabel
								name="bulkEntry.plsnote" />: <span class="fontnormal"> <mifos:mifoslabel
								name="bulkEntry.datafor" />&nbsp; <c:out
								value="${sessionScope.BulkEntry.bulkEntryParent.customerDetail.displayName}" />
							</span> <span class="fontnormal"><mifos:mifoslabel
								name="bulkEntry.hasenter" /><br>
							<br>
							<font class="fontnormalRedBold">
							<html-el:messages id="abc" bundle="bulkEntryUIResources"/>
							 <html-el:errors
								bundle="bulkEntryUIResources" /> </font> </span> <html-el:link
								href="bulkentryaction.do?method=load">
								<mifos:mifoslabel name="bulkEntry.entercollsheetdata" />
							</html-el:link> <span class="fontnormal"><br>
							<br>
							</span></td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			<br>
		</html-el:form>
	</tiles:put>
</tiles:insert>
