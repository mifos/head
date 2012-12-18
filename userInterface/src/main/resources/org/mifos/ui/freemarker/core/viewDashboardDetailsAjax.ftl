[#ftl]

[#include "layout.ftl"]

{
    "sEcho": "${sEcho}",
    "iTotalRecords": "${totalSize?c}",
    "iTotalDisplayRecords": "${totalSize?c}",
    "aaData": [
        [#assign index = iDisplayStart /]
            [#list dashboardDetails as detail]
                [#if index != iDisplayStart]
                ,
                [/#if]
                [#assign index = index + 1 /]
                [   
                    [@compress single_line=true]
                    ${index?c},
                    " <a href='${detail.url}'>
                        ${detail.globalNumber}</a> ",
                    [#if type == 'c']
                        " ${detail.displayName} ",
                    [/#if]
                        " ${detail.state} ",
                        " ${detail.loanOfficer} ",
                        " ${detail.balance} "
                    [#if type == 'l']
                        ," ${detail.displayName} "
                    [/#if]
                    [/@compress]
                ]
        [/#list]
    ]
}