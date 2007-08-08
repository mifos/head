<!--

/**

 * definelookupoptions.jsp    version: 1.0



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

		<script language="javascript" type="text/javascript">



  
  function setLookupOptionData(entity, addoredit, entityType, op){
	lookupoptionsactionform.action="lookupOptionsAction.do";
	lookupoptionsactionform.method.value="addEditLookupOption";
	entity.value = entityType;
	addoredit.value = op;
	lookupoptionsactionform.submit;
  }
  
  </script>

		<html-el:form action="/lookupOptionsAction">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"><html-el:link
								href="lookupOptionsAction.do?method=cancel&amp;currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="configuration.admin" />
							</html-el:link> / </span><span class="fontnormal8ptbold"><mifos:mifoslabel
								name="configuration.define" /> <mifos:mifoslabel
								name="configuration.lookupoptions" />
						</span>
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
									<mifos:mifoslabel name="configuration.lookupoptions" />
								</td>
							</tr>
						</table>
						<br>
						<font class="fontnormalRedBold"><html-el:errors
								bundle="configurationUIResources" /> </font>
						<table border="0" cellpadding="5" cellspacing="0" width="93%">
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"> <mifos:mifoslabel
											name="configuration.salutation" isColonRequired="Yes" />
									</span>
								</td>
								<td valign="top">
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="salutationList" property2="Salutations" size="5"
													style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddSalutation"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigSalutation.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br />
												<html-el:submit property="btnEditSalutation"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit,this.form.ConfigSalutation.value,'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.usertitle"
										isColonRequired="Yes" />
								</td>
								<td width="82%">
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="userTitleList" property2="UserTitles" size="5"
													style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddUserTitle"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigUserTitle.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditUserTitle"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigUserTitle.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.maritalstatus"
										isColonRequired="Yes" />
								</td>
								<td>
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="maritalStatusList" property2="MaritalStatuses"
													size="5" style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddMaritalStatus"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigMaritalStatus.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditMaritalStatus"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigMaritalStatus.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td valign="top">
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="${ConfigurationConstants.ETHINICITY}"
										isColonRequired="Yes" />
								</td>
								<td valign="top">
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="ethnicityList" property2="Ethnicities" size="5"
													style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddEthnicity"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigEthnicity.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditEthnicity"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigEthnicity.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.educationlevel"
										isColonRequired="Yes" />
								</td>
								<td width="82%">
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="educationLevelList" property2="EducationLevels"
													size="5" style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddEducationLevel"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigEducationLevel.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditEducationLevel"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigEducationLevel.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="${ConfigurationConstants.CITIZENSHIP}"
										isColonRequired="Yes" />
								</td>
								<td>
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="citizenshipList" property2="Citizenships"
													size="5" style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddCitizenship"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigCitizenship.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditCitizenship"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigCitizenship.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td valign="top">
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.purposeofloan"
										isColonRequired="Yes" />
								</td>
								<td>
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="purposesOfLoanList" property2="PurposesOfLoan"
													size="5" style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddPurposeOfLoan"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigPurposeOfLoan.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditPurposeOfLoan"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigPurposeOfLoan.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="${ConfigurationConstants.HANDICAPPED}"
										isColonRequired="Yes" />
								</td>
								<td width="82%">
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="handicappedList" property2="Handicappeds"
													size="5" style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddHandicapped"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigHandicapped.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditHandicapped"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigHandicapped.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<mifos:mifoslabel name="configuration.collateraltype"
										isColonRequired="Yes" />
								</td>
								<td>
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="collateralTypeList" property2="CollateralTypes"
													size="5" style="width:136px;">
												</mifos:MifosValueList>
											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddCollateralType"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigCollateralType.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditCollateralType"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigCollateralType.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<span class="mandatorytext"></span>
									<mifos:mifoslabel name="configuration.officertitle"
										isColonRequired="Yes" />
								</td>
								<td>
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="officerTitleList" property2="OfficerTitles"
													size="5" style="width:136px;">
												</mifos:MifosValueList>
												<br />

											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddOfficerTitle"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigOfficerTitle.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditOfficerTitle"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigOfficerTitle.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top">
									&nbsp;
								</td>
								<td>
									&nbsp;
								</td>
							</tr>
							<tr class="fontnormal">
								<td align="right" valign="top" width="18%">
									<mifos:mifoslabel name="configuration.attendance"
										isColonRequired="Yes" />
								</td>
								<td>
									<table border="0" cellpadding="0" cellspacing="0" width="81%">
										<tr>
											<td width="15%">
												<mifos:MifosValueList name="lookupoptionsactionform"
													property="attendanceList" property2="Attendances" size="5"
													style="width:136px;">
												</mifos:MifosValueList>
												<br />

											</td>
											<td align="left" valign="top" width="31%">
												<br />
												<html-el:submit property="btnAddAttendance"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigAttendance.value, 'add');">
													<mifos:mifoslabel name="configuration.add" />
												</html-el:submit>
												<br>
												<html-el:submit property="btnEditAttendance"
													styleClass="insidebuttn" style="width:65px"
													onclick="setLookupOptionData(this.form.entity, this.form.addOrEdit, this.form.ConfigAttendance.value, 'edit');">
													<mifos:mifoslabel name="configuration.edit" />
												</html-el:submit>
											</td>
										</tr>
									</table>
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

								</td>
							</tr>
						</table>
						<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="currentFlowKey"
				value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="entity" value="${requestScope.entity}" />
			<html-el:hidden property="addOrEdit"
				value="${requestScope.addOrEdit}" />
			<html-el:hidden property="method" value="addEditLookupOption" />
			<html-el:hidden property="ConfigSalutation"
				value="${requestScope.ConfigSalutation}" />
			<html-el:hidden property="ConfigMaritalStatus"
				value="${requestScope.ConfigMaritalStatus}" />
			<html-el:hidden property="ConfigUserTitle"
				value="${requestScope.ConfigUserTitle}" />
			<html-el:hidden property="ConfigEducationLevel"
				value="${requestScope.ConfigEducationLevel}" />
			<html-el:hidden property="ConfigCitizenship"
				value="${requestScope.ConfigCitizenship}" />
			<html-el:hidden property="ConfigHandicapped"
				value="${requestScope.ConfigHandicapped}" />
			<html-el:hidden property="ConfigAttendance"
				value="${requestScope.ConfigAttendance}" />
			<html-el:hidden property="ConfigOfficerTitle"
				value="${requestScope.ConfigOfficerTitle}" />
			<html-el:hidden property="ConfigPurposeOfLoan"
				value="${requestScope.ConfigPurposeOfLoan}" />
			<html-el:hidden property="ConfigCollateralType"
				value="${requestScope.ConfigCollateralType}" />
			<html-el:hidden property="ConfigEthnicity"
				value="${requestScope.ConfigEthnicity}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>

