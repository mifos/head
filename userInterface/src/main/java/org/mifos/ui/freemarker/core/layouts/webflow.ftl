[#ftl]
[#macro webflow states currentState]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="ClientsAndAccounts" /]

<div class="content">
	<div class="bread-crumb bordered">
		<ul>
		[#list states as state]
		<li [#if state == currentState]class="active"[/#if]>[@spring.message state/]</li>
		[/#list]
		</ul>
	</div>
	<div class="flow-content bordered">
		[#nested]
	</div>
</div>

[@mifos.footer /]
[/#macro]
