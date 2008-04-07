<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tiles:insert definition=".clientsacclayoutmenu">
	<tiles:put name="body" type="string">

	
		<table width="95%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" valign="top" class="paddingL10">
				<table width="90%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" valign="top"><span class="headingorange"><mifos:mifoslabel
							name="accountStatus.changeaccountstatus" /> - <mifos:mifoslabel
							name="accountStatus.confirmation" /><br><br></span></td>
					</tr>
				</table>
				
				<table width="96%" border="0" cellpadding="0" cellspacing="0">
					<tr class="fontnormalbold">
						<td width="100%" colspan="2" valign="top"><mifos:mifoslabel
							name="accountStatus.statusmessage" />:<br><br></td>
							
					</tr>
					<c:forEach var="account" items="${accountsList}">
					<tr class="fontnormal">
						<td valign="top">
							<span class="fontnormal">
								<html-el:link href="loanAccountAction.do?method=get&globalAccountNum=${account}&randomNUm=${sessionScope.randomNUm}">
									<mifos:mifoslabel name="accountStatus.account" />
									<c:out value="${account}" />
								</html-el:link>
							</span>
						</td>
					</tr>	
					</c:forEach>
				</table>
	</tiles:put>
</tiles:insert>
