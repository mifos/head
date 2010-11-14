var LoanRepayment = {};

LoanRepayment.submit = function (form,action){
    form.action="loanAccountAction.do?method="+action;
    form.submit();
}

LoanRepayment.validDate = function (value) {
    return (
        value!=null &&
        value!="" &&
        (   /^([0]?[1-9]|[12][0-9]|3[01])[/]([0]?[1-9]|1[012])[/]\d{4}$/.test(value)
            &&
            Date.parse(value)
        )
    )
}

$(document).ready(function() {
    $(".date-pick").datepicker({
        dateFormat: 'dd/mm/yy',
        showOn: "button",
        buttonImage: "pages/framework/images/mainbox/calendaricon.gif",
        buttonImageOnly: true
    });

    $('.date-pick').keyfilter(/[0-9,\/]/);

    $("#scheduleViewDate").bind("change keyup click blur", function(){
        var dateValue = $(this).val();
        if(LoanRepayment.validDate(dateValue)) {
            $("#loanRepayment\\.view").attr('disabled', '');
            $("#loanRepayment\\.view").attr('class', 'buttn');
        }else{
            $("#loanRepayment\\.view").attr('disabled', 'disabled');
            $("#loanRepayment\\.view").attr('class', 'disabledbuttn');
        }
    });
});