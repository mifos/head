$(document).ready(function() {

    $(":regex(id, dueDate.[0-9]+)").datepicker({
        dateFormat: 'dd-M-yy',
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
		buttonImageOnly: true
    });

    $('.date-pick-payment-data-beans').datepicker({
        dateFormat: 'dd/mm/yy',
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
		buttonImageOnly: true
    });

    $("form").validate(
        {errorPlacement: function(error, element) {
            error.appendTo( element.parent() );
        },
        errorClass: "validationErr"
    });

    $('.date-pick-payment-data-beans').keyfilter(/[0-9,\/]/);

    $(".date-pick-payment-data-beans").change(function(event) {
        $("form").valid();
    });

    $.validator.addMethod('date-pick-payment-data-beans', function (value) {
        return (
            value==null ||
            value=="" ||
            (   /^([0]?[1-9]|[12][0-9]|3[01])[/]([0]?[1-9]|1[012])[/]\d{4}$/.test(value)
                &&
                Date.parse(value)
            )
        );
    }, 'Please enter a valid date');

    if ($("input[id=schedulePreview.button.validate]") != null) {
        $("input[id=schedulePreview.button.validate]").click(function(event) {
            $("input[name=method]").val("validateInstallments");
            form = $("form[name=loanAccountActionForm]");
            form.action="loanAccountAction.do";
            form.submit();
        });
    }

    if ($("input[id=schedulePreview.button.preview]") != null) {
        $("input[id=schedulePreview.button.preview]").click(function(event) {
            $("input[name=method]").val("preview");
            form = $("form[name=loanAccountActionForm]");
            form.action="loanAccountAction.do";
            form.submit();
        });
    }
  }
);