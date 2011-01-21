[#ftl]
[#macro webflow]
[@mifos.header "title" /]

<div class="content bordered">
	<div class="bread-crumb bordered">
		bread crumb...
	</div>
	<div class="flow-content bordered">
		[#nested]
	</div>
</div>

[@mifos.footer /]
[/#macro]
