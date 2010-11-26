$(document).ready(function(){

    $(":regex(id, monthlyCashFlows\\[[0-9]+\\].expense)").bind("focus", function(){
        $(this).keyfilter(/[0-9,\.]/);
    });

    $(":regex(id, monthlyCashFlows\\[[0-9]+\\].revenue)").bind("focus", function(){
        $(this).keyfilter(/[0-9,\.]/);
    });

    $(":regex(id, totalCapital)").bind("focus", function(){
        $(this).keyfilter(/[0-9,\.]/);
    });

    $(":regex(id, totalLiability)").bind("focus", function(){
        $(this).keyfilter(/[0-9,\.]/);
    });

    $("#captureCashFlowForm").validate(
        {
            errorPlacement: function(error, element) {
                error.appendTo( element.parent() );
            },
            errorClass: "red"
        }
    );

    $.validator.addMethod('amount', function (value) {
        return (
            value==null ||
            value=="" ||
            (
                /^\d{1,10}(\.\d{1,3})?$/.test(value)
            )
        );
    }, 'Invalid amount. Maximum of 10 digits and 3 decimal places are supported.');

    $.validator.addMethod('total-capital-liability', function (value) {
        return (
            value==null ||
            value=="" ||
            (
                /^\d{1,100}(\.\d{1,3})?$/.test(value)
            )
        );
    }, 'Invalid amount. Maximum of 3 decimal places are supported.');

});