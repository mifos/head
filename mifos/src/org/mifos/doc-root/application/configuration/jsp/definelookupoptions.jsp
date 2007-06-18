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

<script language="javascript">


function addAValueToListbox(theSel, textBox)
{
  
   var length = theSel.length;
   if (textBox.value == '')
   {
       textBox.focus();
       return;
   }
   for (i=0; i < length; i++)
   {
      if (theSel.options[i].text == textBox.value)
      {
          return;
      }
   }
   
   if (textBox.property == null)
   {
        var value = '0;' + textBox.value + ';add';
   		var newOpt1 = new Option(textBox.value, value);
   		theSel.options[length] = newOpt1;
   	}
   	else
   	{
   	    for (i=0; i < length; i++)
        {
             if (theSel.options[i].value == textBox.property)
             {
                  var oldValue = theSel.options[i].value;
                  var splitResult = oldValue.split(';');
                  var newValue = null;
                  var flag = splitResult[2];
                  if (flag == 'original')
                  		newValue = splitResult[0] + ';' + textBox.value + ";update";
                  else
                        newValue = splitResult[0] + ';' + textBox.value + ';' + splitResult[2];
                  theSel.options[i].value = newValue;
                  theSel.options[i].text = textBox.value;
             }
        }
   	}
   
   textBox.value = '';
   textBox.property = null;
}

function removeAValueFromListbox(theSelFrom, textBox)
{
	var selIndex = theSelFrom.selectedIndex;
	if (selIndex == -1)
	{
		return;
	}
	var selLength = theSelFrom.length;
	for(i=selLength-1; i>=0; i--)
	{
		if(theSelFrom.options[i].selected)
		{
			textBox.value = theSelFrom.options[i].text;
			textBox.property = theSelFrom.options[i].value; 
		}
	}
	   
} 

function transferData(salutationList, userTitleList, maritalStatusList, ethnicityList, educationLevelList, citizenshipList, 
	purposesOfLoanList, handicappedList, collateralTypeList, officerTitleList, attendanceList )
{
	transferOneListData(salutationList);
	transferOneListData(userTitleList);
	transferOneListData(maritalStatusList);
	transferOneListData(ethnicityList);
	transferOneListData(educationLevelList);
	transferOneListData(citizenshipList);
	transferOneListData(purposesOfLoanList);
	transferOneListData(handicappedList);
	transferOneListData(collateralTypeList);
	transferOneListData(officerTitleList);
	transferOneListData(attendanceList);
 	
}

function transferOneListData(outSel)
{

 	 var selLength =outSel.length;
     outSel.multiple = true;
	 for(i=0; i < selLength; i++)
	 {
	    var value = outSel.options[i].value;
		var splitResult = value.split(';');
		if (splitResult[2] != 'original')
			outSel.options[i].selected = true;
		else
		    outSel.options[i].selected = false;
	 }
	
}				

  
  </script>
  
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
                        <mifos:mifosalphanumtext property="salutation" maxlength="300"/>
                        </td>
                        <td width="31%" align="center" valign="top">
                        <html-el:button property="btnAddSalutation" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.salutationList, this.form.salutation);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditSalutation" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.salutationList, this.form.salutation);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                          </td>
                          <td width="41%">
							<mifos:MifosValueList name="lookupoptionsactionform" property="salutationList" property2="Salutations" size="5" style="width:136px;"  >
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
                    <mifos:mifosalphanumtext property="userTitle" maxlength="300"/>
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddUserTitle" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.userTitleList, this.form.userTitle);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditUserTitle" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.userTitleList, this.form.userTitle);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="userTitleList" property2="UserTitles"  size="5" style="width:136px;"  >
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
                    <mifos:mifosalphanumtext property="maritalStatus" maxlength="300"/>
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddMaritalStatus" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.maritalStatusList, this.form.maritalStatus);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditMaritalStatus" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.maritalStatusList, this.form.maritalStatus);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="maritalStatusList" property2="MaritalStatuses"  size="5" style="width:136px;"  >
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
                        <mifos:mifosalphanumtext property="ethnicity" maxlength="300"/>
                        </td>
                        <td width="31%" align="center" valign="top">
                        <html-el:button property="btnAddEthnicity" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.ethnicityList, this.form.ethnicity);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditEthnicity" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.ethnicityList, this.form.ethnicity);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                          </td>
                        <td width="41%">
                        <mifos:MifosValueList name="lookupoptionsactionform" property="ethnicityList" property2="Ethnicities"  size="5" style="width:136px;"  >
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
                    <mifos:mifosalphanumtext property="educationLevel" maxlength="300"/>
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddEducationLevel" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.educationLevelList, this.form.educationLevel);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditEducationLevel" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.educationLevelList, this.form.educationLevel);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="educationLevelList" property2="EducationLevels" size="5" style="width:136px;"  >
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
                    <mifos:mifosalphanumtext property="citizenship" maxlength="300"/>
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddCitizenship" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.citizenshipList, this.form.citizenship);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditCitizenship" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.citizenshipList, this.form.citizenship);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="citizenshipList" property2="Citizenships"  size="5" style="width:136px;"  >
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
                        <mifos:mifosalphanumtext property="purposeOfLoan" maxlength="300"/>
                        </td>
                        <td width="31%" align="center" valign="top">
                        <html-el:button property="btnAddPurposeOfLoan" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.purposesOfLoanList, this.form.purposeOfLoan);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditPurposeOfLoan" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.purposesOfLoanList, this.form.purposeOfLoan);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                          </td>
                        <td width="41%">
                        <mifos:MifosValueList name="lookupoptionsactionform" property="purposesOfLoanList" property2="PurposesOfLoan" size="5" style="width:136px;"  >
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
                    <mifos:mifosalphanumtext property="handicapped" maxlength="300"/>
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddHandicapped" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.handicappedList, this.form.handicapped);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditHandicapped" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.handicappedList, this.form.handicapped);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="handicappedList" property2="Handicappeds" size="5" style="width:136px;"  >
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
                    <mifos:mifosalphanumtext property="collateralType" maxlength="300"/>
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddCollateralType" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.collateralTypeList, this.form.collateralType);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditCollateralType" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.collateralTypeList, this.form.collateralType);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="collateralTypeList" property2="CollateralTypes" size="5" style="width:136px;"  >
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
                    <mifos:mifosalphanumtext property="officerTitle" maxlength="300"/>
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddOfficerTitle" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.officerTitleList, this.form.officerTitle);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditOfficerTitle" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.officerTitleList, this.form.officerTitle);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="officerTitleList" property2="OfficerTitles" size="5" style="width:136px;"  >
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
                    <mifos:mifosalphanumtext property="attendance" maxlength="300" />
                    </td>
                    <td width="31%" align="center" valign="top">
                    <html-el:button property="btnAddAttendance" styleClass="insidebuttn" style="width:65px" onclick="addAValueToListbox(this.form.attendanceList, this.form.attendance);">
							<mifos:mifoslabel name="configuration.add" />
						</html-el:button>
                          <br>
                          <html-el:button property="btnEditAttendance" styleClass="insidebuttn" style="width:65px" onclick="removeAValueFromListbox(this.form.attendanceList, this.form.attendance);">
							<mifos:mifoslabel name="configuration.edit" />
						</html-el:button>
                    </td>
                    <td width="41%">
                    <mifos:MifosValueList name="lookupoptionsactionform" property="attendanceList" property2="Attendances" size="5" style="width:136px;"  >
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
									<html-el:submit property="submitButton" styleClass="buttn" style="width:70px;" onclick="transferData(this.form.salutationList, this.form.userTitleList, this.form.maritalStatusList, this.form.ethnicityList, this.form.educationLevelList, this.form.citizenshipList, this.form.purposesOfLoanList, this.form.handicappedList, this.form.collateralTypeList, this.form.officerTitleList, this.form.attendanceList );">
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

