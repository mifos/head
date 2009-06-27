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

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/date" prefix="date"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form action="/labelconfigurationaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"><html-el:link href="labelconfigurationaction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="configuration.admin" />
							</html-el:link> / </span><span class="fontnormal8ptbold"><mifos:mifoslabel name="configuration.definelabels" /></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="98%" border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td width="35%" class="headingorange">
									<mifos:mifoslabel name="configuration.definelabels" />
								</td>
							</tr>
						</table>
						<br>
						<font class="fontnormalRedBold"><html-el:errors bundle="configurationUIResources" /> </font>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.officehier" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.headoffice" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="headOffice" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.regoffice" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="regionalOffice" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.subregoffice" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="subRegionalOffice" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.areaoffice" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="areaOffice" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.branchoffice" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="branchOffice" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.clients" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.client" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="client" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.group" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="group" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.center" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="center" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.paymentmodes" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.cash" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="cash" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.check" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="check" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.vouchers" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="vouchers" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.prdtypes" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.loans" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="loans" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.savings" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="savings" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.personalinfo" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<mifos:mifoslabel name="configuration.state" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="state" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.postalcode" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="postalCode" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.ethnicity" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="ethnicity" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.citizenship" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="citizenship" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.handicapped" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="handicapped" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.govtId" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="govtId" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.address" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.address1" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="address1" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.address2" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="address2" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.address3" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="address3" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.statuses" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.partialappl" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="partialApplication" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.pendingappr" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="pendingApproval" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.approved" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="approved" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.cancel" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="cancel" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.closed" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="closed" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.onhold" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="onhold" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.active" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="active" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.inactive" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="inActive" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.activegoodstand" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="activeInGoodStanding" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.activebadstand" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="activeInBadStanding" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.closeobligmet" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="closedObligationMet" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.closeresch" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="closedRescheduled" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.closewriteoff" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="closedWrittenOff" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.gracetypes" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.none" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="none" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.graceallrepay" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="graceOnAllRepayments" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.pringrace" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="principalOnlyGrace" />
								</td>
							</tr>
						</table>
						<br>
						<table width="93%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td colspan="2" class="fontnormalbold">
									<mifos:mifoslabel name="configuration.misc" />
									<br>
									<br>
								</td>
							</tr>
							<tr class="fontnormal">
								<td width="30%" align="right">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.interest" isColonRequired="Yes" />
								</td>
								<td width="70%" valign="top">
									<mifos:mifosalphanumtext property="interest" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.externalId" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="externalId" />
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right">
									<mifos:mifoslabel name="configuration.bulkentry" isColonRequired="Yes" />
								</td>
								<td valign="top">
									<mifos:mifosalphanumtext property="bulkEntry" />
								</td>
							</tr>
						</table>
						<table width="98%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td class="blueline">
									&nbsp;
								</td>
							</tr>
						</table>
						<br>
						<table width="98%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									<html-el:submit property="submitButton" styleClass="buttn">
										<mifos:mifoslabel name="configuration.submit" />
									</html-el:submit>
									&nbsp;

									<html-el:button property="cancelButton" onclick="location.href='labelconfigurationaction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}'" styleClass="cancelbuttn">
										<mifos:mifoslabel name="configuration.cancel" />
									</html-el:button>
								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="method" value="update" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
