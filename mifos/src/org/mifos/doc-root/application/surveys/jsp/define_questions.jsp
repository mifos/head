<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="org.mifos.application.surveys.helpers.AnswerType"%>
<script>
function setDisable() {
       if (document.questionActionForm.answerType.selectedIndex == 0) {
               document.questionActionForm.choice.disabled=true;
               document.questionActionForm.AddButton.disabled=true;
       } else if (document.questionActionForm.answerType.selectedIndex == 3) {
               document.questionActionForm.choice.disabled=false;
               document.questionActionForm.AddButton.disabled=false;
       } else {
               document.questionActionForm.choice.disabled=true;
               document.questionActionForm.AddButton.disabled=true;
       }
}
</script>
<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="middle" class="paddingL15T15"><table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="35%" class="headingorange"><mifos:mifoslabel name="Surveys.addquestions"/></td>
            </tr>

          </table>
            <br>
            <html-el:form action="/questionsAction.do?method=defineQuestions" focus="questionText">
            <table width="98%" border="0" cellpadding="3" cellspacing="0">
              <tr class="fontnormal">
                <td width="24%" align="right">Question:</td>
                <td width="76%"><html-el:text property="questionText"/></td>
              </tr>

              <tr class="fontnormal">
                <td align="right">Answer Type:</td>

                <td><html-el:select property="answerType" styleId="answerType" onchange="setDisable();">
                  <option>Select
                  <option value="${AnswerType.FREETEXT}"><mifos:mifoslabel name="Surveys.Freetext"/>
                  <option value="${AnswerType.NUMBER}"><mifos:mifoslabel name="Surveys.Number"/>
                  <option value="${AnswerType.CHOICE}"><mifos:mifoslabel name="Surveys.Choice"/>
                  <option value="${AnswerType.DATE}"><mifos:mifoslabel name="Surveys.Date"/>
                </html-el:select></td>
              </tr>
              <tr class="fontnormal">
                <td align="right">Answer:</td>
                <td><html-el:text property="choice" styleId="choice" disabled="true"/>
                  <input id="AddButton" type="button" class="insidebuttn" value="Add &gt;&gt;" style="width:65px"  disabled="true"></td>
              </tr>
            </table>
            <table width="98%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr valign="top" class="fontnormal">
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>

                  </tr>
                  </tr>
                </table>
                <br>
            <table width="98%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">&nbsp;<html-el:submit value="Submit"/>&nbsp;<input type="button" name="Button2" value="Cancel" class="cancelbuttn" onClick="location.href='AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}'">
                </td>
              </tr>
              </html-el:form>
            </table>

</tiles:put>
</tiles:insert>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="org.mifos.application.surveys.helpers.AnswerType"%>
<script>
function setDisable() {
       if (document.questionActionForm.answerType.selectedIndex == 0) {
               document.questionActionForm.choice.disabled=true;
               document.questionActionForm.AddButton.disabled=true;
       } else if (document.questionActionForm.answerType.selectedIndex == 3) {
               document.questionActionForm.choice.disabled=false;
               document.questionActionForm.AddButton.disabled=false;
       } else {
               document.questionActionForm.choice.disabled=true;
               document.questionActionForm.AddButton.disabled=true;
       }
}
</script>
<tiles:insert definition=".view">
<tiles:put name="body" type="string">
<table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="middle" class="paddingL15T15"><table width="98%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="35%" class="headingorange"><mifos:mifoslabel name="Surveys.addquestions"/></td>
            </tr>

          </table>
            <br>
            <html-el:form action="/questionsAction.do?method=defineQuestions" focus="questionText">
            <table width="98%" border="0" cellpadding="3" cellspacing="0">
              <tr class="fontnormal">
                <td width="24%" align="right">Question:</td>
                <td width="76%"><html-el:text property="questionText"/></td>
              </tr>

              <tr class="fontnormal">
                <td align="right">Answer Type:</td>

                <td><html-el:select property="answerType" styleId="answerType" onchange="setDisable();">
                  <option>Select
                  <option value="${AnswerType.FREETEXT}"><mifos:mifoslabel name="Surveys.Freetext"/>
                  <option value="${AnswerType.NUMBER}"><mifos:mifoslabel name="Surveys.Number"/>
                  <option value="${AnswerType.CHOICE}"><mifos:mifoslabel name="Surveys.Choice"/>
                  <option value="${AnswerType.DATE}"><mifos:mifoslabel name="Surveys.Date"/>
                </html-el:select></td>
              </tr>
              <tr class="fontnormal">
                <td align="right">Answer:</td>
                <td><html-el:text property="choice" styleId="choice" disabled="true"/>
                  <input id="AddButton" type="button" class="insidebuttn" value="Add &gt;&gt;" style="width:65px"  disabled="true"></td>
              </tr>
            </table>
            <table width="98%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center" class="blueline"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr valign="top" class="fontnormal">
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>

                  </tr>
                  </tr>
                </table>
                <br>
            <table width="98%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td align="center">&nbsp;<html-el:submit value="Submit"/>&nbsp;<input type="button" name="Button2" value="Cancel" class="cancelbuttn" onClick="location.href='AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}'">
                </td>
              </tr>
              </html-el:form>
            </table>

</tiles:put>
</tiles:insert>