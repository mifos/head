<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
<html-el:form action="AccountNotesAction.do?method=preview">
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          	<td class="bluetablehead05"> <span class="fontnormal8pt">
	 		<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_get}'/>
				<html-el:link action="loanAction.do?method=get&globalAccountNum=${param.globalAccountNum}">
					<c:out value="${sessionScope.accountNotesActionForm.accountName}" />
				</html-el:link></span>
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
							<c:out value="${sessionScope.accountNotesActionForm.accountName}"/> &nbsp;#<c:out
								value="${param.globalAccountNum}" /> - 
						</span>
						<mifos:mifoslabel name="Account.Notes" bundle="accountsUIResources"></mifos:mifoslabel>
					</td>
              		<td width="17%" align="right" class="fontnormal">
						<a href="AccountNotesAction.do?method=load&globalAccountNum=<c:out value="${sessionScope.accountNotesActionForm.globalAccountNum}"/>">
						<mifos:mifoslabel name="Account.AddNewNote" bundle="accountsUIResources"></mifos:mifoslabel></a>
				 	</td>
            	</tr>
            	<tr>
					<logic:messagesPresent>
					<td><br><font class="fontnormalRedBold"><html-el:errors
							bundle="accountsUIResources" /></font></td>
						</logic:messagesPresent>
				</tr>
            	
          	</table>
            <br>
            <table width="95%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td>
                	<mifos:mifostabletagdata name="accountNotes" key="allnotes" type="single" 
       					className="AccountNotes" width="95%" border="0" cellspacing="0" cellpadding="0"/>
                </td>
              </tr>
            </table>
            
            <br>
          </td>
        </tr>
      </table>
      <br>
      <html-el:hidden property="globalAccountNum" value="${sessionScope.accountNotesActionForm.globalAccountNum}"/>
  </html-el:form>
</tiles:put>
</tiles:insert>
