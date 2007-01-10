<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
		<html-el:form action="custSearchAction.do">
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15"><font class="fontnormalRedBold"><html-el:errors
						bundle="CustomerSearchUIResources" /> </font>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
						
						<c:set
								var="Office"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'Office')}" />
							<c:set var="isCenterHeirarchyExists"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'GrpHierExists')}" />

								<c:set var="OfficesList"
								value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'OfficesList')}" />
							<td class="fontnormal">Search for: <html-el:text
								property="searchString" maxlength="200"/> 
									
								<html-el:select style="width:136px;" property="officeId">
								<c:choose>								
								<c:when test="${not empty OfficesList}">
								<html-el:option value="0">
									<mifos:mifoslabel name="CustomerSearch.all"/> <mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}"/><mifos:mifoslabel name="CustomerSearch.s"/>
								</html-el:option>																								
								<html-el:options collection="OfficesList" property="officeId" labelProperty="officeName"/>
								</c:when>
								<c:otherwise>
								<c:choose>
								<c:when test="${custSearchActionForm.officeId != null && custSearchActionForm.officeId >1}" >
										<html-el:option value="${custSearchActionForm.officeId}">
									<c:out value='${Office}' />
									</html-el:option>
								</c:when>
								<c:when test='${sessionScope.UserContext.branchId == 1 && custSearchActionForm.officeId == null}'>
								<html-el:option value="0">
									<mifos:mifoslabel name="CustomerSearch.all"/> <mifos:mifoslabel name="${ConfigurationConstants.BRANCHOFFICE}"/><mifos:mifoslabel name="CustomerSearch.s"/>
								</html-el:option>	
								<html-el:option value="${sessionScope.UserContext.branchId}">
									<c:out value='${Office}' />
								</html-el:option>																															
								</c:when>
								<c:otherwise>
								<html-el:option value="${sessionScope.UserContext.branchId}">
									<c:out value='${Office}' />
								</html-el:option>
								</c:otherwise>
								</c:choose>										
								</c:otherwise>
								</c:choose>
								</html-el:select> 
								&nbsp; <html-el:submit
								style="width:60px;" styleClass="buttn">
								<mifos:mifoslabel name="CustomerSearch.search" />
							</html-el:submit></td>
						</tr>
					</table>
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td colspan="2" class="blueline"><img src="images/trans.gif"
								width="5" height="3"></td>
						</tr>
						<tr class="fontnormal">
			                <td colspan="2" valign="top"><img src="images/trans.gif" width="5" height="3"></td>
			            </tr>
						<tr><td>
						<mifos:mifostabletagdata key="customersearch" name="customersearch" type="multiple" width="100%" border="0" cellspacing="0" cellpadding="3"/>
						</td></tr>
					</table>
					</td>
				</tr>
			</table>
		<html-el:hidden property="method" value="mainSearch" />	
		<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			
		</html-el:form>
	</tiles:put>
</tiles:insert>
