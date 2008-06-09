<%--
Copyright (c) 2005-2008 Grameen Foundation USA
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
<!-- updatemeeting.jsp -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">


		<script>
function showMeetingFrequency(){

	var fraq = document.getElementsByName("frequency");
	if (document.meetingActionForm.frequency[0].checked == true){
		document.getElementById("weekDIV").style.display = "block";
		fraq[1].disabled=true;
		document.getElementById("months").style.color="gray";
		document.getElementById("monthDIV").style.display = "none";
		document.getElementsByName("recurWeek")[0].disabled=true;
		}
	else if (document.meetingActionForm.frequency[1].checked == true){
	
		fraq[0].disabled=true;
				document.getElementById("weeks").style.color="gray";
		
		document.getElementById("weekDIV").style.display = "none";
		document.getElementById("monthDIV").style.display = "block";
		
			//get the month type
			if(document.getElementsByName("monthType")[0].checked == true)
			{
				document.getElementsByName("monthType")[1].disabled=true;
				document.getElementsByName("monthRank")[0].disabled=true;
				document.getElementsByName("monthWeek")[0].disabled=true;
				document.getElementsByName("recurMonth")[0].disabled=true;
				document.getElementsByName("dayRecurMonth")[0].disabled=true;
				document.getElementById("labelThe").style.color="gray";
				document.getElementById("labelOfEvery_1").style.color="gray";
				document.getElementById("labelMonths_2").style.color="gray";
				
				
			}
			else if ( document.getElementsByName("monthType")[1].checked == true)
			{
				document.getElementsByName("recurMonth")[0].disabled=true;
				document.getElementsByName("monthType")[0].disabled=true;		
				document.getElementsByName("monthDay")[0].disabled=true;		
				document.getElementsByName("dayRecurMonth")[0].disabled=true;
				document.getElementById("labelDay").style.color="gray";
				document.getElementById("labelOfEvery").style.color="gray";
				document.getElementById("labelMonths_1").style.color="gray";
				
			}
		
		
		
		}
}
function goToCancelPage(){
	meetingActionForm.action="meetingAction.do?method=cancelUpdate";
	meetingActionForm.submit();	
 }

</script>
		<html-el:form action="meetingAction.do?method=update">
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"> <customtags:headerLink/> </span>
				</tr>
			</table>			
			<table width="90%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15"><span
						class="heading">
						<c:out value="${BusinessKey.displayName}" /> - </span>
						<c:if test="${BusinessKey.customerLevel.client}">
							<span class="headingorange">
								<mifos:mifoslabel name="meeting.labelmeetingschedule&groupMembership" bundle="MeetingResources" />
							</span>
						</c:if>
						<c:if test="${BusinessKey.customerLevel.center || BusinessKey.customerLevel.group}">
							<span class="headingorange">
								<mifos:mifoslabel name="meeting.labelMeetingSchedule" bundle="MeetingResources" />
							</span>
						</c:if>
					</td>
				</tr>
			</table>

			<table width="90%" border="0" cellpadding="0" cellspacing="0">
				<tr align="center">
					<td align="left" valign="top" class="paddingL15T15"><font
						class="fontnormalRedBold">
						<html-el:errors bundle="MeetingResources" /> </font></td>
				</tr>
				<tr>
					<td align="left" valign="top" class="paddingL15T15"><span
						class="fontnormal"> 
						<mifos:mifoslabel name="meeting.msgUpdateMeeting" bundle="MeetingResources"/>
						<br>
						<mifos:mifoslabel
						name="meeting.labelCreateInstruction" mandatory="yes"
						bundle="MeetingResources">
					</mifos:mifoslabel> </span><br>
					<br>
					<table width="90%" border="0" cellpadding="3" cellspacing="0">
						<tr class="fontnormal">
							<td align="right" valign="top"><mifos:mifoslabel
								name="meeting.labelFrequencyOfMeeting" mandatory="yes"
								bundle="MeetingResources">
							</mifos:mifoslabel></td>
							<td align="left" valign="top"
								style="border-top: 1px solid #CECECE; border-left: 1px solid #CECECE; border-right: 1px solid #CECECE;">
							<table width="98%" border="0" cellspacing="0" cellpadding="2">
								<tr valign="top" class="fontnormal">
									<td width="24%"><html-el:radio property="frequency" value="1"
										onclick="showMeetingFrequency();" /> <SPAN id="weeks"> <mifos:mifoslabel
										name="meeting.labelWeeks" bundle="MeetingResources" /> </SPAN>
									</td>
									<td width="55%"><html-el:radio property="frequency" value="2"
										onclick="showMeetingFrequency();" /> <SPAN id="months"> <mifos:mifoslabel
										name="meeting.labelMonths" bundle="MeetingResources" /> </SPAN>
									</td>
								</tr>
							</table>
							</td>
						</tr>
						<tr class="fontnormal">
							<td width="22%" align="right" valign="top">&nbsp;</td>
							<td width="59%" align="left" valign="top"
								style="border: 1px solid #CECECE;">

							<div id="weekDIV" style="height:40px; width:380px; "><mifos:mifoslabel
								name="meeting.labelRecurWeeks" bundle="MeetingResources" />
							<table border="0" cellspacing="0" cellpadding="2">
								<tr class="fontnormal">
									<td colspan="4"><mifos:mifoslabel
										name="meeting.labelRecurEvery" bundle="MeetingResources" /> <mifos:mifosnumbertext
										property="recurWeek" size="3" maxlength="3" /> <mifos:mifoslabel
										name="meeting.labelWeeks" bundle="MeetingResources" /> 
										<mifos:select property="weekDay">
											<c:forEach var="weekDay" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}" >
													<html-el:option value="${weekDay.value}">${weekDay.name}</html-el:option>
											</c:forEach>
										</mifos:select>
									</td>
								</tr>
							</table>
							</div>
							<div id="monthDIV" style="height:65px; width:450px; "><mifos:mifoslabel
								name="meeting.labelRecurMonths" bundle="MeetingResources" /> <br>
							<table border="0" cellspacing="0" cellpadding="2">
								<tr class="fontnormal">
									<td><html-el:radio property="monthType" value="1" /></td>
									<td><span id="labelDay"> <mifos:mifoslabel
										name="meeting.labelDay" bundle="MeetingResources" /> </span>

									<mifos:mifosnumbertext property="monthDay" size="3"
										maxlength="2" /> <span id="labelOfEvery"> <mifos:mifoslabel
										name="meeting.labelOfEvery" bundle="MeetingResources" /> </span>

									<mifos:mifosnumbertext property="dayRecurMonth" size="3"
										maxlength="3" /> <span id="labelMonths_1"> <mifos:mifoslabel
										name="meeting.labelMonths" bundle="MeetingResources" /> </span>

									</td>
								</tr>
								<tr class="fontnormal">
									<td><html-el:radio property="monthType" value="2" /></td>
									<td><span id="labelThe"> <mifos:mifoslabel
										name="meeting.labelThe" bundle="MeetingResources" /> </span>


									<mifos:select	property="monthRank" >
										<c:forEach var="weekRank" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekRankList')}" >
												<html-el:option value="${weekRank.id}">${weekRank.name}</html-el:option>
										</c:forEach>
									</mifos:select>
									<mifos:select property="monthWeek">
										<c:forEach var="weekDay" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}" >
												<html-el:option value="${weekDay.value}">${weekDay.name}</html-el:option>
										</c:forEach>
									</mifos:select>
									 <span id="labelOfEvery_1"> <mifos:mifoslabel
										name="meeting.labelOfEvery" bundle="MeetingResources" /> </span>

									<mifos:mifosnumbertext property="recurMonth" size="3"
										maxlength="3" /> <span id="labelMonths_2"> <mifos:mifoslabel
										name="meeting.labelMonths" bundle="MeetingResources" /> </span>
									</td>
								</tr>
							</table>
							</div>

							</td>
						</tr>
						<tr class="fontnormal">
							<td align="right">&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
						<tr class="fontnormal">
							<td align="right"><mifos:mifoslabel
								name="meeting.labelPlaceOfMeeting" bundle="MeetingResources"
								mandatory="yes" /></td>
							<td><html-el:text property="meetingPlace" maxlength="200"/></td>
						</tr>
					<!-- Start Add group membership -->	
						<c:if test="${BusinessKey.customerLevel.client}">
							<tr class="fontnormal">
								<td align="right"><mifos:mifoslabel name="Group.groupMembership" /></td>
								<td><mifos:mifoslabel name="Group.none" /></td>
							</tr>
							<tr>
								<td align="right"></td>
								<td align="left" class="fontnormalbold"><span class="fontnormal">
									<a href="addGroupMembershipAction.do?method=loadSearch&currentFlowKey=${requestScope.currentFlowKey}&randomNUm=${sessionScope.randomNUm}">
										<mifos:mifoslabel name="client.AddGroupMemberShip" bundle="ClientUIResources" /> </a> </span>
								</td>
							</tr>
						</c:if>
				    <!-- End Add group membership -->		
					</table>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
					</table>
					<SCRIPT> showMeetingFrequency();</SCRIPT> <br>
					<table width="93%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<!-- Next are submit and cancel button -->
							<td align="center">
							<html-el:submit styleClass="buttn">
								<mifos:mifoslabel name="meeting.button.save" bundle="MeetingResources"/>
							</html-el:submit> &nbsp; 
							<html-el:button	onclick="goToCancelPage();" property="cancelButton"
								value="Cancel" styleClass="cancelbuttn">
								<mifos:mifoslabel name="office.button.cancel" bundle="OfficeResources"/>
							</html-el:button>
							</td>

						</tr>
					</table>
					<br>
				</tr>
			</table>
			
		</html-el:form>
	</tiles:put>

</tiles:insert>


