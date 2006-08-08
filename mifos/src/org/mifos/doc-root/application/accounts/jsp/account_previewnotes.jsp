<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>

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
            <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td><br>
                      <span class="fontnormal">
			<mifos:mifoslabel name="Account.ReviewText" bundle="accountsUIResources"></mifos:mifoslabel>
			<mifos:mifoslabel name="Account.Submit" bundle="accountsUIResources"></mifos:mifoslabel>
			<mifos:mifoslabel name="Account.Edit" bundle="accountsUIResources"></mifos:mifoslabel>
			<mifos:mifoslabel name="Savings.clickCancelToReturn" bundle="SavingsUIResources"/>
            <mifos:mifoslabel name="${ConfigurationConstants.SAVINGS}"/>
            <mifos:mifoslabel name="Savings.accountDetailsPage" bundle="SavingsUIResources"/>
			</span></td>
                </tr>
                <tr>
                  <td class="blueline"><img src="../images/trans.gif" width="10" height="5"></td>
                </tr>
                <tr>
		   		<td>
   					<font class="fontnormalRedBold"><html-el:errors bundle="accountsUIResources"/></font>
				</td>
				</tr>
              </table>
              <table width="95%" border="0" cellpadding="3" cellspacing="0">
                <tr>
                  <td align="left" valign="top"><img src="../images/trans.gif" width="10" height="5"></td>
                </tr>
                <tr>
                  <td align="left" valign="top">
			<span class="fontnormalbold">
			<c:out value="${userdatefn:getUserLocaleDate(sessionScope.UserContext.pereferedLocale,sessionScope.AccountNotes.commentDate)}"/>
			</span><br>
                      <span class="fontnormal">
			<c:out value="${sessionScope.notesActionForm.comment}"/>	
                      - <em><c:out value="${sessionScope.AccountNotes.personnel.displayName}"/></em></span> </td>
                </tr>
                <tr>
                  <td align="left" valign="top">
                  <html-el:button property="btn" style="width:65px;" styleClass="insidebuttn" onclick="goToEditPage()">
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
                  
				<html-el:submit styleClass="buttn" style="width:70px;">
					<mifos:mifoslabel name="loan.submit" />
				</html-el:submit>
                    &nbsp;&nbsp;
                    <html-el:button property="cancelBtn"  styleClass="cancelbuttn" style="width:70px" onclick="goToCancelPage()">
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
	  <mifos:SecurityParam property="${param.securityParamInput}" />
	  <html-el:hidden property="securityParamInput" value="${param.securityParamInput}" />
      <html-el:hidden property="globalAccountNum" value="${sessionScope.notesActionForm.globalAccountNum}"/>
      <html-el:hidden property="accountTypeId" value="${param.accountTypeId}"/>
</html-el:form>
</tiles:put>
</tiles:insert>

