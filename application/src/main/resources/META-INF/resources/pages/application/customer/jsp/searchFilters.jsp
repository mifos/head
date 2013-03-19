<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<tr class="fontnormal">
     <td>
         <mifos:mifoslabel name="CustomerSearch.filters" bundle="CustomerSearchUIResources"/> 
         [ <a id="filters-toggler" href="javascript:void(0)">
         <span class="showorhide" style="display: none">show</span>
         <span class="showorhide">hide</span>
         </a> ]:
     </td>
</tr>
<tr class="fontnormal">
     <td class="search-filters">
             <mifos:mifoslabel name="CustomerSearch.clients" bundle="CustomerSearchUIResources"/>:
             <select id="home.clientSearch" name="clientSearch">
                <option value="true"><mifos:mifoslabel name="boolean.yes" bundle="messages"/></option>
                <option value="false"><mifos:mifoslabel name="boolean.no" bundle="messages"/></option>
             </select>
             <mifos:mifoslabel name="CustomerSearch.groups" bundle="CustomerSearchUIResources"/>:
             <select id="home.groupSearch" name="groupSearch">
                <option value="true"><mifos:mifoslabel name="boolean.yes" bundle="messages"/></option>
                <option value="false"><mifos:mifoslabel name="boolean.no" bundle="messages"/></option>
             </select>
             <c:if test="${isCenterHierarchyExists=='true'}">
                <mifos:mifoslabel name="CustomerSearch.centers" bundle="CustomerSearchUIResources"/> 
                <select id="home.centerSearch" name="centerSearch">
                <option value="true"><mifos:mifoslabel name="boolean.yes" bundle="messages"/></option>
                <option value="false"><mifos:mifoslabel name="boolean.no" bundle="messages"/></option>
             </select>
             </c:if>
     </td>
</tr>