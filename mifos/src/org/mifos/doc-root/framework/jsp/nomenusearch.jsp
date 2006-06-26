<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
  <html-el:form action="CustomerSearchAction.do?method=loadAllBranches">
        <tr>
          <td class="leftpanelinks">
	<table width="90%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td class="paddingbottom03"><span class="fontnormal8ptbold">
              <mifos:mifoslabel	 name="label.menuSearch" bundle="MenuResources"></mifos:mifoslabel>
              <mifos:mifoslabel	 name="label.client" bundle="MenuResources"></mifos:mifoslabel>
              <mifos:mifoslabel name="label.searchnamesysid" bundle="MenuResources"></mifos:mifoslabel>
</span> </td>
            </tr>
          </table>
            <table width="90%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="100%" colspan="2">
                
                <html-el:text property="searchNode(searchString)" size="20" maxlength="200"/>
				<html-el:hidden property="searchNode(search_name)" value="CustomerSearchResults"/>
				
				<c:choose>
				<c:when test='${sessionScope.UserContext.officeLevelId==5}'>
				<html-el:hidden property="searchNode(search_officeId)" value="${sessionScope.UserContext.branchId}"/> 
				</c:when>
				<c:otherwise>
				<html-el:hidden property="searchNode(search_officeId)" value="0"/> 
				</c:otherwise>
				</c:choose>					
				<html-el:hidden property="officeName" value='${requestScope.Context.businessResults["Office"]}'/>													
													
				</td>
              </tr>
            </table>
            <table width="143" border="0" cellspacing="0" cellpadding="10">
              <tr>
                <td align="right">                
                
                
                <html-el:submit property="searchButton" styleClass="buttn" style="width:60px;" >
                Search
                </html-el:submit>
                
                </td>
              </tr>
            </table>            
            </td>
        </tr>
</html-el:form>