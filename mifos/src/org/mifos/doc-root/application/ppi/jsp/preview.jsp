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
<!-- preview.jsp -->

<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".create">
<tiles:put name="body" type="string">
<html-el:form action="/ppiAction.do?method=update" focus="name">
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
				<td class="headingorange" colspan="5">
				<span class="heading"> 
				<mifos:mifoslabel name="PPI.definePovertyMeasurmentTool" bundle="PPIUIResources" /> - 
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
				<td align="left" class="fontnormal">
				<mifos:mifoslabel name="PPI.Country" bundle="PPIUIResources"/>: 
				<mifos:mifoslabel name="PPI.Country.${results.country}" bundle="PPIUIResources"/>
				<br>
				<mifos:mifoslabel name="PPI.Status" bundle="PPIUIResources"/>: 
				<mifos:mifoslabel name="PPI.${results.state}" bundle="PPIUIResources"/>
				</td>
			</tr>
			<tr>
				<td><br></td>
			</tr>
			<tr>
            	<td width="20%" height="22" class="fontnormalbold">
                <mifos:mifoslabel name="PPI.PoveryStatus" bundle="PPIUIResources" />
                </td>
                <td align="left" class="fontnormalbold">
                <mifos:mifoslabel name="PPI.SurveyScoreLimits" bundle="PPIUIResources" />
                </td>
                <td class="fontnormalbold">&nbsp;</td>
            </tr>
            <tr>
                <td height="22" class="drawtablerow">
                <mifos:mifoslabel name="PPI.Band.VeryPoor" bundle="PPIUIResources" />
                </td>
                <td align="left" class="drawtablerow">
                <c:out value="${results.veryPoorMin}"/>
                -
                <c:out value="${results.veryPoorMax}"/>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td height="22" class="drawtablerow">
                <mifos:mifoslabel name="PPI.Band.Poor" bundle="PPIUIResources" />
                </td>
                <td align="left" class="drawtablerow">
                <c:out value="${results.poorMin}"/>
                -
                <c:out value="${results.poorMax}"/>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td height="22" class="drawtablerow">
                <mifos:mifoslabel name="PPI.Band.AtRisk" bundle="PPIUIResources" />
                </td>
                <td align="left" class="drawtablerow">
                <c:out value="${results.atRiskMin}"/>
                -
                <c:out value="${results.atRiskMax}"/>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td height="22" class="drawtablerow">
                <mifos:mifoslabel name="PPI.Band.NonPoor" bundle="PPIUIResources" />
                </td>
                <td align="left" class="drawtablerow">
                <c:out value="${results.nonPoorMin}"/>
                -
                <c:out value="${results.nonPoorMax}"/>
                </td>
                <td>&nbsp;</td>
            </tr>
		</table><br>
		<table width="93%" border="0" cellpadding="0" cellspacing="0">
			<tr>
                <td align="center">
                <html-el:submit property="button" styleClass="buttn">
                <mifos:mifoslabel name="Surveys.button.submit" bundle="SurveysUIResources" />
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