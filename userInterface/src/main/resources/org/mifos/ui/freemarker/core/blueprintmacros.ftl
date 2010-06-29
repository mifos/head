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
<div>
    <div class="topAlign">
		<a href="yourSettings.do?method=get" title="[@spring.message "yourSettings"/]">[@spring.message "yourSettings"/]</a>
		&nbsp;|&nbsp;
		<a href="loginAction.do?method=logout" title="[@spring.message "logout"/]">[@spring.message "logout"/]</a>
	</div>
    <div>
		<span class="logo"></span>
		<span class="menu">
			<span class="menu"><a href="custSearchAction.do?method=getHomePage" class="[#if currentTab == "Home"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Home" /]">[@spring.message "tab.Home" /]</a><a href="custSearchAction.do?method=loadMainSearch" class="[#if currentTab == "ClientsAndAccounts"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.ClientsAndAccounts" /]">[@spring.message "tab.ClientsAndAccounts" /]</a><a href="reportsAction.do?method=load" class="[#if currentTab == "Reports"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Reports" /]">[@spring.message "tab.Reports" /]</a>
			[@security.authorize ifAllGranted="ROLE_ADMIN"]
			<a href="admin.ftl" class="[#if currentTab == "Home"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Admin" /]">[@spring.message "tab.Admin" /]</a>
			[/@security.authorize]
		</span>
	</div>
    <div class="clear"></div>
    <div class="orangeline">&nbsp;</div>
</div>
[/#macro]

[#macro topNavigationNoSecurity currentTab]
<div>
	<div class="topAlign append-1">
		<span class="logo"></span>
		<a href="yourSettings.do?method=get" title="[@spring.message "yourSettings"/]">[@spring.message "yourSettings"/]</a>&nbsp;|&nbsp;<a href="loginAction.do?method=logout" title="[@spring.message "logout"/]">[@spring.message "logout"/]</a>
	</div>
    <div>
		<span class="menu"><a href="custSearchAction.do?method=getHomePage" class="[#if currentTab == "Home"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Home" /]">[@spring.message "tab.Home" /]</a><a href="custSearchAction.do?method=loadMainSearch" class="[#if currentTab == "ClientsAndAccounts"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.ClientsAndAccounts" /]">[@spring.message "tab.ClientsAndAccounts" /]</a><a href="reportsAction.do?method=load" class="[#if currentTab == "Reports"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Reports" /]">[@spring.message "tab.Reports" /]</a><a href="admin.ftl" class="[#if currentTab == "Admin"]taborange[#else]tablightorange[/#if]" title="[@spring.message "tab.Admin" /]">[@spring.message "tab.Admin" /]</a></span>
	</div>
    <div class="clear"></div>
    <div class="orangeline">&nbsp;</div>
</div>
[/#macro]

[#macro crumbs breadcrumbs]
<div class="span-20 bluedivs paddingLeft">
	    [#list breadcrumbs as messages]
  			[#if messages_has_next]
    			<a href="${messages.link}">[@spring.message "${messages.message}" /]</a>&nbsp;/&nbsp;  [#else] <span class="fontBold">[@spring.message "${messages.message}" /]</span>
  			[/#if]
  		[/#list]
 </div>
[/#macro]

[#macro crumb url]
<div class="span-20 bluedivs paddingLeft"><a href="admin.ftl">[@spring.message "tab.Admin"/]</a>&nbsp;/&nbsp;<span class="fontBold">[@spring.message "${url}"/]</span></div>
[/#macro]

[#macro header pageTitle]
<html>
  <head>
    <title id="${pageTitle}">[@spring.message "${pageTitle}" /]</title>
    <link href="pages/framework/css/maincss.css" rel="stylesheet" type="text/css" />
    <link href="pages/framework/css/screen.css" rel="stylesheet" type="text/css" />   
  </head>
  <body>
[/#macro]
[#macro footer]

</body>
</html>
[/#macro]
