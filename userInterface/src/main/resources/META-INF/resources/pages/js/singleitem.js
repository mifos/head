$(document).ready(function() {
	$("select").each(function() {
		if (!$(this).hasClass("noAutoSelect")) {
			if ($(this).children().length == 2 && $(this).val() == 0) {
				$(this).attr("selectedIndex", 1);
				$(this).trigger('onchange');
			}
	}});
})