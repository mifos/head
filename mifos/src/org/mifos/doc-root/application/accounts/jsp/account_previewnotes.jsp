<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
 <script language="javascript">
  function goToEditPage(){
	notesActionForm.action="notesAction.do?method=previous";
	notesActionForm.submit();
  }

 function goToCancelPage(){
	notesActionForm.action="notesAction.do?method=cancel";
	notesActionForm.submit();
  }
  </script>
<html-el:form action="notesAction.do?method=create">
	<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'AccountNotes')}"
		   var="AccountNotes" />

      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="bluetablehead05">
			  <span class="fontnormal8pt">
	          	<customtags:headerLink/>
	          </span>
          </td>
        </tr>
      </table>
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15">
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
            <tr>
              <td width="83%" class="headingorange">
		<span class="heading">
		<c:out value="${sessionScope.notesActionForm.prdOfferingName}"/>&nbsp;#
		<c:out value="${sessionScope.notesActionForm.globalAccountNum}" />  -
		</span>
		<mifos:mifoslabel name="Account.PreviewNote" bundle="accountsUIResources"></mifos:mifoslabel></td>
            </tr>
          </table>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
            <logic:messagesPresent><tr><td><font class="fontnormalRedBold"><html-el:errors bundle="accountsUIResources" /></font></td></tr></logic:messagesPresent>
                <tr>
                  <td><br>
                      <span class="fontnormal">
            <c:if test="${sessionScope.notesActionForm.accountTypeId == '1'}">
				<mifos:mifoslabel name="accounts.ReviewLoanComplete" bundle="accountsUIResources" />
			</c:if>
			<c:if test="${sessionScope.notesActionForm.accountTypeId == '2'}">
				<mifos:mifoslabel name="accounts.ReviewSavingsComplete" bundle="accountsUIResources" />
			</c:if>
			</span></td>
                </tr>
                <tr>
                  <td class="blueline"><img src="../images/trans.gif" width="10" height="5"></td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td align="left" valign="top"><img src="../images/trans.gif" width="10" height="5"></td>
                </tr>
                <tr>
                  <td align="left" valign="top">
			<span class="fontnormalbold">
			<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.preferredLocale,AccountNotes.commentDate)}"/>
			</span><br>
                      <span class="fontnormal">
			<c:out value="${sessionScope.notesActionForm.comment}"/>
                      - <em><c:out value="${AccountNotes.personnel.displayName}"/></em></span> </td>
                </tr>
                <tr>
                  <td align="left" valign="top">
                  <html-el:button property="btn" styleClass="insidebuttn" onclick="goToEditPage()">
                  <mifos:mifoslabel name="Account.EditLabel" bundle="accountsUIResources"></mifos:mifoslabel>
                  </html-el:button>
                  </td>
                </tr>
              </table>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td height="17" align="center" class="blueline">&nbsp;</td>
                </tr>
              </table>
              <br>
              <table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center">

				<html-el:submit styleClass="buttn">
					<mifos:mifoslabel name="loan.submit" />
				</html-el:submit>
                    &nbsp;&nbsp;
                    <html-el:button property="cancelBtn"  styleClass="cancelbuttn" onclick="goToCancelPage()">
	                    <mifos:mifoslabel name="loan.cancel" />
                    </html-el:button>
				</td>
                </tr>
              </table>
              <br>
          </td>
        </tr>
      </table>
      <br>
      <html-el:hidden property="globalAccountNum" value="${sessionScope.notesActionForm.globalAccountNum}"/>
      <html-el:hidden property="accountTypeId" value="${param.accountTypeId}"/>
      <html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
</html-el:form>
</tiles:put>
</tiles:insert>


