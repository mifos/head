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
    <div class="marginLeft30">
        <div class="span-24">
            <div class="clear">&nbsp;</div>
            <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.batchjobs.batchjobsInformation" /]</span></p>
            <div class="span-23">
                <span class="span-11">[@spring.message "systemAdministration.batchjobs.welcometotheMifosbatchjobsArea" /].</span>
                [@spring.showErrors "<br>" /]
            </div>
            <div class="clear">&nbsp;</div>
            <div class="span-23 borderbtm">
                <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.batchjobs.scheduledTasks" /]</span></p>
            </div>
            [#list model.batchjobs as batchjobs]
            <div class="span-24 paddingLeft">
                <span class="span-1 "><strong>${batchjobs_index + 1}.</strong></span>
                <span class="span-15 ">${batchjobs.name}</span>
            </div>
            [/#list]
        </div>
    </div>
</div>
<!--Main Content Ends-->
[@mifos.footer/]