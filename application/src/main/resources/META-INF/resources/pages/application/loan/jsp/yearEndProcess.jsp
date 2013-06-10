<%--
Copyright (c) 2005-2011 Grameen Foundation USA
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


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<head>

<style>
tr.even {
  background-color: #ddd;
}
tr.odd {
  background-color: #eee;
}

div.scroll {
height: 80%;
width: 100%;
overflow: auto;
float:left;
padding: 0px;
position:relative;
}
table.pkgtable td
{
border-top: 1px dotted #6699CC;
}
table.pkgtable tr.pkgtableheaderrow
{
background-color: #BEC8D1;
text-align: center;
font-family: Verdana;
font-weight: bold;
font-size: 13px;
}

table.burlywoodborder {
border-right: solid 1px #EAEBF4;
border-left : solid 1px #EAEBF4;
border-top : solid 1px #EAEBF4;
border-bottom : solid 1px #EAEBF4;
}
</style>
</head>
<tiles:insert definition=".financialAccountingLayout">
<tiles:put name="body" type="string" >
<script type="text/javascript" src="pages/js/separator.js"></script>


<span id="page.id" title="simpleaccounting"></span>
<mifos:NumberFormattingInfo /> 
<fmt:setLocale value='${sessionScope["org.apache.struts.action.LOCALE"]}'/>
<fmt:setBundle basename="org.mifos.config.localizedResources.SimpleAccountingUIResources"/>
<br><br><br>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">

							<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
									<tr>
										<td align="left" class="headingorange">
											&nbsp;
											<c:choose>
											<c:when test="${ValidateYearEndProcess == true}">
											<mifos:mifoslabel name="simpleAccounting.yearEndProcessSuccess" bundle="simpleAccountingUIResources" />
											</c:when>
											<c:otherwise>
											<mifos:mifoslabel name="simpleAccounting.yearEndProcessFail" bundle="simpleAccountingUIResources" /> ${OldFinancialYearEndDate}
											</c:otherwise>
											</c:choose>
										</td>
									</tr>
									<tr>
										<td align="left" class="headingorange">
											&nbsp;
										  <html-el:link styleId="bulkentryconfirmation.link.enterCollSheetData" href="FinancialAccountingAction.do?method=load">
									           <mifos:mifoslabel name="simpleAccounting.clickToFinancialAccountingPage" bundle="simpleAccountingUIResources"/>
								          </html-el:link>
										</td>
									</tr>
							</table>
			<br>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>

	</tiles:put>
</tiles:insert>
	
