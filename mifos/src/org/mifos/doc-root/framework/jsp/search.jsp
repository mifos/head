 <%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
  <html-el:form action="custSearchAction.do?method=loadAllBranches">
        <tr>
          <td class="leftpanelinks"><table width="90%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td class="paddingbottom03"><span class="fontnormal8ptbold"><mifos:mifoslabel name="framework.searchCriteria" bundle="FrameworkUIResources"></mifos:mifoslabel></span> </td>
            </tr>
          </table>
            <table width="90%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="100%" colspan="2">
                <html-el:text property="searchString" size="20" maxlength="200"/>
				<c:choose>
				<c:when test='${sessionScope.UserContext.officeLevelId==5}'>
				<html-el:hidden property="officeId" value="${sessionScope.UserContext.branchId}"/> 
				</c:when>
				<c:otherwise>
				<html-el:hidden property="officeId" value="0"/> 
				</c:otherwise>
				</c:choose>	
                </td>
              </tr>
            </table>
            <table width="136" border="0" cellspacing="0" cellpadding="3">
              <tr>
                <td align="right">
                 <html-el:submit property="searchButton" styleClass="buttn">
                <mifos:mifoslabel name="framework.search" bundle="FrameworkUIResources"></mifos:mifoslabel>
                </html-el:submit>
                </td>
              </tr>
            </table> 
            </td>
            </tr>
            </html-el:form>