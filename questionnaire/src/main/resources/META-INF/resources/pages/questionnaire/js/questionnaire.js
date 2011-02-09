$(document).ready(function(){
    $(function()
    {
        $('.date-pick').datePicker({startDate:'01/01/1900'});
    });

    $('.date-pick').keyfilter(/[0-9,\/]/);

    $("#questionnaire").validate(
        {
            errorPlacement: function(error, element) {
                error.appendTo( element.parent() );
            },
            errorClass: "validationErr"
        }
    );

    $(".date-pick").change(function(event) {
        $("#questionnaire").valid();
    });

    $.validator.addMethod('date-pick', function (value) {
        return (
            value==null ||
            value=="" ||
            (   /^([0]?[1-9]|[12][0-9]|3[01])[/]([0]?[1-9]|1[012])[/]\d{4}$/.test(value)
                &&
                Date.parse(value)
            )
        );
    }, 'Please enter a valid date');



	$('.txtListSearch', this.parent).keyup(function(event) {
		var search_text = $(this).val();
		var rg = new RegExp(search_text,'i');
		$(this).parent().parent().find(".questionList li label").each(function(){
 			if($(this).attr("tag").search(rg) == -1 && $(this).attr("choice").search(rg) == -1) {
				$(this).parent().css('display', 'none');
 				$(this).css('display', 'none');
				$(this).next().css('display', 'none');
				$(this).next().next().css('display', 'none');
			}
			else {
				$(this).parent().css('display', '');
				$(this).css('display', '');
				$(this).next().css('display', '');
				$(this).next().next().css('display', '');
			}
		});
	});


});
