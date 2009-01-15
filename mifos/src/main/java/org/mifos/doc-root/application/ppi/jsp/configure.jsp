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
<!-- configure.jsp -->

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
<tiles:put name="body" type="string">
<html-el:form action="/ppiAction.do?method=preview" focus="name">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="450" align="left" valign="top" bgcolor="#FFFFFF">
		<br>
	    <table width="93%" border="0" align="center" cellpadding="0" cellspacing="0">
	    	<tr>
	        	<td class="bluetablehead">
		  		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		    <tr>
		    	<td width="27%">
				<table border="0" cellspacing="0" cellpadding="0">
			  		<tr>
			    		<td>
                        <img src="pages/framework/images/timeline/bigarrow.gif" width="17" height="17">
                        </td>
			    		<td class="timelineboldorange">
                        <mifos:mifoslabel name="PPI.ppiConfiguration" bundle="PPIUIResources" />
                        </td>
			  		</tr>
				</table>
		      	</td>
		      	<td width="73%" align="right">
				<table border="0" cellspacing="0" cellpadding="0">
			  		<tr>
			    		<td>
                        <img src="pages/framework/images/timeline/orangearrow.gif" width="17" height="17">
                        </td>
			    		<td class="timelineboldorangelight">
                        <mifos:mifoslabel name="Surveys.review" bundle="SurveysUIResources" />
                        </td>
			  		</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<table width="93%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
	<tr>
		<td align="left" valign="top" class="paddingleftCreates">
		<table width="93%" border="0" cellpadding="3" cellspacing="0">
			<tr>
				<td class="headingorange">
				<span class="heading"> 
				<mifos:mifoslabel name="PPI.definePovertyMeasurementTool" bundle="PPIUIResources" /> - 
				</span> 
				<mifos:mifoslabel name="PPI.longName" bundle="PPIUIResources" />
				</td>
			</tr>
			<tr>
				<td>
                <font class="fontnormalRedBold"> 
                <html-el:errors bundle="PPIUIResources" /> 
                </font>
                </td>
			</tr>
			<tr>
				<td class="fontnormal">
				<mifos:mifoslabel name="PPI.configureInstructions" bundle="PPIUIResources" />
                <br/>
                <mifos:mifoslabel name="funds.mandatoryinstructions" mandatory="yes" bundle="fundUIResources" />
                </td>
			</tr>
		</table><br>
		<table width="93%" border="0" cellpadding="3" cellspacing="0">
        	<tr>
				<td colspan="2" class="fontnormalbold">
				<mifos:mifoslabel name="PPI.selectCountry" bundle="PPIUIResources" /> 
				<br><br>
				</td>
			</tr>
		</table>
		<table width="93%" border="0" cellpadding="3" cellspacing="0">
			<tr>
				<td align="right" class="fontnormal">
				<mifos:mifoslabel name="PPI.Country" bundle="PPIUIResources" mandatory="yes"/>:
				</td>
				<td align="left">
				<html-el:select property="value(country)" style="width:136px;">
				<c:forEach var="country" items="${countries}">
				<html-el:option value="${country}">
				<mifos:mifoslabel name="PPI.Country.${country}" bundle="PPIUIResources"/>
				</html-el:option>
				</c:forEach>
				</html-el:select>
				</td>
			</tr>
			<tr>
				<td align="right" class="fontnormal">
				<mifos:mifoslabel name="PPI.Status" bundle="PPIUIResources" mandatory="yes"/>:
				</td>
				<td align="left">
				<html-el:select property="value(state)" style="width:136px;">
				<html-el:option value="ACTIVE"><mifos:mifoslabel name="PPI.ACTIVE" bundle="PPIUIResources"/></html-el:option>
				<html-el:option value="INACTIVE"><mifos:mifoslabel name="PPI.INACTIVE" bundle="PPIUIResources"/></html-el:option>
				</html-el:select>
				</td>
			</tr>
		</table>
		<table width="93%" border="0" cellpadding="3" cellspacing="0">
        	<tr>
        	 <!-- PPI Admin is commented out on this and the following table -->
        	 <!-- As per issue 1883 -->
				<!-- <td colspan="2">
				<span class="fontnormalbold">
				<mifos:mifoslabel name="PPI.definePovertyStatus" bundle="PPIUIResources" /> 
				</span>
				<br><span class="fontnormal">
				<mifos:mifoslabel name="PPI.definePovertyStatusIntructions" bundle="PPIUIResources" />
				</span><br>
				</td> -->
			</tr>
		</table>
		<table width="93%" border="0" cellpadding="3" cellspacing="0">
			<tr class="fontnormal">
				<td width="30%" align="right"><table width="80%" border="0" cellpadding="3" cellspacing="0">
                	<tr/>
                	<!-- <tr>
                    	<td width="22%" height="22" class="fontnormalbold">
                    	<mifos:mifoslabel name="PPI.PoveryStatus" bundle="PPIUIResources" />
                    	</td>
                        <td colspan="3" align="center" class="fontnormalbold">
                        <mifos:mifoslabel name="PPI.SurveyScoreLimits" bundle="PPIUIResources" />
                        </td>
                        <td width="42%" class="fontnormalbold">&nbsp;</td>
                    </tr>
                    <tr>
                    	<td height="22" class="drawtablerow">
                    	<mifos:mifoslabel name="PPI.Band.VeryPoor" bundle="PPIUIResources" />
                    	</td>
                        <td width="17%" align="center" class="drawtablerow">
                        <html-el:text property="value(veryPoorMin)" size="4" disabled="true"/>
                        </td>
                        <td align="center" class="drawtablerow">-</td>
                        <td width="19%" align="center" class="drawtablerow">
                        <html-el:text property="value(veryPoorMax)" size="4" disabled="true"/>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
					<tr>
                    	<td height="22" class="drawtablerow">
                    	<mifos:mifoslabel name="PPI.Band.Poor" bundle="PPIUIResources" />
                    	</td>
                        <td width="17%" align="center" class="drawtablerow">
                        <html-el:text property="value(poorMin)" size="4" disabled="true"/>
                        </td>
                        <td align="center" class="drawtablerow">-</td>
                        <td width="19%" align="center" class="drawtablerow">
                        <html-el:text property="value(poorMax)" size="4" disabled="true"/>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                    	<td height="22" class="drawtablerow">
                    	
                    	<mifos:mifoslabel name="PPI.Band.AtRisk" bundle="PPIUIResources" />
                    	</td>
                        <td width="17%" align="center" class="drawtablerow">
                        <html-el:text property="value(atRiskMin)" size="4" disabled="true"/>
                        </td>
                        <td align="center" class="drawtablerow">-</td>
                        <td width="19%" align="center" class="drawtablerow">
                        <html-el:text property="value(atRiskMax)" size="4" disabled="true"/>
                        </td>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                    	<td height="22" class="drawtablerow">
                    	<mifos:mifoslabel name="PPI.Band.NonPoor" bundle="PPIUIResources" />
                    	</td>
                        <td width="17%" align="center" class="drawtablerow">
                        <html-el:text property="value(nonPoorMin)" size="4" disabled="true"/>
                        </td>
                        <td align="center" class="drawtablerow">-</td>
                        <td width="19%" align="center" class="drawtablerow">
                        <html-el:text property="value(nonPoorMax)" size="4" disabled="true"/>
                        </td>
                        <td>&nbsp;</td>
                    </tr> -->
        	</tr>
		</table><br><br>
		<table width="93%" border="0" cellpadding="0" cellspacing="0">
			<tr>
                <td align="center">
                <html-el:submit property="button" styleClass="buttn">
                <mifos:mifoslabel name="Surveys.button.preview" bundle="SurveysUIResources" />
                </html-el:submit>&nbsp; 
                <html-el:button property="calcelButton" styleClass="cancelbuttn" onclick="window.location='AdminAction.do?method=load'">
                <mifos:mifoslabel name="Surveys.button.cancel" bundle="SurveysUIResources" />
                </html-el:button>
            	</td>
        	</tr>
        </table>
        <br>
    	</td>
	</tr>
</table>
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form> 
</tiles:put> 
</tiles:insert>