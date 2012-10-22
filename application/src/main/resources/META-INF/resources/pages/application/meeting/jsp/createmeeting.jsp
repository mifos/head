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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".withoutmenu">
	<tiles:put name="body" type="string">
	<span id="page.id" title="createmeeting"></span>
	


<script src="pages/application/meeting/js/meeting.js" >
</script>
		<script>
function showMeetingFrequency(){
	if (document.meetingActionForm.frequency[0].checked == true){
		document.getElementById("weekDIV").style.display = "block";
		document.getElementById("monthDIV").style.display = "none";
		}
	else if (document.meetingActionForm.frequency[1].checked == true){
		document.getElementById("weekDIV").style.display = "none";
		document.getElementById("monthDIV").style.display = "block";
		if(document.meetingActionForm.monthType[0].checked == false && document.meetingActionForm.monthType[1].checked == false)
			document.getElementsByName("monthType")[0].checked=true;
	}
}

function goToCancelPage(){
	document.meetingActionForm.method.value="cancelCreate";
	meetingActionForm.submit();
	
	
  }
</script>

		<html-el:form action="/meetingAction.do">		
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td height="350" align="left" valign="top" bgcolor="#FFFFFF">
					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0">
						<tr>
							<td align="center" class="heading"><!-- People need to pass this information to me in rqquest parameter -->
							<mifos:mifoslabel
								name="meeting.labelMeetingScheduleFor" 
								bundle="MeetingResources" />
								
								<c:choose>
								  <c:when test="${meetingActionForm.customerLevel == CustomerLevel.CLIENT.value}">
								  	<mifos:mifoslabel name="${ConfigurationConstants.CLIENT}"></mifos:mifoslabel>
								  </c:when>
								  
								  <c:when test="${meetingActionForm.customerLevel == CustomerLevel.GROUP.value}">
								  	<mifos:mifoslabel name="${ConfigurationConstants.GROUP}"></mifos:mifoslabel>								  
								  </c:when>
								  
								  <c:when test="${meetingActionForm.customerLevel == CustomerLevel.CENTER.value}">
								  	<mifos:mifoslabel name="${ConfigurationConstants.CENTER}"></mifos:mifoslabel>								  
								  </c:when>
								</c:choose>
								<!-- END People need to pass this information to me in rqquest parameter -->



							</td>
						</tr>
						<tr>
							<td class="blueline"><img src="pages/framework/images/trans.gif"
								width="10" height="10"></td>
						</tr>
					</table>

					<table width="90%" border="0" align="center" cellpadding="0"
						cellspacing="0" class="bluetableborder">
						<tr align="center">
						<td align="left" valign="top" class="paddingL15T15">
							<font class="fontnormalRedBold"><span id="createmeeting.error.message"><html-el:errors
								bundle="MeetingResources" /></span> </font>
						</td>
						</tr>
						<tr>
							<td align="left" valign="top" class="paddingL15T15"><span
								class="fontnormal"> <mifos:mifoslabel
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

											<td width="24%"><html-el:radio styleId="createmeeting.input.frequencyWeeks" property="frequency" value="1"
												onclick="showMeetingFrequency();" /> <span id="createmeeting.label.frequencyWeeks"><mifos:mifoslabel
												name="meeting.labelWeeks" bundle="MeetingResources" /></span></td>
											<td width="55%"><html-el:radio styleId="createmeeting.input.frequencyMonths" property="frequency" value="2"
												onclick="showMeetingFrequency();" /> <span id="createmeeting.label.frequencyMonths"><mifos:mifoslabel
												name="meeting.labelMonths" bundle="MeetingResources" /></span></td>
										</tr>
									</table>
									</td>
								</tr>
								<tr class="fontnormal">
									<td width="22%" align="right" valign="top">&nbsp;</td>
									<td width="59%" align="left" valign="top"
										style="border: 1px solid #CECECE;">

									<div id="weekDIV" style="height:70px; width:420px; "><mifos:mifoslabel
										name="meeting.labelRecurWeeks" bundle="MeetingResources" />



									<table border="0" cellspacing="0" cellpadding="2">
										<tr class="fontnormal">
											<td colspan="4"><mifos:mifoslabel
												name="meeting.labelRecurEvery" bundle="MeetingResources" />


											<mifos:mifosnumbertext styleId="createmeeting.input.weekFrequency" property="recurWeek" size="3"  maxlength="3"/> 
											<span id="createcustomermeeting.label.weekFrequency">

												
											<mifos:mifoslabel
												name="meeting.labelWeeks" bundle="MeetingResources" /></span> 
												<mifos:select styleId="createmeeting.input.dayOfWeek" property="weekDay">
													<c:forEach var="weekDay" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}" >
															<html-el:option value="${weekDay.value}">${weekDay.name}</html-el:option>
													</c:forEach>
												</mifos:select>
												<br />
												<span id="createcustomermeeting.label.meetingStartDate">
													<mifos:mifoslabel name="meeting.meetingStartDateLabel"  bundle="MeetingResources" />
												</span>
												<mifos:mifosalphanumtext property="meetingStartDate" size="15"/>
												<script>
$(document).ready(function() {
	$.datepicker.setDefaults($.datepicker.regional[""]);
	$("input[name=meetingStartDate]").datepicker({
		dateFormat: 'dd/mm/yy',	
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
		buttonImageOnly: true
    });
  }
);
												</script>
												</td>
										</tr>
									</table>
									</div>
									<div id="monthDIV" style="height:65px; width:450px; "><mifos:mifoslabel
										name="meeting.labelRecurMonths" bundle="MeetingResources" />
									<br>
									<table border="0" cellspacing="0" cellpadding="2">
										<tr class="fontnormal">
											<td><html-el:radio styleId="createmeeting.input.monthType1" property="monthType" value="1" /></td>
											<td><span id="createmeeting.label.dayOfMonth"><mifos:mifoslabel name="meeting.labelDay"
												bundle="MeetingResources" /></span> <mifos:mifosnumbertext styleId="createmeeting.input.dayOfMonth"
												property="monthDay" size="3" onfocus="checkMonthType1()" maxlength="2"/> 
												 <mifos:mifoslabel name="meeting.labelOfEvery"
												bundle="MeetingResources" /> <mifos:mifosnumbertext styleId="createmeeting.input.monthFrequency1"
												property="dayRecurMonth" size="3" onfocus="checkMonthType1()" maxlength="3"/> <span id="createmeeting.label.monthFrequency1"><mifos:mifoslabel
												name="meeting.labelMonths" bundle="MeetingResources" /></span></td>
										</tr>
										<tr class="fontnormal">
											<td><html-el:radio styleId="createmeeting.input.monthType2" property="monthType" value="2" /></td>
											<td><mifos:mifoslabel name="meeting.labelThe"
												bundle="MeetingResources" /> 
												<mifos:select	property="monthRank" onfocus="checkMonthType2()">
													<c:forEach var="weekRank" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekRankList')}" >
															<html-el:option value="${weekRank.value}">${weekRank.name}</html-el:option>
													</c:forEach>
											</mifos:select>
											<mifos:select styleId="createmeeting.input.dayOfMonth" property="monthWeek" onfocus="checkMonthType2()">
												<c:forEach var="weekDay" items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'WeekDayList')}" >
															<html-el:option value="${weekDay.value}">${weekDay.name}</html-el:option>
												</c:forEach>
											</mifos:select> <mifos:mifoslabel name="meeting.labelOfEvery"
												bundle="MeetingResources" /> <mifos:mifosnumbertext styleId="createmeeting.input.monthFrequency2"
												property="recurMonth" size="3" onfocus="checkMonthType2()" maxlength="3"/> 
												<span id="createmeeting.label.monthFrequency2">
												<mifos:mifoslabel
												name="meeting.labelMonths" bundle="MeetingResources" /></span></td>
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
									<td align="right"><span id="createmeeting.label.meetingPlace"><mifos:mifoslabel
										name="meeting.labelPlaceOfMeeting" bundle="MeetingResources"
										mandatory="yes" /></span></td>
									<td><html-el:text styleId="createmeeting.input.meetingPlace" property="meetingPlace" maxlength="200"/></td>
								</tr>

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
									<td align="center"><html-el:submit styleId="createmeeting.button.save" styleClass="buttn">
										<mifos:mifoslabel name="meeting.button.save"
											bundle="MeetingResources"></mifos:mifoslabel>
									</html-el:submit> &nbsp; <html-el:button styleId="createmeeting.button.cancel"
										onclick="goToCancelPage();" property="cancelButton"
										styleClass="cancelbuttn">
										<mifos:mifoslabel name="office.button.cancel"
											bundle="OfficeResources"></mifos:mifoslabel>
									</html-el:button></td>

								</tr>
							</table>
							<br>
							
						</tr>
					</table>
					<br>
					</td>
				</tr>
			</table>
			<html-el:hidden property="method" value="create" />
			<!-- hidden veriable which will set input veriable -->
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>

</tiles:insert>


