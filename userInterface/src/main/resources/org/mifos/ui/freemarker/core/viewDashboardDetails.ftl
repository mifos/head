[#ftl]
[#include "layout.ftl"]
[@clientLeftPane "ClientsAndAccounts"]
<span id="page.id" title="viewDashboardDetails"></span>
<div class="content">
    <table id="dashboardDetails" class="datatable">
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
        [#list dashboardDetails as detail]
            <tr>    
                    <td>
                    ${detail_index+1}
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
    [@widget.datatable "dashboardDetails" /]
    [@form.returnToPage  "home.ftl" "button.back" "dashboardDetails.backToHome"/]
</div>
[/@clientLeftPane]