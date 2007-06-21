<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<td align="left" valign="top" bgcolor="#FFFFFF" style="padding-left:8px; padding-top:10px;">
		  <table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td colspan="2" align="left" valign="top">
            <span class="headingorange">
              <mifos:mifoslabel name="Surveys.createsurveyheader" bundle="SurveysUIResources"/>
            </span>
            <br/><br/>
					  <span class="fontnormal">
              <mifos:mifoslabel name="Surveys.createsurveymsg" bundle="SurveysUIResources"/>
              <br/><br/>
              <html-el:link href="surveysAction.do?method=get&value(surveyId)=${requestScope.newSurveyId}">
                <strong><mifos:mifoslabel name="Surveys.viewsurveydetails" bundle="SurveysUIResources"/></strong>
              </html-el:link>
              <br/><br/>
              <html-el:link href="surveysAction.do?method=create_entry">
                <mifos:mifoslabel name="Surveys.definenewsurvey" bundle="SurveysUIResources"/>
              </html-el:link>
            </span>
          </td>
				</tr>
			</table>
    </td>
  </tiles:put>
</tiles:insert>
