[#ftl]
 <!--  Search Filters Begins-->
<div id="filters">
    Filters [ <a id="filters-toggler" href="javascript:void(0)">
     <span class="showorhide" style="display: none">show</span>
     <span class="showorhide">hide</span>
     </a> ]
    <div id="search-filters">
        [@spring.bind "customerSearch.clientSearch" /]
        [@form.label "${spring.status.expression}"][@spring.message "manageRoles.clients" /][/@form.label]: 
        <select id="${spring.status.expression}" name="${spring.status.expression}">
            <option value="true"[@spring.checkSelected true?string/]>[@spring.message "boolean.yes" /]</option>
            <option value="false"[@spring.checkSelected false?string/]>[@spring.message "boolean.no" /]</option>
        </select>
        [@spring.bind "customerSearch.groupSearch" /]
        [@form.label "${spring.status.expression}"][@spring.message "manageRoles.groups" /][/@form.label]: 
        <select id="${spring.status.expression}" name="${spring.status.expression}">
            <option value="true"[@spring.checkSelected true?string/]>[@spring.message "boolean.yes" /]</option>
            <option value="false"[@spring.checkSelected false?string/]>[@spring.message "boolean.no" /]</option>
        </select>
        [@spring.bind "customerSearch.centerSearch" /]
        [#if isCenterHierarchyExists ]
            [@form.label "${spring.status.expression}"][@spring.message "manageRoles.centers" /][/@form.label]: 
        [/#if]
        <select id="${spring.status.expression}" name="${spring.status.expression}" [#if !isCenterHierarchyExists ]style="display:none"[/#if]>
            <option value="true"[@spring.checkSelected true?string/]>[@spring.message "boolean.yes" /]</option>
            <option value="false"[@spring.checkSelected false?string/]>[@spring.message "boolean.no" /]</option>
        </select>
    </div>
</div>
  <!--  Search Filters Ends-->
