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
	    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
	    	<tr>
				<td align="center" class="heading">&nbsp;</td>
	      </tr>
	    </table>
	    <table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
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
                        <mifos:mifoslabel name="PPI.ppiConfiguration" bundle="SurveysUIResources" />
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
<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" class="bluetableborder">
	<tr>
		<td align="left" valign="top" class="paddingleftCreates">
		<table width="93%" border="0" cellpadding="3" cellspacing="0">
			<tr>
				<td class="headingorange">
				<span class="heading"> 
				<mifos:mifoslabel name="PPI.definePovertyMeasurmentTool" bundle="SurveysUIResources" /> - 
				</span> 
				<mifos:mifoslabel name="PPI.longName" bundle="SurveysUIResources" />
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
				<mifos:mifoslabel name="PPI.configureInstructions" bundle="SurveysUIResources" />
                <br/>
                <mifos:mifoslabel name="funds.mandatoryinstructions" mandatory="yes" bundle="fundUIResources" />
                </td>
			</tr>
		</table><br>
		<table width="93%" border="0" cellpadding="3" cellspacing="0">
        	<tr>
				<td colspan="2" class="fontnormalbold">
				<mifos:mifoslabel name="PPI.selectCountry" bundle="SurveysUIResources" /> 
				<br><br>
				</td>
			</tr>
		</table>
		<table width="93%" border="0" cellpadding="3" cellspacing="0">
			<tr>
				<td align="right" class="fontnormal">
				Country:
				</td>
				<td align="left">
				<html-el:select property="value(country)">
				<c:forEach var="country" items="${countries}">
				<html-el:option value="${country.value}"><c:out value="${country}"/></html-el:option>
				</c:forEach>
				</html-el:select>
				</td>
			</tr>
			<tr>
				<td align="right" class="fontnormal">
				Status:
				</td>
				<td align="left">
				<html-el:select property="value(state)">
				<html-el:option value="ACTIVE">Active</html-el:option>
				<html-el:option value="INACTIVE">Inactive</html-el:option>
				</html-el:select>
				</td>
			</tr>
		</table>
        <table width="93%" border="0" cellpadding="0" cellspacing="0">
        	<tr>
            	<td align="center" class="blueline">&nbsp;</td>
        	</tr>
        </table><br>
		<table width="93%" border="0" cellpadding="0" cellspacing="0">
			<tr>
                <td align="center">
                <html-el:submit style="width:65px;" property="button" styleClass="buttn">
                <mifos:mifoslabel name="Surveys.button.preview" bundle="SurveysUIResources" />
                </html-el:submit>&nbsp; 
                <html-el:button property="calcelButton" style="width:65px;" styleClass="cancelbuttn" onclick="window.location='AdminAction.do?method=load'">
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