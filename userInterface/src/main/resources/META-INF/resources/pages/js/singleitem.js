$(document).ready(function() {
	$("select").each(function() {
		if ($(this).children().length <= 2) {
			$(this).attr("selectedIndex", 1);
		}
	});
})