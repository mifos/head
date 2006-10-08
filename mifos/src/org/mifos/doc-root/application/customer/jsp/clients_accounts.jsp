<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutmenu">
	<tiles:put name="body" type="string">
		<html-el:form action="custSearchAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" valign="top" class="paddingL10">
					<table width="90%" border="0" cellpadding="0" cellspacing="3">
						<tr>
							<td align="left" valign="top"><span class="headingorange"> <c:set
								var="Office"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Office')}" />
							<c:set var="isCenterHeirarchyExists"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'GrpHierExists')}" />

								<c:set var="OfficesList"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficesList')}" />
							<c:out value='${Office}' /> <br>
							</span><span class="fontnormalbold"> <mifos:mifoslabel
								name="CustomerSearch.toreview" /> <mifos:mifoslabel
								name="CustomerSearch.or" /> <mifos:mifoslabel
								name="CustomerSearch.edit" /> <mifos:mifoslabel
								name="CustomerSearch.a" /> <mifos:mifoslabel
								name="${ConfigurationConstants.CLIENT}" />, <c:choose>
								<c:when test="${isCenterHeirarchyExists==Constants.YES}">
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,&nbsp;
	                  		<mifos:mifoslabel
										name="${ConfigurationConstants.CENTER}" />,
	                  	</c:when>
								<c:otherwise>
									<mifos:mifoslabel name="${ConfigurationConstants.GROUP}" />,
	                  		</c:otherwise>
							</c:choose> <mifos:mifoslabel name="CustomerSearch.or" /> <mifos:mifoslabel
								name="CustomerSearch.account" /> </span></td>
						</tr>

					</table>
					<br>
					<table width="90%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="313" valign="top">
							<table width="100%" border="0" cellspacing="0" cellpadding="4">
								<tr class="fontnormal">
									<td width="100%" colspan="2" class="bglightblue"><span
										class="heading"> <mifos:mifoslabel
										name="CustomerSearch.search" /> </span></td>
								</tr>
								<font class="fontnormalRedBold"><html-el:errors
									bundle="CustomerSearchUIResources" /> </font>
							</table>
							<table width="90%" border="0" cellspacing="0" cellpadding="4">
								<tr>
									<td class="paddingbottom03"><span class="fontnormal"> <mifos:mifoslabel
										name="CustomerSearch.searchstring" /> </span></td>
								</tr>
							</table>

							<table border="0" cellpadding="4" cellspacing="0">
								<tr>
									<td><html-el:text property="searchString" maxlength="200" /></td>
									<td class="paddingleft05notop"><html-el:select
										style="width:136px;" property="officeId">
										<html-el:option value="0">
											<mifos:mifoslabel name="CustomerSearch.all" /><mifos:mifoslabel	name="${ConfigurationConstants.BRANCHOFFICE}" /><mifos:mifoslabel name="CustomerSearch.s" />
										</html-el:option>
										<html-el:options collection="OfficesList" property="officeId"
											labelProperty="officeName" />
									</html-el:select></td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td align="right" class="paddingleft05notop"><html-el:submit
										style="width:60px;" styleClass="buttn">
										<mifos:mifoslabel name="CustomerSearch.search" />
									</html-el:submit></td>
								</tr>
							</table>
							</td>
							<td width="101" align="center" valign="top" class="headingorange">
							<mifos:mifoslabel name="CustomerSearch.or" />&nbsp;</td>
							<td width="287" valign="top">
							<table width="100%" border="0" cellspacing="0" cellpadding="4">
								<tr class="fontnormal">
									<td width="100%" colspan="2" class="bglightblue"><span
										class="heading"> <mifos:mifoslabel
										name="CustomerSearch.select" />&nbsp; <mifos:mifoslabel
										name="${ConfigurationConstants.BRANCHOFFICE}" /> </span></td>
								</tr>
							</table>
							<div id="Layer2"
								style="border: 1px solid #CECECE; height:100px; width:100%; overflow: auto; padding:6px; margin-top:5px;">
							<span class="fontnormal"> <c:forEach
								items="${OfficesList}" var="office">
								<html-el:link action="custSearchAction.do?method=preview&officeId=${office.officeId}&currentFlowKey=${requestScope.currentFlowKey}"
									>
									<c:out value="${office.officeName}" />
								</html-el:link>
								<br>
							</c:forEach> </span></div>
							</td>
						</tr>
					</table>
					<br>
					<br>
					</td>
				</tr>
			</table>
			<br>
		<html-el:hidden property="method" value="mainSearch" />	
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
