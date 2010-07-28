$(document).ready(function(){
    $(function()
    {
        $('.date-pick').datePicker({startDate:'01/01/1900'});
    });

    $('.date-pick').keyfilter(/[0-9,\/]/);

    $("#survey").validate(
        {
            errorPlacement: function(error, element) {
                error.appendTo( element.parent() );
            },
            errorClass: "validationErr"
        }
    );

    $(".date-pick").change(function(event) {
        $("#survey").valid();
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

});
