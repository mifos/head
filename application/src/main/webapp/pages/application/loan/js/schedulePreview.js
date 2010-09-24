$(document).ready(function(evt){
        $(function()
	    {
	        $('.date-pick').datePicker({startDate:'01/01/1900'});
	    });
	    $('.date-pick').keyfilter(/[0-9,\/]/);
});