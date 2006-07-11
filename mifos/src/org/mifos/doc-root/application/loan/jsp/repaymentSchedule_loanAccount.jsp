<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>

<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
	<script>
			function fun_return(form)
					{
						form.action="accountTrxn.do?method=cancel";
						form.submit();
					}
	</script>
		<html-el:form method="post" action="/loanAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
				<!-- REMOVE AND REPLACE WITH TAG -->
					<td class="bluetablehead05"><span class="fontnormal8pt">
						<mifoscustom:getLoanHeader loanHeader='${sessionScope.header_get}'/>
							<html-el:link action="loanAction.do?method=get&globalAccountNum=${param.globalAccountNum}">
								<c:out value="${param.accountName}" />
							</html-el:link></span>
					</td>
				</tr>
			</table>
			
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL15T15">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="83%" class="headingorange"><span class="heading">
								<c:out value="${param.accountName}"/>&nbsp;#
								<c:out value="${param.globalAccountNum}"/>-
								</span>
								<mifos:mifoslabel name="loan.repayment_sched" bundle="loanUIResources" /> 
								<c:out value="${loanfn:getCurrrentDate(sessionScope.UserContext.pereferedLocale)}" />

							</td>
						</tr>
						            	<tr>
					<logic:messagesPresent>
					<td><br><font class="fontnormalRedBold"><html-el:errors
							bundle="accountsUIResources" /></font></td>
						</logic:messagesPresent>
				</tr>
						
					</table>
					<c:if test="${param.accountStateId != 6 and param.accountStateId != 7 and param.accountStateId !=8 and param.accountStateId !=10}">
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
							<p><br>
							</p>
							</td>
						</tr>
					</table>
		
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td bgcolor="#F0D4A5" style="padding-left:10px; padding-bottom:3px;">
								<span class="fontnormalbold">
									<mifos:mifoslabel name="loan.apply_trans" />
								</span>&nbsp;&nbsp;&nbsp;&nbsp;
								<c:if test="${(param.accountStateId=='5' || param.accountStateId=='9')}">
									<html-el:link href="applyPaymentAction.do?method=load&input=loan&prdOfferingName=${param.accountName}&globalAccountNum=${param.globalAccountNum}&accountId=${param.accountId}&accountType=${param.accountTypeId}
															&recordOfficeId=${param.recordOfficeId}&recordLoanOfficerId=${param.recordLoanOfficerId}&accountStateId=${param.accountStateId}">
										<mifos:mifoslabel name="loan.apply_payment" />
									</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</c:if>
								<c:if test="${param.lastPaymentAction != '10'}">
									<c:choose>
										<c:when test="${param.accountStateId=='5' || param.accountStateId=='9'}">
											<html-el:link href="applyAdjustment.do?method=loadAdjustment&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.accountName}"> 
												<mifos:mifoslabel name="loan.apply_adjustment" />
											</html-el:link>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</c:when>
									</c:choose>
								</c:if>
							
								<html-el:link href="AccountsApplyChargesAction.do?method=load&input=reviewTransactionPage&accountId=${param.accountId}&globalAccountNum=${param.globalAccountNum}&prdOfferingName=${param.accountName}"> 
									<mifos:mifoslabel name="loan.apply_charges" />
								</html-el:link>
							</td>
						</tr>
					</table>
				</c:if>	
					

								<loanfn:getRepaymentTable />
					
					<table width="100%" border="0" cellpadding="1" cellspacing="0">
					<tr valign="top">
                        <td colspan="4">&nbsp;</td>
                      </tr>
                    </table>
					<table width="95%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="returnToAccountDetailsbutton"
								onclick="javascript:fun_return(this.form);"
								styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="loan.returnToAccountDetails"
									bundle="loanUIResources" />
							</html-el:button></td>
						</tr>
					</table>

					<br>
					</td>
				</tr>
			</table>
						<mifos:SecurityParam property="Loan" />
		</html-el:form>
		<html-el:hidden property="input" value="reviewTransactionPage"/>
		<html-el:hidden property="accountId" value="${param.accountId}"/>
		<html-el:hidden property="accountStateId" value="${param.accountStateId}"/>
		<html-el:hidden property="accountTypeId" value="${param.accountTypeId}"/>
		<html-el:hidden property="recordOfficeId" value="${param.recordOfficeId}"/>
		<html-el:hidden property="recordLoanOfficerId" value="${param.recordLoanOfficerId}"/>
		<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}"/>
		<html-el:hidden property="prdOfferingName" value="${param.accountName}"/>
	</tiles:put>
</tiles:insert>
