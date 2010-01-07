<%-- 
Copyright (c) 2005-2008 Grameen Foundation USA
1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
<%@ taglib uri="/sessionaccess" prefix="session"%>

<c:set value="${requestScope.orgSettings}" var="orgSettings"/>

<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<span id="page.id" title="vieworganizationsettings" />
<table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
	          <span class="fontnormal8pt">
	          	 <html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
					<mifos:mifoslabel name="VOS.labelLinkAdmin" bundle="viewOrganizationSettingsUIResources"/>	
				</html-el:link> /
	          </span><span class="fontnormal8ptbold">
          			<mifos:mifoslabel name="VOS.organizationalSettings" bundle="viewOrganizationSettingsUIResources"/>          			
          	   </span>
          </td>
        </tr>
      </table>
<table width="95%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td width="70%" align="left" valign="top" class="paddingL15T15">
			<table width="96%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td height="23" class="headingorange">
						<mifos:mifoslabel name="VOS.organizationalSettings" bundle="viewOrganizationSettingsUIResources" />
						<br/>
					</td>
                </tr>
                <tr>
					<td valign="top">
						<span class="fontnormalbold">
							<mifos:mifoslabel name="VOS.fiscalyear" bundle="viewOrganizationSettingsUIResources" />
							<span class="fontnormal">
								<br/>
								<mifos:mifoslabel name="VOS.workingDays" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.workingDays}"/>
								<br/>
								<mifos:mifoslabel name="VOS.allowCalDefForNextYear" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.allowCalDefForNextYear}"/>
								<mifos:mifoslabel name="VOS.daysBeforeYearEnd" bundle="viewOrganizationSettingsUIResources" />
								<br/>
								<mifos:mifoslabel name="VOS.startOfWeek" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.startOfWeek}"/>
								<br/>
								<mifos:mifoslabel name="VOS.offDays" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.offDays}"/>
								<br/>
								<mifos:mifoslabel name="VOS.holidayMeeting" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.holidayMeeting}"/>
								<br/>
								<br/>
							</span>
						</span>
						
						<span class="fontnormalbold">
							<mifos:mifoslabel name="VOS.locale" bundle="viewOrganizationSettingsUIResources" />
							<span class="fontnormal">
								<br/>
								<mifos:mifoslabel name="VOS.country" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.localeCountryCode}"/>
								<br/>
								<mifos:mifoslabel name="VOS.language" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.localeLanguageCode}"/>
								<br/>
								<br/>
							</span>
						</span>
						
						<span class="fontnormalbold">
							<mifos:mifoslabel name="VOS.accountingrules" bundle="viewOrganizationSettingsUIResources" />
							<span class="fontnormal">
								<br/>
								<mifos:mifoslabel name="VOS.maxInterest" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.maxInterest}"/>
								<br/>
								<mifos:mifoslabel name="VOS.minInterest" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.minInterest}"/>
								<br/>
								<mifos:mifoslabel name="VOS.digitsBeforeDecimal" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.digitsBeforeDecimal}"/>
								<br/>
								<mifos:mifoslabel name="VOS.intDigitsAfterDecimal" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.intDigitsAfterDecimal}"/>
								<br/>
								<mifos:mifoslabel name="VOS.intDigitsBeforeDecimal" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.intDigitsBeforeDecimal}"/>
								<br/>
								<mifos:mifoslabel name="VOS.interestDays" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.interestDays}"/>
								<br/>

								<mifos:mifoslabel name="VOS.currencyRoundingMode" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.currencyRoundingMode}"/>
								<br/>
								
								<mifos:mifoslabel name="VOS.initialRoundingMode" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.initialRoundingMode}"/>
								<br/>
								
								<mifos:mifoslabel name="VOS.finalRoundingMode" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.finalRoundingMode}"/>
								<br/>
								<br/>
							</span>
						</span>
						
						<span class="fontnormalbold">
							<mifos:mifoslabel name="VOS.currencies" bundle="viewOrganizationSettingsUIResources" />
							<span class="fontnormal">

								<c:forEach items="${orgSettings.currencies}" var="currency">
									<br/>
									<mifos:mifoslabel name="VOS.currency" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
									<c:out value="${currency.code}"/>
									<br/>
									<mifos:mifoslabel name="VOS.digitsAfterDecimal" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
									<c:out value="${currency.digitsAfterDecimal}"/>
									<br/>
									<mifos:mifoslabel name="VOS.finalRoundOffMultiple" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
									<c:out value="${currency.finalRoundOffMultiple}"/>
									<br/>
									<mifos:mifoslabel name="VOS.initialRoundOffMultiple" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
									<c:out value="${currency.initialRoundOffMultiple}"/>
									<br/>
								</c:forEach>
								<br/>
							</span>
						</span>
						
						<span class="fontnormalbold">
							<mifos:mifoslabel name="VOS.clientrules" bundle="viewOrganizationSettingsUIResources" />
							<span class="fontnormal">
								<br/>
								<mifos:mifoslabel name="VOS.centerHierarchyExists" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.centerHierarchyExists}"/>
								<br/>
								<mifos:mifoslabel name="VOS.loansForGroups" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.loansForGroups}"/>
								<br/>
								<mifos:mifoslabel name="VOS.clientsOutsideGroups" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.clientsOutsideGroups}"/>
								<br/>
								<mifos:mifoslabel name="VOS.nameSequence" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.nameSequence}"/>
								<br/>
								<mifos:mifoslabel name="VOS.isAgeCheckEnabled" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.isAgeCheckEnabled}"/>
								<br/>
								<mifos:mifoslabel name="VOS.minimumAge" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.minimumAge}"/>
								<br/>
								<mifos:mifoslabel name="VOS.maximumAge" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.maximumAge}"/>
								<br/>
								<mifos:mifoslabel name="VOS.isFamilyDetailsRequired" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.isFamilyDetailsRequired}"/>
								<br/>
								<mifos:mifoslabel name="VOS.maximumNumberOfFamilyMembers" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.maximumNumberOfFamilyMembers}"/>
								<br/>
								<br/>
							</span>
						</span>
						
						<span class="fontnormalbold">
							<mifos:mifoslabel name="VOS.processflow" bundle="viewOrganizationSettingsUIResources" />
							/
							<mifos:mifoslabel name="VOS.optionalstate" bundle="viewOrganizationSettingsUIResources" />
							<span class="fontnormal">
								<br/>
								<mifos:mifoslabel name="VOS.clientPendingState" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.clientPendingState}"/>
								<br/>
								<mifos:mifoslabel name="VOS.groupPendingState" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.groupPendingState}"/>
								<br/>
								<mifos:mifoslabel name="VOS.loanDisbursedState" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.loanDisbursedState}"/>
								<br/>
								<mifos:mifoslabel name="VOS.loanPendingState" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.loanPendingState}"/>
								<br/>
								<mifos:mifoslabel name="VOS.savingsPendingState" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/>
								<c:out value="${orgSettings.savingsPendingState}"/>
								<br/>
								<br/>
							</span>
						</span>
						
						<span class="fontnormalbold">
							<mifos:mifoslabel name="VOS.miscellaneous" bundle="viewOrganizationSettingsUIResources" />
							<span class="fontnormal">
								<br/>
								<mifos:mifoslabel name="VOS.sessionTimeout" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/>
								<c:out value="${orgSettings.sessionTimeout}"/>
								<mifos:mifoslabel name="VOS.minutes" bundle="viewOrganizationSettingsUIResources" />
								<br/>
								<mifos:mifoslabel name="VOS.collectionSheetAdvanceDays" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.collectionSheetAdvanceDays}"/>
								<br/>
								<mifos:mifoslabel name="VOS.backDatedTransactions" bundle="viewOrganizationSettingsUIResources" isColonRequired="Yes"/> 
								<c:out value="${orgSettings.backDatedTransactions}"/>
								<br/>
								<br/>
							</span>
						</span>
					</td>
                </tr>
            </table>              
            <br/>
        </td>            
    </tr>
</table>
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />    
</tiles:put>
</tiles:insert>
