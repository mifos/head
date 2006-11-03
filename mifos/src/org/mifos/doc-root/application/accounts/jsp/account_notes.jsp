<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix = "mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
 <tiles:put name="body" type="string">
<html-el:form action="notesAction.do">
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          	<td class="bluetablehead05">
			  <span class="fontnormal8pt">
	          	<customtags:headerLink/>
	          </span>
          </td>
        </tr>
      </table>
      <c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
      <table width="95%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="70%" align="left" valign="top" class="paddingL15T15">
          	<table width="95%" border="0" cellpadding="3" cellspacing="0">
            	<tr>
              		<td width="83%" class="headingorange">
						<span class="heading">
							<c:if test="${sessionScope.notesActionForm.accountTypeId == '2'}">
									<c:out value="${BusinessKey.savingsOffering.prdOfferingName}"/>
							</c:if>
							<c:if test="${sessionScope.notesActionForm.accountTypeId == '1'}">
									<c:out value="${BusinessKey.loanOffering.prdOfferingName}"/>
							</c:if>
							# <c:out value="${sessionScope.notesActionForm.globalAccountNum}" /> -
						</span>
						<mifos:mifoslabel name="Account.Notes" bundle="accountsUIResources"></mifos:mifoslabel>
					</td>
              		<td width="17%" align="right" class="fontnormal">
						<a href="notesAction.do?method=load&globalAccountNum=<c:out value="${sessionScope.notesActionForm.globalAccountNum}"/>&currentFlowKey=${requestScope.currentFlowKey}">
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
                	<mifos:mifostabletagdata name="accountNote" key="allnotes" type="single"
       					width="95%" border="0" cellspacing="0" cellpadding="0"/>
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
