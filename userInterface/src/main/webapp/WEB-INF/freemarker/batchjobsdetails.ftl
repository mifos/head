[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar htTotal">
    [#include "adminLeftPane.ftl" /]
</div>
<!--  Main Content Begins-->
<div class=" content leftMargin180">
    [@mifos.crumbs breadcrumbs /]
    [@spring.showErrors "<br>" /]
    <div class="marginLeft30">
        <div class="span-21 borderbtm">
            <div class="clear">&nbsp;</div>
            
            [#list model.batchjobs as batchjob]
                [#if batchjob.name == model.jobFailName]
                    <p class="font15"><span class="orangeheading">${batchjob.name}&nbsp;[@spring.message "systemAdministration.batchjobs.batchjobsFailDetails" /]</span></p><br>
                    <p>${batchjob.failDescription}</p>
                [/#if]
            [/#list]
        </div>
        <br /><a href="batchjobs.ftl">[@spring.message "systemAdministration.batchjobs.return" /]</a>
    </div>
</div>
<!--Main Content Ends-->
[@mifos.footer/]