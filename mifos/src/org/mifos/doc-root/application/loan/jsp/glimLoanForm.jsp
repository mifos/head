<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib uri="/tags/struts-html" prefix="html"%>
<%@taglib uri="/tags/struts-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/loan/loanfunctions" prefix="loanfn"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<!-- Prerequisites : create loan account for Group / When Group loan with individual Monitoring is enabled -->
		<c:set
			value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientListSize')}"
			var="clientListSize" />
			
		<table width="96%" border="0" cellpadding="3" cellspacing="0">
			<tr>
				<td width="5%" valign="top" class="drawtablerowboldnolinebg"><input
					type="checkbox"
					onchange="CalculateTotalLoanAmount(CLIENTS_COUNT);"
					onclick="for(var i=0,l=this.form.length; i<l;
					i++){if(this.form[i].type==
					'checkbox' && this.form[i].name !='selectAll1'
					&& this.form[i].name !='intDedDisbursement'
					){this.form[i].checked=this.checked}}
					"
							name="selectAll1"></td>
				<td width="29%" class="drawtablerowboldnolinebg"><mifos:mifoslabel
					name="loan.acc_owner" /></td>
				<td width="31%" class="drawtablerowboldnolinebg"><font color="#FF0000">*</font>&nbsp;<mifos:mifoslabel
					name="${ConfigurationConstants.LOAN}" /> <mifos:mifoslabel
					name="loan.amt" /></td>
				<td width="35%" class="drawtablerowboldnolinebg"><font color="#FF0000">*</font>&nbsp;<mifos:mifoslabel
					name="loan.business_work_act" /> <mifos:mifoslabel
					name="${ConfigurationConstants.LOAN}" /></td>
			</tr>
		    <script type="text/javascript">var CLIENTS_COUNT = 0;</script>

			<c:forEach var="client" varStatus="loopStatus1"
				items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'clientList')}">
				<bean:define id="indice" toScope="request">
					<c:out value="${loopStatus1.index}" />
				</bean:define>
				<tr>
					<td valign="top" class="drawtablerow">
					    <c:choose>
					        <c:when test="${loanfn:isDisabledWhileEditingGlim('clientDetails.clientId',accountState)}">
    							<html:checkbox
								property="clients[${indice}]"
								value="${client.customerId}"
								onclick="return false;"/>
					        </c:when>
					        <c:otherwise>
								<html:checkbox
									property="clients[${indice}]"
									value="${client.customerId}"
									onclick="iselectAllCheck(this)" 						
									onchange="CalculateTotalLoanAmount(CLIENTS_COUNT);" /></td>
						    </c:otherwise>
						</c:choose>
					<td width="29%" valign="top" class="drawtablerow"><span
						class="fontnormalbold"><mifos:mifoslabel
						name="${ConfigurationConstants.CLIENT}"
						isColonRequired="Yes" /></span> <c:out
						value="${client.displayName}" /> <br>
						<span class="fontnormalbold"><mifos:mifoslabel
							name="${ConfigurationConstants.CLIENT_ID}"
							isColonRequired="Yes" /></span> <c:out
							value="${client.globalCustNum}" /> <br>
					<c:if test="${not empty client.governmentId}">												
						<span class="fontnormalbold"><c:out
							value="${ConfigurationConstants.GOVERNMENT}" />&nbsp;<c:out
							value="${ConfigurationConstants.ID}" />:&nbsp;</span> <c:out
							value="${client.governmentId}" />
					<br>
					</c:if>										
					</td>
					<td width="31%" valign="top" class="drawtablerow"><mifos:mifosdecimalinput
						property="clientDetails[${indice}].loanAmount" readonly="${loanfn:isDisabledWhileEditingGlim('clientDetails.loanAmount',accountState)}"
						 onchange="CalculateTotalLoanAmount(CLIENTS_COUNT);"/></td>
					<td width="35%" valign="top" class="drawtablerow">
					
					   <c:choose>
					       <c:when test="${loanfn:isDisabledWhileEditingGlim('clientDetails.purposeOfLoan',accountState)}">
						       <mifos:mifosalphanumtext readonly="true" property="clientDetails[${indice}].businessActivityName" />
						       <mifos:mifosalphanumtext style="display:none;" property="clientDetails[${indice}].businessActivity" />
					       </c:when>
					       
					       <c:otherwise>
					       			<mifos:select
										property="clientDetails[${indice}].businessActivity"
										style="width:136px;">
										<c:forEach var="BusinessActivity"
											items="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessActivities')}">
												<html-el:option value="${BusinessActivity.id}">${BusinessActivity.name}</html-el:option>
										</c:forEach>
									</mifos:select>
					       </c:otherwise>
					   </c:choose>
</td>										
				</tr>
				<script type="text/javascript">CLIENTS_COUNT++;</script>
			</c:forEach>
			</table>
			<table align="right" width="93%" border="0" cellpadding="3"
				cellspacing="0">
				<tr>
					<td align="right" class="fontnormalbold" width="28%"><mifos:mifoslabel
						name="loan.totalamount" />:</td>
					<td valign="top" class="fontnormal"><mifos:mifosdecimalinput
						property="loanAmount" value="0.0" readonly="true"  styleId="sumLoanAmount"  />
					<mifos:mifoslabel
						name="loan.allowed_amount" /> &nbsp; <c:out
						value="${loanAccountActionForm.minLoanAmount}" /> &nbsp; - &nbsp; <c:out
						value="${loanAccountActionForm.maxLoanAmount}" /> )</td>
					<script>CalculateTotalLoanAmount(CLIENTS_COUNT);</script>	
				</tr>
			</table>
