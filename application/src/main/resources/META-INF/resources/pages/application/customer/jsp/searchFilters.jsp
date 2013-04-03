<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/userlocaledate" prefix="userdatefn"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>

<script src="pages/application/customer/js/searchFilters.js"></script>

<tr class="fontnormal">
    <td>
        <br />
        <b><mifos:mifoslabel name="CustomerSearch.filters" bundle="CustomerSearchUIResources"/></b>
        [ <a id="filters-toggler" href="javascript:void(0)">
        <span class="showorhide" style="display: none">show</span>
        <span class="showorhide">hide</span>
        </a> ]:
        <br /><br />
    </td>
</tr>
<tr class="fontnormal">
    <td class="search-filters">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr class="fontnormal">
                <td colspan="2"><mifos:mifoslabel name="CustomerSearch.creationDate" bundle="CustomerSearchUIResources" isColonRequired="yes" /></td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="manageLoanProducts.previewLoanProduct.startdate" bundle="messages" isColonRequired="yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <input type="text" name="filters.creationDateRangeStart" />
                </td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="manageLoanProducts.previewLoanProduct.enddate" bundle="messages" isColonRequired="yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <input type="text" name="filters.creationDateRangeEnd" />
                </td>
            </tr>
            <tr class="fontnormal">
                <td colspan="2">
                    <br />
                    <mifos:mifoslabel name="CustomerSearch.clients" bundle="CustomerSearchUIResources" isColonRequired="yes" />
                    <select id="clientSearch" name="filters.customerLevels['CLIENT']">
                        <option value="true"><mifos:mifoslabel name="boolean.yes" bundle="messages"/></option>
                        <option value="false"><mifos:mifoslabel name="boolean.no" bundle="messages"/></option>
                    </select>
                </td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="manageRoles.status" bundle="messages" isColonRequired="yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <c:set var="availibleCustomerStates" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'availibleCustomerStates')}" />
                    <select name="filters.customerStates['CLIENT']">
                        <option value="0"><mifos:mifoslabel name="CustomerSearch.all" bundle="CustomerSearchUIResources" /></option>
                        <c:forEach items="${availibleCustomerStates['CLIENT']}" var="state">
                           <option value="${state.id}">${state.statusName}</option>
                        </c:forEach>
                     </select>
                </td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="systemUsers.preview.gender" bundle="messages" isColonRequired="yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <c:set var="availibleClientGenders" scope="request" value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'availibleClientGenders')}" />
                    <select name="filters.gender">
                        <option value="0"><mifos:mifoslabel name="CustomerSearch.all" bundle="CustomerSearchUIResources" /></option>
                        <c:forEach items="${availibleClientGenders}" var="gender">
                            <option value="${gender.id}">${gender.name}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="manadatoryHiddenFields.citizenship" bundle="messages" isColonRequired="yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <input type="text" name="filters.citizenship" maxlength="200" />
                </td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="manadatoryHiddenFields.ethnicity" bundle="messages" isColonRequired="yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <input type="text" name="filters.ethnicity" maxlength="200" />
                </td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="configuration.businessactivity" isColonRequired="Yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <input type="text" name="filters.businessActivity" maxlength="200" />
                </td>
            </tr>
            <tr class="fontnormal">
                <td colspan="2">
                    <br />
                    <mifos:mifoslabel name="CustomerSearch.groups" bundle="CustomerSearchUIResources" isColonRequired="yes" />
                <select id="groupSearch" name="filters.customerLevels['GROUP']">
                    <option value="true"><mifos:mifoslabel name="boolean.yes" bundle="messages"/></option>
                    <option value="false"><mifos:mifoslabel name="boolean.no" bundle="messages"/></option>
                </select>
                </td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="manageRoles.status" bundle="messages" isColonRequired="yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <select name="filters.customerStates['GROUP']">
                        <option value="0"><mifos:mifoslabel name="CustomerSearch.all" bundle="CustomerSearchUIResources" /></option>
                        <c:forEach items="${availibleCustomerStates['GROUP']}" var="state">
                            <option value="${state.id}">${state.statusName}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <c:if test="${isCenterHierarchyExists=='true'}">
            <tr class="fontnormal">
                <td colspan="2">
                    <mifos:mifoslabel name="CustomerSearch.centers" bundle="CustomerSearchUIResources" isColonRequired="yes"/>
                    <select id="centerSearch" name="filters.customerLevels['CENTER']">
                        <option value="true"><mifos:mifoslabel name="boolean.yes" bundle="messages"/></option>
                        <option value="false"><mifos:mifoslabel name="boolean.no" bundle="messages"/></option>
                    </select>
                </td>
            </tr>
            <tr class="fontnormal">
                <td width="20%" align="right">
                    <mifos:mifoslabel name="manageRoles.status" bundle="messages" isColonRequired="yes" />
                </td>
                <td width="80%" class="paddingL10">
                    <select name="filters.customerStates['CENTER']">
                        <option value="0"><mifos:mifoslabel name="CustomerSearch.all" bundle="CustomerSearchUIResources" /></option>
                        <c:forEach items="${availibleCustomerStates['CENTER']}" var="state">
                            <option value="${state.id}">${state.statusName}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            </c:if>
         </table>
     </td>
</tr>