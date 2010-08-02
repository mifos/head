[#ftl]
[#import "spring.ftl" as spring]
[#assign security=JspTaglibs["http://www.springframework.org/security/tags"]]

[#-- To use macros defined here add the following directive at the top of an ftl file --]
[#-- [#import "macros.ftl" as mifos] --]

[#-- header macro --]
[#-- Generate the Mifos header with tabbed navigation --]
[#-- currentTab values: "Home","ClientsAndAccounts","Reports","Admin" --]
[#-- usage   [@mifos.topNavigation currentTab="Home" /] --]
[#macro topNavigation currentTab]
<div id = "homePageHeader">
	<IMG id="logo" height=74 alt=""
					src="pages/framework/images/logo.gif" width=188></td>

	<div id="top-right-links">
      <a id="settings" href="#">[@spring.message "yourSettings"/]</a> |
   	<a id="logout_link" href="j_spring_security_logout">[@spring.message "logout"/]</a>
	</div>
	<div id="top-menu-bar">
		<ul id="simple-menu">
		<li><a href="home.ftl" id="header.tab.home" title="[@spring.message "tab.Home" /]" [#if currentTab == "Home"]class="current"[/#if]>[@spring.message "tab.Home" /]</a></li>
		<li><a href="clientsAndAccounts.ftl" id="header.tab.clientsAndAccounts" title="[@spring.message "tab.ClientsAndAccounts" /]" [#if currentTab == "ClientsAndAccounts"]class="current"[/#if]>[@spring.message "tab.ClientsAndAccounts" /]</a></li>
		<li><a href="" id="header.tab.reports" title="[@spring.message "tab.Reports" /]" [#if currentTab == "Reports"]class="current"[/#if]>[@spring.message "tab.Reports" /]</a></li>
		[@security.authorize ifAllGranted="ROLE_ADMIN"]
		<li><a href="AdminAction.do?method=load"  id="header.tab.admin" title="[@spring.message "tab.Admin" /]" [#if currentTab == "Admin"]class="current"[/#if]>[@spring.message "tab.Admin" /]</a></li>
		[/@security.authorize]
		</ul>
	</div>
</div>
[/#macro]

[#macro topNavigationNoSecurity currentTab]
<div id = "homePageHeader">
	<IMG id="logo" height=74 alt=""
					src="images/logo.gif" width=188></td>

	<div id="top-right-links">
      <a id="settings" href="#">[@spring.message "yourSettings"/]</a> |
   	<a id="logout" href="j_spring_security_logout">[@spring.message "logout"/]</a>
	</div>
	<div id="top-menu-bar">
		<ul id="simple-menu">
		<li><a href="home.ftl" id="header.tab.home" title="[@spring.message "tab.Home" /]" [#if currentTab == "Home"]class="current"[/#if]>[@spring.message "tab.Home" /]</a></li>
		<li><a href="clientsAndAccounts.ftl" id="header.tab.clientsAndAccounts" title="[@spring.message "tab.ClientsAndAccounts" /]" [#if currentTab == "ClientsAndAccounts"]class="current"[/#if]>[@spring.message "tab.ClientsAndAccounts" /]</a></li>
		<li><a href="" id="header.tab.reports" title="[@spring.message "tab.Reports" /]" [#if currentTab == "Reports"]class="current"[/#if]>[@spring.message "tab.Reports" /]</a></li>
		<li><a href="AdminAction.do?method=load"  id="header.tab.admin" title="[@spring.message "tab.Admin" /]" [#if currentTab == "Admin"]class="current"[/#if]>[@spring.message "tab.Admin" /]</a></li>
		</ul>
	</div>
</div>
[/#macro]

[#macro header pageTitle ]
<html>
  <head>
    <title id="${pageTitle}">[@spring.message "${pageTitle}" /]</title>
    <link href="cheetah.css.ftl" rel="stylesheet" type="text/css" />
    <link href="gazelle.css.ftl" rel="stylesheet" type="text/css" />

  </head>
  <body>
[/#macro]

[#macro footer]
</body>
</html>
[/#macro]


[#macro formSingleSelectWithPrompt path options selectPrompt attributes=""]
    [@spring.bind path/]
    <select id="${spring.status.expression}" name="${spring.status.expression}" ${attributes}>
        <option value="" [@spring.checkSelected ""/]>${selectPrompt}</option>
        [#if options?is_hash]
            [#list options?keys as value]
            <option value="${value?html}"[@spring.checkSelected value/]>${options[value]?html}</option>
            [/#list]
        [#else]
            [#list options as value]
            <option value="${value?html}"[@spring.checkSelected value/]>${value?html}</option>
            [/#list]
        [/#if]
    </select>
[/#macro]



[#macro showAllErrors path]
    [@spring.bind path/]
    [#if spring.status.errorMessages?size > 0]
        <ol>
	     [#list spring.status.errorMessages as error]
	      <li>${error}</li>
	     [/#list]
	    </ol>
	[/#if]
[/#macro]

[#macro formCheckbox path attributes=""]
	[@spring.bind path /]
    [#assign id="${spring.status.expression}"]
    [#assign isSelected = spring.status.value?? && spring.status.value?string=="true"]
	<input type="hidden" name="_${id}" value="true"/>
	<input type="checkbox" id="${id}" name="${id}"[#if isSelected] checked="checked"[/#if] ${attributes}/>
[/#macro]

[#macro formCheckboxes path options separator attributes=""]
	[@spring.bind path /]
    [#list options as value]
    [#assign id="${spring.status.expression}${value_index}"]
    [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
    <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
    <label for="${id}" style="float:none;">${value?html}</label>${separator}
    [/#list]
    <input type="hidden" name="_${spring.status.expression}" value="on"/>
[/#macro]

[#macro formRadioButtons path options separator attributes=""]
	[@spring.bind path /]
    [#list options as value]
    [#assign id="${spring.status.expression}${value_index}"]
    <input type="radio" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if spring.stringStatusValue == value] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
    <label for="${id}" style="float:none;">${value?html}</label>${separator}
    [/#list]
[/#macro]