[#ftl]
[#include "layout.ftl"]
[@clientLeftPane "ClientsAndAccounts"]
[#if dashboardDetails??]
    [@widget.dashboard totalSize?c ajaxUrl disableSorting/]
[#else]
    [@widget.dashboard 0 ajaxUrl disableSorting/]
[/#if]
<span id="page.id" title="viewDashboardDetails"></span>

<div class="content">
    <table id="dashboard" class="datatable">
        <thead>
            <tr>
                <th>
                    <span class='fontnormalbold'>
                    #
                    <span>
                </th>
                [#list tableHeaders as header]
                <th>
                    <span class='fontnormalbold'>
                    ${header}
                    <span>
                </th>
                [/#list]
            </tr>
        </thead>
       <tbody>
       [#if iDisplayStart??]
        [#assign index = iDisplayStart /]
        [#else] [#assign index = 1 /]
        [/#if]
        [#list dashboardDetails as detail]
            <tr>    
                    <td>
                    ${index?c}
                    [#assign index = index + 1 /]
                    </td>
                    <td>
                        <a href="${detail.url}">
                        ${detail.globalNumber}</a>
                    </td>
                    [#if type == 'c']
                        <td>
                        ${detail.displayName}
                        </td>
                    [/#if]
                    <td>
                        ${detail.state}
                    </td>
                    <td>
                        ${detail.loanOfficer}
                    </td>
                    <td>
                        ${detail.balance}
                    </td>
                    [#if type == 'l']
                    <td>
                        ${detail.displayName}
                    </td>
                    [/#if]
                </tr>
        [/#list]
        </tbody>
    </table>
    [@form.returnToPage  "home.ftl" "button.back" "dashboardDetails.backToHome"/]
</div>
[/@clientLeftPane]