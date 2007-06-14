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
		<html-el:form action="/lookupOptionsAction" >
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05">
						<span class="fontnormal8pt"><html-el:link href="lookupOptionsAction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}">
								<mifos:mifoslabel name="configuration.admin" />
							</html-el:link> / </span><span class="fontnormal8ptbold"><mifos:mifoslabel name="configuration.define" /> <mifos:mifoslabel name="configuration.lookupoptions" /></span>
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
						<font class="fontnormalRedBold"><html-el:errors bundle="configurationUIResources" /> </font>
		                     <table width="93%" border="0" cellpadding="3" cellspacing="0">
              <tr class="fontnormal">
                <td align="right" valign="top">
                   <mifos:mifoslabel name="configuration.salutation" isColonRequired="Yes"  />
                </td>
                <td valign="top"><table width="86%" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td width="28%" valign="top">
                        <mifos:mifosalphanumtext property="salutation" />
                        </td>
                        <td width="31%" align="center" valign="top">
                        <html-el:button property="btnAddSalutation" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditSalutation" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                          </td>
                          <td width="41%">
							<mifos:MifosValueList name="lookupoptionsactionform" property="Salutations" property2="saluationListBox" size="5" style="width:136px;"  >
							</mifos:MifosValueList>    
						</td> 
                      </tr>
                    </table>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr class="fontnormal">
                <td width="18%" align="right" valign="top"><span class="mandatorytext">
					<mifos:mifoslabel name="configuration.usertitle" isColonRequired="Yes"  />
                </td>
                <td width="82%"><table width="86%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="28%" valign="top">
                    <mifos:mifosalphanumtext property="userTitle" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddUserTitle" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditUserTitle" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="UserTitles" property2="userTitleListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                    </td>
                  </tr>
                </table>
</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top"><span class="mandatorytext">
					<mifos:mifoslabel name="configuration.maritalstatus" isColonRequired="Yes" />
                </td>
                <td><table width="86%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="28%" valign="top">
                    <mifos:mifosalphanumtext property="maritalStatus" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddMaritalStatus" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditMaritalStatus" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="MaritalStatuses" property2="maritalStatusListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                    </td>
                  </tr>
                </table>
</td>
              </tr>
			  <tr class="fontnormal">
			    <td align="right" valign="top">&nbsp;</td>
			    <td valign="top">&nbsp;</td>
			    </tr>
			  <tr class="fontnormal">
                <td align="right" valign="top">
					<mifos:mifoslabel name="configuration.ethnicity" isColonRequired="Yes"  />
                </td>
                <td valign="top"><table width="86%" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td width="28%" valign="top">
                        <mifos:mifosalphanumtext property="ethnicity" />
                        </td>
                        <td width="31%" align="center" valign="top">
                        <html-el:button property="btnAddEthnicity" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditEthnicity" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                          </td>
                        <td width="41%">
                        <mifos:MifosValueList name="lookupoptionsactionform" property="Ethnicities" property2="ethnicityListBox" size="5" style="width:136px;"  >
							</mifos:MifosValueList> 
                        </td>
                      </tr>
                    </table>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr class="fontnormal">
                <td width="18%" align="right" valign="top"><span class="mandatorytext"></span>
                <mifos:mifoslabel name="configuration.educationlevel" isColonRequired="Yes" />
                </td>
                <td width="82%"><table width="86%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="28%" valign="top">
                    <mifos:mifosalphanumtext property="educationLevel" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddEducationLevel" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditEducationLevel" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="EducationLevels" property2="educationLevelListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                    </td>
                  </tr>
                </table>
</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top"><span class="mandatorytext"></span>
					<mifos:mifoslabel name="configuration.citizenship" isColonRequired="Yes" />
                </td>
                <td><table width="86%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="28%" valign="top">
                    <mifos:mifosalphanumtext property="citizenship" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddCitizenship" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditCitizenship" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="Citizenships" property2="citizenshipListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                    </td>
                  </tr>
                </table>
</td>
              </tr>
			  <tr class="fontnormal">
			    <td align="right" valign="top">&nbsp;</td>
			    <td valign="top">&nbsp;</td>
			    </tr>
			  <tr class="fontnormal">
                <td align="right" valign="top">
                <mifos:mifoslabel name="configuration.purposeofloan" isColonRequired="Yes"  />
                </td>
                <td valign="top"><table width="86%" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td width="28%" valign="top">
                        <mifos:mifosalphanumtext property="purposeOfLoan" />
                        </td>
                        <td width="31%" align="center" valign="top">
                        <html-el:button property="btnAddPurposeOfLoan" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditPurposeOfLoan" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                          </td>
                        <td width="41%">
                        <mifos:MifosValueList name="lookupoptionsactionform" property="PurposesOfLoan" property2="purposesOfLoanListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                        </td>
                      </tr>
                    </table>
                </td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr class="fontnormal">
                <td width="18%" align="right" valign="top"><span class="mandatorytext"></span>
                <mifos:mifoslabel name="configuration.handicapped" isColonRequired="Yes"  />
                </td>
                <td width="82%"><table width="86%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="28%" valign="top">
                    <mifos:mifosalphanumtext property="handicapped" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddHandicapped" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditHandicapped" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="Handicappeds" property2="handicappedListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                    </td>
                  </tr>
                </table>
</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">
					<mifos:mifoslabel name="configuration.collateraltype" isColonRequired="Yes"  />
                </td>
                <td><table width="86%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="28%" valign="top">
                    <mifos:mifosalphanumtext property="collateralType" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddCollateralType" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditCollateralType" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="CollateralTypes" property2="collateralTypeListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                    </td>
                  </tr>
                </table></td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top"><span class="mandatorytext"></span>
                <mifos:mifoslabel name="configuration.officertitle" isColonRequired="Yes"  />
                  </td>
                <td><table width="86%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="28%" valign="top">
                    <mifos:mifosalphanumtext property="officerTitle" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddOfficerTitle" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditOfficerTitle" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="OfficerTitles" property2="officerTitleListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                    </td>
                  </tr>
                </table>
</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">&nbsp;</td>
                <td>&nbsp;</td>
              </tr>
              <tr class="fontnormal">
                <td align="right" valign="top">
                <mifos:mifoslabel name="configuration.attendance" isColonRequired="Yes"  />
                </td>
                <td><table width="86%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td width="28%" valign="top">
                    <mifos:mifosalphanumtext property="attendance" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddAttendance" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditAttendance" styleClass="insidebuttn" style="width:65px" onclick="">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="Attendances" property2="attendanceListBox" size="5" style="width:136px;"  >
					</mifos:MifosValueList> 
                    </td>
                  </tr>
                </table></td>
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
									<html-el:submit property="submitButton" styleClass="buttn" style="width:70px;">
										<mifos:mifoslabel name="configuration.submit" />
									</html-el:submit>
									&nbsp;

									<html-el:button property="cancelButton" onclick="location.href='lookupOptionsAction.do?method=cancel&currentFlowKey=${requestScope.currentFlowKey}'" styleClass="cancelbuttn" style="width:70px;">
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

