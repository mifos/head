 <%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
  <html-el:form action="CustomerSearchAction.do?method=loadAllBranches">
        <tr>
          <td class="leftpanelinks"><table width="90%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td class="paddingbottom03"><span class="fontnormal8ptbold">Search
                  by name, system ID or account number </span> </td>
            </tr>
          </table>
            <table width="90%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="100%" colspan="2">
                <html-el:text property="searchNode(searchString)" size="20" maxlength="200"/>
				<html-el:hidden property="searchNode(search_name)" value="CustomerSearchResults"/>
				
				<html-el:hidden property="officeName" value='${requestScope.Context.businessResults["Office"]}'/>													
				<c:choose>
				<c:when test='${sessionScope.UserContext.officeLevelId==5}'>
				<html-el:hidden property="searchNode(search_officeId)" value="${sessionScope.UserContext.branchId}"/> 
				</c:when>
				<c:otherwise>
				<html-el:hidden property="searchNode(search_officeId)" value="0"/> 
				</c:otherwise>
				</c:choose>	
				
                </td>
              </tr>
            </table>
             
            <table width="136" border="0" cellspacing="0" cellpadding="3">
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