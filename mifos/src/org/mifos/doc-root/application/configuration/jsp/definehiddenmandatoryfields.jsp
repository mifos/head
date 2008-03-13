<!--

/**

 * definehiddenmandatoryfields.jsp    version: 1.0



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

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form action="/hiddenmandatoryconfigurationaction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"><html-el:link href="hiddenmandatoryconfigurationaction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="configuration.admin" />
							</html-el:link> / </span><span class="fontnormal8ptbold"><mifos:mifoslabel name="configuration.define" /> <mifos:mifoslabel name="configuration.hiddenmandfields" /></span>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" align="left" valign="top" class="paddingL15T15">
						<table width="98%" border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td width="35%" class="headingorange">
									<mifos:mifoslabel name="configuration.define" />
									<mifos:mifoslabel name="configuration.hiddenmandfields" />
								</td>
							</tr>
						</table>
						<br>
						<span class="fontnormalbold"></span>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="24%" class="drawtablehd">
									<mifos:mifoslabel name="configuration.syswidefields" />
								</td>
								<td width="28%" class="drawtablehd">
									<mifos:mifoslabel name="configuration.hide" />
								</td>
								<td width="28%" class="drawtablehd">
									<mifos:mifoslabel name="configuration.mandatory" />
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.EXTERNALID}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemExternalId" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatorySystemExternalId" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.ETHINICITY}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemEthnicity" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatorySystemEthnicity" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.CITIZENSHIP}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemCitizenShip" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatorySystemCitizenShip" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.HANDICAPPED}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemHandicapped" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatorySystemHandicapped" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.educationlevel" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemEducationLevel" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatorySystemEducationLevel" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.photo" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemPhoto" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatorySystemPhoto" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.assigning" />
									<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />s
									<mifos:mifoslabel name="configuration.topositions" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemAssignClientPostions" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.ADDRESS1}" />
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatorySystemAddress1" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.ADDRESS3}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemAddress3" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.CITY}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemCity" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.STATE}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemState" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.country" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemCountry" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.POSTAL_CODE}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemPostalCode" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.receiptiddate" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemReceiptIdDate" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.collateraltypenotes" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideSystemCollateralTypeNotes" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
						</table>
						<span class="heading"></span>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="24%" class="drawtablehd">
									<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}" />
									/
									<mifos:mifoslabel name="configuration.systemuserfields" />
								</td>
								<td width="28%" class="drawtablehd">
									<mifos:mifoslabel name="configuration.hide" />
								</td>
								<td width="28%" class="drawtablehd">
									<mifos:mifoslabel name="configuration.mandatory" />
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.middlename" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideClientMiddleName" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.secondlastname" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideClientSecondLastName" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatoryClientSecondLastName" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.GOVERNMENT_ID}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideClientGovtId" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatoryClientGovtId" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.spousefathermiddlename" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideClientSpouseFatherMiddleName" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.spousefathersecondlastname" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideClientSpouseFatherSecondLastName" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatoryClientSpouseFatherSecondLastName" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.phonenumber" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideClientPhone" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatoryClientPhone" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.trained" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideClientTrained" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatoryClientTrained" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.trainedon" />
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatoryClientTrainedOn" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.Businessworkactivities" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideClientBusinessWorkActivities" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
						</table>
						<span class="heading"></span>
						<table width="96%" border="0" cellpadding="3" cellspacing="0">
							<tr>
								<td width="24%" class="drawtablehd">
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />s
									<mifos:mifoslabel name="configuration.fields" />
								</td>
								<td width="28%" class="drawtablehd">
									<mifos:mifoslabel name="configuration.hide" />
								</td>
								<td width="28%" class="drawtablehd">
									<mifos:mifoslabel name="configuration.mandatory" />
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.ADDRESS1}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideGroupAddress1" value="1"/>
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="mandatoryGroupAddress1" value="1"/>
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.ADDRESS2}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideGroupAddress2" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="${ConfigurationConstants.ADDRESS3}" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideGroupAddress3" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									<mifos:mifoslabel name="configuration.trained" />
								</td>
								<td class="drawtablerow">
									<html-el:checkbox property="hideGroupTrained" value="1"/>
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
							<tr>
								<td class="drawtablerow">
									&nbsp;
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
								<td class="drawtablerow">
									&nbsp;
								</td>
							</tr>
						</table>
						<table width="96%" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center">
									<html-el:submit property="submitButton" styleClass="buttn" style="width:70px;">
										<mifos:mifoslabel name="configuration.submit" />
									</html-el:submit>
									&nbsp;

									<html-el:button property="cancelButton" onclick="location.href='hiddenmandatoryconfigurationaction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}'" styleClass="cancelbuttn" style="width:70px;">
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
