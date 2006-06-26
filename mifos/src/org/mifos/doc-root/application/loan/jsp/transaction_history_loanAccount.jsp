<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/c" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>

<tiles:insert definition=".view">
	<tiles:put name="body" type="string">
		<html-el:form method="post" action="/loanAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="bluetablehead05"><span class="fontnormal8pt"><html-el:link
						href="clientsaccounts.htm">
						<mifos:mifoslabel name="loan.branch-01" bundle="LoanUIResources" />
					</html-el:link> /&nbsp;<html-el:link href="CenterDetails.htm">
						<mifos:mifoslabel name="loan.kanakpura_center"
							bundle="LoanUIResources" />
					</html-el:link> / <html-el:link href="GroupDetails.htm">
						<mifos:mifoslabel name="loan.hope&effort_group"
							bundle="LoanUIResources" />
					</html-el:link> / <html-el:link href="loan_account_detail.htm">
						                <mifos:mifoslabel name="loan.edu_loan"  />
               							 <mifos:mifoslabel name="${ConfigurationConstants.LOAN}" />

					</html-el:link> / </span></td>
				</tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"><span class="heading">Educational
							Loan # 12334 - </span><mifos:mifoslabel name="loan.transc_history" bundle="LoanUIResources" /></td>
						</tr>

					</table>
					<br>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="9%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.date" bundle="LoanUIResources" /></td>
							<td width="9%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.payment_id" bundle="LoanUIResources" /></td>
							<td width="9%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.transaction_id" bundle="LoanUIResources" /></td>

							<td width="9%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.ins" bundle="LoanUIResources" /></td>
							<td width="9%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.type" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.gl_cod" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.debit" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.credit" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.balance" bundle="LoanUIResources" /></td>

							<td width="9%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.date_posted" bundle="LoanUIResources" /></td>
							<td width="9%" class="drawtablerowboldnoline"><mifos:mifoslabel
								name="loan.posted_by" bundle="LoanUIResources" /></td>
						</tr>
						<tr>
							<td width="9%" class="drawtablerow">12/10/2000</td>
							<td width="9%" class="drawtablerow">101</td>
							<td width="9%" class="drawtablerow">201</td>

							<td width="9%" class="drawtablerow">2</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.disb_pr" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerow">11111</td>
							<td width="9%" align="right" class="drawtablerow">6000</td>
							<td width="9%" align="right" class="drawtablerow">&nbsp;</td>
							<td width="9%" align="right" class="drawtablerow">6000</td>

							<td width="9%" class="drawtablerow">12/2/2000</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.mariev" bundle="LoanUIResources" /></td>
						</tr>
						<tr>
							<td width="9%" class="drawtablerow">120/9/2000</td>
							<td width="9%" class="drawtablerow">102</td>
							<td width="9%" class="drawtablerow">202</td>

							<td width="9%" class="drawtablerow">1</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.fee_rec" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerow">12222</td>
							<td width="9%" align="right" class="drawtablerow">&nbsp;</td>
							<td width="9%" align="right" class="drawtablerow">-50</td>
							<td width="9%" align="right" class="drawtablerow">6050</td>

							<td width="9%" class="drawtablerow">12/2/2000</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.mariev" bundle="LoanUIResources" /></td>
						</tr>
						<tr>
							<td width="9%" class="drawtablerow">12/08/2000</td>
							<td width="9%" class="drawtablerow">102</td>
							<td width="9%" class="drawtablerow">203</td>

							<td width="9%" class="drawtablerow">2</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.fee_pd" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerow">13333</td>
							<td width="9%" align="right" class="drawtablerow">&nbsp;</td>
							<td width="9%" align="right" class="drawtablerow">-100</td>
							<td width="9%" align="right" class="drawtablerow">3400</td>

							<td width="9%" class="drawtablerow">12/2/2000</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.mariev" bundle="LoanUIResources" /></td>
						</tr>
						<tr>
							<td width="9%" class="drawtablerow">12/07/2000</td>
							<td width="9%" class="drawtablerow">103</td>
							<td width="9%" class="drawtablerow">204</td>

							<td width="9%" class="drawtablerow">10</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.pr_pd" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerow">14444</td>
							<td width="9%" align="right" class="drawtablerow">&nbsp;</td>
							<td width="9%" align="right" class="drawtablerow">-10</td>
							<td width="9%" align="right" class="drawtablerow">1210</td>

							<td width="9%" class="drawtablerow">12/2/2000</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.mariev" bundle="LoanUIResources" /></td>
						</tr>
						<tr>
							<td width="9%" class="drawtablerow">12/05/2000</td>
							<td width="9%" class="drawtablerow">103</td>
							<td width="9%" class="drawtablerow">205</td>

							<td width="9%" class="drawtablerow">12</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.intr_rec" bundle="LoanUIResources" /></td>
							<td width="9%" align="right" class="drawtablerow">15555</td>
							<td width="9%" align="right" class="drawtablerow">10</td>
							<td width="9%" align="right" class="drawtablerow">&nbsp;</td>
							<td width="9%" align="right" class="drawtablerow">1450</td>

							<td width="9%" class="drawtablerow">12/2/2000</td>
							<td width="9%" class="drawtablerow"><mifos:mifoslabel
								name="loan.mariev" bundle="LoanUIResources" /></td>
						</tr>
					</table>
					<br>
					<table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center"><html-el:button property="returnToAccountDetailsbutton"
								onclick="location.href='loan_account_detail.htm';"
								styleClass="buttn" style="width:165px;">
								<mifos:mifoslabel name="button.returnToAccountDetails"
									bundle="LoanUIResources" />
							</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<br>
		</html-el:form>
	</tiles:put>
</tiles:insert>
