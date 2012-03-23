$(document).ready(function() {
	$("[id$=DD], [id$=MM]").each(function(index) {
		var val = $(this).val();
		if (val.length == 1 && val < 10) {
			val = "0" + val;
		}
		$(this).val(val);
	});
});