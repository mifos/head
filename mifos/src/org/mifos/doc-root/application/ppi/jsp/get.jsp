<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<script src="pages/application/surveys/js/questions.js" type="text/javascript"></script>
<table width="95%" border="0" cellpadding="0" cellspacing="0">
    <tr>
            <td width="70%" align="left" valign="top" class="paddingL15T15"><table width="96%" border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td height="23" colspan = "100" class="headingorange">
                <mifos:mifoslabel name="PPI.ppiToolConfiguration" bundle="PPIUIResources" />
                </td>
                </tr>
              
              <tr>
				
                <td height="23" class="fontnormal"><p><span class="fontnormal">                  </span>
                <span class="fontnormal">
                <c:choose>
                  <c:when test="${retrievedSurvey.state == 1}"><img src="pages/framework/images/status_activegreen.gif" width="8" height="9"><mifos:mifoslabel name="Surveys.Active"/></c:when>
                  <c:when test="${retrievedSurvey.state == 0}"><img src="pages/framework/images/status_closedblack.gif" width="8" height="9"><mifos:mifoslabel name="Surveys.Inactive"/></c:when>      
                </c:choose><br>
                </span>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
				<td align="left" class="fontnormal">
				<mifos:mifoslabel name="PPI.Country" bundle="PPIUIResources"/>: 
				<mifos:mifoslabel name="PPI.Country.${retrievedSurvey.countryAsEnum}" bundle="PPIUIResources"/>
				<br>
				<mifos:mifoslabel name="PPI.Status" bundle="PPIUIResources"/>:
				<mifos:mifoslabel name="PPI.${retrievedSurvey.stateAsEnum}" bundle="PPIUIResources"/>
				</td>
				</tr>
				<tr>
				<tr><td>&nbsp;</td></tr>
            	<td width="20%" height="22" class="fontnormalbold">
                <mifos:mifoslabel name="PPI.PoveryStatus" bundle="PPIUIResources" />
                </td>
                <td align="left" class="fontnormalbold">
                <mifos:mifoslabel name="PPI.SurveyScoreLimits" bundle="PPIUIResources" />
                </td>
                <td class="fontnormalbold">&nbsp;</td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td height="22" class="drawtablerow">
                <mifos:mifoslabel name="PPI.Band.VeryPoor" bundle="PPIUIResources" />
                </td>
                <td align="left" class="drawtablerow">
                <c:out value="${retrievedSurvey.veryPoorMin}"/>
                -
                <c:out value="${retrievedSurvey.nonPoorMax}"/>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td height="22" class="drawtablerow">
                <mifos:mifoslabel name="PPI.Band.Poor" bundle="PPIUIResources" />
                </td>
                <td align="left" class="drawtablerow">
                <c:out value="${retrievedSurvey.poorMin}"/>
                -
                <c:out value="${retrievedSurvey.poorMax}"/>
                </td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td height="22" class="drawtablerow">
                <mifos:mifoslabel name="PPI.Band.AtRisk" bundle="PPIUIResources" />
                </td>
                <td align="left" class="drawtablerow">
                <c:out value="${retrievedSurvey.atRiskMin}"/>
                -
                <c:out value="${retrievedSurvey.atRiskMax}"/>
                </td>
                <td>&nbsp;</td>
            </tr>
            </table>              
            <br></td>
            <td valign="top" align="right"><br><html-el:link action="ppiAction.do?method=configure">
            <mifos:mifoslabel name="PPI.editPPIToolConfiguration" bundle="PPIUIResources"/>
            </html-el:link>
            </td>
          </tr>
        </table>    

	</tiles:put>
</tiles:insert>
